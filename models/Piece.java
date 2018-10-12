package models;


public class Piece {
	public boolean team;
	public boolean rank; // true is king
	public Position location;
	
	public Piece(boolean t, boolean r, Position l) {
		team = t;
		r = rank;
		location = l;
	}
}
