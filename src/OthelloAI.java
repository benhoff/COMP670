// OthelloAI.java
//
// COMP 670 AI Game Project: Othello
//
// Your AI should be implemented in a class that implements this OthelloAI
// interface. Remember that your AI class needs to follow a specific
// naming convention.  It should be called "OthelloAI[StudentID#]". So, if
// your team leader's student ID# is 1234567, your AI class should be called
// "OthelloAI1234567".  Naturally, you should write that class in a file
// called "OthelloAI1234567.java".


public interface OthelloAI
{
	// chooseMove() takes an OthelloGameState and chooses the best move,
	// returning an OthelloMove that indicates what the move is.  For
	// example, if the appropriate move is to place a tile in row 0 column 3,
	// you'd return a new OthelloMove with row 0 and column 3.
	public OthelloMove chooseMove(OthelloGameState state);
}
