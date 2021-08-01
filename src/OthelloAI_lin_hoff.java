import java.util.ArrayList;
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

			// TODO: use number of available moves to increase the depth dynamically
			List<OthelloMove> initialValidMoves = getValidMoves(gameState);
			if (this.amBlack) {
				System.out.println(getNumTilesOnBoard(gameState) + " tiles on board: Black considering " + initialValidMoves.size()
						+ " valid moves.");
			} else {
				System.out.println(getNumTilesOnBoard(gameState) + " tiles on board: White considering " + initialValidMoves.size()
						+ " valid moves.");
			}
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
//			System.out.print("Analyzing move " + i + "/" + (validMoves.size() - 1));

			// Break if past the time limit
			long loopStartTime = System.currentTimeMillis();
			long elapsedTime = loopStartTime - moveStartTime;
			if (elapsedTime > TIME_LIMIT_MILLIS) {
				if (originalCall) {
					System.out.println("****** Time's up! Breaking out. Elapsed time: " + elapsedTime + " ms *****");
				}
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

		if (originalCall) {
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - this.moveStartTime;
			System.out.println("    Finished processing after " + elapsedTime + " ms.");
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
		int numTilesOnBoard = getNumTilesOnBoard(gameState);
		if (numTilesOnBoard < 7) {
			// max tiles left = 60
			return 7;
		} else if (numTilesOnBoard >= 7 && numTilesOnBoard < 14) {
			// max tiles left = 53
			return 6;
		} else if (numTilesOnBoard >= 14 && numTilesOnBoard < 24) {
			// max tiles left = 50
			return 6;
		} else if (numTilesOnBoard >= 24 && numTilesOnBoard < 34) {
			// max tiles left = 40
			return 7;
		} else if (numTilesOnBoard >= 34 && numTilesOnBoard < 44) {
			// max tiles left = 30
			return 6;
		} else if (numTilesOnBoard >= 44 && numTilesOnBoard < 52) {
			// max tiles left = 20
			return 7;
		} else {
			return MAX_DEPTH;
		}
	}

	private int getNumTilesOnBoard(OthelloGameState gameState) {
		return gameState.getBlackScore() + gameState.getWhiteScore();
	}
}
