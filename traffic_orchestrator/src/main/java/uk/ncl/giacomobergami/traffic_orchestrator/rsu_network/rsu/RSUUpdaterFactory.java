package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.rsu;

import uk.ncl.giacomobergami.utils.pipeline_confs.OrchestratorConfiguration;
import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

public class RSUUpdaterFactory {
    public static RSUUpdater generateFacade(String clazzPath,
                                            Double default_comm_radius,
                                            Integer default_max_vehicle_communication) {
        return ReflectiveFactoryMethod
                .getInstance(RSUUpdater.class)
                .generateFacade(
                        clazzPath,
                        () -> new UpdateRSUFromConfiguration(default_comm_radius, default_max_vehicle_communication),
                        default_comm_radius,
                        default_max_vehicle_communication);
    }
}
