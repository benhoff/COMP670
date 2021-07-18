
public class OthelloAI_lin_hoff implements OthelloAI {
	class SearchResult {
		public int score;
		public OthelloMove move;
		public SearchResult(int inScore, OthelloMove inMove)
		{
			score = inScore;
			move = inMove;
		}
	}
	public OthelloMove chooseMove(OthelloGameState state)
	{
		// boolean isGameOver = state.gameIsOver();
		boolean aiIsBlack = state.isBlackTurn();
		
		OthelloMove result = new OthelloMove(0,0);
		return result;
	}
	
	protected int getEvaluation(OthelloGameState state)
	{
		// FIXME: Research if we need to check which player we are?
		
		// eval(state) = number_of tiles belonging to me - number of tiles belonging to my opponent
		int evaluation = 0;
		boolean isBlackTurn = state.isBlackTurn();
		int whiteScore = state.getWhiteScore();
		int blackScore = state.getBlackScore();
		if (isBlackTurn)
		{
			evaluation = blackScore - whiteScore;
		}
		else
		{
			evaluation = whiteScore - blackScore;
		}
		return evaluation;
		
	}
	
	protected int evaluateNumberOfValidMoves(OthelloGameState s)
	{
		int count = 0;
		for (int x = 0; x < 8; x++)
		{
			for (int y = 0; y < 8; y++)
			{
				// row, column
				boolean isValidMove = s.isValidMove(x, y);
				if (isValidMove)
				{
					count += 1;
				}
			}
		}
		return count;
	}

	// FIXME: Your AI will be given 5 seconds of CPU time to choose each of its moves. Need to implement this
	protected SearchResult search(OthelloGameState s, boolean calculateMax, int depth)
	{
		int evaluation = getEvaluation(s);
		if (depth == 0)
		{
			return new SearchResult(evaluation, null);
		}
		
		
		// TODO: use number of available moves to increase the depth dynamically
		// int possibleMoves = evaluateNumberOfValidMoves(s);
		
		
		// Create a default `bestMove` that we can replace
		SearchResult bestMove = new SearchResult(0, null);
		
		
		// for each valid move that I can make
		for (int x = 0; x < 8; x++)
		{
			for (int y = 0; y < 8; y++)
			{
				// Check if this is valid move, if it's not, we'll continue on
				boolean isValidMove = s.isValidMove(x, y);
				if (!isValidMove)
				{
					continue;
				}
				// Create a reference that we can put our move result into
				SearchResult moveSearchResult = null;
				// clone the state so that we don't mess up the original
				OthelloGameState movedState = s.clone();
				// Make the move
				movedState.makeMove(x, y);
				
				// We need to calculate either the min or the max score. If it's the other players turn, we want
				// to calculate their minimum. In this game, turns don't always alternate 1 for 1, so we need to check if
				// we get another turn based on the move we made.
				boolean sameNextTurn = s.isBlackTurn() == movedState.isBlackTurn();
				
				// If it's the same next turn, we'll keep the `calculateMax` variable the same
				if (sameNextTurn)
				{
					moveSearchResult = search(movedState, calculateMax, depth - 1);
				}
				// If it's NOT the same next turn, we'll flip the `calculateMax`
				else
				{
					moveSearchResult = search(movedState, !calculateMax, depth - 1);
				}
				
				
				if (calculateMax)
				{
					if (bestMove.score < moveSearchResult.score)
					{
						bestMove = moveSearchResult;
					}
				}
				else {
					if (bestMove.score > moveSearchResult.score)
					{
						bestMove = moveSearchResult;
					}
				}
			}
			
		}
		
		// FIXME: handle the edge case that all the scores are 0 by randomly selecting a valid move
		if (bestMove.move == null)
		{
			
		}
		
		return bestMove;
	}
}
