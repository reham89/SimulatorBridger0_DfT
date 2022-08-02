package uk.ncl.giacomobergami.utils.shared_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import uk.ncl.giacomobergami.utils.gir.CartesianPoint;

import java.util.Objects;

// To be renamed as "EdgeDevice"

@JsonPropertyOrder({
        "tl_id",
        "x",
        "y",
        "communication_radius",
        "max_vehicle_communication"
})
@JsonIgnoreProperties({"x, y"})
public class RSU implements CartesianPoint {
    @JsonProperty("tl_id")
    public String tl_id;

    @JsonProperty("x")
    public double x;

    @JsonProperty("y")
    public double y;

    @JsonProperty("communication_radius")
    public double communication_radius;

    @JsonProperty("max_vehicle_communication")
    public double max_vehicle_communication;

    public RSUProgram program_rsu;

    public RSUProgram getProgram_rsu() {
        return program_rsu;
    }

    public void setProgram_rsu(RSUProgram program_rsu) {
        this.program_rsu = program_rsu;
    }

    public RSU() {

    }

    public RSU(String id, double x, double y, double communication_radius,
               double max_vehicle_communication) {
        this.tl_id = id;
        this.x = x;
        this.y = y;
        this.communication_radius = communication_radius;
        this.max_vehicle_communication = max_vehicle_communication;
    }

    public String getTl_id() {
        return tl_id;
    }

    public void setTl_id(String tl_id) {
        this.tl_id = tl_id;
    }

    public void setX(double tl_x) {
        this.x = tl_x;
    }
    public void setY(double tl_y) {
        this.y = tl_y;
    }

    public double getCommunication_radius() {
        return communication_radius;
    }

    public void setCommunication_radius(double communication_radius) {
        this.communication_radius = communication_radius;
    }

    public double getMax_vehicle_communication() {
        return max_vehicle_communication;
    }

    public void setMax_vehicle_communication(double max_vehicle_communication) {
        this.max_vehicle_communication = max_vehicle_communication;
    }

    @Override
    public String toString() {
        return "RSU{" +
                "tl_id='" + tl_id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", communication_radius=" + communication_radius +
                ", max_vehicle_communication=" + max_vehicle_communication +
                '}';
    }

    //    public ConfiguationEntity.VMEntity asVMEntity(int pes,
//                                                  double mips,
//                                                  int ram,
//                                                  double storage,
//                                                  long bw,
//                                                  String cloudletPolicy) {
//        ConfiguationEntity.VMEntity result = new ConfiguationEntity.VMEntity();
//        result.setBw(bw);
//        result.setCloudletPolicy(cloudletPolicy);
//        result.setPes(pes);
//        result.setMips(mips);
//        result.setRam(ram);
//        result.setStorage(storage);
//        result.setName(tl_id);
//        return result;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSU rsu = (RSU) o;
        return Double.compare(rsu.x, x) == 0 && Double.compare(rsu.y, y) == 0 && Double.compare(rsu.communication_radius, communication_radius) == 0 && Double.compare(rsu.max_vehicle_communication, max_vehicle_communication) == 0 && Objects.equals(tl_id, rsu.tl_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tl_id, x, y, communication_radius, max_vehicle_communication);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }
}
