package game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


import models.Move;
import models.Piece;
import models.Player;
import models.Position;


public class Checker {
	
	boolean debug = false;
	int boardSize = 8;
	int rowsOccupied = 3;
	int depth = 5;
	
	public Player human_main = new Player();
	public Player ai_main = new Player();
	
	public int[][] board_main = new int[boardSize][boardSize];
	
	public int[] evenHuman = new int[ ]{0, 2, 4, 6};
	public int[] oddHuman = new int[ ]{1, 3, 5, 7};
	
	
	public Checker(boolean debug) {
		// TODO Auto-generated constructor stub
		this.debug = debug;
		human_main.setTeamOn(true);
		setPlayerPieces();
	}

	public void setPlayerPieces() {
		for(int i = 0; i < 3; i++) {
			if(i%2 == 0) {
				for(int j : evenHuman) {
					Piece pawn = new Piece(true, false, new Position(boardSize - i - 1, j));
					board_main[boardSize - i - 1][j] = 1;
					human_main.getArmy().add(pawn);
				}
				for(int j : oddHuman) {
					Piece pawn = new Piece(false, false, new Position(i, j));
					board_main[i][j] = -1;
					ai_main.getArmy().add(pawn);
				}
			}
			else {
				for(int j : oddHuman) {
					Piece pawn = new Piece(true, false, new Position(boardSize - i - 1, j));
					board_main[boardSize - i - 1][j] = 1;
					human_main.getArmy().add(pawn);
				}
				for(int j : evenHuman) {
					Piece pawn = new Piece(false, false, new Position(i, j));
					board_main[i][j] = -1;
					ai_main.getArmy().add(pawn);
				}
			}
		}
	}
	
	public void playTurn(Player machine, Player hum, int[][] tempBoard, int depth, boolean max_min) {
		Player artificial = null;
		Player human_intel = null;
		try {
			human_intel = (Player)hum.clone();
			artificial = (Player)machine.clone();
			int[][] board_copy = copy_board(tempBoard);
			List<Move> allMoves = getAllPossibleMoves(artificial, board_copy);
			if(!allMoves.isEmpty()) {
				if(depth == 0) {
					for(Move m0 : allMoves) {
						int[][] final_board = copy_board(tempBoard);
						Player h1 = (Player)hum.clone();
						Player m1 = (Player)machine.clone();
						updateBoard(m0, final_board, h1, m1);
						m0.profit = (-10)*stateValue(h1, m1, final_board);
					}
					Collections.sort(allMoves, new MoveCompare());
					if(max_min) {
						Move fina_move = allMoves.get(allMoves.size() - 1);
						updateBoard(fina_move, tempBoard, hum, machine);
						printBoard();
					}
					else {
						Move fina_move = allMoves.get(0);
						updateBoard(fina_move, tempBoard, hum, machine);
						printBoard();
					}
					return;
				} else {
					for(Move m0 : allMoves) {
						int[][] final_board = copy_board(tempBoard);
						Player h1 = (Player)hum.clone();
						Player m1 = (Player)machine.clone();
						playTurn(m1,h1,final_board,depth--, max_min, m0);
					}
				}
			} else {
				System.out.println("AI is has ran out moves :( You win");
				//System.exit(0);
				return;
			}
			if(debug) System.out.println("From " + allMoves.get(0).current.x + " " + allMoves.get(0).current.y  + " New Location " + allMoves.get(0).newLocation.x + " " + allMoves.get(0).newLocation.y);
			updateBoard(allMoves.get(0), board_copy,artificial, human_intel);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void playTurn(Player machine, Player hum, int[][] tempBoard, int depth, boolean max_min, Move parentMove) {
		Player artificial = null;
		Player human_intel = null;
		try {
			human_intel = (Player)hum.clone();
			artificial = (Player)machine.clone();
			int[][] board_copy = copy_board(tempBoard);
			List<Move> allMoves = getAllPossibleMoves(artificial, board_copy);
			if(!allMoves.isEmpty()) {
				if(depth == 0) {
					for(Move m0 : allMoves) {
						int[][] final_board = copy_board(tempBoard);
						Player h1 = (Player)hum.clone();
						Player m1 = (Player)machine.clone();
						updateBoard(m0, final_board, h1, m1);
						m0.profit = (-10)*stateValue(h1, m1, final_board);
					}
					return;
				} else {
					for(Move m0 : allMoves) {
						int[][] final_board = copy_board(tempBoard);
						Player h1 = (Player)hum.clone();
						Player m1 = (Player)machine.clone();
						playTurn(m1,h1,final_board,depth--, max_min, m0);
					}
				}
				Collections.sort(allMoves, new MoveCompare());
				if(max_min) {
					Move fina_move = allMoves.get(allMoves.size() - 1);
					parentMove.profit =+ fina_move.profit;
				}
				else {
					Move fina_move = allMoves.get(0);
					parentMove.profit =+ fina_move.profit;
				}
			} else {
				System.out.println("AI is has ran out moves :( You win");
				System.exit(0);
				return;
			}
			if(debug) System.out.println("From " + allMoves.get(0).current.x + " " + allMoves.get(0).current.y  + " New Location " + allMoves.get(0).newLocation.x + " " + allMoves.get(0).newLocation.y);
			updateBoard(allMoves.get(0), board_copy,artificial, human_intel);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int stateValue(Player human, Player machine, int[][] givenBoard) {
		return human.getArmy().size() - machine.getArmy().size();
	}
	
	
	public List<Move> getAllPossibleMoves(Player p, int[][] tempBoard) {
		List<Move> allMoves = new ArrayList<Move>();
		int val = p.getTeamOn() ? 1 : -1;
		for(Piece pawn : p.getArmy()) {
			allMoves.addAll(getSinglePawnAllMoves(pawn.location, p.getTeamOn(), val, pawn.rank, tempBoard));
		}
		for(Move m : allMoves) {
			//if(debug) System.out.println("Move : " + m.current.x + " " + m.current.y + " " + m.newLocation.x + " " + m.newLocation.y + " " + m.mandatory);
			if(m.mandatory) {
				List<Move> singleMove = new ArrayList<Move>();
				singleMove.add(m);
				return singleMove;
			}
		}
		return allMoves;
	}
	
	public void updateBoard(Move m, int[][] gameBoard, Player human1, Player machine) {
		//if(debug) System.out.println("Move : " + m.current.x + " " + m.current.y + " " + m.newLocation.x + " " + m.newLocation.y + " Value " + gameBoard[m.current.x][m.current.y]);
		if(gameBoard[m.current.x][m.current.y] == 1) {
			for(Piece p : human1.getArmy()) {
				if((p.location.x == m.current.x) && (p.location.y == m.current.y)) {
					if(m.mandatory) {
						int diff_x = m.newLocation.x - m.current.x;
						int diff_y = m.newLocation.y - m.current.y;
						p.location.x = m.newLocation.x + diff_x;
						p.location.y = m.newLocation.y + diff_y;
						if(p.location.x == 0) p.rank = true; 
						gameBoard[p.location.x][p.location.y] = 1;
						int removeIdx = -1;
						for(int i = 0; i < machine.getArmy().size(); i++) {	
							if((machine.getArmy().get(i).location.x == m.newLocation.x) && (machine.getArmy().get(i).location.y == m.newLocation.y)) {
								removeIdx = i;
								break;
							}
						}
						machine.getArmy().remove(removeIdx);
						gameBoard[m.current.x][m.current.y] = 0;
						gameBoard[m.newLocation.x][m.newLocation.y] = 0;
						return;
					}
					else {
						p.location.x = m.newLocation.x;
						p.location.y = m.newLocation.y;
						if(p.location.x == 0) p.rank = true; 
						gameBoard[m.newLocation.x][m.newLocation.y] = 1;
					}
				}
			}
			/*
			while(hasToJumpAgain(human1, machine, gameBoard)) {
				if(debug) System.out.println("Continuing the jump ..");
				getUserInput(human1, machine, gameBoard);
				printBoard();
			}
			*/
		} else {
			for(Piece p : machine.getArmy()) {
				if((p.location.x == m.current.x) && (p.location.y == m.current.y)) {
					if(m.mandatory) {
						int diff_x = m.newLocation.x - m.current.x;
						int diff_y = m.newLocation.y - m.current.y;
						p.location.x = m.newLocation.x + diff_x;
						p.location.y = m.newLocation.y + diff_y;
						if(p.location.x == 7) p.rank = true;
						gameBoard[p.location.x][p.location.y] = -1;
						int removeIdx = -1;
						for(int i = 0; i < human1.getArmy().size(); i++) {	
							if((human1.getArmy().get(i).location.x == m.newLocation.x) && (human1.getArmy().get(i).location.y == m.newLocation.y)) {
								removeIdx = i;
								break;
							}
						}
						human1.getArmy().remove(removeIdx);
						gameBoard[m.current.x][m.current.y] = 0;
						gameBoard[m.newLocation.x][m.newLocation.y] = 0;
						return;
					}
					else {
						p.location.x = m.newLocation.x;
						p.location.y = m.newLocation.y;
						if(p.location.x == boardSize - 1) p.rank = true;
						gameBoard[m.newLocation.x][m.newLocation.y] = -1;
					}
					break;
				}
			}
			/*
			while(hasToJumpAgain(machine, human1, gameBoard)) {
				if(debug) System.out.println("Continuing the jump ..");
				playAIMove();
				printBoard();
				
			}
			*/
		}
		gameBoard[m.current.x][m.current.y] = 0;
	}
	
	public ArrayList<Move> getSinglePawnAllMoves(Position l, boolean direction, int val, boolean king, int[][] tempBoard) {
		//if(debug) System.out.println("Pawn Location : " + l.x + " " + l.y + " val  " + val + " King  " + king);
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
					int diff_x = m.newLocation.x - m.current.x;
					int diff_y = m.newLocation.y - m.current.y;
					if(tempBoard[m.newLocation.x][m.newLocation.y] + val == 0 && validCapture(m, diff_x, diff_y, tempBoard)) {
						validMoves.clear();
						validMoves.add(m);
						m.mandatory = true;
						return validMoves;
					}
					if(tempBoard[m.newLocation.x][m.newLocation.y] == 0) validMoves.add(m);
				} else {
					if(king) {
						int diff_x = m.newLocation.x - m.current.x;
						int diff_y = m.newLocation.y - m.current.y;
						if(tempBoard[m.newLocation.x][m.newLocation.y] + val == 0 && validCapture(m, diff_x, diff_y, tempBoard)) {
							validMoves.clear();
							validMoves.add(m);
							m.mandatory = true;
							return validMoves;
						}
						if(tempBoard[m.newLocation.x][m.newLocation.y] == 0) validMoves.add(m);
					}
				}
			} else {
				
				if(x < m.newLocation.x) {
					int diff_x = m.newLocation.x - m.current.x;
					int diff_y = m.newLocation.y - m.current.y;
					if(tempBoard[m.newLocation.x][m.newLocation.y] + val == 0  && validCapture(m, diff_x, diff_y, tempBoard)) {
						validMoves.clear();
						validMoves.add(m);
						m.mandatory = true;
						return validMoves;
					}
					if(tempBoard[m.newLocation.x][m.newLocation.y] == 0) validMoves.add(m);
				} else {
					if(king) {
						int diff_x = m.newLocation.x - m.current.x;
						int diff_y = m.newLocation.y - m.current.y;
						if(tempBoard[m.newLocation.x][m.newLocation.y] + val == 0  && validCapture(m, diff_x, diff_y, tempBoard)) {
							validMoves.clear();
							validMoves.add(m);
							m.mandatory = true;
							return validMoves;
						}
						if(tempBoard[m.newLocation.x][m.newLocation.y] == 0) validMoves.add(m);
					}
				}
			}
		}
		return validMoves;
	}
	
	
	private boolean validCapture(Move m, int diff, int direction, int[][] tempBoard) {
		// TODO Auto-generated method stub
		if(m.newLocation.x + diff < 0 || m.newLocation.x + diff > 7 || m.newLocation.y + direction < 0 || m.newLocation.y + direction > 7) {
			return false;
		}
		if(tempBoard[m.newLocation.x + diff][m.newLocation.y + direction] == 0) return true;
		return false; 
	}

	public void start() {
		// TODO Auto-generated method stub

		//playTurn();
		//printBoard();
		/*
		while(hasToJumpAgain(ai_main, human_main, board_main)) {
			playTurn();
			printBoard();
		}
		*/
		//getUserInput();
		printBoard();
		Scanner sc = new Scanner(System.in);
		autoHumanMove();
		printBoard();
		sc.nextLine();
		playAIMove();
		//printBoard(); 
		/*
		if(debug) {
			for(Piece p : human_main.getArmy()) {
				System.out.println("Human army : " + p.location.x + " " + p.location.y + " rank " + p.rank);
			}
			for(Piece p : ai_main.getArmy()) {
				System.out.println("AI army : " + p.location.x + " " + p.location.y  + " rank " + p.rank);
			}
		}
		*/
		start();
	}
	
	public void autoHumanMove() {
		Move best_possible = getBestPossibleMove(board_main, ai_main, human_main, depth, true);
		if(best_possible == null) {
			System.out.println("You Lose :)");
			System.exit(0);
		}
		if(debug) System.out.println("Playing Human AI Turn : " + best_possible.current.x + " "  + best_possible.current.y + " -> " + best_possible.newLocation.x + " " + best_possible.newLocation.y);
		updateBoard(best_possible, board_main, human_main, ai_main);
	}
	
	public void playAIMove() {
		Move best_possible = getBestPossibleMove(board_main, human_main, ai_main, depth, true);
		if(best_possible == null) {
			System.out.println("you win :)");
			System.exit(0);
		}
		if(debug) System.out.println("AI Turn : " + best_possible.current.x + " "  + best_possible.current.y + " -> " + best_possible.newLocation.x + " " + best_possible.newLocation.y);
		updateBoard(best_possible, board_main, human_main, ai_main);
	}
	
	public Move getBestPossibleMove(int[][] grid, Player human, Player ai, int depth, boolean min_max) {
		Move best = null;
		List<Move> allMoves = getAllPossibleMoves(ai, grid);
		if(!allMoves.isEmpty()) {
			if(depth == 0) {
				for(Move m0 : allMoves) {
					int[][] final_board = copy_board(grid);
					Player h1;
					Player m1; 
					try {
						h1 = (Player)human.clone();
						m1 = (Player)ai.clone();
						updateBoard(m0, final_board, h1, m1);
						m0.profit = ai.getTeamOn() ? (10)*stateValue(h1, m1, final_board) : (-10)*stateValue(h1, m1, final_board);
						//System.out.println("Profit value : " + m0.profit);
					}
					 catch (CloneNotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				Collections.sort(allMoves, new MoveCompare());
				Move fina_move;
				if(min_max) {
					fina_move = getRandomOfBestValues(allMoves);
				}
				else {
					fina_move = getRandomOfWorsttValues(allMoves);
				}
				return fina_move;
			}
		}
		return best;
	}
	
	public Move getRandomOfWorsttValues(List<Move> allMoves) {
		int i = allMoves.size();
		Random rand = new Random();
		int	min  = i - 1;
		double min_val = allMoves.get(min).profit;
		i--;
		while(i > 0) {
			if(allMoves.get(i).profit == min_val) {
				i--;
			}
			else break;
		}
		int randomNum = rand.nextInt((min - i) + 1) + i;
		return allMoves.get(randomNum);
	}
	
	public Move getRandomOfBestValues(List<Move> allMoves) {
		int i = 0;
		Random rand = new Random();
		int	min  = 0;
		
		double max = allMoves.get(0).profit;
		while(i < allMoves.size()) {
			if(allMoves.get(i).profit == max) {
				i++;
			}
			else break;
		}
		int randomNum = rand.nextInt((i - min) + 1) + min;
		if(randomNum > allMoves.size() - 1) randomNum--;
		if(randomNum < 0) randomNum++; 
		return allMoves.get(randomNum);
	}
	
	public void playTurn() {
		playTurn(ai_main, human_main, board_main, depth, true);
	}
	public void printBoard() {
		printBoard(board_main);
	}
	
	public boolean hasToJumpAgain(Player p, Player opp, int[][] tempBoard) {
		List<Move> allmoves = getAllPossibleMoves(p,tempBoard);
		if(!allmoves.isEmpty()) return allmoves.get(0).mandatory;
		return false;
	}
	
	public int[][] copy_board(int[][] boardToCopy) {
		int[][] newBoard = new int[boardSize][boardSize];
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				newBoard[i][j] = boardToCopy[i][j];
			}
		}
		return newBoard;
	}
	
	public void getUserInput() {
		getUserInput(human_main, ai_main, board_main);
	}
	
	public void getUserInput(Player human, Player machine, int[][] tempBoard) {
		// TODO Auto-generated method stub
		/*
		Scanner inp = new Scanner(System.in);
		int x1 = inp.nextInt();
		int y1 = inp.nextInt();
		int x2 = inp.nextInt();
		int y2 = inp.nextInt();
		*/
		try {
			Player human_intel = (Player)human.clone();
			Player artificial = (Player)machine.clone();

			int[][] board_copy = copy_board(tempBoard);
			List<Move> moves = getAllPossibleMoves(human_intel, board_copy);
			if(moves.isEmpty())  {
				System.out.println("You lose :(");
				System.exit(0);
				return;
			}
			for(Move m : moves) {
					updateBoard(m, tempBoard, human, machine);
					//printBoard();
					//start();
					return;
			}
			System.out.println("Invalid move, Please try again !");
			getUserInput(human, machine, tempBoard);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printBoard(int[][] boardToPrint) {
		for(int i = 0; i < boardSize; i++) {
			System.out.println(" ");
			for(int j = 0; j < boardSize; j++) {
				if(boardToPrint[i][j] == -1) {
					System.out.print("A ");
				} else if(boardToPrint[i][j] == 1) {
					System.out.print("B ");
				}
				else System.out.print("O ");
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
				if(playerBoard[i][j] == -1) {
					System.out.print("A ");
				} else if(playerBoard[i][j] == -1) {
					System.out.print("B ");
				}
				else System.out.print(playerBoard[i][j] + "  ");
			}
		}
		System.out.println(" ");
	}
	
	
}
