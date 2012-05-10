package elements;

import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import entities.AbstractGravityMoveableEntity;
import entities.Entity;
import static org.lwjgl.opengl.GL11.*;

public class Player extends AbstractGravityMoveableEntity {
	
	
	protected Rectangle hitbox_left = new Rectangle();
	protected Rectangle hitbox_top = new Rectangle();
	protected Rectangle hitbox_right = new Rectangle();
	protected Rectangle hitbox_bottom = new Rectangle();
	
	public boolean onFeet = true;
	
	public Player(double x, double y, double width, double height) {
		super(x, y, width, height);
		

	}
	public void draw(float red, float green, float blue) {
		glColor3f(red, green, blue);
		glRecti((int) x, (int)y, (int)x + (int) width, (int) y + (int) height);
	}
	
	public void draw(Texture texture, int step, float gravplier) {
		//test
		texture.bind();
		if(gravplier==1) {
			glBegin(GL_QUADS);
		        glTexCoord2f(0, 0);
		        glVertex2i((int) x, (int) y); // Upper-left
		        glTexCoord2f(1, 0);
		        glVertex2i((int) (x+width), (int) y); // Upper-right
		        glTexCoord2f(1, 1);
		        glVertex2i((int) (x+width), (int) (y+height+2*(-1+step))); // Bottom-right
		        glTexCoord2f(0, 1);
		        glVertex2i((int) x, (int) (y+height+2*(-1+step))); // Bottom-left
		    glEnd();
		} else {
		glBegin(GL_QUADS);
	        glTexCoord2f(0, 0);
	        glVertex2i((int) x, (int) y+2*(-1+step)); // Upper-left
	        glTexCoord2f(1, 0);
	        glVertex2i((int) (x+width), (int) y+2*(-1+step)); // Upper-right
	        glTexCoord2f(1, 1);
	        glVertex2i((int) (x+width), (int) (y+height)); // Bottom-right
	        glTexCoord2f(0, 1);
	        glVertex2i((int) x, (int) (y+height)); // Bottom-left
	    glEnd();
		}
	}
	
	@Override
	public void draw() {
		
		//draw shape
		glColor3f(0f, 0f, 1f);
		glRecti((int) x, (int)y, (int)x + (int) width, (int) y + (int) height);
		
		//draw print
//		glColor3f(1f, 0f, 0f);
//		glBegin(GL_TRIANGLES);
//		glVertex2d(x, y);
//		glVertex2d(x+width, y);
//		glVertex2d(x+(width/2), y+20);
//		glEnd();
//		
//		glColor3f(1f, 1f, 0f);
//		glBegin(GL_LINES);
//		glVertex2d(x, y);
//		glVertex2d(x+width, y);
//		glEnd();
//		
//		glColor3f(1f, 1f, 0f);
//		glBegin(GL_LINES);
//		glVertex2d(x, y+1);
//		glVertex2d(x+(width/2), y+1);
//		glEnd();
		
		//draw hitboxes
//		glColor3f(0f,1f,0f);
//		glRectd(hitbox_left.x, hitbox_left.y, hitbox_left.x + hitbox_left.width, hitbox_left.y + hitbox_left.height);
//		glRectd(hitbox_top.x, hitbox_top.y, hitbox_top.x + hitbox_top.width, hitbox_top.y + hitbox_top.height);
//		glRectd(hitbox_right.x, hitbox_right.y, hitbox_right.x + hitbox_right.width, hitbox_right.y + hitbox_right.height);
//		glRectd(hitbox_bottom.x, hitbox_bottom.y, hitbox_bottom.x + hitbox_bottom.width, hitbox_bottom.y + hitbox_bottom.height);
		
	}
	
	public int intersect_dir(Entity other) {
		//return direction of collision
		//no collision = 0;left=1;top=2;right=3;bottom=4
		int out = 0;
		if(bottom_intersects(other)) {
			out = 4;
			onFeet=true;
		} else if (top_intersects(other)) {
			out = 2;
			onFeet=false;
		} else if (left_intersects(other)) {
			out = 1;
		} else if (right_intersects(other)) {
			out = 3;
		}
		return out;
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

}
