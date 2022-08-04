package uk.ncl.giacomobergami.components.sdn_traffic;

import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

public class SDNTrafficPolicyGeneratorFacade {
    public static SDNTrafficSchedulingPolicy generateFacade(String clazzPath) {
        return ReflectiveFactoryMethod
                .getInstance(SDNTrafficSchedulingPolicy.class)
                .generateFacade(clazzPath, SDNTrafficPolicyFairShare::new);
    }
}
