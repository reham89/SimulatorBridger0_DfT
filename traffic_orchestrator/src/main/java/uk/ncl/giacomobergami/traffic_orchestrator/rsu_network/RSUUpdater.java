package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network;

import uk.ncl.giacomobergami.traffic_orchestrator.OrchestratorConfigurator;
import uk.ncl.giacomobergami.utils.shared_data.RSU;

import java.util.function.Consumer;

public abstract class RSUUpdater implements Consumer<RSU> {
    OrchestratorConfigurator conf;
    public RSUUpdater(OrchestratorConfigurator conf) {
        this.conf = conf;
    }
}
