package uk.ncl.giacomobergami.SumoOsmosisBridger.traffic_orchestrator;


public class TopKConnectionsConfiguration {
    public int top_k = -1;
    public double squaredDistance = -1.0;
    public int getTop_k() {
        return top_k;
    }
    public void setTop_k(int top_k) {
        this.top_k = top_k;
    }
    public double getSquaredDistance() {
        return squaredDistance;
    }
    public void setSquaredDistance(double squaredDistance) {
        this.squaredDistance = squaredDistance;
    }
}
