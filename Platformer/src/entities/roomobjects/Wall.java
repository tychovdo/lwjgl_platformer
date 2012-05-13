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
		glColor3f(0f, 0f, 0.2f);
		glRectd(x, y, x + width, y + height);
		
		//bordered rectangle:
//		int bordersize=4;
//		glColor3f(0f, 0f, 0.2f);
//		glRectd(x,y,x+width,y+bordersize);
//		glRectd(x,y+height-bordersize,x+width,y+height);
//		glRectd(x,y+bordersize,x+bordersize,y+height-bordersize);
//		glRectd(x+width-bordersize,y+bordersize,x+width,y+height-bordersize);
//		glColor3f(0.2f,0.5f,1f);
//		glRectd(x+bordersize,y+bordersize,x+width-bordersize,y+height-bordersize);
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub

	}

}
