package uk.ncl.giacomobergami.SumoOsmosisBridger.traffic_converter;

import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import uk.ncl.giacomobergami.utils.structures.ImmutablePair;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.opencsv.*;

public class DfTConverter extends TrafficConverter {

    private final SUMOConfiguration concreteConf;
    private long earliestTime;
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
    private static Logger logger = LogManager.getRootLogger();

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

        logger.trace("Loading the traffic light information...");

        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            List<String[]> rows = reader.readAll();
            //determining the indices of columns
            int timeColumnIndex = Arrays.asList(rows.get(0)).indexOf("hour");
            int VehColumnIndex = Arrays.asList(rows.get(0)).indexOf("All_motor_vehicles");
            int eastColumnIndex = Arrays.asList(rows.get(0)).indexOf("Easting");
            int northColumnIndex = Arrays.asList(rows.get(0)).indexOf("Northing");
            int laneColumnIndex = Arrays.asList(rows.get(0)).indexOf("Direction_of_travel");
            int dateColumnIndex = Arrays.asList(rows.get(0)).indexOf("Count_date");
            int idColumnIndex = Arrays.asList(rows.get(0)).indexOf("Count_point_id");
            int hourColumnIndex = Arrays.asList(rows.get(0)).indexOf("hour");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            Function<String[], ImmutablePair<LocalDateTime, Integer>> f = o1 -> {
                String dateString = o1[dateColumnIndex];
                String hourString = o1[hourColumnIndex];
                LocalDateTime dateTime = LocalDateTime.parse(dateString, dateFormatter);
                // dateTime = LocalDate.parse(dateString, dateFormatter).atStartOfDay();
                int hour = Integer.parseInt(hourString);
                dateTime = dateTime.withHour(hour); // add the time in "hour" to the date
                var id = o1[idColumnIndex];
                return new ImmutablePair<>(dateTime, Integer.parseInt(id));
            };
            var body = rows.subList(1, rows.size());
            body.sort(Comparator.comparing(f::apply));

            for (int i = 0; i < body.size(); i++) {
                String[] row = body.get(i);
                //   String curr = String.valueOf(row[dateColumnIndex]);
                //  double currTime = Double.parseDouble(row[timeColumnIndex]); //
                //double currTime = 1; // bec each row has 1 hour which is 3600 sec
                double x = Double.parseDouble(row[eastColumnIndex]);
                double y = Double.parseDouble(row[northColumnIndex]);

                String lane = row[laneColumnIndex];
                String dateString = row[dateColumnIndex];
                String hourString = row[hourColumnIndex];
                //  String dateTimeString = dateString + "  " + hourString;
                //  System.out.println("dateString" + dateString);
                LocalDateTime dateTime = LocalDateTime.parse(dateString, dateFormatter);
                // dateTime = LocalDate.parse(dateString, dateFormatter).atStartOfDay();
                int hour = Integer.parseInt(hourString);
                dateTime = dateTime.withHour(hour); // add the time in "hour" to the date
                double currTime =(dateTime.toEpochSecond(ZoneOffset.UTC) -earliestTime);

                temporalOrdering.add(currTime);
                var ls = new ArrayList<TimedIoT>();
                timedIoTDevices.put(currTime, ls);
                int N = Integer.parseInt(row[VehColumnIndex]);
                // generate ID for vehicles
                for (int counter = 0; counter < N;) {
                    TimedIoT rec = new TimedIoT();
                    rec.id = "id_" + counter;
                    //rec.numberOfVeh = N; // need to check
                    rec.x = x;
                    rec.y = y;
                    rec.lane = lane;
                    rec.simtime = currTime; //need to solve it!! let it i?
                    ls.add(rec);
                    counter++;
                }
            }
              // 1. Extract all ID values
            Set<String> uniqueIds = new HashSet<>();
            for (int i = 1; i < body.size(); i++) {
                uniqueIds.add(body.get(i)[idColumnIndex]);
            }
            List<String> allIds = new ArrayList<>();
            for (int i = 1; i < body.size(); i++) {
                allIds.add(body.get(i)[idColumnIndex]);
            }
            // 2. Filter out duplicates
            Set<String> traffic_lights = new HashSet<>(allIds);
            // 3. Loop over the unique ID values
            for (String id : traffic_lights) {
                for (int i = 0; i < body.size(); i++) {
                    if (body.get(i)[idColumnIndex].equals(id)) {
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
                var tmp = netGen.apply(roadSideUnits);
            tmp.forEach((k, v) -> {
                connectionPath.put(k.id, v.id);
            });
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
        return new ArrayList<>(new TreeSet<>(temporalOrdering));
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
            List<String[]> allRows = reader.readAll();
            String[] headers = allRows.get(0);
            int dateColumnIndex = Arrays.asList(headers).indexOf("Count_date");
            int hourColumnIndex = Arrays.asList(headers).indexOf("hour");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Initialize earliest and latest DateTime to extreme values
            LocalDateTime earliestDateTime = LocalDateTime.MAX;
            LocalDateTime latestDateTime = LocalDateTime.MIN;

            for (String[] row : allRows.subList(1, allRows.size())) {
                String dateString = row[dateColumnIndex];
                int hour = Integer.parseInt(row[hourColumnIndex]);
                LocalDateTime dateTime = LocalDate.parse(dateString, dateFormatter).atStartOfDay().withHour(hour);

                if (dateTime.isBefore(earliestDateTime)) {
                    earliestDateTime = dateTime;
                }
                if (dateTime.isAfter(latestDateTime)) {
                    latestDateTime = dateTime;
                }
            }

            earliestTime = earliestDateTime.toEpochSecond(ZoneOffset.UTC);
            long latestTime = latestDateTime.toEpochSecond(ZoneOffset.UTC);

            // Adjust configuration based on the calculated times
            conf.begin = 0;
            conf.end = latestTime - earliestTime;
            conf.step = 3600; // Assuming each step is 1 second


        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file);
            e.printStackTrace();
            return false;
        } catch (IOException | CsvException e) {
            System.out.println("Error reading file: " + file);
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
