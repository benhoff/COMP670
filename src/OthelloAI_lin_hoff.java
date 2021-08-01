import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OthelloAI_lin_hoff implements OthelloAI {
	private boolean amBlack;
	private int MAX_DEPTH = 60;
	private int TIME_LIMIT_MILLIS = 4980;
	private long moveStartTime;

	class MoveAndScore {
		public int score;
		public OthelloMove move;

		public MoveAndScore(OthelloMove inMove, int inScore) {
			move = inMove;
			score = inScore;
		}
	}

	public OthelloMove chooseMove(OthelloGameState gameState) {
		if (gameState.gameIsOver()) {
			return null;
		} else {
			this.moveStartTime = System.currentTimeMillis();
			this.amBlack = gameState.isBlackTurn();
			int initialSearchDepth = determineDepthLimit(gameState);

			List<OthelloMove> initialValidMoves = getValidMoves(gameState);
			return getBestMoveAndScore(gameState, initialSearchDepth, initialValidMoves, true).move;
		}
	}

	private MoveAndScore getBestMoveAndScore(OthelloGameState gameState, int depth, List<OthelloMove> validMoves,
			boolean originalCall) {
		// Set initial values
		OthelloMove bestMove = validMoves.get(0);
		int bestScore = Integer.MIN_VALUE;
		MoveAndScore bestMoveAndScore = new MoveAndScore(bestMove, bestScore);

		for (int i = 0; i < validMoves.size(); i++) {

			// Break if past the time limit
			long loopStartTime = System.currentTimeMillis();
			long elapsedTime = loopStartTime - moveStartTime;
			if (elapsedTime > TIME_LIMIT_MILLIS) {
				return bestMoveAndScore;
			}

			OthelloMove move = validMoves.get(i);
			OthelloGameState stateAfterMove = getNewState(gameState, move);

			int moveScore;
			if (depth == 0) {
				moveScore = getMyScore(stateAfterMove);
			} else {
				List<OthelloMove> childValidMoves = getValidMoves(stateAfterMove);
                Collections.shuffle(childValidMoves);

				if (childValidMoves.isEmpty()) {
					moveScore = getMyScore(stateAfterMove);
				} else {
					moveScore = getBestMoveAndScore(stateAfterMove, depth - 1, childValidMoves, false).score;
				}
			}

			if (maximizing(gameState)) {
				if (moveScore > bestScore) {
					bestScore = moveScore;
					bestMoveAndScore = new MoveAndScore(move, moveScore);
				}
			} else {
				if (moveScore < bestScore) {
					bestScore = moveScore;
					bestMoveAndScore = new MoveAndScore(move, moveScore);
				}
			}
		}

		return bestMoveAndScore;
	}

	private boolean maximizing(OthelloGameState state) {
		return this.amBlack && state.isBlackTurn();
	}

	private OthelloGameState getNewState(OthelloGameState state, OthelloMove move) {
		OthelloGameState newState = state.clone();
		newState.makeMove(move.getRow(), move.getColumn());
		return newState;
	}

	private int getMyScore(OthelloGameState state) {
		int whiteScore = state.getWhiteScore();
		int blackScore = state.getBlackScore();
		if (this.amBlack) {
			return blackScore - whiteScore;
		} else {
			return whiteScore - blackScore;
		}
	}

	private List<OthelloMove> getValidMoves(OthelloGameState gameState) {
		List<OthelloMove> result = new ArrayList<OthelloMove>();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (gameState.isValidMove(x, y)) {
					result.add(new OthelloMove(x, y));
				}
			}
		}
		return result;
	}

	private int determineDepthLimit(OthelloGameState gameState) {
		if (getNumTilesOnBoard(gameState) < 54) {
			return 6;
		} else {
			return MAX_DEPTH;
		}
	}

	private int getNumTilesOnBoard(OthelloGameState gameState) {
		return gameState.getBlackScore() + gameState.getWhiteScore();
	}
}
