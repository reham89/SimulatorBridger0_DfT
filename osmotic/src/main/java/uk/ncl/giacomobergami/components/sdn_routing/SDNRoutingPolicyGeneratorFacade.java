package uk.ncl.giacomobergami.components.sdn_routing;

import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

public class SDNRoutingPolicyGeneratorFacade {
    public static SDNRoutingPolicy generateFacade(String clazzPath) {
        return ReflectiveFactoryMethod
                .getInstance(SDNRoutingPolicy.class)
                .generateFacade(clazzPath, SDNRoutingLoadBalancing::new);
    }
}


