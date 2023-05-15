package uk.ncl.giacomobergami.SumoOsmosisBridger.traffic_converter;

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
import uk.ncl.giacomobergami.utils.structures.StraightforwardAdjacencyList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.*;
import com.opencsv.*;
import org.ejml.simple.SimpleMatrix;
import java.io.FileReader;
import java.io.IOException;
import uk.me.jstott.jcoord.OSRef;
import uk.me.jstott.jcoord.LatLng;
import org.apache.commons.csv.*;



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
    private static Logger logger = LogManager.getRootLogger();
    List<String[]> data = new ArrayList<>();
    List<TimedIoT> timedIoTs = new ArrayList<>();
    List<TimedEdge> timedEdges = new ArrayList<>();
    Projection utmProjection = ProjectionFactory.fromPROJ4Specification(ProjectionConstants.UTM_ZONE_30_PROJ4_SPEC);
    CoordinateReferenceSystem utmCRS = new CRSFactory().createFromName("EPSG:32630");
    CoordinateReferenceSystem latLonCRS = new CRSFactory().createFromName("EPSG:4326");
    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    UTMRef utmRef = latLng.toUTMRef();
    String csvFilePath;
    String row;
    List<String> rows = new ArrayList<>();
    List<Double> temporalOrdering; // the time in CSV file is in integer format

    public DfTConverter(TrafficConfiguration conf, String csvFilePath) {
        super(conf);
        this.csvFilePath = csvFilePath;
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
        List<Double> temporalOrdering = new ArrayList<>();
        connectionPath.clear();
        temporalOrdering.clear();
        timedIoTDevices.clear();
        networkFile = null;
        File file = new File(concreteConf.DfT_file_path);
        Document DfTFile = null;
        try {
            DfTFile = db.parse(csvFilePath);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }
        String csvFilePath = concreteConf.getDfT_file();
        CSVParser parser = new CSVParser(new FileReader(csvFilePath), CSVFormat.DEFAULT.withHeader());
         List<String[]> rows = reader.readAll();  //reads all the rows of the CSV file and stores them in a list called rows.
        // String[] header = reader.readNext();  // Skip the header row
            int timeColumnIndex = Arrays.asList(rows.get(0)).indexOf("hour"); //retrieves the index of the column with the header "hour" by getting the first row in the CSV file, converting it to a list using Arrays.asList()
            int VehColumnIndex = Arrays.asList(rows.get(0)).indexOf("All_motor_vehicles");
            //  we put both IoT and Edge in 1 loop?
            for (int i = 1; i < rows.size(); i++) { // assuming that the first row contains the column headers
                String[] row = rows.get(i); // gets the current row
                String timeValue = row[timeColumnIndex]; //  retrieves the value in the "hour" column for the current row
                double currTime = Double.parseDouble(timeValue); // convert it to double
                temporalOrdering.add(currTime);
                var ls = new ArrayList<TimedIoT>();
                timedIoTDevices.put(currTime, ls);
                TimedIoT rec = new TimedIoT();
                int numberOfVeh = Integer.parseInt(row[VehColumnIndex]);
                rec.numberOfVeh = numberOfVeh;
                ls.add(rec);
            }
        NodeList traffic_lights = null;
        try {
            traffic_lights = XPathUtil.evaluateNodeList(networkFile, "/net/junction[@type='traffic_light']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
        for (int i = 1; i < rows.size(); i++) {
                String regionName = row[headers.indexOf("RegionName")];
                String localAuthorityName = row[headers.indexOf("LocalAuthorityName")];
                String roadName = row[headers.indexOf("RoadName")];
                String startJunctionRoadName = row[headers.indexOf("StartJunctionRoadName")];
                String endJunctionRoadName = row[headers.indexOf("EndJunctionRoadName")];
                double latitude = row[headers.indexOf("Latitude")];
                double longitude = row[headers.indexOf("Longitude")];
                // convert latitude & longitude to UTM:
                LatLng latLng = new LatLng(latitude, longitude);
                OSRef osRef = latLng.toOSRef();
                UTMRef utm = latLng.toUTMRef();
                double X = utm.getEasting();
                double Y = utm.getNorthing();

                var rsu = new TimedEdge();
                rsu.regionName = regionName;
                rsu.localAuthorityName = localAuthorityName;
                rsu.roadName = roadName;
                rsu.startJunctionRoadName = startJunctionRoadName;
                rsu.endJunctionRoadName = endJunctionRoadName;
                rsu.X = X;
                rsu.Y = Y;
                roadSideUnits.add(rsu);
        }
        connectionPath.clear();
        var tmp = netGen.apply(roadSideUnits);
        tmp.forEach((k, v) -> {
            connectionPath.put(k.id, v.id);
        });
        return true;
        }

    /*
    private List<String[]> readCSV(File file) {
            List<String[]> records = new ArrayList<>();
            try (Scanner scanner = new Scanner(file);) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] values = line.split(",");
                    records.add(values);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return records;
        }
*/
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
        // Clear the input data list
        data.clear();
        // Clear the TimedIoT and TimedEdge lists
        timedIoTs.clear();
        timedEdges.clear();
        temporalOrdering.clear();
        timedIoTDevices.clear();
        networkFile = null;
        connectionPath.clear();
    }

    @Override
    public boolean runSimulator(long begin, long end, long step) {
        return false;
    }

}
