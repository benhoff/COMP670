import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

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
		// boolean aiIsBlack = state.isBlackTurn();

		int initialSearchDepth = 10;
		int initialAlpha = Integer.MIN_VALUE;
		int initialBeta = Integer.MAX_VALUE;
		boolean initialCalculateMax = true;
		
		SearchResult searchResult = search(state, initialSearchDepth, initialAlpha, initialBeta, initialCalculateMax);
		OthelloMove result = searchResult.move;
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
	
	protected List<Point> getValidMoves(OthelloGameState s)
	{
		List<Point> result = new ArrayList<Point>();
		for (int x = 0; x < 8; x++)
		{
			for (int y = 0; y < 8; y++)
			{
				// Check if this is valid move, if it's not, we'll continue on
				boolean isValidMove = s.isValidMove(x, y);
				if (isValidMove)
				{
					result.add(new Point(x, y));
				}
			}
		}
		return result;
	}

	protected SearchResult search(OthelloGameState s, int depth, int alpha, int beta, boolean calculateMax)
	{
		// FIXME: Your AI will be given 5 seconds of CPU time to choose each of its moves. Need to implement this
		int evaluation = getEvaluation(s);
		if (depth == 0 || s.gameIsOver())
		{
			return new SearchResult(evaluation, null);
		}
		
		// Create a default `bestMove` that we can replace
		SearchResult bestMove = new SearchResult(0, null);
		
		List<Point> validMoves = getValidMoves(s);
		// int possibleMoves = validMoves.size();
		// TODO: use number of available moves to increase the depth dynamically
		
		int maxEval = Integer.MIN_VALUE;
		int minEval = Integer.MAX_VALUE;

		for (Point move : validMoves)
		{
			// Create a reference that we can put our move result into
			SearchResult moveSearchResult;
			// clone the state so that we don't mess up the original
			OthelloGameState movedState = s.clone();
			// Make the move
			movedState.makeMove(move.x, move.y);
			
			// We need to calculate either the min or the max score. If it's the other players turn, we want
			// to calculate their minimum. In this game, turns don't always alternate 1 for 1, so we need to check if
			// we get another turn based on the move we made.
			boolean sameNextTurn = s.isBlackTurn() == movedState.isBlackTurn();
			
			// If it's the same next turn, we'll keep the `calculateMax` variable the same
			if (sameNextTurn)
			{
				moveSearchResult = search(movedState, depth - 1, maxEval, minEval, calculateMax);
			}
			// If it's NOT the same next turn, we'll flip the `calculateMax`
			else
			{
				moveSearchResult = search(movedState, depth - 1, maxEval, minEval, !calculateMax);
			}
			
			
			if (calculateMax)
			{
				alpha = Math.max(alpha, moveSearchResult.score);
				if (bestMove.score < moveSearchResult.score)
				{
					bestMove = moveSearchResult;
				}

			}
			else {
				beta = Math.min(beta, moveSearchResult.score);
				if (bestMove.score > moveSearchResult.score)
				{
					bestMove = moveSearchResult;
				}
			}
			if (beta <= alpha)
			{
				break;
			}
		}
		
		// FIXME: handle the edge case that all the scores are 0 by randomly selecting a valid move
		if (bestMove.move == null)
		{
			
		}
		
		return bestMove;
	}
}
