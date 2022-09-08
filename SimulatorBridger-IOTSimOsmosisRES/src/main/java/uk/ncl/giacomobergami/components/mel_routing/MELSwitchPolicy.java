/*
 * MELSwitchPolicy.java
 * This file is part of SimulatorBridger-IOTSimOsmosisRES
 *
 * Copyright (C) 2022 - Giacomo Bergami
 *
 * SimulatorBridger-IOTSimOsmosisRES is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * SimulatorBridger-IOTSimOsmosisRES is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimulatorBridger-IOTSimOsmosisRES. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ncl.giacomobergami.components.mel_routing;

import org.cloudbus.osmosis.core.OsmoticBroker;
import uk.ncl.giacomobergami.components.iot.IoTDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface MELSwitchPolicy extends Predicate<String> {
    default boolean test(String s) { return s.matches("^\\S*.[*]$"); }
    default List<String> getCandidateMELsFromPattern(String pattern,
                                                     OsmoticBroker self) {
        List<String> instances = new ArrayList<>();
        String reg = pattern.replaceAll("(.\\*)$", "");
        reg = "^"+reg+".[0-9]+$";
        for(String melName: self.iotVmIdByName.keySet()){
            if (melName.matches(reg)){
                instances.add(melName);
            }
        }
        return instances;
    }

    String apply(IoTDevice ioTDevice, String melName, OsmoticBroker broker);
}
