package uk.ncl.giacomobergami.components.iot_protocol;

import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

public class IoTProtocolGeneratorFactory {

    public static IoTProtocol generateFacade(String clazzPath) {
        return ReflectiveFactoryMethod
                .getInstance(IoTProtocol.class)
                .generateFacade(clazzPath, XMPPProtocol::new);
    }



}
