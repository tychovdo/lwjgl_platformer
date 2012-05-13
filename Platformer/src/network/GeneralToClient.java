package network;



public class GeneralToClient {
	public int player_id;
	public double x;
	public double y;
	public int gravplier;
	public boolean forced; // when 'forced=true', the client will update the information even when 'player_id==client-myID'
	public boolean exists; // is player online
	public int level;
	public boolean locked;
}