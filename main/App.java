package main;

import game.Checker;

public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean debug = true;
		Checker game = new Checker(debug);
		//game.start(true);
		game.initSetup();
	}

}
