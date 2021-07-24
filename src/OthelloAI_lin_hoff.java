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

	private SearchResult getBestMoveAndScore(OthelloGameState s, int depth, int currentMax, int currentMin, boolean calculateMax) {
		long searchStartTime = System.currentTimeMillis();
		int currentStateValue = getEvaluation(s);
		if (depth == 0 || s.gameIsOver()) {
			return new SearchResult(currentStateValue, null);
		}

		List<Point> validMoves = getValidMoves(s);
		int numValidMoves = validMoves.size();
		System.out.println("There are " + numValidMoves + " valid moves.");

		int maxEval = Integer.MIN_VALUE;
		int minEval = Integer.MAX_VALUE;
		SearchResult bestMove = new SearchResult(0, null);

		int currentMove = 1;
		for (Point move : validMoves) {
			System.out.println("Analyzing valid move #" + currentMove);
			currentMove++;

			// Break if past the time limit
			long timeLimitMillis = 100; // TODO: set this as close to 5000 as possible
			long loopStartTime = System.currentTimeMillis();
			if (loopStartTime - searchStartTime > timeLimitMillis) {
				break;
			}

			SearchResult currentBestMoveAndScore;
			OthelloGameState state = s.clone();
			state.makeMove(move.x, move.y);

			// Calculate min or max depending on whose turn it is
			boolean currentPlayerGetsAnotherTurn = s.isBlackTurn() == state.isBlackTurn();
			if (currentPlayerGetsAnotherTurn) {
				// If it's the same player next turn, keep `calculateMax` variable the same
				currentBestMoveAndScore = getBestMoveAndScore(state, depth - 1, maxEval, minEval, calculateMax);
			} else {
				// If it's NOT the same player next turn, flip the `calculateMax`
				currentBestMoveAndScore = getBestMoveAndScore(state, depth - 1, maxEval, minEval, !calculateMax);
			}

			if (calculateMax) {
				currentMax = Math.max(currentMax, currentBestMoveAndScore.score);
				if (bestMove.score < currentBestMoveAndScore.score) {
					bestMove = currentBestMoveAndScore;
				}

			} else {
				currentMin = Math.min(currentMin, currentBestMoveAndScore.score);
				if (bestMove.score > currentBestMoveAndScore.score) {
					bestMove = currentBestMoveAndScore;
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
		if (bestMove.move == null) {
			bestMove.move = new OthelloMove(validMoves.get(0).x, validMoves.get(0).y);
		}

		return bestMove;
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

	private List<Point> getValidMoves(OthelloGameState s) {
		List<Point> result = new ArrayList<Point>();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				// Check if this is valid move, if it's not, we'll continue on
				boolean isValidMove = s.isValidMove(x, y);
				if (isValidMove) {
					result.add(new Point(x, y));
				}
			}
		}
		return result;
	}
}
