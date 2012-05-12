package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import network.LevelRequest;
import network.LoginRequest;
import network.SomeRequest;
import network.posRequest;
import network.posResponse;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import elements.Player;

public class ServerEngine {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	List<Player> players = new ArrayList<Player>();
	public int gravplier = 1;
	
	public int level = 1;
	public boolean locked = true;
	public int playerCount = 0;
	
	
	
	
	Server server = new Server();
	
	
	public ServerEngine(){
		
		
		
		
		server.start();
		try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Kryo kryo = server.getKryo();
		kryo.register(SomeRequest.class);
		kryo.register(LoginRequest.class);
		kryo.register(posRequest.class);
		kryo.register(posResponse.class);
		kryo.register(LevelRequest.class);
	
		
		
		
		
		server.addListener(new Listener() {
			   public void received (Connection connection, Object object) {
			      if (object instanceof SomeRequest) {
			         SomeRequest request = (SomeRequest)object;
			         gravplier = request.gravplier;
			      }
			      if (object instanceof posRequest) {
			    	  		//receive player position
				         posRequest request = (posRequest)object;
				         players.get(request.player_id).setX(request.x);
				         players.get(request.player_id).setY(request.y);
				         	//respond with all player positions
				         for(int i=0;i<players.size();i++) {
				        	 posResponse response = new posResponse();
					         	//when player sends location out of bounds, reset position
					         int x = (int) players.get(request.player_id).getX();
					         int y = (int) players.get(request.player_id).getY();
					         if((x<0 || x>WIDTH)||(y<0 || y>HEIGHT)) {
					        	 players.get(request.player_id).setX(150);
					        	 players.get(request.player_id).setY(150);
					        	 response.forced = true;
					         }
				        	 response.player_id = i;
				        	 response.x = players.get(i).getX();
				        	 response.y = players.get(i).getY();   
				        	 response.gravplier = gravplier;
				        	 response.exists = players.get(i).exists;
				        	 response.locked = locked;
				        	 response.level = level;
				        	 
				        	 connection.sendTCP(response);
				         }
				  }
			      if (object instanceof LoginRequest) {
			    	  	//add player on login
			    	  LoginRequest request = (LoginRequest)object;
				      players.add(new Player(0,0,20,20));
				      players.get(request.player_id).name = request.name;
				      playerCount++;
				      checkLocked();
				  }
			      if (object instanceof LevelRequest) {
			    	  LevelRequest request = (LevelRequest)object;
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
			      		  level = request.nextLevel;
			      		  if(playerCount>1) {
			      			  locked = true;
			      		  } else {
			      			  locked = false;
			      		  }
				      	  for(Player player : players) {
				      		  player.reachedExit = false;
				      	  }
			      	  }
			    	  
				  }
			   }
			   public void disconnected(Connection connection) {        
		    	  	players.get(connection.getID()-1).exists = false;
		    	  	playerCount--;
		    	  	checkLocked();
		       }
		});
		
	}
	public void checkLocked() {
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
	
	public static void main(String[] args) throws IOException {
		new ServerEngine();
	}

}

