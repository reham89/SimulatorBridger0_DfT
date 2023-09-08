package uk.ncl.giacomobergami.SumoOsmosisBridger.traffic_converter;

import com.opencsv.exceptions.CsvException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ncl.giacomobergami.traffic_converter.abstracted.TrafficConverter;
import uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.netgen.NetworkGenerator;
import uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.netgen.NetworkGeneratorFactory;
import uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.rsu.RSUUpdater;
import uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.rsu.RSUUpdaterFactory;
import uk.ncl.giacomobergami.utils.data.XPathUtil;
import uk.ncl.giacomobergami.utils.data.YAML;
import uk.ncl.giacomobergami.utils.pipeline_confs.TrafficConfiguration;
import uk.ncl.giacomobergami.utils.shared_data.edge.TimedEdge;
import uk.ncl.giacomobergami.utils.shared_data.iot.TimedIoT;
import uk.ncl.giacomobergami.utils.structures.StraightforwardAdjacencyList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;
import com.opencsv.*;

public class DfTConverter extends TrafficConverter {

    private final SUMOConfiguration concreteConf;
    private final NetworkGenerator netGen;
    private final RSUUpdater rsuUpdater;
    private DocumentBuilder db;
    Document networkFile;
    StraightforwardAdjacencyList<String> connectionPath;
    HashMap<Double, List<TimedIoT>> timedIoTDevices;
    HashSet<TimedEdge> roadSideUnits;
    // private static Logger logger = LogManager.getRootLogger();
    List<String[]> data = new ArrayList<>();
    List<TimedIoT> timedIoTs = new ArrayList<>();
    List<TimedEdge> timedEdges = new ArrayList<>();
    List<String> rows = new ArrayList<>();
    List<Double> temporalOrdering;

    public DfTConverter(TrafficConfiguration conf) {
        super(conf);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            db = null;
        }
        concreteConf = YAML.parse(SUMOConfiguration.class, new File(conf.YAMLConverterConfiguration)).orElseThrow();
        temporalOrdering = new ArrayList<>();
        networkFile = null;
        timedIoTDevices = new HashMap<>();
        roadSideUnits = new HashSet<>();
        netGen = NetworkGeneratorFactory.generateFacade(concreteConf.generateRSUAdjacencyList);
        rsuUpdater = RSUUpdaterFactory.generateFacade(concreteConf.updateRSUFields,
                concreteConf.default_rsu_communication_radius,
                concreteConf.default_max_vehicle_communication);
        connectionPath = new StraightforwardAdjacencyList<>();
    }

    @Override
    protected boolean initReadSimulatorOutput() {
        connectionPath.clear();
        temporalOrdering.clear();
        timedIoTDevices.clear();
        networkFile = null;
        File file = new File(concreteConf.DfT_file_path);
        Document DfTFile = null;
//        try {
//            DfTFile = db.parse(file);
//        } catch (SAXException | IOException e) {
//            e.printStackTrace();
//            return false; }
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            List<String[]> rows = reader.readAll();
            String[][] array = rows.toArray(new String[0][]);
            //determining the indices of columns
            int timeColumnIndex = Arrays.asList(rows.get(0)).indexOf("hour");
            int VehColumnIndex = Arrays.asList(rows.get(0)).indexOf("All_motor_vehicles");
            int eastColumnIndex = Arrays.asList(rows.get(0)).indexOf("Easting");
            int northColumnIndex = Arrays.asList(rows.get(0)).indexOf("Northing");
            int laneColumnIndex = Arrays.asList(rows.get(0)).indexOf("Direction_of_travel");
            int dateColumnIndex = Arrays.asList(rows.get(0)).indexOf("Count_date");
            int idColumnIndex = Arrays.asList(rows.get(0)).indexOf("Count_point_id");
            // Group rows by Count_date
            Map<String, List<String[]>> groupedByDate = new HashMap<>();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                String date = row[dateColumnIndex];
                groupedByDate
                        .computeIfAbsent(date, k -> new ArrayList<>())
                        .add(row);
            }
            // Iterate over each date
            for (String date : groupedByDate.keySet()) {
                List<String[]> dateRows = groupedByDate.get(date);
                // Iterate from 1 to 24
                for (double currTime = 1; currTime <= 24; currTime++) {
                    boolean found = false;
                    // Check if there's a row for currTime
                    for (String[] row : dateRows) {
                        int hour = Integer.parseInt(row[timeColumnIndex]);
                        if (hour == currTime) {
                            // Create TimedIoT and add to temporalOrdering
                            int N = Integer.parseInt(row[VehColumnIndex]);
                            double x = Double.parseDouble(row[eastColumnIndex]);
                            double y = Double.parseDouble(row[northColumnIndex]);
                            String lane = row[laneColumnIndex];
                            temporalOrdering.add(currTime);
                            var ls = new ArrayList<TimedIoT>();
                            timedIoTDevices.put(currTime, ls);
                            // generate ID for vehicles
                            for (int counter = 1; counter <= N;) {
                                TimedIoT rec = new TimedIoT();
                                rec.id = "id_" + counter;
                                //rec.numberOfVeh = N; // need to check
                                rec.x = x;
                                rec.y = y;
                                rec.lane = lane;
                                rec.simtime = currTime;
                                ls.add(rec);
                                counter++;
                            }
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        // If currTime doesn't match any row, continue to the next currTime
                        continue;
                    }
                }
            }

              // 1. Extract all ID values
            List<String> allIds = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                allIds.add(rows.get(i)[idColumnIndex]);
            }
            // 2. Filter out duplicates
            Set<String> traffic_lights = new HashSet<>(allIds);
            // 3. Loop over the unique ID values
            for (String id : traffic_lights) {
                for (int i = 0; i < rows.size(); i++) {
                    if (rows.get(i)[idColumnIndex].equals(id)) {
                       var curr = rows.get(i);
                       var rsu = new TimedEdge(
                               String.valueOf(curr[idColumnIndex]),
                                Double.parseDouble(curr[eastColumnIndex]),
                                Double.parseDouble(curr[northColumnIndex]),
                                concreteConf.default_rsu_communication_radius,
                                concreteConf.default_max_vehicle_communication, 0);
                        rsuUpdater.accept(rsu);
                        roadSideUnits.add(rsu);
                        break;
                    }
                }
            }
                    connectionPath.clear();
                var tmp = netGen.apply(roadSideUnits);
            }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + file);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file: " + file);
            e.printStackTrace();
        } catch (CsvException e) {
            System.out.println("Error parsing CSV file: " + file);
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected List<Double> getSimulationTimeUnits() {
        return temporalOrdering;
    }

    @Override
    protected Collection<TimedIoT> getTimedIoT(Double tick) {
        return timedIoTDevices.get(tick);
    }

    @Override
    protected StraightforwardAdjacencyList<String> getTimedEdgeNetwork(Double tick) {
        return connectionPath;
    }

    @Override
    protected HashSet<TimedEdge> getTimedEdgeNodes(Double tick) {
        return roadSideUnits.stream().map(x -> {
            var ls = x.copy();
            ls.setSimtime(tick);
            return ls;
        }).collect(Collectors.toCollection(HashSet<TimedEdge>::new));
    }

    @Override
    protected void endReadSimulatorOutput() {
        data.clear();
        timedIoTs.clear();
        timedEdges.clear();
        temporalOrdering.clear();
        timedIoTDevices.clear();
        networkFile = null;
        connectionPath.clear();
    }

    @Override
    public boolean runSimulator(TrafficConfiguration conf) {
        File file = new File(concreteConf.DfT_file_path);
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            List<String[]> rows = reader.readAll();
            String[] headers = rows.get(0);
            int dateColumnIndex = Arrays.asList(headers).indexOf("Count_date");
            int hourColumnIndex = Arrays.asList(headers).indexOf("hour");
            // convert the string to date and time
            // DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
               DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime earliestDateTime = LocalDateTime.MAX;
            LocalDateTime latestDateTime = LocalDateTime.MIN;
            // find the earliest and latest date
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                String dateString = row[dateColumnIndex];
                String hourString = row[hourColumnIndex];
                //  String dateTimeString = dateString + "  " + hourString;
               //  System.out.println("dateString" + dateString);
                LocalDateTime dateTime = LocalDateTime.parse(dateString, dateFormatter);
                // dateTime = LocalDate.parse(dateString, dateFormatter).atStartOfDay();
                int hour = Integer.parseInt(hourString);
                dateTime = dateTime.withHour(hour); // add the time in "hour" to the date
                if (dateTime.isBefore(earliestDateTime)) {
                    earliestDateTime = dateTime;
                }
                if (dateTime.isAfter(latestDateTime)) {
                    latestDateTime = dateTime;
                }
            }

            // Convert the earliest and latest date to seconds
            long earliestTime = earliestDateTime.toEpochSecond(ZoneOffset.UTC);
            long latestTime = latestDateTime.toEpochSecond(ZoneOffset.UTC);

            conf.begin = 0;
            conf.end = latestTime - earliestTime;
            conf.step = 3600;

            // calculate the event's new timestamp (the start time for the current row)
            List<String[]> filteredRows = new ArrayList<>();
            for (String[] row : rows) {
                if(row[dateColumnIndex].equals("Count_date")) {
                    continue;
                }
                LocalDateTime dateTime = LocalDateTime.parse(row[dateColumnIndex], dateFormatter);
                int hour = Integer.parseInt(row[hourColumnIndex]);
                dateTime = dateTime.withHour(hour);
                long timeInSeconds = dateTime.toEpochSecond(ZoneOffset.UTC) - earliestTime;
                if (timeInSeconds >= conf.begin && timeInSeconds <= conf.end) {
                    row[dateColumnIndex] = String.valueOf(timeInSeconds);
                    filteredRows.add(row);
                }
            }
            rows = filteredRows;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file: " + file);
            e.printStackTrace();
        } catch (CsvException e) {
            System.out.println("Error parsing CSV file: " + file);
            e.printStackTrace();
        }
        return true;
    }
}