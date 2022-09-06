package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.messages;

import uk.ncl.giacomobergami.utils.gir.CartesianPoint;

public class PayloadForIoTAgent implements CartesianPoint {
    public String MELName;
    public double x;
    public double y;

    public PayloadForIoTAgent(String MELName, double x, double y) {
        this.MELName = MELName;
        this.x = x;
        this.y = y;
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
