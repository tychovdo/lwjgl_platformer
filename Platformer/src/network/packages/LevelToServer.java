package network.packages;

public class LevelToServer {
	public int player_id;
	public int nextLevel;
	public boolean finished; // when 'finished=true', 'player_id' reached the LevelSwitcher.
							 // when 'finished=false', a LevelToClient is requested.
}
