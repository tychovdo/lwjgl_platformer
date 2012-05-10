package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import levels.LevelLoader;

import org.lwjgl.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import elements.Player;
import elements.Wall;

import static org.lwjgl.opengl.GL11.*;

public class Engine {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final double GRAVITY = -0.035;
	
	private int gravplier = 1; //gravity multiplier (gravplier=-1 on reversed gravity)
	
	private boolean gameloop = true;
	private boolean shiftReady = true;
	private int keyTimer = 0;
	
	private long startTime; 
	
	private long lastFrame;
	
	private Texture tex_player; 
	private Player player;
	private Player player2;
	
	public int levelPreviousFrame = 1337;
	public int level;
	
	private List<Wall> walls = new ArrayList<Wall>();
	
	public Engine() {
		startTime = getTime();
		
		init();
		while (gameloop) {
			loadLevel();
			input();	
			logic(getDelta());
			render();		
			
			

			Display.update();
			Display.sync(60);
			
			
			if (Display.isCloseRequested()) {
				gameloop = false;
			}
		}
		Display.destroy();
	}
	


	private void init() {
		setUpSettings();
		setUpDisplay();
		setUpOpenGL();
		setUpResources();
		setUpEntities();
		setUpTimer();

	}
	
	private void loadLevel() {
		if(levelPreviousFrame!=level) {
			walls.clear();
			System.out.println(level);
			int dws = 20; //default wall size
			//borders
			walls.add(new Wall(0,0,WIDTH,dws)); //floor
			walls.add(new Wall(0,0,dws,HEIGHT)); //left wall
			walls.add(new Wall(WIDTH-dws,0,dws,HEIGHT)); //right wal
			walls.add(new Wall(0,HEIGHT-dws,WIDTH, dws)); //roof
			
			//platforms
			walls.addAll(LevelLoader.load(level));
			levelPreviousFrame=level;
			//}
		}
		
	}
	
	private void input() {
		
		//Movement X-axis
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			player.setDX(-0.35);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			player.setDX(0.35);
		} else {
			player.setDX(0);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			player2.setDX(-0.35);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			player2.setDX(0.35);
		} else {
			player2.setDX(0);
		}
		
		//Movement Y-axis
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			for(Wall wall : walls) {
				//if (player.getDY()==0) {
				if(player.onGround(wall)) {
					player.setDY(0.8);
				}
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			for(Wall wall : walls) {
				//if (player.getDY()==0) {
				if(player.onRoof(wall)) {
					player.setDY(-0.8);
				}
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			for(Wall wall : walls) {
				//if (player.getDY()==0) {
				if(player2.onGround(wall)) {
					player2.setDY(0.8);
				}
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			for(Wall wall : walls) {
				//if (player.getDY()==0) {
				if(player2.onRoof(wall)) {
					player2.setDY(-0.8);
				}
			}
		}
		
		//Special keys
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if(shiftReady) {
				gravplier = -gravplier;
				System.out.println("gravplier: "+gravplier);
				shiftReady=false;
				
			}
		}
		
		//Cheats:
		if (Keyboard.isKeyDown(Keyboard.KEY_O)&&keyTimer<1) {
			level--;
			keyTimer=15;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_P)&&keyTimer<1) {
			level++;
			keyTimer=15;
		}
		
	}


	private long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	private int getDelta() {
		long currentTime = getTime();
		int delta = (int) (currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}
	private void setUpSettings() {
		level = 3;
	}
	private void setUpDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle("Pong");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	private void setUpOpenGL() {
		glDisable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glOrtho(0, WIDTH, 0, HEIGHT, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glViewport(0, 0, WIDTH, HEIGHT);
		
	}
	private void setUpResources() {
		tex_player = loadTexture("player");
	}
	private void setUpEntities() {
		player = new Player(100,100,32,32);
		player.setMAX_DY(1);
		player2 = new Player(200,100,32,32);
		player2.setMAX_DY(1);
	}
    private Texture loadTexture(String key) {
        try {
                return TextureLoader.getTexture("PNG", new FileInputStream(new File("res/" + key + ".png")));
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return null;
    }
    
	private void setUpTimer() {
		lastFrame = getTime();
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		
		//draw Non-Textured entities:
		glDisable(GL_TEXTURE_2D);
		
		glColor3f(0.1f,0.2f,0.6f);
		glRectd(0, 0, WIDTH, HEIGHT);
		
		glColor3f(0f, 0f, 0.2f);
		for (Wall wall : walls) {
			wall.draw();
		}

		//draw Textured entities:
		glEnable(GL_TEXTURE_2D);
		
		glColor3f(1f,1f,1f);
		player.draw(tex_player,getStep(50, 5),gravplier);
		
		glColor3f(1f,0.2f,1f);
		player2.draw(tex_player,getStep(50, 5),gravplier);
		

		
	}
	
	private int getStep(int speed, int amount) {
		int x = (int) (((getTime()-startTime)/speed)%amount);
		if(x>(amount/2)) {
			x = x - (((x-(amount/2))*2)-1);
			return x;
		} else {
			return x;
		}
	}
	private void logic(int delta) {
		player.update(delta);
		player2.update(delta);	
		
		player.setAY(GRAVITY*gravplier);
		player2.setAY(GRAVITY*gravplier);
		
		
		if(keyTimer>0) {
			keyTimer--;
		}
		
		if(!shiftReady) {
			if(((gravplier==1) && (player.onFeet)) || ((gravplier==-1) && (!player.onFeet))) {
				shiftReady = true;
				System.out.println("Ready to shift");
			}
		}
		
		
		//while(player.intersects(wall) | player.intersects(wall2) | player.intersects(wall3)) {
		for (Wall wall : walls) {	
			while(player.intersect_dir(wall)!=0) {
				switch(player.intersect_dir(wall)) {
					case 1:
						player.setDX(0);
						player.setAX(0);
						player.setX(player.getX()+1);
					break;
					case 2:
						player.setDY(0);
						player.setAY(0);
						player.setY(player.getY()-1);
					break;
					case 3:
						player.setDX(0);
						player.setAX(0);
						player.setX(player.getX()-1);
					break;
					case 4:
						player.setDY(0);
						player.setAY(0);
						player.setY(player.getY()+1);
					break;
				}
			}
		}
		for (Wall wall : walls) {	
			while(player2.intersect_dir(wall)!=0) {
				switch(player2.intersect_dir(wall)) {
					case 1:
						player2.setDX(0);
						player2.setAX(0);
						player2.setX(player2.getX()+1);
					break;
					case 2:
						player2.setDY(0);
						player2.setAY(0);
						player2.setY(player2.getY()-1);
					break;
					case 3:
						player2.setDX(0);
						player2.setAX(0);
						player2.setX(player2.getX()-1);
					break;
					case 4:
						player2.setDY(0);
						player2.setAY(0);
						player2.setY(player2.getY()+1);
					break;
				}
			}
		}
//		if(gravplier==1) {
//			player.setY(player.getY()-1);
//		} else {
//			player.setY(player.getY()+1);
//		}
		
		
		
		
	}


	public static void main(String[] args) {
		new Engine();
	}
}

