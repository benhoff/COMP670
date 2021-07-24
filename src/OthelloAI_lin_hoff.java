import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class OthelloAI_lin_hoff implements OthelloAI {
	class SearchResult {
		public int score;
		public OthelloMove move;

		public SearchResult(int inScore, OthelloMove inMove) {
			score = inScore;
			move = inMove;
		}
	}

	public OthelloMove chooseMove(OthelloGameState state) {
		if (state.gameIsOver()) {
			System.out.println("Game is over; returning null");
			return null;
		} else {
			int searchDepth = 10;
			int currentMax = Integer.MIN_VALUE;
			int currentMin = Integer.MAX_VALUE;
			boolean initialCalculateMax = true;

			// TODO: use number of available moves to increase the depth dynamically
			return getBestMoveAndScore(state, searchDepth, currentMax, currentMin, initialCalculateMax).move;
		}
	}

	private SearchResult getBestMoveAndScore(OthelloGameState currentState, int depth, int currentMax, int currentMin,
			boolean shouldCalculateMax) {
		long searchStartTime = System.currentTimeMillis();
		int currentStateValue = getEvaluation(currentState);
		if (depth == 0 || currentState.gameIsOver()) {
			return new SearchResult(currentStateValue, null);
		}

		List<OthelloMove> validMoves = getValidMoves(currentState);
		int numValidMoves = validMoves.size();
		System.out.println("There are " + numValidMoves + " valid moves.");

		int maxEval = Integer.MIN_VALUE;
		int minEval = Integer.MAX_VALUE;
		SearchResult currentBestMove = new SearchResult(0, validMoves.get(0));

		for (int i = 1; i < numValidMoves; i++) {
			System.out.println("Analyzing valid move #" + i);

			// Break if past the time limit
			long timeLimitMillis = 100; // TODO: set this as close to 5000 as possible
			long loopStartTime = System.currentTimeMillis();
			if (loopStartTime - searchStartTime > timeLimitMillis) {
				break;
			}

			SearchResult currentBestMoveAndScore;
			OthelloGameState state = currentState.clone();
			OthelloMove currentMove = validMoves.get(i);
			state.makeMove(currentMove.getRow(), currentMove.getColumn());

			// Calculate min or max depending on whose turn it is
			boolean currentPlayerGetsAnotherTurn = currentState.isBlackTurn() == state.isBlackTurn();
			if (currentPlayerGetsAnotherTurn) {
				// If it's the same player next turn, keep `calculateMax` variable the same
				currentBestMoveAndScore = getBestMoveAndScore(state, depth - 1, maxEval, minEval, shouldCalculateMax);
			} else {
				// If it's NOT the same player next turn, flip the `calculateMax`
				currentBestMoveAndScore = getBestMoveAndScore(state, depth - 1, maxEval, minEval, !shouldCalculateMax);
			}

			if (shouldCalculateMax) {
				currentMax = Math.max(currentMax, currentBestMoveAndScore.score);
				if (currentBestMove.score < currentBestMoveAndScore.score) {
					currentBestMove = currentBestMoveAndScore;
				}

			} else {
				currentMin = Math.min(currentMin, currentBestMoveAndScore.score);
				if (currentBestMove.score > currentBestMoveAndScore.score) {
					currentBestMove = currentBestMoveAndScore;
				}
			}

			long loopEndTime = System.currentTimeMillis();
			long loopDuration = loopEndTime - loopStartTime;
			System.out.println("Move loop duration: " + loopDuration + " ms");

			// Prune if possible
			if (currentMin <= currentMax) {
				break;
			}
		}

		// If no best move was determined, pick first valid move
		if (currentBestMove.move == null) {
			OthelloMove firstValidMove = validMoves.get(0);
			currentBestMove.move = new OthelloMove(firstValidMove.getRow(), firstValidMove.getColumn());
		}

		return currentBestMove;
	}

	private int getEvaluation(OthelloGameState state) {
		// FIXME: Research if we need to check which player we are?

		// TODO: consider better scoring method than just "# black - # white"
		int evaluation = 0;
		boolean isBlackTurn = state.isBlackTurn();
		int whiteScore = state.getWhiteScore();
		int blackScore = state.getBlackScore();
		if (isBlackTurn) {
			evaluation = blackScore - whiteScore;
		} else {
			evaluation = whiteScore - blackScore;
		}
		return evaluation;

	}

	private List<OthelloMove> getValidMoves(OthelloGameState s) {
		List<OthelloMove> result = new ArrayList<OthelloMove>();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				// Check if this is valid move, if it's not, we'll continue on
				boolean isValidMove = s.isValidMove(x, y);
				if (isValidMove) {
					result.add(new OthelloMove(x, y));
				}
			}
		}
		return result;
	}
}
