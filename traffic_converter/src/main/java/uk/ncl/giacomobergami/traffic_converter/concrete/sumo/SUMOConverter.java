package uk.ncl.giacomobergami.traffic_converter.concrete.sumo;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ncl.giacomobergami.traffic_converter.abstracted.TrafficConverter;
import uk.ncl.giacomobergami.utils.data.GZip;
import uk.ncl.giacomobergami.utils.data.XPathUtil;
import uk.ncl.giacomobergami.utils.data.YAML;
import uk.ncl.giacomobergami.utils.shared_data.RSU;
import uk.ncl.giacomobergami.utils.shared_data.TimedVehicle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SUMOConverter extends TrafficConverter {
    private SUMOConfiguration conf;
    private final DocumentBuilderFactory dbf;
    private DocumentBuilder db;

    public SUMOConverter(String YAMLConverterConfiguration,
                         String RSUCsvFile,
                         String VehicleCSVFile)  {
        super(RSUCsvFile, VehicleCSVFile);
        dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            db = null;
        }
        conf = YAML.parse(SUMOConfiguration.class, new File(YAMLConverterConfiguration)).orElseThrow();
    }

    @Override
    public boolean runSimulator(long begin,
                             long end,
                             long step) {
        if (new File(conf.trace_file).exists()) {
            System.out.println("Skipping the sumo running: the trace_file already exists");
            return true;
        }
        File fout = new File(conf.logger_file);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(conf.sumo_program, "-c", conf.sumo_configuration_file_path, "--begin", Long.toString(begin), "--end", Long.toString(end), "--step-length", Long.toString(step), "--fcd-output", conf.trace_file);
        try {
            Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            int exitCode = process.waitFor();
            bw.write("\nExited with error code : ");
            bw.write(exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean dumpOrchestratorConfiguration() {
//        HashMap<Double, Long> problemSolvingTime = new HashMap<>();
//        HashMap<Double, ArrayList<LocalTimeOptimizationProblem.Solution>> simulationSolutions = new HashMap<>();
        List<Double> temporalOrdering = new ArrayList<>();
//        HashSet<Vehicle> intersectingVehicles = new HashSet<>();
//        ArrayList<CSVOsmosisAppFromTags.TransactionRecord> xyz = new ArrayList<>();
        File file = new File(conf.sumo_configuration_file_path);
//        File folderOut = new File(conf.OsmosisConfFiles);
//        File folderOut2 = new File(conf.OsmosisOutput);
//        if (! folderOut2.exists()) {
//            folderOut2.mkdirs();
//        } else if (folderOut2.isFile()) {
//            System.err.println("ERROR: the current file exists, and it is a file: a folder was expected. " + folderOut2);
//            System.exit(1);
//        }
//        if (! folderOut.exists()){
//            folderOut.mkdirs();
//        } else if (folderOut.isFile())  {
//            System.err.println("ERROR: the current file exists, and it is a file: a folder was expected. " + folderOut);
//            System.exit(1);
//        }
        Document configurationFile = null;
        try {
            configurationFile = db.parse(file);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }
//        double distanceSquared = conf.maximum_tl_distance_in_meters * conf.maximum_tl_distance_in_meters;

        File network_python = null;
        try {
            network_python = Paths.get(file.getParent(), XPathUtil.evaluate(configurationFile, "/configuration/input/net-file/@value"))
                    .toFile();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
        if (!network_python.exists()) {
            System.err.println("ERR: file " + network_python.getAbsolutePath() + " from " + file.getAbsolutePath() + " does not exists!");
            System.exit(1);
        } else if (network_python.getAbsolutePath().endsWith(".gz")) {
            String ap = network_python.getAbsolutePath();
            ap = ap.substring(0, ap.lastIndexOf('.'));
            try {
                GZip.decompressGzip(network_python.toPath(), new File(ap).toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            network_python = new File(ap);
        }
        System.out.println("Loading the traffic light information...");
        Document networkFile = null;
        try {
            networkFile = db.parse(network_python);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }
        ArrayList<RSU> tls = new ArrayList<>();
//        HashMap<String, Integer> tlsMap = new HashMap<>();
        NodeList traffic_lights = null;
        try {
            traffic_lights = XPathUtil.evaluateNodeList(networkFile, "/net/junction[@type='traffic_light']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0, N = traffic_lights.getLength(); i<N; i++) {
            var curr = traffic_lights.item(i).getAttributes();
            writeRSUCsv(new RSU(curr.getNamedItem("id").getTextContent(),
                    Double.parseDouble(curr.getNamedItem("x").getTextContent()),
                    Double.parseDouble(curr.getNamedItem("y").getTextContent()),
                    0,
                    0));
        }
        writeRSUCsvEnd();



//        DoubleMatrix sqDistanceMatrix = DoubleMatrix.zeros(traffic_lights.getLength(),traffic_lights.getLength());
//        for (int i = 0, N = traffic_lights.getLength(); i<N; i++) {
//            var semX = tls.get(i);
//            for (int j = 0; j<i; j++) {
//                var semY = tls.get(j);
//                final double deltaX = semX.tl_x - semY.tl_x;
//                final double deltaY = semX.tl_y - semY.tl_y;
//                sqDistanceMatrix.put(i, j, ((deltaX * deltaX) + (deltaY * deltaY)));
//                sqDistanceMatrix.put(j, i, ((deltaX * deltaX) + (deltaY * deltaY)));
//            }
//        }

//        CartesianDistanceFunction f = new CartesianDistanceFunction();
        File trajectory_python = new File(conf.trace_file);
        if (!trajectory_python.exists()) {
            System.err.println("ERROR: sumo has not built the trace file: " + trajectory_python.getAbsolutePath());
            return false;
        }
        System.out.println("Loading the vehicle information...");
        Document trace_document = null;
        try {
            trace_document = db.parse(trajectory_python);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }

        NodeList timestamp_eval;
//        ArrayList<CSVOsmosisRecord> csvFile = new ArrayList<>();
        try {
            timestamp_eval = XPathUtil.evaluateNodeList(trace_document, "/fcd-export/timestep");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0, N = timestamp_eval.getLength(); i<N; i++) {
            var curr = timestamp_eval.item(i);
            Double currTime = Double.valueOf(curr.getAttributes().getNamedItem("time").getTextContent());
            var tag = timestamp_eval.item(i).getChildNodes();
            for (int j = 0, M = tag.getLength(); j < M; j++) {
                var veh = tag.item(j);
                if (veh.getNodeType() == Node.ELEMENT_NODE) {
                    assert (Objects.equals(veh.getNodeName(), "vehicle"));
                    var attrs = veh.getAttributes();
                    TimedVehicle rec = new TimedVehicle();
                    rec.angle = Double.parseDouble(attrs.getNamedItem("angle").getTextContent());
                    rec.x = Double.parseDouble(attrs.getNamedItem("x").getTextContent());
                    rec.y = Double.parseDouble(attrs.getNamedItem("y").getTextContent());
                    rec.speed = Double.parseDouble(attrs.getNamedItem("speed").getTextContent());
                    rec.pos = Double.parseDouble(attrs.getNamedItem("pos").getTextContent());
                    rec.slope = Double.parseDouble(attrs.getNamedItem("slope").getTextContent());
                    rec.id = (attrs.getNamedItem("id").getTextContent());
                    rec.type = (attrs.getNamedItem("type").getTextContent());
                    rec.lane = (attrs.getNamedItem("lane").getTextContent());
                    rec.simtime = currTime;
                    writeVehicleCsv(rec);
                }
            }
        }
        writeVehicleCsvEnd();

        return true;
    }
}
