package uk.ncl.giacomobergami.components.mel_routing;

import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

public class MELRoutingPolicyGeneratorFacade {
    public static MELRoutingPolicy generateFacade(String clazzPath) {
        return ReflectiveFactoryMethod
                .getInstance(MELRoutingPolicy.class)
                .generateFacade(clazzPath, RoundRobinMELRoutingPolicy::new);
    }
}
