package game;

import java.util.Comparator;

import models.Move;

public class MoveCompare implements Comparator<Move> {

	@Override
	public int compare(Move o1, Move o2) {
		// TODO Auto-generated method stub
		if(o1.profit == o2.profit) return 0;
		return o1.profit > o2.profit ? 1 : -1;
	}
	

}
