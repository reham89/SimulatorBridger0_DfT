package uk.ncl.giacomobergami.components.networking;

import org.cloudbus.cloudsim.edge.core.edge.LegacyConfiguration;
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

    public LegacyConfiguration.EdgeDeviceEntity asLegacyEdgeDeviceEntity() {
        var result = new LegacyConfiguration.EdgeDeviceEntity();
        result.setMips(mips);
        result.setStorage(storage);
        result.setBwSize(bw);
        result.setRamSize(ram);
        result.setPes(pes);
        result.setName(name);
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

    public static List<LegacyConfiguration.EdgeDeviceEntity> asLegacyEdgeDeviceEntity(File name) {
        var reader = csvReader().beginCSVRead(name);
        ArrayList<LegacyConfiguration.EdgeDeviceEntity> ls = new ArrayList<>();
        while (reader.hasNext()) {
            ls.add(reader.next().asLegacyEdgeDeviceEntity());
        }
        return ls;
    }

    public static List<LegacyConfiguration.HostEntity> asLegacyHostEntity(File name) {
        var reader = csvReader().beginCSVRead(name);
        ArrayList<LegacyConfiguration.HostEntity> ls = new ArrayList<>();
        while (reader.hasNext()) {
            ls.add(reader.next().asLegacyHostEntity());
        }
        return ls;
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
