package entities;

public abstract class AbstractGravityMoveableEntity extends
		AbstractMoveableEntity implements GravityMoveableEntity {

	
	protected double ax, ay;
	protected double max_dy;
	
	public AbstractGravityMoveableEntity(double x, double y, double width, double height) {
		super(x, y, width, height);
	}

	@Override
	public void update(int delta) {
		dx = dx + ax;
		if((dy+ax)>-max_dy && (dy+ax)<max_dy) {
			dy = dy + ay;
		}
		this.x += delta * dx;
		this.y += delta * dy;
	}
	
	public double getAX() {
		
		return ax;
	}

	public double getAY() {
		
		return ay;
	}
	public double getMAX_AY() {
		return max_dy;
	}

	public void setAX(double ax) {
		this.ax = ax;
	}

	public void setAY(double ay) {
		this.ay = ay;
	}
	public void setMAX_DY(double max_dy) {
		this.max_dy = max_dy;
	}


}
