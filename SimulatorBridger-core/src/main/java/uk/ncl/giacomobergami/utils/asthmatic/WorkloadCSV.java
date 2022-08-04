package uk.ncl.giacomobergami.utils.asthmatic;

public class WorkloadCSV {
    public String OsmesisApp;                      //Different name per row
    public long ID;                                //Different id per row
    public double DataRate_Sec;                    // ~ Arbitrary
    public double StartDataGenerationTime_Sec;     //Time when the node starts connecting to the first MEL
    public double  StopDataGeneration_Sec;          //Time after the disconnection to the MEL
    public String  IoTDevice;                       //Source
    public double IoTDeviceOutputData_Mb;          // ~ Arbitrary
    public String MELName;                         //Target
    public long OsmesisEdgelet_MI;                 // ~ Arbitrary
    public long MELOutputData_Mb;                  // ~ Arbitrary
    public String VmName;                          // The same VM assocaited to the MEL of choice
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
