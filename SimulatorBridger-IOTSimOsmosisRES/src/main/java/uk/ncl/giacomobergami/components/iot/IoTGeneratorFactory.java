package uk.ncl.giacomobergami.components.iot;

import org.cloudbus.cloudsim.edge.core.edge.LegacyConfiguration;
import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class IoTGeneratorFactory {
    public static IoTDevice generateFacade(LegacyConfiguration.IotDeviceEntity onta, AtomicInteger flowid) {
        return ReflectiveFactoryMethod
                .getInstance(IoTDevice.class)
                .generateFacade(onta.getIoTClassName(), (Supplier<IoTDevice>) () -> new CarSensor( onta, flowid), onta, flowid);
    }
}
