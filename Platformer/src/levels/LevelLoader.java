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
import elements.Wall;

public class LevelLoader {

	
	public static List<Wall> load(String levelpack, int level) {
		List<Wall> adder = new ArrayList<Wall>();
		
		adder = ReadFileAsBytes(levelpack + "/" + level + ".lvl");
//		int dws = 20;
//		
//		switch(level) {
//		case 3:
//			adder.add(new Wall(100,100,100,dws));
//			adder.add(new Wall(300,200,100,dws));
//			adder.add(new Wall(100,300,100,dws));
//			break;
//		case 4:
//			adder.add(new Wall(100,100,700,dws));
//			adder.add(new Wall(100,100,dws,400));
//			adder.add(new Wall(100,500,600,dws));
//			adder.add(new Wall(700,200,dws,300+dws));
//			adder.add(new Wall(200,200,500,dws));
//			adder.add(new Wall(200,200,dws,200));
//			adder.add(new Wall(200,400,400,dws));
//			adder.add(new Wall(600,300,dws,100+dws));
//			adder.add(new Wall(300,300,300,dws));
//			break;
//		case 5:
//			adder.add(new Wall(100,100,dws,400));
//			adder.add(new Wall(200,200,500,dws));
//			adder.add(new Wall(600,300,dws,100+dws));
//
//			break;
//		
//		}
		
		return adder;
	}
	
	public static void writeFileAsBytes(String fullPath, List<Wall> walls) throws IOException
	{
	    File file = new File(fullPath);

	    // Now write the data array to the file.
	    try {
	      // Create an output stream to the file.
	      FileOutputStream file_output = new FileOutputStream (file);
	      // Wrap the FileOutputStream with a DataOutputStream
	      DataOutputStream data_out = new DataOutputStream (file_output);

	      // Write the data to the file in an integer/double pair
	      for (int i=0; i < walls.size(); i++) {
	    	  Wall wall = walls.get(i);
	          data_out.writeInt ((int) wall.getX());
	          data_out.writeInt ((int) wall.getY());
	          data_out.writeInt ((int) wall.getWidth());
	          data_out.writeInt ((int) wall.getHeight());
	      }
	      // Close file when finished with it..
	      file_output.close ();
	    }
	    catch (IOException e) {
	       System.out.println ("IO exception = " + e );
	    }
	}
	
	public static List<Wall> ReadFileAsBytes(String fullPath) {
		List<Wall> walls = new ArrayList<Wall>(16);
		
		File file = new File(fullPath);
	    int i_data = 0;
	    int count = 0;
	    int[] data = new int[4];
	    
	    
	    try {
	      // Wrap the FileInputStream with a DataInputStream
	      FileInputStream file_input = new FileInputStream (file);
	      DataInputStream data_in    = new DataInputStream (file_input );

	      while (true) {
	        try {
	          i_data = data_in.readInt ();
	          data[count] = i_data;
	          if(count<3) {
	        	  count++;
	          } else {
	        	  walls.add(new Wall(data[0],data[1],data[2],data[3]));
	        	  count=0;
	          }
	        }
	        catch (EOFException eof) {
	          //System.out.println ("End of File");
	          break;
	        }
	        //System.out.printf ("%3d, ", i_data);
	      }
	      data_in.close ();
	    } catch  (IOException e) {
	       System.out.println ( "IO Exception =: " + e );
	    }
	    
		return walls;
	}
}
