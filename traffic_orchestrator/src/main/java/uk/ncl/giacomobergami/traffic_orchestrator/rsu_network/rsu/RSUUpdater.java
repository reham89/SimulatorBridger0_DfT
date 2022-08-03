package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.rsu;

import uk.ncl.giacomobergami.utils.pipeline_confs.OrchestratorConfiguration;
import uk.ncl.giacomobergami.utils.shared_data.RSU;

import java.util.function.Consumer;

public abstract class RSUUpdater implements Consumer<RSU> {
    protected final Double default_comm_radius;
    protected final Integer default_max_vehicle_communication;
    public RSUUpdater(Double default_comm_radius, Integer default_max_vehicle_communication) {
        this.default_comm_radius = default_comm_radius;
        this.default_max_vehicle_communication = default_max_vehicle_communication;
    }
}
