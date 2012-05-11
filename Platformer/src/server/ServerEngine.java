package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import network.LevelRequest;
import network.LevelResponse;
import network.SomeRequest;
import network.posRequest;
import network.posResponse;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import elements.Player;

public class ServerEngine {
	
	List<Player> players = new ArrayList<Player>();
	public int gravplier = 1;
	
	ServerEngine() throws IOException {
		
		players.add(new Player(0,0,20,20));
		players.add(new Player(0,0,20,20));
		
		
		Server server = new Server();
		server.start();
		server.bind(54555, 54777);
		
		Kryo kryo = server.getKryo();
		kryo.register(SomeRequest.class);
		kryo.register(posRequest.class);
		kryo.register(posResponse.class);
		kryo.register(LevelRequest.class);
		kryo.register(LevelResponse.class);
		kryo.register(Player.class);
		
		
		
		server.addListener(new Listener() {
			   public void received (Connection connection, Object object) {
			      if (object instanceof SomeRequest) {
			         SomeRequest request = (SomeRequest)object;
			         gravplier = request.gravplier;
			      }
			      if (object instanceof posRequest) {
				         posRequest request = (posRequest)object;
				         players.get(request.player_id).setX(request.x);
				         players.get(request.player_id).setY(request.y);
				         
				        
				         for(int i=0;i<players.size();i++) {
				        	 posResponse response = new posResponse();
				        	 response.player_id = i;
				        	 response.x = players.get(i).getX();
				        	 response.y = players.get(i).getY();   	
				        	 response.gravplier = gravplier;
				        	 connection.sendTCP(response);
				         }
				         
				  }
			   }
			});
	}
	
	
	public static void main(String[] args) throws IOException {
		new ServerEngine();
	}

}

