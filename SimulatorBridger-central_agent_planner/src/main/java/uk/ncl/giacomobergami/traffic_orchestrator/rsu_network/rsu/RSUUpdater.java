/*
 * RSUUpdater.java
 * This file is part of SimulatorBridger-central_agent_planner
 *
 * Copyright (C) 2022 - Giacomo Bergami
 *
 * SimulatorBridger-central_agent_planner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * SimulatorBridger-central_agent_planner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimulatorBridger-central_agent_planner. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.rsu;

import uk.ncl.giacomobergami.utils.shared_data.edge.TimedEdge;

import java.util.function.Consumer;

public abstract class RSUUpdater implements Consumer<TimedEdge> {
    protected final Double default_comm_radius;
    protected final Integer default_max_vehicle_communication;
    public RSUUpdater(Double default_comm_radius, Integer default_max_vehicle_communication) {
        this.default_comm_radius = default_comm_radius;
        this.default_max_vehicle_communication = default_max_vehicle_communication;
    }
}
