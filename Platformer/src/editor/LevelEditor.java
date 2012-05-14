package editor;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import loaders.LevelLoader;
import loaders.SystemInfo;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import entities.Entity;
import entities.roomobjects.LevelSpawnpoint;
import entities.roomobjects.LevelSwitcher;
import entities.roomobjects.Spike;
import entities.roomobjects.Wall;

public class LevelEditor {

	// constants:
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	// settings:
	
	// room data
//	private String levelpack;   
	private int level;
	private int levelPreviousFrame = 1337;      //when 'level!=levelPreviousFrame' a new level is asked from server
	private boolean locked;     				//when 'locked=false' only 1 more player is needed to go to next level
	

	// input:
	private int keyTimer = 0;               //a timer to handle keystrokes per second
	private boolean mouseEnabled;
	private int first_x;
	private int first_y;
	int second_x;
	int second_y;
	private int entity_type = 1;
	private boolean mousePressed;
	
	
	// fps settings:
	SystemInfo sysInfo = new SystemInfo();
	private boolean gameloop = true;

	// Game objects:
		// resources:
	private Texture tex_lock;
	private Texture tex_arrow;
	private Texture[] tex_mine = new Texture[2];
//  private Texture tex_coin;
	
	List<Texture> textures = new ArrayList<Texture>();
	private List<Wall> walls = new ArrayList<Wall>();
	private List<LevelSwitcher> levelswitchers = new ArrayList<LevelSwitcher>();
	private List<Spike> spikes = new ArrayList<Spike>();
	LevelSpawnpoint spawn;



	
	public LevelEditor() {
		init();
		
//		try {
//			LevelLoader.writeFileAsBytes("levelpack0/1.lvl", LevelLoader.loadHardcodedlevel(1));
//			LevelLoader.writeFileAsBytes("levelpack0/2.lvl", LevelLoader.loadHardcodedlevel(2));
//			LevelLoader.writeFileAsBytes("levelpack0/3.lvl", LevelLoader.loadHardcodedlevel(3));
//			LevelLoader.writeFileAsBytes("levelpack0/4.lvl", LevelLoader.loadHardcodedlevel(4));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		while (gameloop) {
			
			input();	
			loadLevel();
			logic(sysInfo.getDelta());
			
			render();		
			
			Display.update();
			Display.sync(60);
			
			if (Display.isCloseRequested()) {
				gameloop = false;
			}
		}
		Display.destroy();
		System.exit(0);
	}
	


	private void init() {
		setUpSettings();
		setUpEntities();	
		setUpDisplay();
		setUpOpenGL();
		setUpResources();
		setUpStartTime();
	}
	
	private void input() { //TODO: external control settings, dynamic player amount
			
		// Input:
        if (mouseEnabled || Mouse.isButtonDown(0)) {
            mouseEnabled = true;
            boolean mouseClicked = Mouse.isButtonDown(0);
            if (mouseClicked) {
            	if(!mousePressed) {
            		first_x = Mouse.getX();
            		first_y = Mouse.getY();
            		mousePressed = true;
            	}
            } else {
            	if(mousePressed) {
            		second_x = Mouse.getX();
            		second_y = Mouse.getY();
            		createEntity();
            		mousePressed=false;
            	}
            }
            
        }
        
		if (Keyboard.isKeyDown(Keyboard.KEY_O)&&keyTimer<1) {
			level--;
			keyTimer=15;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_P)&&keyTimer<1) {
			level++;
			keyTimer=15;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_Z)&&keyTimer<1) {
			removeLastEntity();
			keyTimer=15;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)&&keyTimer<1) {
			first_x=0;
			keyTimer=15;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)&&keyTimer<1) {
			first_x=WIDTH;
			keyTimer=15;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_Q)&&keyTimer<1) {
			first_y=0;
			keyTimer=15;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_W)&&keyTimer<1) {
			first_y=HEIGHT;
			keyTimer=15;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)&&keyTimer<1) {
			List<Entity> entities = new ArrayList<Entity>();
			entities.clear();
			entities.addAll(walls);
			entities.addAll(levelswitchers);
			entities.addAll(spikes);
			entities.add(spawn);
			try {
				LevelLoader.writeFileAsBytes("levelpack0/"+level+".lvl", entities);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			keyTimer=15;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_F)&&keyTimer<1) {
			entity_type = 4;
			keyTimer=15;
		}

		
	}

	private void removeLastEntity() {
		switch(entity_type ) {
		case 1:
			if(walls.size()>0) {
				walls.remove(walls.size()-1);
			}
			break;
		case 4:
			if(spikes.size()>0) {
				spikes.remove(spikes.size()-1);
			}
			break;
		}
	}
	private void createEntity() {
		switch(entity_type ) {
		case 1:
			walls.add(new Wall(Math.min(first_x, second_x),Math.min(first_y, second_y),Math.abs(first_x-second_x),Math.abs(first_y-second_y)));
			break;
		case 4:
			int temp = second_x % 32;
			spikes.add(new Spike(Math.min(first_x, second_x),Math.min(first_y, second_y),Math.abs(first_x-second_x)-temp-1,10));
			break;
		}
	}



	private void setUpSettings() {
//		levelpack = "levelpack0";
		level = 1;
	}
	private void setUpDisplay() {
		// initiate display
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle("LWJGL Platformer");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	private void setUpOpenGL() { 
		// initiate OpenGL
		glDisable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glOrtho(0, WIDTH, 0, HEIGHT, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glViewport(0, 0, WIDTH, HEIGHT);	
		glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	private void setUpResources() {
		
		// load object textures
//		tex_coin = loadTexture("coin.png");
		tex_lock = loadTexture("lock.png");
		tex_arrow = loadTexture("arrow.png");
		
		tex_mine[0] = loadTexture("Mine1.png");
		tex_mine[1] = loadTexture("Mine2.png");
	}

	private void setUpEntities() {
		loadLevel();
	}
	private void setUpStartTime() {
		sysInfo.startTime = SystemInfo.getTime();
	}
    
	private Texture loadTexture(String filename) {
    	//return texture, in res/ folder, from filename
        try {
        	//return TextureLoader.getTexture("PNG", new FileInputStream(new File("res/" + filename)));
        	//texture = InternalTextureLoader.get().getTexture(data, filter == FILTER_LINEAR ? SGL.GL_LINEAR : SGL.GL_NEAREST);
        	return TextureLoader.getTexture("PNG", new FileInputStream(new File("res/" + filename)), GL11.GL_NEAREST);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return null;
    }

	private void render() { //TODO: enable 2d in class instead of in render()
		// clear screen
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		// draw Non-Textured entities:
		glDisable(GL_TEXTURE_2D);

			// draw background
		glColor3f(0.1f,0.2f,0.6f);
		glRectd(0, 0, WIDTH, HEIGHT);
		
			// draw walls
		for (Wall wall : walls) {
			wall.draw();
		}
		spawn.draw();


		// draw Textured entities:
		glEnable(GL_TEXTURE_2D);

			// draw levelswitchers
		glColor3f(1f,1f,1f);
		for (LevelSwitcher ls : levelswitchers) {
			if(locked) {
				ls.draw(tex_lock);
			} else {
				ls.draw(tex_arrow);
			}
		}
		
		for (Spike spike : spikes) {
			//spike.draw(tex_mine[getStep(300,2)]);
			spike.draw(tex_mine[0]);
		}
		
		glDisable(GL_TEXTURE_2D);
		if(mousePressed) {
			GL11.glColor4f(1f,1f,1f,0.5f);
			glRectd(first_x,first_y,Mouse.getX(),Mouse.getY());
		}
		
	}
	@SuppressWarnings("unchecked")
	private void loadLevel() {
		if(levelPreviousFrame!=level) { 
			walls.clear();
//			int dws = 20; //default wall size
//			walls.add(new Wall(0,0,WIDTH,dws)); 
//			walls.add(new Wall(0,0,dws,HEIGHT)); 
//			walls.add(new Wall(WIDTH-dws,0,dws,HEIGHT)); 
//			walls.add(new Wall(0,HEIGHT-dws,WIDTH, dws));
			walls.addAll((Collection<? extends Wall>) LevelLoader.load("levelpack0", level, 1));
			
			levelswitchers.clear();
			levelswitchers.addAll((Collection<? extends LevelSwitcher>) LevelLoader.load("levelpack0",level,2));
			if(levelswitchers.size()==0) {
				System.out.println("No levelswitcher set on this map...");
			}
			
			spikes.clear();
			spikes.addAll((Collection<? extends Spike>) LevelLoader.load("levelpack0",level,4));
			
			List<LevelSpawnpoint> temp = new ArrayList<LevelSpawnpoint>();
			temp.clear();
			temp.addAll((Collection<? extends LevelSpawnpoint>) LevelLoader.load("levelpack0",level,3));
			if(temp.size()>0) {
				spawn = temp.get(0);
			} else {
				spawn = new LevelSpawnpoint(150,150);
				System.out.println("No spawn point set on this map...");
			}
			levelPreviousFrame=level;
		}
		
	}
	

	private void logic(int delta) {
		// handle cooldown for (cheat)buttons
		if(keyTimer>0) {
			keyTimer--;
		}
	}


	public static void main(String[] args) {
		new LevelEditor();
	}
}

