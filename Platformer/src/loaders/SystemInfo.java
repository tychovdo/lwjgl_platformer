package loaders;

import org.lwjgl.Sys;

public class SystemInfo {

	public long lastFrame;
	public long startTime;
	
	public int getStep(int speed, int amount) {
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

	public static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	public int getDelta() {
		long currentTime = getTime();
		int delta = (int) (currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}

	
}
