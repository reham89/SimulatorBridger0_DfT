package uk.ncl.giacomobergami.components.iot;

import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity;
import org.cloudbus.cloudsim.edge.iot.network.EdgeNetworkInfo;
import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

import java.util.function.Supplier;

public class IoTGeneratorFactory {

    public static IoTDevice generateFacade(ConfiguationEntity.IotDeviceEntity onta) {
        return ReflectiveFactoryMethod
                .getInstance(IoTDevice.class)
                .generateFacade(onta.getIoTClassName(), (Supplier<IoTDevice>) () -> new CarSensor( onta), onta);
    }



}
