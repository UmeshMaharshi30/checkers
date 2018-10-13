/**
 * 
 */
package models;

import java.util.ArrayList;

/**
 * @author umesh
 *
 */


public class Player implements Cloneable {
	int score = 0;
	boolean hasToMove = false;
	boolean teamOn; // true is human(A)
	ArrayList<Piece> army = new ArrayList<Piece>();
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public boolean isHasToMove() {
		return hasToMove;
	}
	public void setHasToMove(boolean hasToMove) {
		this.hasToMove = hasToMove;
	}
	public boolean getTeamOn() {
		return teamOn;
	}
	public void setTeamOn(boolean human) {
		this.teamOn = human;
	}
	public ArrayList<Piece> getArmy() {
		return army;
	}
	public void setArmy(ArrayList<Piece> army) {
		this.army = army;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Player clone = new Player();
		clone.setTeamOn(this.getTeamOn());
		clone.setScore(this.getScore());
		ArrayList<Piece> armyClone = new ArrayList<Piece>();
		for(Piece p : this.army) {
			Piece pClone = new Piece(p.team, p.rank, new Position(p.location.x, p.location.y));
			pClone.rank = p.rank;
			armyClone.add(pClone);
		}
		clone.setArmy(armyClone);
        return clone;
    }
	
	public double getProfit() {
		double prof = 0.0;
		for(Piece p : army) {
			prof = prof + 1;
			if(p.rank) prof = prof + 5;
		}
		return prof;
		
	}
	
}
