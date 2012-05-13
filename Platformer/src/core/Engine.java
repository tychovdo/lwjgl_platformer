package core;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glRectd;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import loaders.KryoLoader;
import network.GeneralToClient;
import network.GeneralToServer;
import network.LevelToClient;
import network.LevelToServer;
import network.LoginToServer;
import network.PosToServer;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import server.ServerEngine;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import entities.movable.Player;
import entities.roomobjects.LevelSwitcher;
import entities.roomobjects.Wall;

public class Engine {

	// constants:
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	private static final double GRAVITY = -0.035;
	
	// settings:
	private String server_ip;
	private int server_port1;
	private int server_port2;
	private boolean singleplayer = false;
	
	// room data
	private String levelpack;   
	private int level;
	private int playerAmount;   
	private int levelPreviousFrame = 1337;  //when 'level!=levelPreviousFrame' a new level is asked from server
	private int myID;           				//the player_id from the player on this client
	private boolean locked;     				//when 'locked=false' only 1 more player is needed to go to next level
	private int gravplier = 1; 				//gravity multiplier (gravplier=-1 on reversed gravity)
	

	// input:
	private boolean shiftReady = true;      //cooldown for gravity-shift
	private int keyTimer = 0;               //a timer to handle keystrokes per second
	
	// fps settings:
	private boolean gameloop = true;
	private long startTime; 
	private long lastFrame;
	
	// Game objects:
		// resources:
	private Texture tex_lock;
	private Texture tex_arrow;
		// private Texture tex_coin;
	private List<Texture> textures = new ArrayList<Texture>();
		// objects:
	private List<Player> players = new ArrayList<Player>();
	private List<Wall> walls = new ArrayList<Wall>();
	private List<LevelSwitcher> levelswitchers = new ArrayList<LevelSwitcher>();
		// networking:
	ServerEngine singleplayerServer;
	Client client;
	Kryo kryo;
	private boolean gpCheck = true;     //needed for synchronising with serverside gravplier


	
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
			syncLevel();
			logic(getDelta());
			
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
		setUpNetworking();
		setUpDisplay();
		setUpOpenGL();
		setUpResources();
		setUpStartTime();
	}
	
	private void input() { //TODO: external control settings, dynamic player amount
		
		// Movement X-axis
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			players.get(myID).setDX(-0.35);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			players.get(myID).setDX(0.35);
		} else {
			players.get(myID).setDX(0);
		}
		// Movement Y-axis
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
		
		// Special keys
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if(shiftReady) {
				GeneralToServer request = new GeneralToServer();
				request.gravplier = -gravplier;
				client.sendTCP(request);
				shiftReady=false;
				gpCheck = false;
			}
		}
		
		// Cheats:
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
	private void setUpStartTime() {
		startTime = getTime();
	}
	
	private void setUpSettings() {
		// load serverdata from inputbox
		server_ip=JOptionPane.showInputDialog(null,"Server IP:");
		server_port1 = 54555;
		server_port2 = 54777;
		levelpack = "levelpack0";
		level = 1;
		playerAmount = 8;
		
		if(server_ip.equals("singleplayer")) {
			singleplayer = true;
			server_ip = "localhost";
			
		}
		System.out.println("======== Loading game ========");
		System.out.println("Server IP:          " + server_ip);
		System.out.println("Server Port 1:      " + server_port1);
		System.out.println("Server Port 2:      " + server_port2);
		System.out.println("Levelpack:          " + levelpack);
		System.out.println("First level:        " + level);
		System.out.println("Amount of players:  " + playerAmount);
	}
	private void setUpNetworking() {
		// hosts server inside of
		// client when in singleplayer mode
		
		if(singleplayer) {
			singleplayerServer = new ServerEngine();
		}
		
		client = new Client();   //needed for connection with server
		kryo = client.getKryo(); //needed to serialize objects to send over network
		kryo = KryoLoader.register(kryo);
		client.start();
		

		try { // connect with server:
			client.connect(5000, server_ip, server_port1, server_port2);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't connect with server...");
			System.exit(0);
		}
		
		
		client.addListener(new Listener() { // Listener for incoming packages
			public void received (Connection connection, Object object) {
				if (object instanceof GeneralToClient) { 
					
					// incoming package: 'posResponse'
					// updates position of 1 certain player
					// updates other player position ('forced=true' will update his own posision aswell)
					// checks if player exists
					// updates 'level', 'locked', 'gravplier'
					
					GeneralToClient response = (GeneralToClient)object;
					if(response.player_id!=myID || response.forced) {
						players.get(response.player_id).setX(response.x);
						players.get(response.player_id).setY(response.y);  
						players.get(response.player_id).exists = response.exists;
						if(response.exists==false) {
							System.out.println(response.player_id + "left.");
						}
					}
					level = response.level;
			        locked = response.locked;
			        
			        if(gravplier!=response.gravplier) {
			        	gravplier = response.gravplier;
			        	gpCheck = true;
			        }
				}
				if (object instanceof LevelToClient) {
					// incoming package: 'LevelResponse'
					// sends current level data: 'walls' and 'levelswitchers' ('spawn' is only serverside)
					LevelToClient response = (LevelToClient)object;
					walls = response.walls;
					levelswitchers = response.levelswitchers;  	  
				}
			}
		});
		
		
		myID = client.getID() - 1; 
		
		// Inform server you made a connection (TODO: finish names)
		LoginToServer request = new LoginToServer();
		request.player_id = myID;
		request.name = "Player";
		client.sendTCP(request);
		
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
	}
	private void setUpResources() {
		// load character textures
		for(int i=0;i<playerAmount;i++) {
			textures.add(loadTexture("/sprites/char"+i+".png"));
		}
		
		// load object textures
//		tex_coin = loadTexture("coin.png");
		tex_lock = loadTexture("lock.png");
		tex_arrow = loadTexture("arrow.png");
		
		
	}
	private void setUpEntities() {
		// load yourself (other will
		// will be added when server
		// sends their posResponse's
		players.add(new Player(0,0,32,32));	
		players.get(myID).setX(150);
		players.get(myID).setY(150);
	}
    
	private Texture loadTexture(String filename) {
    	//return texture, in res/ folder, from filename
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

		// draw Textured entities:
		glEnable(GL_TEXTURE_2D);
		
			// draw players
		for(int i=0;i<players.size();i++) {
			if(players.get(i).exists) {
				players.get(i).draw(textures.get(i),getStep(50, 5),gravplier);	
			}
		}
			// draw levelswitchers
		for (LevelSwitcher ls : levelswitchers) {
			if(locked) {
				ls.draw(tex_lock);
			} else {
				ls.draw(tex_arrow);
			}
		}
		
	}
	private void syncLevel() {
		// asks the server for level
		// data when a new level needs
		// to be load
		if(levelPreviousFrame!=level) { 
			LevelToServer request = new LevelToServer();
			request.finished = false;
			client.sendTCP(request);
			
			levelPreviousFrame=level;
		}
		
	}
	
	private int getStep(int speed, int amount) {
		// return a value between 0 and 'amount'
		// based on the system time. it is used
		// in animations. 
		int x = (int) (((getTime()-startTime)/speed)%amount);
		if(x>(amount/2)) {
			x = x - (((x-(amount/2))*2)-1);
			return x;
		} else {
			return x;
		}
	}
	private void logic(int delta) {
		
			// update player location and add gravity acceleration
		for (int i=0;i<players.size();i++) {
			players.get(i).update(delta);	
			players.get(i).setAY(GRAVITY*gravplier);
		}
			// handle cooldown for (cheat)buttons
		if(keyTimer>0) {
			keyTimer--;
		}
			// activate shiftReady if player_0 
			// lands on his feet. (or head if 
			// gravity is reversed). 'shiftReady'
			// enables all users to switch shift
			// the gravity.
		if((!shiftReady) && (gpCheck)) {
			if(((gravplier==1) && (players.get(0).onFeet)) || ((gravplier==-1) && (!players.get(0).onFeet))) {
				shiftReady = true;
			}
		}
			// collision detection 
		for(Player player : players) {
			for (Wall wall : walls) { 
				// collision (players ==> walls)
				// every player has 4 collision boxes
				// when player ends up in a wall after
				// calculating his position. he will be
				// pushed backed before rendering happens
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
		}
		for (LevelSwitcher ls : levelswitchers) {
			if(players.get(myID).intersects(ls)) {
				// tells server when local-player 
				// collides with levelswitcher
				LevelToServer request = new LevelToServer();
				request.finished = true;
				request.player_id = myID; 
				request.nextLevel =  ls.getNextLevel();
				client.sendTCP(request);
			}
		}
		
		//sends local-player position to server
		PosToServer request = new PosToServer();
		request.player_id = myID; 
		request.x = players.get(myID).getX();
		request.y = players.get(myID).getY();
		client.sendTCP(request);
	}


	public static void main(String[] args) {
		new Engine();
	}
}

