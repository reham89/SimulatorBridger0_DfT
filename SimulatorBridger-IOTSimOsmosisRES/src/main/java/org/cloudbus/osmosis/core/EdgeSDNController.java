/*
 * Title:        IoTSim-Osmosis 1.0
 * Description:  IoTSim-Osmosis enables the testing and validation of osmotic computing applications 
 * 			     over heterogeneous edge-cloud SDN-aware environments.
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2020, Newcastle University (UK) and Saudi Electronic University (Saudi Arabia) 
 * 
 */

package org.cloudbus.osmosis.core;

import org.cloudbus.cloudsim.edge.core.edge.LegacyConfiguration;
import org.cloudbus.cloudsim.edge.core.edge.EdgeDataCenter;
import uk.ncl.giacomobergami.components.sdn_routing.SDNRoutingPolicyGeneratorFacade;
import uk.ncl.giacomobergami.components.sdn_traffic.SDNTrafficPolicyGeneratorFacade;
import uk.ncl.giacomobergami.components.sdn_traffic.SDNTrafficSchedulingPolicy;
import uk.ncl.giacomobergami.components.sdn_routing.SDNRoutingPolicy;


/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class EdgeSDNController extends SDNController {
	EdgeDataCenter edgeDataCenters;	

	public EdgeSDNController(String name,
							 SDNTrafficSchedulingPolicy sdnPolicy,
							 SDNRoutingPolicy sdnRouting){
		super(name, sdnPolicy,sdnRouting);			
	}

	public EdgeSDNController(String name, String traffic, String routing, EdgeDataCenter datacenter) {
		this(name,
				SDNTrafficPolicyGeneratorFacade.generateFacade(traffic),
				SDNRoutingPolicyGeneratorFacade.generateFacade(routing));
		setName(name);
		setDatacenter(datacenter);
	}

	public EdgeSDNController(LegacyConfiguration.ControllerEntity controllerEntity, EdgeDataCenter datacenter) {
		this(controllerEntity.getName(),
				SDNTrafficPolicyGeneratorFacade.generateFacade(controllerEntity.getTrafficPolicy()),
				SDNRoutingPolicyGeneratorFacade.generateFacade(controllerEntity.getRoutingPolicy()));
		setName(controllerEntity.getName());
		setDatacenter(datacenter);
	}
}
