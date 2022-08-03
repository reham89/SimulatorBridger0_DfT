package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.rsu;

import uk.ncl.giacomobergami.utils.pipeline_confs.OrchestratorConfiguration;
import uk.ncl.giacomobergami.utils.shared_data.RSU;

public class UpdateRSUFromConfiguration extends RSUUpdater {
    public UpdateRSUFromConfiguration(Double default_comm_radius, Integer default_max_vehicle_communication) {
        super(default_comm_radius, default_max_vehicle_communication);
    }
    @Override
    public void accept(RSU rsu) {
        if (default_comm_radius > 0) {
            rsu.communication_radius = default_comm_radius;
        }
        if (default_max_vehicle_communication> 0) {
            rsu.max_vehicle_communication = default_max_vehicle_communication;
        }
    }
}
