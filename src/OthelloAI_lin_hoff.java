import java.util.ArrayList;
import java.util.List;

public class OthelloAI_lin_hoff implements OthelloAI {
	private boolean amBlack;

	class MoveAndScore {
		public int score;
		public OthelloMove move;

		public MoveAndScore(OthelloMove inMove, int inScore) {
			move = inMove;
			score = inScore;
		}
	}

	public OthelloMove chooseMove(OthelloGameState state) {
		if (state.gameIsOver()) {
			return null;
		} else {
			this.amBlack = state.isBlackTurn();
			int initialSearchDepth = 7;

			// TODO: use number of available moves to increase the depth dynamically
			List<OthelloMove> initialValidMoves = getValidMoves(state);
			return getBestMoveAndScore(state, initialSearchDepth, initialValidMoves).move;
		}
	}

	private MoveAndScore getBestMoveAndScore(OthelloGameState currentState, int depth, List<OthelloMove> validMoves) {
		long searchStartTime = System.currentTimeMillis();

		// Set initial values
		OthelloMove bestMove = validMoves.get(0);
		OthelloGameState stateAfterMove = getNewState(currentState, bestMove);
		int bestScore = getEvaluation(stateAfterMove);
		MoveAndScore bestMoveAndScore = new MoveAndScore(bestMove, bestScore);

		int worstScore = bestScore; // since there is only one score, it is the best and the worst

		for (int i = 1; i < validMoves.size(); i++) {
			System.out.print("Analyzing move " + i + "/" + (validMoves.size() - 1));

			// Break if past the time limit
			long timeLimitMillis = 4500; // TODO: set this as close to 5000 as possible
			long loopStartTime = System.currentTimeMillis();
			if (loopStartTime - searchStartTime > timeLimitMillis) {
				System.out.println("****** Time's up! Breaking out. *****");
				return bestMoveAndScore;
			}

			OthelloMove move = validMoves.get(i);
			stateAfterMove = getNewState(currentState, move);
			int moveScore = getMyScore(stateAfterMove);

			if (depth == 0) {
				moveScore = getMyScore(stateAfterMove);
			} else {
				List<OthelloMove> childValidMoves = getValidMoves(stateAfterMove);
				if (childValidMoves.isEmpty()) {
					moveScore = getMyScore(stateAfterMove);
				} else {
					moveScore = getBestMoveAndScore(stateAfterMove, depth - 1, childValidMoves).score;
				}
			}

			if (maximizing(currentState)) {
				if (moveScore > bestScore) {
					bestScore = moveScore;
					bestMoveAndScore = new MoveAndScore(move, moveScore);
				} else if (moveScore < worstScore) {
					worstScore = moveScore;
				}
			} else {
				if (moveScore < bestScore) {
					bestScore = moveScore;
					bestMoveAndScore = new MoveAndScore(move, moveScore);
				} else if (moveScore > worstScore) {
					worstScore = moveScore;
				}
			}

			long loopEndTime = System.currentTimeMillis();
			long loopDuration = loopEndTime - loopStartTime;
			System.out.print("    " + loopDuration + " ms\n");
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

	private int getMyScore(OthelloGameState newState) {
		if (this.amBlack) {
			return newState.getBlackScore();
		} else {
			return newState.getWhiteScore();
		}
	}

	private int getEvaluation(OthelloGameState state) {
		int evaluation = 0;
		int whiteScore = state.getWhiteScore();
		int blackScore = state.getBlackScore();
		if (this.amBlack) {
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
				if (s.isValidMove(x, y)) {
					result.add(new OthelloMove(x, y));
				}
			}
		}
		return result;
	}
}
