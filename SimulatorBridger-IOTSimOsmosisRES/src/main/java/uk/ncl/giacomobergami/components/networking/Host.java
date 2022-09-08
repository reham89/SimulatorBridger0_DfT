package uk.ncl.giacomobergami.components.networking;

import org.cloudbus.cloudsim.edge.core.edge.LegacyConfiguration;
import org.cloudbus.cloudsim.edge.core.edge.Mobility;
import uk.ncl.giacomobergami.utils.data.CSVMediator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Host {
    public String name;
    public int pes;
    public int ram;
    public int bw;
    public long storage;
    public int mips;
    public double x;
    public double y;
    public double z;
    public double signalRange;

    public Host(String name, int pes, int ram, int bw, long storage, int mips, double x, double y, double z, double signalRange) {
        this.name = name;
        this.pes = pes;
        this.ram = ram;
        this.bw = bw;
        this.storage = storage;
        this.mips = mips;
        this.x = x;
        this.y = y;
        this.z = z;
        this.signalRange = signalRange;
    }

    public Host() {}
    public Host(LegacyConfiguration.EdgeDeviceEntity x) {
        mips = x.getMips();
        storage = x.getStorage();
        bw = x.getBwSize();
        ram = x.getRamSize();
        pes = x.getPes();
        name = x.getName();
        this.x = x.getLocation().x;
        y = x.getLocation().y;
        z = x.getLocation().z;
        signalRange = x.getSignalRange();
    }

    public Host(LegacyConfiguration.HostEntity x) {
        mips = (int)x.getMips();
        storage = x.getStorage();
        bw = (int)x.getBw();
        ram = x.getRam();
        pes = (int)x.getPes();
        name = x.getName();
        this.x = y = z = 0;
        signalRange = Double.MAX_VALUE;
    }

    public LegacyConfiguration.EdgeDeviceEntity asLegacyEdgeDeviceEntity() {
        var result = new LegacyConfiguration.EdgeDeviceEntity();
        result.setMips(mips);
        result.setStorage(storage);
        result.setBwSize(bw);
        result.setRamSize(ram);
        result.setPes(pes);
        result.setName(name);
        result.setLocation(new Mobility.Location(x, y, z));
        result.setSignalRange(signalRange);
        return result;
    }



    private static CSVMediator<Host> readerWriter = null;
    public static CSVMediator<Host> csvReader() {
        if (readerWriter == null)
            readerWriter = new CSVMediator<>(Host.class);
        return readerWriter;
    }

    public LegacyConfiguration.HostEntity asLegacyHostEntity() {
        var result = new LegacyConfiguration.HostEntity();
        result.setMips(mips);
        result.setStorage(storage);
        result.setBw(bw);
        result.setRam(ram);
        result.setPes(pes);
        result.setName(name);
        return result;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getSignalRange() {
        return signalRange;
    }

    public void setSignalRange(double signalRange) {
        this.signalRange = signalRange;
    }

    public static CSVMediator<Host> getReaderWriter() {
        return readerWriter;
    }

    public static void setReaderWriter(CSVMediator<Host> readerWriter) {
        Host.readerWriter = readerWriter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPes() {
        return pes;
    }

    public void setPes(int pes) {
        this.pes = pes;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getBw() {
        return bw;
    }

    public void setBw(int bw) {
        this.bw = bw;
    }

    public long getStorage() {
        return storage;
    }

    public void setStorage(long storage) {
        this.storage = storage;
    }

    public int getMips() {
        return mips;
    }

    public void setMips(int mips) {
        this.mips = mips;
    }
}
