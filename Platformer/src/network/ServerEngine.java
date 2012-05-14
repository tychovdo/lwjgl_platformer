package network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import network.packages.GeneralToClient;
import network.packages.GeneralToServer;
import network.packages.LevelToClient;
import network.packages.LevelToServer;
import network.packages.LoginToServer;
import network.packages.PosToServer;

import loaders.KryoLoader;
import loaders.LevelLoader;
import loaders.SystemInfo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import entities.movable.Player;
import entities.roomobjects.LevelSpawnpoint;
import entities.roomobjects.LevelSwitcher;
import entities.roomobjects.Spike;
import entities.roomobjects.Wall;

public class ServerEngine {
	// constants:
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final boolean hardcore = false;
	
	// room data:
	private int level = 1;
	private boolean locked = true;
	private int gravplier = 1;
	private int playerAmount = 0;
	
	// game objects:
	List<Player> players = new ArrayList<Player>();
	List<Wall> walls = new ArrayList<Wall>();
	List<LevelSwitcher> levelswitchers = new ArrayList<LevelSwitcher>();
	List<Spike> spikes = new ArrayList<Spike>();
	
	LevelSpawnpoint spawn = new LevelSpawnpoint(150, 150);
	
	// network:
	Server server = new Server();
	
	
	public ServerEngine(){
		server.start();
		try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		Kryo kryo = server.getKryo();
		kryo = KryoLoader.register(kryo);
		
		loadLevel();

		//listen to packages from clients:
		server.addListener(new Listener() {
			   public void received (Connection connection, Object object) {
			      if (object instanceof GeneralToServer) {
			         GeneralToServer request = (GeneralToServer)object;
			         if(request.gotHit) {
			        	 if (hardcore) {
			        		 // reset all player pos, forceupdate, reset/update 'locked'
				        	 for(int i=0;i<players.size();i++) {
					        	 players.get(i).setX(spawn.getX());
					        	 players.get(i).setY(spawn.getY());
					        	 players.get(i).kill();
					        	 gravplier = 1;
					        	 players.get(i).reachedExit = false;
					        	 sendGeneral(true,i);
					        	 checkLocked();
				        	 }
			        	 } else {
			        		 // reset player_id pos, forceupdate
			        		 players.get(request.player_id).setX(spawn.getX());
			        		 players.get(request.player_id).setY(spawn.getY());
				        	 players.get(request.player_id).kill();
			        		 gravplier = 1;
			        		 sendGeneral(true,request.player_id);
			        	 }
			         } else {
			        	 gravplier = request.gravplier;
			         }
			      }
			      if (object instanceof PosToServer) {
			    	  		//receive player position
				         PosToServer request = (PosToServer)object;
				         players.get(request.player_id).setX(request.x);
				         players.get(request.player_id).setY(request.y);
				         	//respond with all player positions
				         for(int i=0;i<players.size();i++) {
					         	//when player sends location out of bounds, reset position
					         int x = (int) players.get(request.player_id).getX();
					         int y = (int) players.get(request.player_id).getY();
					         if((x<0 || x>WIDTH)||(y<0 || y>HEIGHT)) {
					        	 players.get(request.player_id).setX(spawn.getX());
					        	 players.get(request.player_id).setY(spawn.getX());
					        	 sendGeneral(true,i);
					         } else {
					        	 sendGeneral(false,i);
					         }
				         }
				  }
			      if (object instanceof LoginToServer) {
			    	  	//add player on login
			    	  LoginToServer request = (LoginToServer)object;
				      players.add(new Player(0,0,20,20));
				      players.get(request.player_id).name = request.name;
				      playerAmount++;
				      System.out.println(playerAmount);
				      checkLocked();
				  }
			      if (object instanceof LevelToServer) {
			    	  LevelToServer request = (LevelToServer)object;
			    	  if(request.finished) {
			    		  // when the player sends 'finished'
			    		  // the server will check 'locked' and 'level' 
			    		  // status.
			    		  players.get(request.player_id).reachedExit = true;
				          int nonFinishedPlayers = 0;
				      	  for(Player player : players) {
				      		  if(player.exists && !player.reachedExit) {
				      			  nonFinishedPlayers++;
				      		  }
				      	  }
				      	  if (nonFinishedPlayers>1) { 
				      		  locked = true; 
				      	  } else if(nonFinishedPlayers==1){
				      		  locked = false; 
				      	  } else {
				      		  	// goto new level: update 'level',
				      		  	// load level-entities, set players to 'spawn',
				      		  	// reset 'locked'
				      		  level = request.nextLevel;
				      		  loadLevel();
				      		  for(int i=0;i<players.size();i++) {
				      			players.get(i).setX(spawn.getX());
				      			players.get(i).setY(spawn.getY());
				      			sendGeneral(true,i);
				      		  }
				      		  if(playerAmount>1) {
				      			  locked = true;
				      		  } else {
				      			  locked = false;
				      		  }
					      	  for(Player player : players) {
					      		  player.reachedExit = false;
					      	  }
				      	  }
			    	  }	else {
			    		  // when 'finished==false'
			    		  // send roomobjects to client (LevelSpawnpoint is serverside only)
			    		  LevelToClient response = new LevelToClient();
			    		  response.walls = walls;
			    		  response.levelswitchers = levelswitchers;
			    		  response.spikes = spikes;
			    		  connection.sendTCP(response);
			    	  }			    	  
			      }
			   }
			   public void disconnected(Connection connection) {        
				   players.get(connection.getID()-1).exists = false;
				   playerAmount--;
				   checkLocked();
			   }
		});
		
	}
	@SuppressWarnings("unchecked")
	public void loadLevel() {
		walls.clear();
		
		int dws = 20; //default wall size
		walls.add(new Wall(0,0,WIDTH,dws)); 
		walls.add(new Wall(0,0,dws,HEIGHT)); 
		walls.add(new Wall(WIDTH-dws,0,dws,HEIGHT)); 
		walls.add(new Wall(0,HEIGHT-dws,WIDTH, dws));
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
		
	}
	public void checkLocked() {
		//update 'locked' status
		int nonFinishedPlayers = 0;
    	  for(Player player : players) {
    		  if(player.exists && !player.reachedExit) {
    			  nonFinishedPlayers++;
    		  }
    	  }
    	  if (nonFinishedPlayers>1) {
    		  locked = true;
    	  } else if(nonFinishedPlayers==1){
    		  locked = false;
    	  }
	}
	public void sendGeneral(boolean forced, int player_id) {
		// sends general data to client (including player positions)
		GeneralToClient response = new GeneralToClient();
		response.forced = forced;
		response.player_id = player_id;
		response.x = players.get(player_id).getX(); 
		response.y = players.get(player_id).getY();
		if(players.get(player_id).dead) {
			if(SystemInfo.getTime()>players.get(player_id).deadTime+1000) {
				players.get(player_id).revive();
				response.forced = true;
			}
		}
		response.dead = players.get(player_id).dead;
		response.gravplier = gravplier;
		response.exists = players.get(player_id).exists;
		response.locked = locked;
		response.level = level;	 
		server.sendToAllTCP(response);
	}
	
	public static void main(String[] args) throws IOException {
		new ServerEngine();
	}

}

