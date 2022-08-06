package uk.ncl.giacomobergami.components;

import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity;
import org.cloudbus.cloudsim.edge.iot.CarSensor;
import org.cloudbus.cloudsim.edge.iot.IoTDevice;
import org.cloudbus.cloudsim.edge.iot.network.EdgeNetworkInfo;
import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

import java.util.function.Supplier;

public class IoTGeneratorFactory {

    public static IoTDevice generateFacade(String clazzPath, EdgeNetworkInfo eni, ConfiguationEntity.IotDeviceEntity onta) {
        return ReflectiveFactoryMethod
                .getInstance(IoTDevice.class)
                .generateFacade(clazzPath, (Supplier<IoTDevice>) () -> new CarSensor(eni, onta), eni, onta);
    }



}
