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
        try {
            DfTFile = db.parse(file);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            List<String[]> rows = reader.readAll();
            int timeColumnIndex = Arrays.asList(rows.get(0)).indexOf("hour");
            int VehColumnIndex = Arrays.asList(rows.get(0)).indexOf("All_motor_vehicles");
            int eastColumnIndex = Arrays.asList(rows.get(0)).indexOf("Easting");
            int northColumnIndex = Arrays.asList(rows.get(0)).indexOf("Northing");

            int counter = 0;
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                // double timeValue = Double.parseDouble(row[timeColumnIndex]);
                double timeValue = 3600;
                int N = Integer.parseInt(row[VehColumnIndex]);
                double x = Double.parseDouble(row[eastColumnIndex]);
                double y = Double.parseDouble(row[northColumnIndex]);
                double currTime = Double.parseDouble(String.valueOf(timeValue)); //currTime is 3600 or "hour" value?
                temporalOrdering.add(currTime);
                var ls = new ArrayList<TimedIoT>();
                timedIoTDevices.put(currTime, ls);
                // generate ID for vehicles
                TimedIoT rec = new TimedIoT();
                for (int j = 0; j < N; j++) {
                    rec.id = "id_" + counter;
                    counter++; // need to close the loop here?
                    rec.numberOfVeh = N; // need to check
                    rec.x = x;
                    rec.y = y;
                    ls.add(rec);
                }
            }

            List<String> traffic_lights = new ArrayList<>();
            int idColumnIndex = Arrays.asList(rows.get(0)).indexOf("Count_point_id");
            traffic_lights = new ArrayList<>();
            // String[] headers = rows.get(0); // This assumes the first row contains headers int eastColumnIndex = Arrays.asList(rows.get(0)).indexOf("Easting");
            for (int i = 1; i < rows.size(); i++) {
                String[] curr = rows.get(i);
                var rsu = new TimedEdge(curr[idColumnIndex],
                        Double.parseDouble(curr[eastColumnIndex]),
                        Double.parseDouble(curr[northColumnIndex]),
                        concreteConf.default_rsu_communication_radius,
                        concreteConf.default_max_vehicle_communication, 0);
                rsuUpdater.accept(rsu);
                roadSideUnits.add(rsu);
            }
            connectionPath.clear();
            var tmp = netGen.apply(roadSideUnits);
            tmp.forEach((k, v) -> {
                connectionPath.put(k.id, v.id);
            });
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
        Document DfTFile = null;
        try {
            DfTFile = db.parse(file);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            List<String[]> rows = reader.readAll();
            String[] headers = rows.get(0);
            int dateColumnIndex = Arrays.asList(headers).indexOf("Count_date");
            int hourColumnIndex = Arrays.asList(headers).indexOf("hour");
            // convert the string to date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm");
            LocalDateTime earliestDateTime = LocalDateTime.MAX;
            LocalDateTime latestDateTime = LocalDateTime.MIN;

            // find the earliest and latest date
            for (String[] row : rows) {
                LocalDateTime dateTime = LocalDateTime.parse(row[dateColumnIndex], formatter);
                int hour = Integer.parseInt(row[hourColumnIndex]);
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
                LocalDateTime dateTime = LocalDateTime.parse(row[dateColumnIndex], formatter);
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
