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
import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;
import com.opencsv.*;


public class DfTConverter extends TrafficConverter {

    private SUMOConfiguration concreteConf;
    private final DocumentBuilderFactory dbf;
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
    List<Double> temporalOrdering; // the time in CSV file is in integer format

    public DfTConverter(TrafficConfiguration conf) {
        super(conf);
        dbf = DocumentBuilderFactory.newInstance();
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
                // need to check how to calculate currTime and timeValue
                double currTime = Double.parseDouble(String.valueOf(timeValue));
                temporalOrdering.add(currTime);

                var ls = new ArrayList<TimedIoT>();
                timedIoTDevices.put(currTime, ls);

                for (int j = 0; j < N; j++) {
                    TimedIoT rec = new TimedIoT();
                    rec.id = "id_" + counter;
                    counter++;
                    rec.numberOfVeh = N;
                    rec.x = x;
                    rec.y = y;
                    ls.add(rec);
                }
            }
        NodeList traffic_lights = null;
        try {
            traffic_lights = XPathUtil.evaluateNodeList(networkFile, "/net/junction[@type='traffic_light']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
            String[] headers = rows.get(0); // This assumes the first row contains headers
            for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
                String regionName = row[Arrays.asList(headers).indexOf("RegionName")];
                String localAuthorityName = row[Arrays.asList(headers).indexOf("LocalAuthorityName")];
                String roadName = row[Arrays.asList(headers).indexOf("RoadName")];
                String startJunctionRoadName = row[Arrays.asList(headers).indexOf("StartJunctionRoadName")];
                String endJunctionRoadName = row[Arrays.asList(headers).indexOf("EndJunctionRoadName")];
                double x = Double.parseDouble(row[Arrays.asList(headers).indexOf("Easting")]);
                double y = Double.parseDouble(row[Arrays.asList(headers).indexOf("Northing")]);

                var rsu = new TimedEdge();
                rsu.regionName = regionName;
                rsu.localAuthorityName = localAuthorityName;
                rsu.roadName = roadName;
                rsu.startJunctionRoadName = startJunctionRoadName;
                rsu.endJunctionRoadName = endJunctionRoadName;
                // the position of the edge device is similar to position of IoT device ?
                rsu.x = x;
                rsu.y = y;
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
     public boolean runSimulator(long begin, long end, long step) {
        // no need to filter! already filtered by rows? timevalue=3600, how to set the end?
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
            List<String[]> filteredRows = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                begin = 0;
                end = 3600;
                step = 1;
                long timeValue = 3600;
                // filter the events within the timestamp range of one hour (3600 seconds)
                if (timeValue >= begin && timeValue < end) {
                    filteredRows.add(row);
                }
            }
            // Replace the original rows with the filtered rows
            rows = (List<String[]>) filteredRows;

    /*    for (int i = 1; i < rows.size(); i++) {
            String[] row = new String[]{rows.get(i)};
            long timeValue = Long.parseLong(row[timeColumnIndex]);

            // Convert hourValue to (hours:minutes:seconds) format
             begin = Long.parseLong(String.format("%02d:00:00", timeValue));
             step = 1;
             end = Long.parseLong(String.format("%02d:59:59", timeValue));

            // filter the events within the begin and end
            if (timeValue >= begin && timeValue <= end) {
                filteredRows.add(row);
            }

        } */

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
