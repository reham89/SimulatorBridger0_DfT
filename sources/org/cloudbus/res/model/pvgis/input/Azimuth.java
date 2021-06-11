package org.cloudbus.res.model.pvgis.input;

import lombok.Data;

@Data
public class Azimuth {
    private int value;
    private boolean optimal;
    private String description;
    private String units;
}
