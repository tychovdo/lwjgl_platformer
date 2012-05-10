package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import levels.LevelLoader;

import org.lwjgl.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.newdawn.slick.SlickException;
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
	private int levelPreviousFrame = 1337;
	
	public int level;
	private int playerAmount;
	
	private List<Texture> textures = new ArrayList<Texture>();
	private List<Player> players = new ArrayList<Player>();
	private List<Wall> walls = new ArrayList<Wall>();
	

	
	public Engine() {

		init();
		while (gameloop) {

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
	
	private void input() { //external control settings, dynamic player amount
		
		//Movement X-axis
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			players.get(0).setDX(-0.35);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			players.get(0).setDX(0.35);
		} else {
			players.get(0).setDX(0);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			players.get(1).setDX(-0.35);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			players.get(1).setDX(0.35);
		} else {
			players.get(1).setDX(0);
		}
		
		//Movement Y-axis
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			for(Wall wall : walls) {
				//if (players.get(0).getDY()==0) {
				if(players.get(0).onGround(wall) || players.get(0).onGround(players.get(1))) {
					players.get(0).setDY(0.8);
				}
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			for(Wall wall : walls) {
				//if (players.get(0).getDY()==0) {
				if(players.get(0).onRoof(wall)) {
					players.get(0).setDY(-0.8);
				}
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			for(Wall wall : walls) {
				//if (players.get(0).getDY()==0) {
				if(players.get(1).onGround(wall) || players.get(0).onGround(players.get(1))) {
					players.get(1).setDY(0.8);
				}
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			for(Wall wall : walls) {
				//if (players.get(0).getDY()==0) {
				if(players.get(1).onRoof(wall)) {
					players.get(1).setDY(-0.8);
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
	private void setUpTimer() {
		startTime = getTime();
	}
	
	private void setUpSettings() {
		//load default settings
		try {
            //load a properties file
			URL url = this.getClass().getResource("/config/defaultSettings");
			FileInputStream propfile = new FileInputStream(url.getFile());	
			Properties prop = new Properties();
    		prop.load(propfile);
            //get the property value and load it
            level = Integer.parseInt(prop.getProperty("first_level"));
    		playerAmount = Integer.parseInt(prop.getProperty("amount_of_players"));
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
	private void setUpDisplay() {
		//initiate display
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle("LWJGL Platformer");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	private void setUpOpenGL() {
		//initiate opengl
		glDisable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glOrtho(0, WIDTH, 0, HEIGHT, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glViewport(0, 0, WIDTH, HEIGHT);	
	}
	private void setUpResources() {
		//load character textures
		for(int i=0;i<playerAmount;i++) {
			textures.add(loadTexture("/sprites/char"+i+".png"));
		}
		
	}
	private void setUpEntities() {
		//load entities
		
		//load players
		for(int i=0;i<playerAmount;i++) {
			players.add(new Player(i*100+100,100,32,32));	
		}
	}
    
	private Texture loadTexture(String filename) {
    	//return texture from filename
        try {
                return TextureLoader.getTexture("PNG", new FileInputStream(new File("res/" + filename)));
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return null;
    }
    


	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		
		renderLevel();
		
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
		//color variation: 		//glColor3f(0.6f,1f,0.3f);;
		for(int i=0;i<playerAmount;i++) {
			glColor3d(players.get(i).getRed(),players.get(i).getGreen(),players.get(i).getRed());
			players.get(i).draw(textures.get(i),getStep(50, 5),gravplier);	
		}


		
	}
	private void renderLevel() {
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
		for (int i=0;i<playerAmount;i++) {
			players.get(i).update(delta);	
			players.get(i).setAY(GRAVITY*gravplier);
		}

		
		
		if(keyTimer>0) {
			keyTimer--;
		}
		
		if(!shiftReady) {
			if(((gravplier==1) && (players.get(0).onFeet)) || ((gravplier==-1) && (!players.get(0).onFeet))) {
				shiftReady = true;
				System.out.println("Ready to shift");
			}
		}
		for(int i=0;i<playerAmount;i++) {
			for (Wall wall : walls) {	
				while(players.get(i).intersect_dir(wall)!=0) {
					switch(players.get(i).intersect_dir(wall)) {
						case 1:
							players.get(i).setDX(0);
							players.get(i).setAX(0);
							players.get(i).setX(players.get(i).getX()+1);
						break;
						case 2:
							players.get(i).setDY(0);
							players.get(i).setAY(0);
							players.get(i).setY(players.get(i).getY()-1);
						break;
						case 3:
							players.get(i).setDX(0);
							players.get(i).setAX(0);
							players.get(i).setX(players.get(i).getX()-1);
						break;
						case 4:
							players.get(i).setDY(0);
							players.get(i).setAY(0);
							players.get(i).setY(players.get(i).getY()+1);
						break;
					}
				}
			}
		}
	}


	public static void main(String[] args) {
		new Engine();
	}
}

