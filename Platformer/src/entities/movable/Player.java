package entities.movable;
import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import loaders.SystemInfo;

import org.newdawn.slick.opengl.Texture;

import entities.AbstractAcceleratedEntity;
import entities.Entity;

public class Player extends AbstractAcceleratedEntity {
	public boolean onFeet = true;
	float red,green,blue;
	public String name;
	public boolean dead = false;
	public long deadTime;
	public boolean exists = true;
	public boolean reachedExit = false;
	
	public Player(double x, double y, double width, double height) {
		super(x, y, width, height);
		setMAX_DY(1);
		
		//set random color:
		Random random = new Random();
		red=0.5f+(random.nextFloat()/2);
		green=0.5f+(random.nextFloat()/2);
		blue=0.8f;
		entity_type = 10;
	}
	
	
	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}
	
	public void draw(Texture texture, int step, float gravplier) {
		if(dead) {
			if(step>1) {
				glColor3f(1f,0f,0f);
			}	
		} else {
			glColor3d(red,green,blue);
		}
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
	public void kill() {
		dead = true;
		deadTime = SystemInfo.getTime();
	}
	public void revive() {
		dead = false;
	}
}
