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

package org.cloudbus.cloudsim.osmesis.examples.uti;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Array;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.sdn.Link;
import org.cloudbus.cloudsim.sdn.NetworkNIC;
import org.cloudbus.cloudsim.sdn.SDNHost;
import org.cloudbus.cloudsim.sdn.Switch;
import org.cloudbus.cloudsim.sdn.power.PowerUtilizationHistoryEntry;
import org.cloudbus.cloudsim.sdn.power.PowerUtilizationInterface;
import org.cloudbus.osmosis.core.Flow;
import org.cloudbus.osmosis.core.OsmoticAppDescription;
import org.cloudbus.osmosis.core.OsmoticBroker;
import org.cloudbus.osmosis.core.WorkflowInfo;
import uk.ncl.giacomobergami.utils.data.CSVMediator;
import uk.ncl.giacomobergami.utils.data.JSON;


/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class PrintResults {
	List<OsmoticAppDescription> appList;
	List<PrintOsmosisAppFromTags> osmoticAppsStats;
	List<OsmesisOverallAppsResults> overallAppResults;
	List<EnergyConsumption> dataCenterEnergyConsumption;
	List<PowerConsumption> hpc;
	List<PowerConsumption> spc;
	List<ActualPowerUtilizationHistoryEntry> puhe;
	List<ActualHistoryEntry> ahe;
	TreeMap<String, List<String>> app_to_path;

	public void dumpCSV(File folder) {
		if (!folder.exists()) {
			folder.mkdirs();
		}
		new CSVMediator<>(OsmoticAppDescription.class).writeAll(new File(folder, "appList.csv"), appList);
		new CSVMediator<>(PrintOsmosisAppFromTags.class).writeAll(new File(folder, "osmoticAppsStats.csv"), osmoticAppsStats);
		new CSVMediator<>(OsmesisOverallAppsResults.class).writeAll(new File(folder, "overallAppResults.csv"), overallAppResults);
		new CSVMediator<>(EnergyConsumption.class).writeAll(new File(folder, "dataCenterEnergyConsumption.csv"), dataCenterEnergyConsumption);
		new CSVMediator<>(PowerConsumption.class).writeAll(new File(folder, "HostPowerConsumption.csv"), hpc);
		new CSVMediator<>(PowerConsumption.class).writeAll(new File(folder, "SwitchPowerConsumption.csv"), spc);
		new CSVMediator<>(ActualPowerUtilizationHistoryEntry.class).writeAll(new File(folder, "PowerUtilisationHistory.csv"), puhe);
		new CSVMediator<>(ActualHistoryEntry.class).writeAll(new File(folder, "HistoryEntry.csv"), ahe);
		try {
			Files.writeString(new File(folder, "paths.json").toPath(), new Gson().toJson(app_to_path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addHostPowerConsumption(String dcName, String name, double energy) {
		if (hpc == null) hpc = new ArrayList<>();
		hpc.add(new PowerConsumption(dcName, name, energy));
	}
	public void addSwitchPowerConsumption(String dcName, String name, double energy) {
		if (spc == null) spc = new ArrayList<>();
		spc.add(new PowerConsumption(dcName, name, energy));
	}

	public void addHostUtilizationHistory(String dcName, String name, List<PowerUtilizationHistoryEntry> utilizationHisotry) {
		if (puhe == null) puhe = new ArrayList<>();
		if (utilizationHisotry == null) return;
		utilizationHisotry.forEach(x -> puhe.add(new ActualPowerUtilizationHistoryEntry(dcName, name, x)));
	}

	public void addSwitchUtilizationHistory(String dcName, String name, List<Switch.HistoryEntry> utilizationHisotry) {
		if (ahe == null) ahe = new ArrayList<>();
		if (utilizationHisotry == null) return;
		utilizationHisotry.forEach(x -> ahe.add(new ActualHistoryEntry(dcName, name, x)));
	}
		
	public void collectNetworkData(List<OsmoticAppDescription> appList) {
		osmoticAppsStats = new ArrayList<>();
		overallAppResults = new ArrayList<>();
		List<WorkflowInfo> tags = new ArrayList<>();
		for(OsmoticAppDescription app : appList){
			for(WorkflowInfo workflowTag : OsmoticBroker.workflowTag){
				if(app.getAppID() == workflowTag.getAppId()){
					tags.add(workflowTag);
				}
			}
			tags.forEach(x -> this.generateAppTag(x, osmoticAppsStats));
			tags.clear();
		}


		for(OsmoticAppDescription app : appList){
			for(WorkflowInfo workflowTag : OsmoticBroker.workflowTag){
				workflowTag.getAppId();
				if(app.getAppID() == workflowTag.getAppId()){
					tags.add(workflowTag);
				}
			}
			if (!tags.isEmpty())
				printAppStat(app, tags, overallAppResults);
			tags.clear();
		}
		this.appList = appList;
	}

	public void collectDataCenterData(String dcName,
									  List<SDNHost> hostList,
									  List<Switch> switchList,
									  double finishTime) {
		EnergyConsumption ec = new EnergyConsumption();
		ec.dcName = dcName;
		ec.finishTime = finishTime;
		if(hostList != null){
			for(SDNHost sdnHost:hostList) {
				Host host = sdnHost.getHost();
				PowerUtilizationInterface scheduler =  (PowerUtilizationInterface) host.getVmScheduler();
				scheduler.addUtilizationEntryTermination(finishTime);
				double energy = scheduler.getUtilizationEnergyConsumption();
				ec.addHostPowerConsumption(energy);
				addHostPowerConsumption(dcName, sdnHost.getName(), energy);
				addHostUtilizationHistory(dcName, sdnHost.getName(), scheduler.getUtilizationHisotry());
			}
		}
		for(Switch sw:switchList) {
			sw.addUtilizationEntryTermination(finishTime);
			double energy = sw.getUtilizationEnergyConsumption();
			ec.addSwitchPowerConsumption(energy);
			addSwitchPowerConsumption(dcName, sw.getName(), energy);
			addSwitchUtilizationHistory(dcName, sw.getName(), sw.getUtilizationHisotry());
		}

		ec.finalise();
		if (dataCenterEnergyConsumption == null) dataCenterEnergyConsumption = new ArrayList<>();
		dataCenterEnergyConsumption.add(ec);
	}

	public static class OsmesisOverallAppsResults {
		public String App_Name;
		public String IoTDeviceDrained;
		public double IoTDeviceBatteryConsumption;
		public long TotalIoTGeneratedData;
		public long TotalEdgeLetSizes;
		public long TotalMELGeneratedData;
		public long TotalCloudLetSizes;
		public double StartTime;
		public double EndTime;
		public double SimluationTime;
		public double appTotalRunningTime;
	}

	private void printAppStat(OsmoticAppDescription app,
							  List<WorkflowInfo> tags,
							  List<OsmesisOverallAppsResults> list) {
		String appName = app.getAppName();
		String isIoTDeviceDrained = app.getIoTDeviceBatteryStatus();
		double iotDeviceTotalConsumption = app.getIoTDeviceBatteryConsumption();
		long TotalIoTGeneratedData = 0;
		long TotalEdgeLetSizes = 0;
		long TotalMELGeneratedData = 0;
		long TotalCloudLetSizes = 0;
		double appTotalRunningTmie = 0;
		OsmesisOverallAppsResults fromTag = new OsmesisOverallAppsResults();

		double StartTime = app.getAppStartTime();
		double EndTime = tags.get(tags.size()-1).getCloudLet().getFinishTime();
		double SimluationTime = EndTime - StartTime;
		
		WorkflowInfo firstWorkflow = tags.get(0);
		WorkflowInfo secondWorkflow = tags.size() > 1 ? tags.get(1) : null;
		
		if((secondWorkflow != null) && (firstWorkflow.getFinishTime() > secondWorkflow.getSartTime())) {
			appTotalRunningTmie = EndTime - StartTime;			
		} else {
			for(WorkflowInfo workflowTag : tags){
				appTotalRunningTmie += workflowTag.getFinishTime() - workflowTag.getSartTime(); 
			}
		}
		if (StartTime < 0.0) {
			StartTime = EndTime - appTotalRunningTmie;
		}
		
		for(WorkflowInfo workflowTag : tags){
			TotalIoTGeneratedData += workflowTag.getIotDeviceFlow().getSize(); 
			TotalEdgeLetSizes += workflowTag.getEdgeLet().getCloudletLength(); 
			TotalMELGeneratedData += workflowTag.getEdgeToCloudFlow().getSize();
			TotalCloudLetSizes += workflowTag.getCloudLet().getCloudletLength();			   
		}
		
		fromTag.App_Name = appName;
		fromTag.IoTDeviceDrained = isIoTDeviceDrained;
		fromTag.IoTDeviceBatteryConsumption = iotDeviceTotalConsumption;
		fromTag.TotalIoTGeneratedData = TotalIoTGeneratedData;
		fromTag.TotalEdgeLetSizes = TotalEdgeLetSizes;
		fromTag.TotalMELGeneratedData = TotalMELGeneratedData;
		fromTag.TotalCloudLetSizes = TotalCloudLetSizes;
		fromTag.StartTime = StartTime;
		fromTag.EndTime = EndTime;
		fromTag.SimluationTime = SimluationTime;
		fromTag.appTotalRunningTime = appTotalRunningTmie;
		list.add(fromTag);
	}

	public static class PrintOsmosisAppFromTags {
		public int APP_ID;
		public String AppName;
		public int Transaction;
		public double StartTime;
		public double FinishTime;
		public String IoTDeviceName;
		public String MELName;
		public long DataSizeIoTDeviceToMEL_Mb;
		public double TransmissionTimeIoTDeviceToMEL;
		public double EdgeLetMISize;
		public double EdgeLet_MEL_StartTime;
		public double EdgeLet_MEL_FinishTime;
		public double EdgeLetProccessingTimeByMEL;
		public String DestinationVmName;
		public long DataSizeMELToVM_Mb;
		public double TransmissionTimeMELToVM;
		public double CloudLetMISize;
		public double CloudLetProccessingTimeByVM;
		public double TransactionTotalTime;
	}

	public static List<String> sortLinks(List<Link> ls) {
		HashMap<String, Integer> ingoingCount = new HashMap<>();
		HashMap<String, Integer> outgoingCount = new HashMap<>();
		HashMultimap<String, String> m = HashMultimap.create();
		Set<String> set = new HashSet<>();
		for (var edge : ls) {
			if (edge == null) continue;
			m.put(edge.src().getName(), edge.dst().getName());
			set.add(edge.src().getName());
			set.add(edge.dst().getName());
			ingoingCount.compute(edge.dst().getName(), (s, integer) -> integer == null ? 1 : integer+1);
			outgoingCount.compute(edge.src().getName(), (s, integer) -> integer == null ? 1 : integer+1);
		}
		String src = null;
		String dst = null;
		for (var x : set) {
			if (!ingoingCount.containsKey(x)) {
				if (src != null)
					throw new RuntimeException("Wrong assumption");
				src = x;
			}
			if (!outgoingCount.containsKey(x)) {
				if (dst != null)
					throw new RuntimeException("Wrong assumption");
				dst = x;
			}
		}
		ingoingCount.clear();
		outgoingCount.clear();
		ArrayList<String> result = new ArrayList<>();
//		while (!Objects.equals(src, dst)) {
//			result.add(src);
//			src = m.get(src);
//		}
//		result.add(src);
		return result;
	}

	public void generateAppTag(WorkflowInfo workflowTag,
							   List<PrintOsmosisAppFromTags> list) {
			ArrayList<Link> ls1 = new ArrayList<>();
			var sx = workflowTag.getEdgeToCloudFlow();
			if ((sx != null) && (sx.getNodeOnRouteList() != null)) ls1.addAll(sx.getLinkList());
	//		Collections.reverse(ls1);
			var dx = workflowTag.getIotDeviceFlow();
			if ((dx != null) && (dx.getNodeOnRouteList() != null)) ls1.addAll(dx.getLinkList());
			if (app_to_path == null) app_to_path = new TreeMap<>();

			var res = sortLinks(new ArrayList<>(ls1));
			app_to_path.put(workflowTag.getAppName(), res);

			PrintOsmosisAppFromTags fromTag = new PrintOsmosisAppFromTags();
			fromTag.APP_ID = workflowTag.getAppId();
			fromTag.AppName = workflowTag.getAppName();
			fromTag.Transaction = workflowTag.getWorkflowId();
			fromTag.StartTime = workflowTag.getSartTime();
			fromTag.FinishTime = workflowTag.getFinishTime();
			fromTag.IoTDeviceName = workflowTag.getIotDeviceFlow().getAppNameSrc();
			fromTag.MELName = workflowTag.getIotDeviceFlow().getAppNameDest() + " (" +workflowTag.getSourceDCName() + ")";
			fromTag.DataSizeIoTDeviceToMEL_Mb = workflowTag.getIotDeviceFlow().getSize();
			fromTag.TransmissionTimeIoTDeviceToMEL = workflowTag.getIotDeviceFlow().getTransmissionTime();
			fromTag.EdgeLetMISize = workflowTag.getEdgeLet().getCloudletLength();
			fromTag.EdgeLet_MEL_StartTime = workflowTag.getEdgeLet().getExecStartTime();
			fromTag.EdgeLet_MEL_FinishTime = workflowTag.getEdgeLet().getFinishTime();
			fromTag.EdgeLetProccessingTimeByMEL = workflowTag.getEdgeLet().getActualCPUTime();
			fromTag.DestinationVmName = workflowTag.getEdgeToCloudFlow().getAppNameDest() + " (" +workflowTag.getDestinationDCName() + ")";
			fromTag.DataSizeMELToVM_Mb = workflowTag.getEdgeToCloudFlow().getSize();
			fromTag.TransmissionTimeMELToVM = workflowTag.getEdgeToCloudFlow().getTransmissionTime();
			fromTag.CloudLetMISize = workflowTag.getCloudLet().getCloudletLength();
			fromTag.CloudLetProccessingTimeByVM = workflowTag.getCloudLet().getActualCPUTime();
			fromTag.TransactionTotalTime =  workflowTag.getIotDeviceFlow().getTransmissionTime() + workflowTag.getEdgeLet().getActualCPUTime()
					+ workflowTag.getEdgeToCloudFlow().getTransmissionTime() + workflowTag.getCloudLet().getActualCPUTime();
			list.add(fromTag);
	}

	public static class PowerConsumption {
		public String dcName;
		public String name;
		public double energy;
		public PowerConsumption() {}
		public PowerConsumption(String dcName, String name, double energy) {
			this.dcName = dcName;
			this.name = name;
			this.energy = energy;
		}
	}

	public static class ActualPowerUtilizationHistoryEntry {
		public String dcName;
		public String name;
		public double startTime;
		public double usedMips;

		public ActualPowerUtilizationHistoryEntry(String dcName, String name, PowerUtilizationHistoryEntry entry) {
			this.dcName = dcName;
			this.name = name;
			this.startTime = entry.startTime;
			this.usedMips = entry.usedMips;
		}
	}

	public static class ActualHistoryEntry {
		private String dcName;
		private String name;
		public double startTime;
		public double numActivePorts;

		public ActualHistoryEntry(String dcName, String name, Switch.HistoryEntry entry) {
			this.dcName = dcName;
			this.name = name;
			this.startTime = entry.startTime;
			this.numActivePorts = entry.numActivePorts;
		}
	}
	public static class EnergyConsumption {
		public String dcName;
		public double finishTime;
		public double HostEnergyConsumed;
		public double SwitchEnergyConsumed;
		public double TotalEnergyConsumed;

		public void finalise() {
			TotalEnergyConsumed = HostEnergyConsumed + SwitchEnergyConsumed;
		}

		public void addHostPowerConsumption(double energy) {
			HostEnergyConsumed += energy;
		}
		public void addSwitchPowerConsumption(double energy) {
			SwitchEnergyConsumed += energy;
		}
	}
}
