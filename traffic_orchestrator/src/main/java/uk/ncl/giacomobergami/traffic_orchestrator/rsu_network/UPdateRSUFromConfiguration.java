package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network;

import uk.ncl.giacomobergami.traffic_orchestrator.OrchestratorConfigurator;
import uk.ncl.giacomobergami.utils.shared_data.RSU;

public class UPdateRSUFromConfiguration extends RSUUpdater {
    public UPdateRSUFromConfiguration(OrchestratorConfigurator conf) {
        super(conf);
    }
    @Override
    public void accept(RSU rsu) {
        if (conf.reset_rsu_communication_radius > 0) {
            rsu.communication_radius = conf.reset_rsu_communication_radius;
        }
        if (conf.reset_max_vehicle_communication > 0) {
            rsu.max_vehicle_communication = conf.reset_max_vehicle_communication;
        }
    }
}
