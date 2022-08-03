package uk.ncl.giacomobergami.utils.asthmatic;

import uk.ncl.giacomobergami.utils.algorithms.ClusterDifference;
import uk.ncl.giacomobergami.utils.shared_data.VehicularProgram;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkloadFromVehicularProgram {
    private VehicularProgram program;
    private List<WorkloadCSV> result;        Double startTime = null;
    Double endTime;
    String mel;
    Double lastTick;
    String firstId;

    public WorkloadFromVehicularProgram(VehicularProgram program) {
        this.program = program;
        result = new ArrayList<>();
        init();
    }

    public void setNewVehicularProgram(VehicularProgram program) {
        this.program = program;
        if (result == null)
            result = new ArrayList<>();
        init();
    }

    private void init() {
        endTime = null;
        mel = null;
        lastTick = null;
        firstId = null;
        result.clear();
        startTime = null;
    }

    public List<WorkloadCSV> generateFirstMileSpecifications(double micro_interval,
                                                             AtomicInteger ai,
                                                             Map<String, Integer> mel_to_vm) {
        init();
        boolean firstFound = false;
        for (var tick : program.pathingAtEachSimulationTime.entrySet()) {
            lastTick = tick.getKey();
            var val = tick.getValue();
            if (val.isStartingProgram) {
                if ((val.setInitialClusterConnection == null)
                        || (val.setInitialClusterConnection.isEmpty())) {

                } else {
                    if (val.setInitialClusterConnection.size() != 1)
                        throw new RuntimeException("ERROR");
                    firstId = val.localInformation.id;
                    firstFound = true;
                    buildUpNewWorkload(val.setInitialClusterConnection.get(0), lastTick);
                }
            } else {
                if ((val.setConnectionVariation == null) || (val.setConnectionVariation.change == ClusterDifference.type.UNCHANGED)) continue;
                if (!firstFound) {
                    firstId = val.localInformation.id;
                    firstFound = true;
                }
                if (mel != null) {
                    if (val.setConnectionVariation.changes.get(mel) != ClusterDifference.typeOfChange.REMOVAL_OF)
                        throw new RuntimeException("ERROR");
                    generateWorkloadAtStop(micro_interval, ai, mel_to_vm);
                }
                if (val.setConnectionVariation.changes.size() > 2)
                    throw new RuntimeException("ERROR");
                for (var x : val.setConnectionVariation.changes.entrySet()) {
                    if (x.getValue() == ClusterDifference.typeOfChange.REMOVAL_OF) continue;
                    var initi = x.getKey();
                    buildUpNewWorkload(initi, lastTick);
                    break;
                }
            }
        }
        finalize(micro_interval, ai, mel_to_vm);
        return result;
    }



    public List<WorkloadCSV> generateLastMileSpecifications(double micro_interval,
                                                            AtomicInteger ai,
                                                            Map<String, Integer> mel_to_vm) {
        for (var tick : program.pathingAtEachSimulationTime.entrySet()) {
            lastTick = tick.getKey();
            var val = tick.getValue();
            if (val.isStartingProgram) firstId = val.localInformation.id;
            boolean hasNoMel = mel == null;
            boolean hasNoShortestPah = ((val.shortest_path == null) || val.shortest_path.isEmpty());
            if (hasNoShortestPah) {
                if (hasNoMel) continue;
                generateWorkloadAtStop(micro_interval, ai, mel_to_vm);
            } else {
                if (hasNoMel) {
                    buildUpNewWorkload(val.shortest_path.get(val.shortest_path.size()-1).getVal2().tl_id, lastTick);
                } else {
                    generateWorkloadAtStop(micro_interval, ai, mel_to_vm);
                    buildUpNewWorkload(val.shortest_path.get(val.shortest_path.size()-1).getVal2().tl_id, lastTick);
                }
            }
        }
        finalize(micro_interval, ai, mel_to_vm);
        return result;
    }

    private void buildUpNewWorkload(String initi, Double lastTick) {
        mel = initi;
        startTime = lastTick;
        endTime = null;
    }

    private void finalize(double micro_interval, AtomicInteger ai, Map<String, Integer> mel_to_vm) {
        if ((mel != null) && (startTime != null)) {
            generateWorkloadAtStop(micro_interval, ai, mel_to_vm);
        }
    }

    private void generateWorkloadAtStop(double micro_interval, AtomicInteger ai, Map<String, Integer> mel_to_vm) {
        endTime = lastTick - micro_interval;
        if (Objects.equals(startTime, endTime))
            endTime += (micro_interval/ 100.0);
        else if (endTime > (startTime - micro_interval))
            endTime -= micro_interval;
        else
            throw new RuntimeException("ERROR");
        WorkloadCSV wl = new WorkloadCSV();
        wl.OsmesisApp = "App_"+ ai.get();
        wl.ID = ai.getAndIncrement();
        wl.StopDataGeneration_Sec = endTime;
        wl.StartDataGenerationTime_Sec = startTime;
        wl.DataRate_Sec = micro_interval / 100.0;
        wl.IoTDevice = firstId;
        wl.IoTDeviceOutputData_Mb = 90;
        wl.MELName = mel;
        wl.OsmesisEdgelet_MI = 250;
        wl.MELOutputData_Mb = 70;
        wl.VmName = "VM_"+ mel_to_vm.get(mel);
        wl.OsmesisCloudlet_MI = 200;
        result.add(wl);
        buildUpNewWorkload(null, null);
    }
}
