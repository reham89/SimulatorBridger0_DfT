package uk.ncl.giacomobergami.components.cloudlet_scheduler;

import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

import java.util.function.Supplier;

public class CloudletSchedulerGeneratorFactory {
    public static CloudletScheduler generateFacade(String className) {
        return ReflectiveFactoryMethod
                .getInstance(CloudletScheduler.class)
                .generateFacade(className, (Supplier<CloudletScheduler>) CloudletSchedulerTimeShared::new);
    }
}
