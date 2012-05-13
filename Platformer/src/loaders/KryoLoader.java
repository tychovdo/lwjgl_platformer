package loaders;

import java.util.ArrayList;

import network.packages.GeneralToClient;
import network.packages.GeneralToServer;
import network.packages.LevelToClient;
import network.packages.LevelToServer;
import network.packages.LoginToServer;
import network.packages.PosToServer;

import com.esotericsoftware.kryo.Kryo;

public class KryoLoader {

	public static Kryo register(Kryo kryo) {
		//registers are needed for kryo (serialization library to easily send objects over the network).
		kryo.register(GeneralToServer.class);
		kryo.register(LoginToServer.class);
		kryo.register(PosToServer.class);
		kryo.register(GeneralToClient.class);
		kryo.register(LevelToServer.class);
		kryo.register(LevelToClient.class);
		kryo.register(ArrayList.class);
		kryo.register(entities.roomobjects.Wall.class);
		kryo.register(entities.roomobjects.LevelSwitcher.class);
		kryo.register(java.awt.Rectangle.class);
		
		return kryo;
	}

}
