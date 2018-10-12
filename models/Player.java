/**
 * 
 */
package models;

import java.util.ArrayList;

/**
 * @author umesh
 *
 */


public class Player {
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
}
