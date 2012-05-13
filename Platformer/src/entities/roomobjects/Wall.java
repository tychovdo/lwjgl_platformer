package entities.roomobjects;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glRectd;
import entities.AbstractEntity;

public class Wall extends AbstractEntity {
	
	public Wall(double x, double y, double width, double height) {
		super(x, y, width, height);
		entity_type = 1;
	}
	public Wall() {
		
	}

	
	@Override
	public void draw() {
		
		//full rectangle:
		//glColor3f(0f, 0f, 0.2f);
		//glRectd(x, y, x + width, y + height);
		
		//bordered rectangle:
		glColor3f(0f, 0f, 0.2f);
		glRectd(x,y,x+width,y+4);
		glRectd(x,y+height-4,x+width,y+height);
		glRectd(x,y+4,x+4,y+height-4);
		glRectd(x+width-4,y+4,x+width,y+height-4);
		glColor3f(0.2f,0.5f,1f);
		glRectd(x+4,y+4,x+width-4,y+height-4);
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub

	}

}
