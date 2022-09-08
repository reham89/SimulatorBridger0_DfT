/*
 * OsmoticConfiguration.java
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

package uk.ncl.giacomobergami.components.simulator;

public class OsmoticConfiguration {
    public String   configurationFile;
    public String   osmesisAppFile;
    public String   RES_CONFIG_FILE;
    public String   AGENT_CONFIG_FILE;
    public int      num_user;
    public boolean  trace_flag;
    public String   simulationStartTime;
    public String   OsmesisBroker;
    public double   terminate_simulation_at;
    public String   mel_switch_policy;

    public String getMel_switch_policy() {
        return mel_switch_policy;
    }
    public void setMel_switch_policy(String mel_switch_policy) {
        this.mel_switch_policy = mel_switch_policy;
    }
    public double getTerminate_simulation_at() {
        return terminate_simulation_at;
    }

    public void setTerminate_simulation_at(double terminate_simulation_at) {
        this.terminate_simulation_at = terminate_simulation_at;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }
    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }
    public String getOsmesisAppFile() {
        return osmesisAppFile;
    }
    public void setOsmesisAppFile(String osmesisAppFile) {
        this.osmesisAppFile = osmesisAppFile;
    }
    public String getRES_CONFIG_FILE() {
        return RES_CONFIG_FILE;
    }
    public void setRES_CONFIG_FILE(String RES_CONFIG_FILE) {
        this.RES_CONFIG_FILE = RES_CONFIG_FILE;
    }
    public String getAGENT_CONFIG_FILE() {
        return AGENT_CONFIG_FILE;
    }
    public void setAGENT_CONFIG_FILE(String AGENT_CONFIG_FILE) {
        this.AGENT_CONFIG_FILE = AGENT_CONFIG_FILE;
    }
    public int getNum_user() {
        return num_user;
    }
    public void setNum_user(int num_user) {
        this.num_user = num_user;
    }
    public boolean isTrace_flag() {
        return trace_flag;
    }
    public void setTrace_flag(boolean trace_flag) {
        this.trace_flag = trace_flag;
    }
    public String getSimulationStartTime() {
        return simulationStartTime;
    }
    public void setSimulationStartTime(String simulationStartTime) {
        this.simulationStartTime = simulationStartTime;
    }
    public String getOsmesisBroker() {
        return OsmesisBroker;
    }
    public void setOsmesisBroker(String osmesisBroker) {
        OsmesisBroker = osmesisBroker;
    }
}
