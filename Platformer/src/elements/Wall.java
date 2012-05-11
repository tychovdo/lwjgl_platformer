package elements;

import static org.lwjgl.opengl.GL11.glRectd;
import entities.AbstractEntity;

public class Wall extends AbstractEntity {
	
	public Wall(double x, double y, double width, double height) {
		super(x, y, width, height);
		entity_type = 1;
	}
	
	@Override
	public void draw() {
		glRectd(x, y, x + width, y + height);
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub

	}

}
