package uk.ncl.giacomobergami.components.mel_routing;

import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

public class MELRoutingPolicyGeneratorFacade {
    public static MELSwitchPolicy generateFacade(String clazzPath) {
        return ReflectiveFactoryMethod
                .getInstance(MELSwitchPolicy.class)
                .generateFacade(clazzPath, RoundRobinMELSwitchPolicy::new);
    }
}
