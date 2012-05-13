package entities.roomobjects;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glRectd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2i;

import org.newdawn.slick.opengl.Texture;

import entities.AbstractEntity;

public class Spike extends AbstractEntity {
	
	public Spike(double x, double y, double width, double height) {
		super(x, y, width, height);
		entity_type = 4;
	}
	public Spike() {
		
	}

	public void draw(Texture texture) {
		texture.bind();

		for(int i=0;i<(width);i+=32) {
			int bordersize = 3;
			switch((int) height % 10) {
			case 0: //on ground:
				glEnable(GL_TEXTURE_2D);
				glColor3f(1f,1f,1f);
				glBegin(GL_QUADS);
			        glTexCoord2f(0, 0);
			        glVertex2i((int) x+i, (int) (y+32)); 
			        glTexCoord2f(1, 0);
			        glVertex2i((int) (x+i+32), (int) (y+32)); 
			        glTexCoord2f(1, 1);
			        glVertex2i((int) (x+i+32), (int) y); 
			        glTexCoord2f(0, 1);
			        glVertex2i((int) x+i, (int) y); 
			    glEnd();
			    
				glDisable(GL_TEXTURE_2D);
				glColor3f(0f, 0f, 0.2f);
				glRectd(x-bordersize,y,x,y+28);
				glRectd(x+width,y,x+width+bordersize,y+28);

			    break;
			case 1: //on roof:
				glEnable(GL_TEXTURE_2D);
				glColor3f(1f,1f,1f);
				glBegin(GL_QUADS);
			        glTexCoord2f(0, 1);
			        glVertex2i((int) x+i, (int) (y+32)); 
			        glTexCoord2f(1, 1);
			        glVertex2i((int) (x+i+32), (int) (y+32)); 
			        glTexCoord2f(1, 0);
			        glVertex2i((int) (x+i+32), (int) y); 
			        glTexCoord2f(0, 0);
			        glVertex2i((int) x+i, (int) y); 
		        glEnd();
		        
				glDisable(GL_TEXTURE_2D);
				glColor3f(0f, 0f, 0.2f);
				glRectd(x-bordersize,y+4,x,y+4+28);
				glRectd(x+width,y+4,x+width+bordersize,y+4+28);
		    break;
			}
		}

	}
	
	@Override
	public void draw() {
		//full rectangle:
		glColor3f(1f, 0f, 0f);
		glRectd(x, y, x + width, y + height);
		
		//bordered rectangle:
//		glColor3f(0f, 0f, 0.2f);
//		glRectd(x,y,x+width,y+4);
//		glRectd(x,y+height-4,x+width,y+height);
//		glRectd(x,y+4,x+4,y+height-4);
//		glRectd(x+width-4,y+4,x+width,y+height-4);
//		glColor3f(0.2f,0.5f,1f);
//		glRectd(x+4,y+4,x+width-4,y+height-4);
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub

	}

}
