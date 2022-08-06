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

import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity;
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

public class CloudSDNController extends SDNController {	

	public CloudSDNController(ConfiguationEntity.ControllerEntity params) {
		super(params.name,
				SDNTrafficPolicyGeneratorFacade.generateFacade(params.getTrafficPolicy()),
				SDNRoutingPolicyGeneratorFacade.generateFacade(params.getRoutingPolicy()));
		setName(params.name);
	}

	public CloudSDNController(String name, SDNTrafficSchedulingPolicy sdnPolicy, SDNRoutingPolicy sdnRouting){
		super(name, sdnPolicy,sdnRouting);		
	}
}