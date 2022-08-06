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

package org.cloudbus.cloudsim.edge.core.edge;



import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.provisioners.*;
import org.cloudbus.cloudsim.sdn.example.policies.VmSchedulerTimeSharedEnergy;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class EdgeDevice extends Host {
	
	private String deviceName;	

	private boolean enabled;		
	
	public EdgeDevice(int id, String deviceName, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner,
			long storage, List<? extends Pe> peList) {
		super(id, ramProvisioner, bwProvisioner, storage, peList,
				new VmSchedulerTimeSharedEnergy(peList));
		this.deviceName = deviceName;
		this.enabled = true;
	}

	public static List<Pe> generatePEList( ConfiguationEntity.EdgeDeviceEntity hostEntity) {
		return IntStream.range(0, hostEntity.getPes())
				.mapToObj(i -> new Pe(i, new PeProvisionerSimple(hostEntity.getMips())))
				.collect(Collectors.toList());
//		var peList = new LinkedList<Pe>();
//		int peId=0;
//		for(int i= 0; i < hostEntity.getPes(); i++) {
//			peList.add(new Pe(peId++,new PeProvisionerSimple(hostEntity.getMips())));
//		}
	}

    public EdgeDevice(AtomicInteger idGen, ConfiguationEntity.EdgeDeviceEntity hostEntity) {
        this(idGen.getAndIncrement(),
				hostEntity.getName(),
				new RamProvisionerSimple(hostEntity.getRamSize()),
				new BwProvisionerSimple(hostEntity.getBwSize()),
				hostEntity.getStorage(),
				generatePEList(hostEntity));
    }


    public String getDeviceName() {
		return deviceName;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
