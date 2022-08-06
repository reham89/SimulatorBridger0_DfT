package org.cloudbus.cloudsim.edge.core.edge;

public class Mobility {

	public boolean movable;
	public double velocity;
	public double totalMovingDistance;
	public MovingRange range;
	public double signalRange;
	public Location location;

	public Mobility(Location location) {
		super();
		this.location = new Location(location.x,location.y,location.z);
	}

    public Mobility(ConfiguationEntity.MobilityEntity moto) {
		this(moto.getLocation());
		movable = moto.isMovable();
		if (moto.isMovable()) {
			range = new MovingRange(moto.getRange().beginX,
									moto.getRange().endX,
					moto.getRange().beginY,
					moto.getRange().endY);
			signalRange = moto.getSignalRange();
			velocity = moto.getVelocity();
		}
    }

    public static class MovingRange{
		public int beginX, beginY;
		public int endX, endY;

		public MovingRange() {
			super();
		}

		public MovingRange(int beginX, int endX, int beginY, int endY) {
			super();
			this.beginX = beginX;
			this.endX = endX;
			this.beginY = beginY;
			this.endY = endY;
		}

	}

	public static class Location{
		public double x;
		public double y;
		public double z;

		public Location(double x,double y,double z) {
			this.x = x;
			this.y = y;
			this.z = z;

		}
	}
}
