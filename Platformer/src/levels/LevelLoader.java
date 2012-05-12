package levels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import elements.LevelSwitcher;
import elements.Wall;
import entities.Entity;

public class LevelLoader {

	
	public static List<Entity> load(String levelpack, int level, int entity_type) {
		
		//load all entities from 'level' in 'levelpack'
		List<Entity> adder = new ArrayList<Entity>();
		adder = loadHardcodedlevel(level);
		//adder = ReadFileAsBytes(levelpack + "/" + level + ".lvl");
		
		//filter and return entities which correspond with 'entity_type'
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
	    File file = new File(fullPath);

	    // Now write the data array to the file.
	    try {
	      // Create an output stream to the file.
	      FileOutputStream file_output = new FileOutputStream (file);
	      // Wrap the FileOutputStream with a DataOutputStream
	      DataOutputStream data_out = new DataOutputStream (file_output);

	      // Write the data to the file in an integer/double pair
	      for (int i=0; i < entities.size(); i++) {
	    	  Entity entity = entities.get(i);
	    	  data_out.writeInt ((int) entity.getEntityType());
	    	  switch(entity.getEntityType()) {
	    	  case 1:
		          data_out.writeInt ((int) entity.getX());
		          data_out.writeInt ((int) entity.getY());
		          data_out.writeInt ((int) entity.getWidth());
		          data_out.writeInt ((int) entity.getHeight());
		          break;
	    	  case 2:
		          data_out.writeInt ((int) entity.getX());
		          data_out.writeInt ((int) entity.getY());
		          data_out.writeInt ((int) ((LevelSwitcher) entity).getNextLevel());
		          data_out.writeInt ((int) 10);
		          break;
	    	  }

	      }
	      // Close file when finished with it..
	      file_output.close ();
	    }
	    catch (IOException e) {
	       System.out.println ("IO exception = " + e );
	    }
	}
	
	public static List<Entity> ReadFileAsBytes(String fullPath) {
		List<Entity> entities = new ArrayList<Entity>();
		
		File file = new File(fullPath);
	    int i_data = 0;
	    int count = 0;
	    int[] data = new int[5];
	    
	    
	    
	    try {
	      // Wrap the FileInputStream with a DataInputStream
	    FileInputStream file_input = new FileInputStream (file);
	    DataInputStream data_in    = new DataInputStream (file_input );

		while (true) {
			try {
				i_data = data_in.readInt ();
				data[count] = i_data;
				if(count<4) {
					count++;
				} else {
					switch(data[0]) {
					case 1: //walls
						entities.add(new Wall(data[1],data[2],data[3],data[4]));
						break;
					case 2:
						entities.add(new LevelSwitcher(data[1],data[2],data[3]));
						break;
					}
					count=0;
				}
			}
			catch (EOFException eof) {
				//System.out.println ("End of File");
				break;
			}
		}
	        //System.out.printf ("%3d, ", i_data);	      data_in.close ();
	    } catch  (IOException e) {
	       System.out.println ( "IO Exception =: " + e );
	    }
	    
		return entities;
	}
	
	public static List<Entity> loadHardcodedlevel(int level) {
		List<Entity> entities = new ArrayList<Entity>();
		switch(level) {
		case 1:
			entities.add(new Wall(100,100,100,20));
			entities.add(new Wall(300,300,100,20));
			entities.add(new LevelSwitcher(500,400,2));
			break;
		case 2:
			entities.add(new Wall(100,100,20,200));
			entities.add(new Wall(300,300,20,200));
			entities.add(new LevelSwitcher(500,100,4));
			break;
			
		case 3:
			entities.add(new Wall(100,100,200,20));
			entities.add(new Wall(300,300,200,20));
			entities.add(new LevelSwitcher(300,400,1));
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
			
			entities.add(new LevelSwitcher(700,450,3));
			
			break;
		}


		return entities;
	}
	
}
