package uk.ncl.giacomobergami.utils.shared_data.abstracted;

import uk.ncl.giacomobergami.utils.gir.CartesianPoint;

import java.sql.Time;

public interface TimedObject<T> extends CartesianPoint {
    public String getId();
    public double getSimtime();
    public T copy();
}
