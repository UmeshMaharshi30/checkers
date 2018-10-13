package game;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
	int depth = 4;

	String stateFile = "StateFile.txt";

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


	public double stateValue(Player human, Player machine, int[][] givenBoard) {
		return human.getProfit() - machine.getProfit();
	}


	public List<Move> getAllPossibleMoves(Player p, int[][] tempBoard) {
		List<Move> allMoves = new ArrayList<Move>();
		List<Move> allMandatory = new ArrayList<Move>();
		int val = p.getTeamOn() ? 1 : -1;
		for(Piece pawn : p.getArmy()) {
			if(pawn.team) if(pawn.location.x == 0) pawn.rank = true;
			if(!pawn.team) if(pawn.location.y == 7) pawn.rank = true;
			allMoves.addAll(getSinglePawnAllMoves(pawn.location, p.getTeamOn(), val, pawn.rank, tempBoard));
		}
		for(Move m : allMoves) {
			//if(debug) System.out.println("Move : " + m.current.x + " " + m.current.y + " " + m.newLocation.x + " " + m.newLocation.y + " " + m.mandatory);
			if(m.mandatory) {
				List<Move> singleMove = new ArrayList<Move>();
				singleMove.add(m);
				allMandatory.add(m);
			}
		}
		if(allMandatory.size() > 0) return allMandatory; 
		return allMoves;
	}

	public void updateBoard(Move m, int[][] gameBoard, Player human1, Player machine) {
		//if(debug) System.out.println("Move : " + m.current.x + " " + m.current.y + " " + m.newLocation.x + " " + m.newLocation.y + " Value " + gameBoard[m.current.x][m.current.y]);
		if(gameBoard[m.current.x][m.current.y] == 1) {
			for(Piece p : human1.getArmy()) {
				if(p.location.x == 0) p.rank = true;
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
			while(hasToJumpAgain(human1, machine, gameBoard) && m.mandatory) {
				Move m1 = getBestPossibleMove(gameBoard,machine, human1, 0, true);
				updateBoard(m1, gameBoard, human1, machine);
			}
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
			while(hasToJumpAgain(machine, human1, gameBoard)  && m.mandatory) {
				Move m1 = getBestPossibleMove(gameBoard,human1, machine, 0, true);
				updateBoard(m1, gameBoard, human1, machine);
			}
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
						//System.out.println("King Location " + m.newLocation.x + " " + m.newLocation.y);	
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

	public void initSetup() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome to Alpha Beta Game");
		System.out.println("Enter digit for following functions :");
		System.out.println("1 : Enter a state and Agent will return a move to be made for AI");
		System.out.println("2 : Enter a state and Agent will return its Evaluation value");
		System.out.println("3 : Enter a state and Agent will return all possible legal moves");
		System.out.println("4 : To start a game Agent Vs Agent");
		System.out.println("5 : To start a game Agent Vs Human");
		System.out.println("6 : To exit");
		System.out.println("Note, very function takes the file as start input");
		System.out.println("Have fun !");
		int menu = 0;
		while(menu != 6) {
			menu = sc.nextInt();
			switch (menu) {
			case 1:
				readFileUpdateState();
				System.out.println("Note it will exit if the game can be decided in few steps !");
				Move m = getBestPossibleMove(board_main, human_main, ai_main, 4, true);
				if(m != null) System.out.println("Best Possible Move " + m.current.x + " " + m.current.y + " -> " + m.newLocation.x + " " + m.newLocation.y);
				else System.out.println("No moves possible");
				break;
			case 2:
				readFileUpdateState();
				System.out.println("Evaluation for B " + human_main.getProfit());
				System.out.println("Evaluation for A " + ai_main.getProfit());
				break;
			case 3:
				readFileUpdateState();
				List<Move> allMoves_B = getAllPossibleMoves(human_main, board_main);
				System.out.println("For Player B : " + allMoves_B.size());
				for(Move m1 : allMoves_B) {
					System.out.println(m1.current.x + " " + m1.current.y + " -> " + m1.newLocation.x + " " + m1.newLocation.y);
				}
				List<Move> allMoves_A = getAllPossibleMoves(ai_main, board_main);
				System.out.println("For Player A : " + allMoves_A.size());
				for(Move m2 : allMoves_A) {
					System.out.println(m2.current.x + " " + m2.current.y + " -> " + m2.newLocation.x + " " + m2.newLocation.y);
				}
				break;	
			case 4:
				readFileUpdateState();
				start(true);
				break;
			case 5:
				readFileUpdateState();
				start(false);
				break;	
			case 6:
				System.out.println("Thank you for playing");
				break;	
			default:
				System.out.println("Invalid Key !");
				break;
			}
		}
	}

	private void readFileUpdateState() {
		// TODO Auto-generated method stub
		System.out.println("Please make sure there is file name StateFile.txt in the home directory");
		System.out.println("Also, Use A for AI and B for Human and O for empty");
		File file = new File(stateFile); 
		String st; 
		board_main = new int[boardSize][boardSize];
		human_main = new Player();
		ai_main = new Player();
		human_main.setTeamOn(true);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));   
			int row = 0;
			while ((st = br.readLine()) != null) { 
				int col = 0;
				st = st.trim();
				String[] stateLine = st.split(" ");
				for(String c : stateLine) {
					if(c.equals("B")) {
						boolean rank = false;
						if(row == 0) rank = true;
						Piece pawn = new Piece(true, rank, new Position(row, col));
						board_main[row][col] = 1;
						pawn.rank = rank;
						human_main.getArmy().add(pawn);
					} else if(c.equals("A")) {
						boolean rank = false;
						if(row == 7) rank = true;
						Piece pawn = new Piece(false, rank, new Position(row, col));
						pawn.rank = rank;
						board_main[row][col] = -1;
						ai_main.getArmy().add(pawn);
					}
					col++;
				}
				row++;    
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public void start(boolean auto) {
		// TODO Auto-generated method stub
		//playTurn();
		printBoard();
		if(!auto) {
			System.out.println("Your Turn, use format 0 1 1 2, where (0,1) is the coin position and (1,2) is the new position");
			System.out.println("It will automatically capture if that is legal move !");
		}
		/*
		while(hasToJumpAgain(ai_main, human_main, board_main)) {
			playTurn();
			printBoard();
		}
		 */
		if(auto) autoHumanMove(human_main, ai_main, board_main);
		else getUserInput(human_main, ai_main, board_main);
		printBoard();
		//Scanner sc = new Scanner(System.in);
		//autoHumanMove();
		//printBoard();
		playAIMove();
		//sc.nextLine();

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
		start(auto);
	}

	public void autoHumanMove(Player human, Player machine, int[][] tempBoard) {


		try {
			Player human_intel = (Player)human.clone();
			Player artificial = (Player)machine.clone();

			int[][] board_copy = copy_board(tempBoard);
			List<Move> moves = getAllPossibleMoves(human_intel, board_copy);
			if(moves.isEmpty())  {
				System.out.println("You/Player B lose :(");
				System.exit(0);
				return;
			}
			/*
			System.out.println("Valid moves ");
			for(Move m : moves) {
				System.out.println(m.current.x + " " + m.current.y + " " + m.newLocation.x + " " + m.newLocation.y);
			}
			System.out.println("Mandatory moves ");
			for(Move m : moves) {
				if(m.mandatory) System.out.println(m.current.x + " " + m.current.y + " " + m.newLocation.x + " " + m.newLocation.y);
			}
			 */
			Move best_possible = getRandomOfBestValues(moves);
			if(debug) System.out.println("Human Auto : " + best_possible.current.x + " "  + best_possible.current.y + " -> " + best_possible.newLocation.x + " " + best_possible.newLocation.y);
			updateBoard(best_possible, tempBoard, human, machine);
			return;
			//System.out.println("Invalid move, Please try again !");
			//getUserInput(human, machine, tempBoard);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public Move getBestPossibleMove(int[][] grid, Player opp, Player player, int depth, boolean min_max) {
		if(depth < 0) return null;
		int tempDepth = depth;
		Move best = null;
		List<Move> allMoves = getAllPossibleMoves(player, grid);
		if(!allMoves.isEmpty()) {
			if(depth == 0) { 
				for(Move m0 : allMoves) {
					int[][] final_board = copy_board(grid);
					Player h1;
					Player m1; 
					try {
						h1 = (Player)opp.clone();
						m1 = (Player)player.clone();
						updateBoard(m0, final_board, h1, m1);
						m0.profit = player.getTeamOn() ? (10)*stateValue(h1, m1, final_board) : (-10)*stateValue(h1, m1, final_board);
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
			} else {
				for(Move m0 : allMoves) {
					int[][] final_board = copy_board(grid);
					Player h1;
					Player m1; 
					try {
						h1 = (Player)opp.clone();
						m1 = (Player)player.clone();
						printBoard(final_board);
						updateBoard(m0, final_board, h1, m1);
						m0.profit = player.getTeamOn() ? (10)*stateValue(h1, m1, final_board) : (-10)*stateValue(h1, m1, final_board);
						autoHumanMove(h1, m1, final_board);
						printBoard(final_board);
						Move c1 = getBestPossibleMove(final_board, h1, m1, tempDepth - 1, !min_max);
						if(c1 != null) m0.profit = m0.profit + c1.profit;
						//System.out.println("Profit value : " + m0.profit);
					}
					catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			Collections.sort(allMoves, new MoveCompare());
			/*
			for(Move m : allMoves) {
				System.out.println("Move value : " + m.current.x + " " + m.current.y + " " + m.newLocation.x + " " + m.newLocation.y + " " +m.profit);
			}
			*/
			Move fina_move;
			if(min_max) {
				fina_move = getRandomOfBestValues(allMoves);
			}
			else {
				fina_move = getRandomOfWorsttValues(allMoves);
			}
			return fina_move;
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

	public void printBoard() {
		printBoard(board_main);
	}

	public boolean hasToJumpAgain(Player p, Player opp, int[][] tempBoard) {
		List<Move> allmoves = getAllPossibleMoves(p,tempBoard);
		/*
		for(Move m : allmoves) {
			System.out.println("Move : " + m.mandatory + " " + m.current.x + " " + m.current.y + " " + m.newLocation.x + " " + m.newLocation.y);
		}
		 */
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

	public void getUserInput(Player human, Player machine, int[][] tempBoard) {
		// TODO Auto-generated method stub

		Scanner inp = new Scanner(System.in);
		int x1 = inp.nextInt();
		int y1 = inp.nextInt();
		int x2 = inp.nextInt();
		int y2 = inp.nextInt();

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
			System.out.println("Valid moves ");
			for(Move m : moves) {
				System.out.println(m.current.x + " " + m.current.y + " " + m.newLocation.x + " " + m.newLocation.y);
			}
			for(Move m : moves) {
				if((m.current.x == x1 && m.current.y == y1) && (m.newLocation.x == x2 && m.newLocation.y == y2)) {
					updateBoard(m, tempBoard, human, machine);
					return;
				}
				/*
					updateBoard(m, tempBoard, human, machine);
					return;
				 */
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
