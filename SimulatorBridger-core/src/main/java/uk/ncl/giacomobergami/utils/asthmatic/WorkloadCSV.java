package uk.ncl.giacomobergami.utils.asthmatic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"OsmesisApp","ID","DataRate_Sec","StartDataGenerationTime_Sec","StopDataGeneration_Sec","IoTDevice","IoTDeviceOutputData_Mb","MELName","OsmesisEdgelet_MI","MELOutputData_Mb","VmName","OsmesisCloudlet_MI"})
public class WorkloadCSV {
    @JsonProperty("OsmesisApp")
    public String OsmesisApp;                      //Different name per row
    @JsonProperty("ID")
    public long ID;                                //Different id per row
    @JsonProperty("DataRate_Sec")
    public double DataRate_Sec;                    // ~ Arbitrary
    @JsonProperty("StartDataGenerationTime_Sec")
    public double StartDataGenerationTime_Sec;     //Time when the node starts connecting to the first MEL
    @JsonProperty("StopDataGeneration_Sec")
    public double  StopDataGeneration_Sec;          //Time after the disconnection to the MEL
    @JsonProperty("IoTDevice")
    public String  IoTDevice;                       //Source
    @JsonProperty("IoTDeviceOutputData_Mb")
    public double IoTDeviceOutputData_Mb;          // ~ Arbitrary
    @JsonProperty("MELName")
    public String MELName;                         //Target
    @JsonProperty("OsmesisEdgelet_MI")
    public long OsmesisEdgelet_MI;                 // ~ Arbitrary
    @JsonProperty("MELOutputData_Mb")
    public long MELOutputData_Mb;                  // ~ Arbitrary
    @JsonProperty("VmName")
    public String VmName;                          // The same VM assocaited to the MEL of choice
    @JsonProperty("OsmesisCloudlet_MI")
    public long OsmesisCloudlet_MI;                // ~ Arbitrary

    public String getOsmesisApp() {
        return OsmesisApp;
    }
    public void setOsmesisApp(String osmesisApp) {
        OsmesisApp = osmesisApp;
    }
    public long getID() {
        return ID;
    }
    public void setID(long ID) {
        this.ID = ID;
    }
    public double getDataRate_Sec() {
        return DataRate_Sec;
    }
    public void setDataRate_Sec(double dataRate_Sec) {
        DataRate_Sec = dataRate_Sec;
    }
    public double getStartDataGenerationTime_Sec() {
        return StartDataGenerationTime_Sec;
    }

    public void setStartDataGenerationTime_Sec(double startDataGenerationTime_Sec) {
        StartDataGenerationTime_Sec = startDataGenerationTime_Sec;
    }

    public double getStopDataGeneration_Sec() {
        return StopDataGeneration_Sec;
    }

    public void setStopDataGeneration_Sec(double stopDataGeneration_Sec) {
        StopDataGeneration_Sec = stopDataGeneration_Sec;
    }

    public String getIoTDevice() {
        return IoTDevice;
    }

    public void setIoTDevice(String ioTDevice) {
        IoTDevice = ioTDevice;
    }

    public double getIoTDeviceOutputData_Mb() {
        return IoTDeviceOutputData_Mb;
    }

    public void setIoTDeviceOutputData_Mb(double ioTDeviceOutputData_Mb) {
        IoTDeviceOutputData_Mb = ioTDeviceOutputData_Mb;
    }

    public String getMELName() {
        return MELName;
    }

    public void setMELName(String MELName) {
        this.MELName = MELName;
    }

    public long getOsmesisEdgelet_MI() {
        return OsmesisEdgelet_MI;
    }

    public void setOsmesisEdgelet_MI(long osmesisEdgelet_MI) {
        OsmesisEdgelet_MI = osmesisEdgelet_MI;
    }

    public long getMELOutputData_Mb() {
        return MELOutputData_Mb;
    }

    public void setMELOutputData_Mb(long MELOutputData_Mb) {
        this.MELOutputData_Mb = MELOutputData_Mb;
    }

    public String getVmName() {
        return VmName;
    }

    public void setVmName(String vmName) {
        VmName = vmName;
    }

    public long getOsmesisCloudlet_MI() {
        return OsmesisCloudlet_MI;
    }

    public void setOsmesisCloudlet_MI(long osmesisCloudlet_MI) {
        OsmesisCloudlet_MI = osmesisCloudlet_MI;
    }
}
