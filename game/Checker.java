package game;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.tools.DocumentationTool.Location;

import models.Move;
import models.Piece;
import models.Player;
import models.Position;


public class Checker {
	public Player human = new Player();
	public Player ai = new Player();
	int boardSize = 8;
	int rowsOccupied = 3;
	public int[][] board = new int[boardSize][boardSize];
	public int[] evenHuman = new int[ ]{0, 2, 4, 6};
	public int[] oddHuman = new int[ ]{1, 3, 5, 7};
	
	public Checker() {
		human.setTeamOn(true);
		setPlayerPieces();
	}
	
	public void setPlayerPieces() {
		for(int i = 0; i < 3; i++) {
			if(i%2 == 0) {
				for(int j : evenHuman) {
					Piece pawn = new Piece(true, false, new Position(boardSize - i - 1, j));
					board[boardSize - i - 1][j] = 1;
					human.getArmy().add(pawn);
				}
				for(int j : oddHuman) {
					Piece pawn = new Piece(false, false, new Position(i, j));
					board[i][j] = -1;
					ai.getArmy().add(pawn);
				}
			}
			else {
				for(int j : oddHuman) {
					Piece pawn = new Piece(true, false, new Position(boardSize - i - 1, j));
					board[boardSize - i - 1][j] = 1;
					human.getArmy().add(pawn);
				}
				for(int j : evenHuman) {
					Piece pawn = new Piece(false, false, new Position(i, j));
					board[i][j] = -1;
					ai.getArmy().add(pawn);
				}
			}
		}
	}
	
	public void playTurn() {
		List<Move> allMoves = getAllPossibleMoves(ai);
		System.out.println("From " + allMoves.get(0).current.x + " " + allMoves.get(0).current.y  + " New Location " + allMoves.get(0).newLocation.x + " " + allMoves.get(0).newLocation.y);
		updateBoard(allMoves.get(0), board, human, ai);
		printBoard();
	}
	
	
	public List<Move> getAllPossibleMoves(Player p) {
		List<Move> allMoves = new ArrayList<Move>();
		int val = p.getTeamOn() ? 1 : -1;
		for(Piece pawn : p.getArmy()) {
			allMoves.addAll(getSinglePawnAllMoves(pawn.location, p.getTeamOn(), val, pawn.rank));
		}
		for(Move m : allMoves) {
			if(m.mandatory) {
				List<Move> singleMove = new ArrayList<Move>();
				singleMove.add(m);
				return singleMove;
			}
		}
		return allMoves;
	}
	
	public void updateBoard(Move m, int[][] gameBoard, Player human1, Player machine) {
		if(gameBoard[m.current.x][m.current.y] == 1) {
			for(Piece p : human1.getArmy()) {
				if((p.location.x == m.current.x) && (p.location.y == m.current.y)) {
					p.location.x = m.newLocation.x;
					p.location.y = m.newLocation.y;
					if(gameBoard[m.newLocation.x][m.newLocation.y] == -1) {
						int removeIdx = -1;
						for(int i = 0; i < machine.getArmy().size(); i++) {	
							if((machine.getArmy().get(i).location.x == m.newLocation.x) && (machine.getArmy().get(i).location.y == m.newLocation.y)) {
								removeIdx = i;
								break;
							}
						}
						machine.getArmy().remove(removeIdx);
					}
				}
			}
		} else {
			for(Piece p : machine.getArmy()) {
				if((p.location.x == m.current.x) && (p.location.y == m.current.y)) {
					p.location.x = m.newLocation.x;
					p.location.y = m.newLocation.y;
					if(gameBoard[m.newLocation.x][m.newLocation.y] == 1) {
						int removeIdx = -1;
						for(int i = 0; i < human1.getArmy().size(); i++) {	
							if((human1.getArmy().get(i).location.x == m.newLocation.x) && (human1.getArmy().get(i).location.y == m.newLocation.y)) {
								removeIdx = i;
								break;
							}
						}
						human1.getArmy().remove(removeIdx);
					}
				}
			}
		}
		gameBoard[m.newLocation.x][m.newLocation.y] = gameBoard[m.current.x][m.current.y];
		gameBoard[m.current.x][m.current.y] = 0;
	}
	
	public ArrayList<Move> getSinglePawnAllMoves(Position l, boolean direction, int val, boolean king) {
		int x = l.x;
		int y = l.y;
		ArrayList<Move> moves = new ArrayList<Move>();
		ArrayList<Move> validMoves = new ArrayList<Move>();
		moves.add(new Move(new Position(x, y), new Position(x - 1, y - 1)));
		moves.add(new Move(new Position(x, y), new Position(x - 1, y + 1)));
		moves.add(new Move(new Position(x, y), new Position(x + 1, y - 1)));
		moves.add(new Move(new Position(x, y), new Position(x + 1, y + 1)));	
		for(Move m : moves) {
			if(m.newLocation.x < 0 || m.newLocation.y < 0 || m.newLocation.y > boardSize - 1 || m.newLocation.x > boardSize - 1) {
				continue;
			}
			if(direction) {
				if(x > m.newLocation.x) {
					if(board[m.newLocation.x][m.newLocation.y] + val == 0) {
						validMoves.clear();
						validMoves.add(m);
						m.mandatory = true;
						return validMoves;
					}
					if(board[m.newLocation.x][m.newLocation.y] != val) validMoves.add(m);
				}
				if(king) {
					if(board[m.newLocation.x][m.newLocation.y] + val == 0) {
						validMoves.clear();
						validMoves.add(m);
						m.mandatory = true;
						return validMoves;
					}
					if(board[m.newLocation.x][m.newLocation.y] != val) validMoves.add(m);
				}
			} else {
				if(x < m.newLocation.x) {
					if(board[m.newLocation.x][m.newLocation.y] + val == 0) {
						validMoves.clear();
						validMoves.add(m);
						m.mandatory = true;
						return validMoves;
					}
					if(board[m.newLocation.x][m.newLocation.y] != val) validMoves.add(m);
				}
				if(king) {
					if(board[m.newLocation.x][m.newLocation.y] + val == 0) {
						validMoves.clear();
						validMoves.add(m);
						m.mandatory = true;
						return validMoves;
					}
					if(board[m.newLocation.x][m.newLocation.y] != val) validMoves.add(m);
				}
			}
		}
		return validMoves;
	}
	
	
	public void start() {
		// TODO Auto-generated method stub
		printBoard();
		//printPlayer(ai);
		//printPlayer(human);
		//getAllPossibleMoves(ai);
		//getAllPossibleMoves(human);
		playTurn();
		getUserInput();
		start();
	}
	
	public void getUserInput() {
		// TODO Auto-generated method stub
		Scanner inp = new Scanner(System.in);
		int x1 = inp.nextInt();
		int y1 = inp.nextInt();
		int x2 = inp.nextInt();
		int y2 = inp.nextInt();
		ArrayList<Move> moves = getSinglePawnAllMoves(new Position(x1, y1), true, 1, false);
		for(Move m : moves) {
			if(m.newLocation.x == x2 && m.newLocation.y == y2) {
				updateBoard(m, board, human, ai);
				//printBoard();
				return;
			}
		}
		System.out.println("Invalid move, Please try again with a different move");
		getUserInput();
	}

	public void printBoard() {
		for(int i = 0; i < boardSize; i++) {
			System.out.println(" ");
			for(int j = 0; j < boardSize; j++) {
				System.out.print(board[i][j] + "  ");
			}
		}
		System.out.println(" ");
	}
	
	public void printPlayer(Player p) {
		System.out.println("Player Typer : " + (p.getTeamOn() ? "Human" : "AI"));
		System.out.println("Player Score : " + p.getScore());
		System.out.println("Trun to play : " + p.isHasToMove());
		int ptype = p.getTeamOn() ? 1 : -1;
		int[][] playerBoard = new int[boardSize][boardSize];
		for(Piece pawn : p.getArmy()) {
			playerBoard[pawn.location.x][pawn.location.y] = ptype; 
		}
		for(int i = 0; i < boardSize; i++) {
			System.out.println(" ");
			for(int j = 0; j < boardSize; j++) {
				System.out.print(playerBoard[i][j] + "  ");
			}
		}
		System.out.println(" ");
	}
	
	
}
