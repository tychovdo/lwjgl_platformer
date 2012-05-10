package elements;

import java.awt.Color;
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import entities.AbstractGravityMoveableEntity;
import entities.Entity;
import static org.lwjgl.opengl.GL11.*;

public class Player extends AbstractGravityMoveableEntity {
	
	//player test

	public boolean onFeet = true;
	protected Color color;
	
	
	public Player(double x, double y, double width, double height) {
		super(x, y, width, height);
		setMAX_DY(1);
		color = new Color(1f,1f,1f);
	}
	
	
	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
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
		        glVertex2i((int) x, (int) (y+height+2*(-1+step))); // Bottom-left
		        glTexCoord2f(1, 0);
		        glVertex2i((int) (x+width), (int) (y+height+2*(-1+step))); // Bottom-right
		        glTexCoord2f(1, 1);
		        glVertex2i((int) (x+width), (int) y); // Upper-right
		        glTexCoord2f(0, 1);
		        glVertex2i((int) x, (int) y); // Upper-left



		    glEnd();
		} else {
		glBegin(GL_QUADS);
	        glTexCoord2f(0, 1);
	        glVertex2i((int) x, (int) (y+height)); // Bottom-left
	        glTexCoord2f(1, 1);
	        glVertex2i((int) (x+width), (int) (y+height)); // Bottom-right
	        glTexCoord2f(1, 0);
	        glVertex2i((int) (x+width), (int) y+2*(-1+step)); // Upper-right
	        glTexCoord2f(0, 0);
	        glVertex2i((int) x, (int) y+2*(-1+step)); // Upper-left



	    glEnd();
		}
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


	public int getRed() {
		return color.getGreen();
	}
	public int getGreen() {
		return color.getGreen();
	}
	public int getBlue() {
		return color.getGreen();
	}
	public void setColor(float red, float green, float blue) {
		color = new Color(red,green,blue);
	}
	
	
	
	//Collision detection methods
	


}
