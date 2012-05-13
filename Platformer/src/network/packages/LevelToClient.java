package network.packages;

import java.util.ArrayList;
import java.util.List;

import entities.roomobjects.LevelSwitcher;
import entities.roomobjects.Wall;

public class LevelToClient {
	public List<Wall> walls = new ArrayList<Wall>();
	public List<LevelSwitcher> levelswitchers = new ArrayList<LevelSwitcher>();
	// LevelSpawnpoint is serverside only
}
