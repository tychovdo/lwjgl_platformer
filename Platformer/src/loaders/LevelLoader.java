package loaders;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import entities.roomobjects.LevelSpawnpoint;
import entities.roomobjects.LevelSwitcher;
import entities.roomobjects.Spike;
import entities.roomobjects.Wall;

public class LevelLoader {

	
	public static List<Entity> load(String levelpack, int level, int entity_type) {
		// Returns an ArrayList with all of the objects
		// corresponding 'entity_type' in a certain 'level'@'levelpack'.

		List<Entity> adder = new ArrayList<Entity>();
		//adder = loadHardcodedlevel(level);
		adder = ReadFileAsBytes(levelpack + "/" + level + ".lvl");
		List<Entity> filteredAdder = new ArrayList<Entity>();
		for(Entity entity : adder) {
			if(entity.getEntityType()==entity_type) {
				filteredAdder.add(entity);
			}
		}
		return filteredAdder;
	}
	
	public static void writeFileAsBytes(String fullPath, List<Entity> entities) throws IOException
	{
		// Stores 'entities' in 'fullPath' in a binary file
		
	    File file = new File(fullPath);
	    try {
	      FileOutputStream file_output = new FileOutputStream (file);
	      DataOutputStream data_out = new DataOutputStream (file_output);
	      for (int i=0; i < entities.size(); i++) {
	    	  Entity entity = entities.get(i);
	    	  
	    	  switch(entity.getEntityType()) {
	    	  case 1:
	    		  data_out.writeInt ((int) entity.getEntityType());
		          data_out.writeInt ((int) entity.getX());
		          data_out.writeInt ((int) entity.getY());
		          data_out.writeInt ((int) entity.getWidth());
		          data_out.writeInt ((int) entity.getHeight());
		          break;
	    	  case 2:
	    		  data_out.writeInt ((int) entity.getEntityType());
		          data_out.writeInt ((int) entity.getX());
		          data_out.writeInt ((int) entity.getY());
		          data_out.writeInt ((int) ((LevelSwitcher) entity).getNextLevel());
		          data_out.writeInt ((int) 10);
		          break;
	    	  case 3:
	    		  data_out.writeInt ((int) entity.getEntityType());
	    		  data_out.writeInt ((int) entity.getX());
	    		  data_out.writeInt ((int) entity.getY());
	    		  data_out.writeInt ((int) 10);
	    		  data_out.writeInt ((int) 10);
	    		  break;
	    	  case 4:
	    		  data_out.writeInt ((int) entity.getEntityType());
		          data_out.writeInt ((int) entity.getX());
		          data_out.writeInt ((int) entity.getY());
		          data_out.writeInt ((int) entity.getWidth());
		          data_out.writeInt ((int) entity.getHeight());
		          break;
	    	  }

	      }
	      file_output.close ();
	    }
	    catch (IOException e) {
	       System.out.println ("IO exception = " + e );
	    }
	}
	
	public static List<Entity> ReadFileAsBytes(String fullPath) {
		// returns an ArrayList with the entities stored in 'fullPath'
		
		List<Entity> entities = new ArrayList<Entity>();
		
		File file = new File(fullPath);
	    int i_data = 0;
	    int count = 0;
	    int[] data = new int[5];
	    try {
	    FileInputStream file_input = new FileInputStream (file);
	    DataInputStream data_in    = new DataInputStream (file_input );
		while (true) {
			try {
				i_data = data_in.readInt ();
				data[count] = i_data;
				if(count<4) {
					count++;
				} else {
					System.out.println(data[0]+":"+data[1]+","+data[2]+","+data[3]+","+data[4]);
					switch(data[0]) {
					
					case 1: //walls
						entities.add(new Wall(data[1],data[2],data[3],data[4]));
						break;
					case 2: //levelswitchers
						entities.add(new LevelSwitcher(data[1],data[2],data[3]));
						break;
					case 3: //levelspawnpoint
						entities.add(new LevelSpawnpoint(data[1],data[2]));
						break;
					case 4:
						entities.add(new Spike(data[1],data[2],data[3],data[4]));
						break;
					}
					count=0;
				}
			}
			catch (EOFException eof) {
				break;
			}
		}
//        System.out.printf ("%3d, ", i_data);	      data_in.close ();
	    } catch  (IOException e) {
	       System.out.println ( "IO Exception =: " + e );
	    }
		return entities;
	}
	
	public static List<Entity> loadHardcodedlevel(int level) {
		// returns an ArrayList with entities (hardcoded in void) from a certain 'level'
		List<Entity> entities = new ArrayList<Entity>();
		switch(level) {
		case 1:
			entities.add(new LevelSpawnpoint(50,200));
			entities.add(new Wall(0,0,800,200));
			entities.add(new Wall(0,350,800,250));
			
			entities.add(new Spike(150,200,128,10));
			entities.add(new Spike(357,320,64,11));
			entities.add(new Spike(524,200,256,10));
			
			entities.add(new LevelSwitcher(670,318,2));
			break;
		case 2:
			entities.add(new LevelSpawnpoint(150,150));
			entities.add(new Wall(100,100,20,200));
			entities.add(new Wall(300,300,20,200));
			entities.add(new LevelSwitcher(500,100,3));
			break;
		case 3:
			entities.add(new LevelSpawnpoint(500,10));
			entities.add(new Wall(650,450,150 ,20));
			entities.add(new Wall(650,250,20 ,200));
			entities.add(new Wall(550,150,20 ,500));
			entities.add(new Wall(550,150,300,20));
			
			entities.add(new LevelSwitcher(60,20,4));
			break;
		case 4:
			entities.add(new Wall(260,20,40,440));
			entities.add(new Wall(480,120,40,460));
			entities.add(new Wall(230,460,100,40));
			entities.add(new Wall(450,100,100,40));
			
			entities.add(new Wall(700,160,150,20));
			entities.add(new Wall(500,300,75,80));
			entities.add(new Wall(575,300,75,20));
			entities.add(new Wall(600,430,300,20));

			entities.add(new LevelSwitcher(700,450,1));
			entities.add(new LevelSpawnpoint(150,150));
			break;
		}


		return entities;
	}
	
}
