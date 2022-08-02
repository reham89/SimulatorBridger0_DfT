package uk.ncl.giacomobergami.traffic_converter.abstracted;

import uk.ncl.giacomobergami.utils.shared_data.RSU;
import uk.ncl.giacomobergami.utils.shared_data.RSUMediator;
import uk.ncl.giacomobergami.utils.shared_data.TimedVehicle;
import uk.ncl.giacomobergami.utils.shared_data.TimedVehicleMediator;

import java.io.*;

public abstract class TrafficConverter {

    private final String RSUCsvFile;
    private final String vehicleCSVFile;
    protected RSUMediator rsum;
    protected TimedVehicleMediator vehm;
    protected RSUMediator.CSVWriter rsuwrite;
    protected TimedVehicleMediator.CSVWriter vehwrite;

    public TrafficConverter(String RSUCsvFile, String VehicleCSVFile) {
        this.RSUCsvFile = RSUCsvFile;
        vehicleCSVFile = VehicleCSVFile;
        rsum = new RSUMediator();
        rsuwrite = null;
        vehm = new TimedVehicleMediator();
        vehwrite = null;
    }

    public boolean writeRSUCsv(RSU object) {
        if (rsuwrite == null) {
            rsuwrite = rsum.beginCSVWrite(new File(RSUCsvFile));
            if (rsuwrite == null) return false;
        }
        return rsuwrite.write(object);
    }

    public boolean writeRSUCsvEnd() {
        if (rsuwrite != null) {
            try {
                rsuwrite.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean writeVehicleCsv(TimedVehicle object) {
        if (vehwrite == null) {
            vehwrite = vehm.beginCSVWrite(new File(vehicleCSVFile));
            if (vehwrite == null) return false;
        }
        return vehwrite.write(object);
    }

    public boolean writeVehicleCsvEnd() {
        if (vehwrite != null) {
            try {
                vehwrite.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public abstract boolean runSimulator(long begin, long end, long step);

    public abstract boolean dumpOrchestratorConfiguration();

}
