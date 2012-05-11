package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import levels.LevelLoader;

import network.LevelRequest;
import network.LevelResponse;
import network.SomeRequest;
import network.posRequest;
import network.posResponse;

import org.lwjgl.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import elements.LevelSwitcher;
import elements.Player;
import elements.Wall;

import static org.lwjgl.opengl.GL11.*;

public class Engine {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final double GRAVITY = -0.035;
	
	public String server_ip;
	public int server_port1;
	public int server_port2;
	
	public String levelpack;
	public int level;
	public int playerAmount;
	
	public int myID;
	
	private int gravplier = 1; //gravity multiplier (gravplier=-1 on reversed gravity)
	private boolean gpCheck = true;
	
	private boolean gameloop = true;
	private boolean shiftReady = true;
	private int keyTimer = 0;
	
	private long startTime; 
	private long lastFrame;
	private int levelPreviousFrame = 1337;
	

	//private Texture tex_coin;
	private Texture tex_arrow;
	
	private List<Texture> textures = new ArrayList<Texture>();
	private List<Player> players = new ArrayList<Player>();
	private List<Wall> walls = new ArrayList<Wall>();
	private List<LevelSwitcher> levelswitchers = new ArrayList<LevelSwitcher>();

	Client client;
	Kryo kryo;
	
	public Engine() {
		
		
		
//		try {
//			LevelLoader.writeFileAsBytes("levelpack0/1.lvl", LevelLoader.loadHardcodedlevel(1));
//			LevelLoader.writeFileAsBytes("levelpack0/2.lvl", LevelLoader.loadHardcodedlevel(2));
//			LevelLoader.writeFileAsBytes("levelpack0/3.lvl", LevelLoader.loadHardcodedlevel(3));
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		
		init();
		while (gameloop) {
			
			input();	
			loadLevel();
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
		setUpNetworking();
		setUpDisplay();
		setUpOpenGL();
		setUpResources();
		setUpEntities();
		setUpTimer();
	}
	
	private void input() { //external control settings, dynamic player amount
		
		//Movement X-axis
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			players.get(myID).setDX(-0.35);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			players.get(myID).setDX(0.35);
		} else {
			players.get(myID).setDX(0);
		}
		//Movement Y-axis
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			for(Wall wall : walls) {
				//if (players.get(myID).getDY()==0) {
				if(players.get(myID).onGround(wall)) {
					players.get(myID).setDY(0.8);
				}
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			for(Wall wall : walls) {
				//if (players.get(myID).getDY()==0) {
				if(players.get(myID).onRoof(wall)) {
					players.get(myID).setDY(-0.8);
				}
			}
		}
		
		//Special keys
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if(shiftReady) {
				SomeRequest request = new SomeRequest();
				request.gravplier = -gravplier;
				client.sendTCP(request);
				shiftReady=false;
				gpCheck = false;
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
		if (Keyboard.isKeyDown(Keyboard.KEY_U)) {

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
		//load serverdata from inputbox
		server_ip=JOptionPane.showInputDialog(null,"Server IP:");
		//server_port=JOptionPane.showInputDialog(null,"Server Port:  ");

		//load default settings from file
		try {
            //load a properties file
			URL url = this.getClass().getResource("/config/defaultSettings");
			FileInputStream propfile = new FileInputStream(url.getFile());	
			Properties prop = new Properties();
    		prop.load(propfile);
    		
            //get the property value and load it
    		server_port1 = Integer.parseInt(prop.getProperty("server_port1"));
    		server_port2 = Integer.parseInt(prop.getProperty("server_port2"));
    		levelpack = prop.getProperty("level_pack");
            level = Integer.parseInt(prop.getProperty("first_level"));
    		playerAmount = Integer.parseInt(prop.getProperty("amount_of_players"));
    		System.out.println("======== Loading game ========");
    		System.out.println("Server IP:          " + server_ip);
    		System.out.println("Server Port 1:      " + server_port1);
    		System.out.println("Server Port 2:      " + server_port2);
    		System.out.println("Levelpack:          " + levelpack);
    		System.out.println("First level:        " + level);
    		System.out.println("Amount of players:  " + playerAmount);
    		
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
	private void setUpNetworking() {
		client = new Client();
		
		kryo = client.getKryo();
		
		Kryo kryo = client.getKryo();
		kryo.register(SomeRequest.class);
		kryo.register(posRequest.class);
		kryo.register(posResponse.class);
		kryo.register(LevelRequest.class);
		kryo.register(LevelResponse.class);
		
		
		client.start();
		try {
			client.connect(5000, server_ip, server_port1, server_port2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		client.addListener(new Listener() {
			   public void received (Connection connection, Object object) {
			      if (object instanceof posResponse) {
				        posResponse response = (posResponse)object;
				        if(response.player_id!=myID) {
					        players.get(response.player_id).setX(response.x);
					        players.get(response.player_id).setY(response.y);
					        if(gravplier!=response.gravplier) {
					        	gravplier = response.gravplier;
					        	gpCheck = true;
					        }
				        }
				  }
			   }
			});

		
		myID = client.getID() - 1;
		
		
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
		//tex_coin = loadTexture("coin.png");
		tex_arrow = loadTexture("arrow.png");
		
		
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
		
		//draw Non-Textured entities:
		glDisable(GL_TEXTURE_2D);

			//draw background
		glColor3f(0.1f,0.2f,0.6f);
		glRectd(0, 0, WIDTH, HEIGHT);
		
			//draw walls
		glColor3f(0f, 0f, 0.2f);
		for (Wall wall : walls) {
			wall.draw();
		}

		//draw Textured entities:
		glEnable(GL_TEXTURE_2D);
		
			//draw players
		for(int i=0;i<playerAmount;i++) {
			players.get(i).draw(textures.get(i),getStep(50, 5),gravplier);	
		}
			//draw levelswitchers
		for (LevelSwitcher ls : levelswitchers) {
			ls.draw(tex_arrow);
		}

//test
		
	}
	@SuppressWarnings("unchecked")
	private void loadLevel() {
		if(levelPreviousFrame!=level) {
			walls.clear();
			int dws = 20; //default wall size
			
				//borders
			walls.add(new Wall(0,0,WIDTH,dws)); //floor
			walls.add(new Wall(0,0,dws,HEIGHT)); //left wall
			walls.add(new Wall(WIDTH-dws,0,dws,HEIGHT)); //right wal
			walls.add(new Wall(0,HEIGHT-dws,WIDTH, dws)); //roof
			
				//platforms
			walls.addAll((Collection<? extends Wall>) LevelLoader.load(levelpack, level, 1));
			
			
				//bonusses
			levelswitchers.clear();
			levelswitchers.addAll((Collection<? extends LevelSwitcher>) LevelLoader.load("levelpack0",level,2));
			
			levelPreviousFrame=level;
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
		
		
		
			//update player location and add gravity acceleration
		
		for (int i=0;i<playerAmount;i++) {
			players.get(i).update(delta);	
			players.get(i).setAY(GRAVITY*gravplier);
		}

		
			//cooldown for cheatbutton
		if(keyTimer>0) {
			keyTimer--;
		}
		
			//activate shiftReady if player_0 lands on his feet. (or head if gravity is reversed)
		if((!shiftReady) && (gpCheck)) {
			if(((gravplier==1) && (players.get(0).onFeet)) || ((gravplier==-1) && (!players.get(0).onFeet))) {
				shiftReady = true;
				System.out.println("Ready to shift gravity");
			}
		}
			//collision detection 
		for(Player player : players) {
			for (Wall wall : walls) { //collision (players ==> walls)	
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
			for (LevelSwitcher ls : levelswitchers) {
				if(player.intersects(ls)) {
					level = ls.getNextLevel();
					System.out.println("Next level: " + ls.getNextLevel());
				}
			}
		}
		posRequest request = new posRequest();
		request.player_id = myID; 
		request.x = players.get(myID).getX();
		request.y = players.get(myID).getY();
		client.sendTCP(request);
	}


	public static void main(String[] args) {
		new Engine();
	}
}

