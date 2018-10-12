package models;

public class Move {
	public Piece toMove;
	public Position current;
	public Position newLocation;
	public boolean mandatory;
	
	public Move(Position k, Position l) {
		current = k;
		newLocation = l;
	}
}
