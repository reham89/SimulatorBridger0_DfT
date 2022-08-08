package uk.ncl.giacomobergami.components.allocation_policy;

import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

import java.util.function.Supplier;

public class VmAllocationPolicyGeneratorFactory {
    public static VmAllocationPolicy generateFacade(String clazz) {
        return ReflectiveFactoryMethod
                .getInstance(VmAllocationPolicy.class)
                .generateFacade(clazz, (Supplier<VmAllocationPolicy>) VmAllocationPolicyCombinedLeastFullFirst::new);
    }
}
