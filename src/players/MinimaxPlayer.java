package players;

import evaluation.Evaluator;
import game.Board;

public class MinimaxPlayer extends MinimaxPlayerBase {

    public MinimaxPlayer(int player, Evaluator evaluator, int maxDepth) {
        super(player, evaluator, maxDepth);
    }

    @Override
    protected int searchNode(Board board, int currentPlayer, int depth, int alpha, int beta) {
        if (isTerminal(board)) {
            return terminalScore(board, depth);
        }
        if (depth == maxDepth) {
            return evaluator.evaluate(board);
        }

        int[] validMoves = orderedMoves(board);

        int best;
        if (currentPlayer == player) {
            best = Integer.MIN_VALUE;
            for (int move : validMoves) {
                Board successor = new Board(board);
                successor.make_move(currentPlayer, move);
                best = Math.max(best, search(successor, opponent, depth + 1, alpha, beta));
            }
        } else {
            best = Integer.MAX_VALUE;
            for (int move : validMoves) {
                Board successor = new Board(board);
                successor.make_move(currentPlayer, move);
                best = Math.min(best, search(successor, player, depth + 1, alpha, beta));
            }
        }
        return best;
    }
}
