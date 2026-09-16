package players;

import evaluation.Evaluator;
import game.Board;

import java.util.List;

public class MinimaxABPlayer extends MinimaxPlayerBase {

    public MinimaxABPlayer(int player, Evaluator evaluator, int maxDepth) {
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

        List<Integer> validMoves = orderedMoves(board);
        int score;
        if (currentPlayer == player) {
            score = Integer.MIN_VALUE;
            for (int move : validMoves) {
                Board successor = new Board(board);
                successor.make_move(currentPlayer, move);
                score = Math.max(score, search(successor, opponent, depth + 1, alpha, beta));
                alpha = Math.max(alpha, score);
                if (alpha >= beta) break;
            }
        } else {
            score = Integer.MAX_VALUE;
            for (int move : validMoves) {
                Board successor = new Board(board);
                successor.make_move(currentPlayer, move);
                score = Math.min(score, search(successor, player, depth + 1, alpha, beta));
                beta = Math.min(beta, score);
                if (alpha >= beta) break;
            }
        }
        return score;
    }
}
