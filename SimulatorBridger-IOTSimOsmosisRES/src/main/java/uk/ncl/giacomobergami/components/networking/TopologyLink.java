package uk.ncl.giacomobergami.components.networking;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.cloudbus.cloudsim.edge.core.edge.LegacyConfiguration;
import uk.ncl.giacomobergami.utils.data.CSVMediator;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@JsonPropertyOrder({"network", "source", "destination", "bandwidth"})
public class TopologyLink {
    public String network;
    public String source;
    public String destination;
    public long bandwidth;

    public TopologyLink() {}

    public TopologyLink(String network, LegacyConfiguration.LinkEntity onta) {
        this.network = network;
        this.source = onta.getSource();
        this.destination = onta.getDestination();
        this.bandwidth = onta.getBw();
    }

    public String leftProjection() {
        return network;
    }

    private static CSVMediator<TopologyLink> readerWriter = null;
    public static CSVMediator<TopologyLink> csvReader() {
        if (readerWriter == null)
            readerWriter = new CSVMediator<>(TopologyLink.class);
        return readerWriter;
    }

    public LegacyConfiguration.LinkEntity rightProjection() {
        var a = new LegacyConfiguration.LinkEntity();
        a.setBw(bandwidth);
        a.setSource(source);
        a.setDestination(destination);
        return a;
    }

    public static Map<String, Collection<LegacyConfiguration.LinkEntity>> asNetworkedLinks(File csv) {
        Map<String, Collection<LegacyConfiguration.LinkEntity>> result = new HashMap<>();
        var reader = csvReader().beginCSVRead(csv);
        while (reader.hasNext()) {
            var x = reader.next();
            result.computeIfAbsent(x.leftProjection(), s -> new HashSet<>()).add(x.rightProjection());
        }
        return result;
    }

    public static Map<String, Collection<LegacyConfiguration.LinkEntity>> asNetworkedLinks(Collection<TopologyLink> links) {
        Map<String, Collection<LegacyConfiguration.LinkEntity>> result = new HashMap<>();
        if (links != null) for (var x : links) {
            result.computeIfAbsent(x.leftProjection(), s -> new HashSet<>()).add(x.rightProjection());
        }
        return result;
    }

    public String getNetwork() {
        return network;
    }
    public void setNetwork(String network) {
        this.network = network;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public long getBandwidth() {
        return bandwidth;
    }
    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }
}
