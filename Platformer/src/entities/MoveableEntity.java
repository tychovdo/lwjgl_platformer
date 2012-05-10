package entities;

import java.awt.Rectangle;

public interface MoveableEntity extends Entity {
	
	public boolean onGround(Entity other) ;
	public boolean onRoof(Entity other) ;
	
	public boolean top_intersects(Entity other) ;
	public boolean left_intersects(Entity other) ;
	public boolean right_intersects(Entity other) ;
	public boolean bottom_intersects(Entity other) ;
	
	public double getDX();
	public double getDY();
	public void setDX(double dx);
	public void setDY(double dy);
}

// AbstractEntity.java

