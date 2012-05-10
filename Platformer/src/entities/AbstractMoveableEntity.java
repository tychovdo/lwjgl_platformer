package entities;

import java.awt.Rectangle;

public abstract class AbstractMoveableEntity extends AbstractEntity implements MoveableEntity {

	protected double dx ,dy;
	
	
	protected Rectangle hitbox_left = new Rectangle();
	protected Rectangle hitbox_top = new Rectangle();
	protected Rectangle hitbox_right = new Rectangle();
	protected Rectangle hitbox_bottom = new Rectangle();
	
	
	public AbstractMoveableEntity(double x, double y, double width,
			double height) {
		super(x, y, width, height);
		this.dx = 0;
		this.dy = 0;
	}
	
	@Override
	public void update(int delta) {
		this.x += delta * dx;
		this.y += delta * dy;
	}
	
	
	
	public boolean onGround(Entity other) {
		Rectangle feet = new Rectangle();
		feet.setBounds((int) (x+(width/3))	, (int) (y-1)	, (int) (width/3)	, (int) (height/8));
		return feet.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
	public boolean onRoof(Entity other) {
		Rectangle head = new Rectangle();
		head.setBounds((int) (x+(width/3))	, (int) (y+height+1)	, (int) (width/3)	, (int) 1);
		return head.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
	public boolean top_intersects(Entity other) {
		hitbox_top.setBounds((int) (x+(width/3))	, (int) (y+(height/8*7))	, (int) (width/3)	, (int) (height/8));
		return hitbox_top.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
	public boolean left_intersects(Entity other) {
		hitbox_left.setBounds((int) (x)				, (int) (y+(height/8))		, (int) (width/2)	, (int) (height/8*6));
		return hitbox_left.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
	public boolean right_intersects(Entity other) {
		hitbox_right.setBounds((int) (x+(width/2))				, (int) (y+(height/8))		, (int) (width/2)	, (int) (height/8*6));
		return hitbox_right.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
	public boolean bottom_intersects(Entity other) {
		hitbox_bottom.setBounds((int) (x+(width/3))	, (int) (y)	, (int) (width/3)	, (int) (height/8));
		return hitbox_bottom.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
	
	
	public double getDX() {
		return dx;
	}
	public double getDY() {
		return dy;
	}
	public void setDX(double dx) {
		this.dx = dx;
	}
	public void setDY(double dy) {
		this.dy = dy;
	}
	
}


