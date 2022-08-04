package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.rsu;

import uk.ncl.giacomobergami.utils.shared_data.edge.TimedEdge;

public class UpdateRSUFromConfiguration extends RSUUpdater {
    public UpdateRSUFromConfiguration(Double default_comm_radius, Integer default_max_vehicle_communication) {
        super(default_comm_radius, default_max_vehicle_communication);
    }
    @Override
    public void accept(TimedEdge timedEdge) {
        if (default_comm_radius > 0) {
            timedEdge.communication_radius = default_comm_radius;
        }
        if (default_max_vehicle_communication> 0) {
            timedEdge.max_vehicle_communication = default_max_vehicle_communication;
        }
    }
}
