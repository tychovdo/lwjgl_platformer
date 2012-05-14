package entities.roomobjects;

import static org.lwjgl.opengl.GL11.*;

import entities.AbstractEntity;
public class LevelSpawnpoint extends AbstractEntity {
	
	public LevelSpawnpoint(int x, int y) {
		this.x = x;
		this.y = y;
		entity_type = 3;
	}
	
	@Override
	public void draw() {
		glColor4f(1f,0f,0f,0.5f);
		glRectd(x,y,x+32,y+32);
	}
	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}
}
