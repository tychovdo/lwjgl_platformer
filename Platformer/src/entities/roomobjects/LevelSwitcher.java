package entities.roomobjects;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glRectd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2i;

import org.newdawn.slick.opengl.Texture;

import entities.AbstractEntity;

public class LevelSwitcher extends AbstractEntity {
	protected int nextLevel;
	
	public LevelSwitcher() {
		
	}
	public LevelSwitcher(double x, double y, int nextLevel) {
		super(x, y, 32, 32);
		this.nextLevel = nextLevel;
		
		entity_type = 2;
		//System.out.println("Created @ "+x+","+y);
	}
	
	
	public void draw(Texture texture) {
		texture.bind();
		glBegin(GL_QUADS);
	        glTexCoord2f(0, 0);
	        glVertex2i((int) x, (int) (y+height)); // Bottom-left
	        glTexCoord2f(1, 0);
	        glVertex2i((int) (x+width), (int) (y+height)); // Bottom-right
	        glTexCoord2f(1, 1);
	        glVertex2i((int) (x+width), (int) y); // Upper-right
	        glTexCoord2f(0, 1);
	        glVertex2i((int) x, (int) y); // Upper-left
	    glEnd();
	}
	@Override
	public void draw() {
		glRectd(x, y, x + width, y + height);
	}
	
	public int getNextLevel() {
		return nextLevel;
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
	}

}
