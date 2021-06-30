// OthelloGameState.java
//
// COMP 670 AI Game Project: Othello
//
// An OthelloGameState represents the state of an Othello game at a given
// time.  You may not find all of these methods useful in your solution,
// though it's fine if you use all of them.


public interface OthelloGameState
extends Cloneable
{
	// getCell() returns the contents of the cell at the given row and
	// column.
	public OthelloCell getCell(int row, int col);


	// isBlackTurn() returns true if it's the black player's turn, false
	// if it's the white player's turn.
	public boolean isBlackTurn();


	// gameIsOver() returns true if the game is over.
	public boolean gameIsOver();


	// isValidMove() returns true if the given row and column represent
	// a valid move on behalf of the player whose turn it is, false if
	// not.
	public boolean isValidMove(int row, int col);
	
	
	// makeMove() places a tile in the given row and column on behalf of
	// the player whose turn it is, flipping any other tiles that should
	// be flipped as a result.  If the move is not valid for the current
	// player according to the rules of the game, an InvalidMoveException
	// is thrown.
	public void makeMove(int row, int col);
	
	
	// getBlackScore() returns the number of black tiles on the board.
	public int getBlackScore();


	// getWhiteScore() returns the number of white tiles on the board.
	public int getWhiteScore();


	// clone() creates a new OthelloGameState that is a copy of this one.
	// You'll use this in your AI to create temporary states to explore
	// the various possibilities.
	public OthelloGameState clone();
}
