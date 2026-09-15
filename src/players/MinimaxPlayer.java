package players;

import evaluation.Evaluator;
import game.Board;

import java.util.List;

public class MinimaxPlayer extends Player {
    private final Evaluator evaluator;
    private final int MAX_DEPTH;
    private final int opponent;

    public MinimaxPlayer(int player, Evaluator evaluator, int MAX_DEPTH) {
        super(player);
        this.evaluator = evaluator;
        this.MAX_DEPTH = MAX_DEPTH;
        this.opponent = player == 1 ? 2 : 1;
    }

    @Override
    public int getMove(Board board) {
        List<Integer> validMoves = board.get_valid_moves();
        int bestMove = 0;
        int bestScore = Integer.MIN_VALUE;

        for (int move : validMoves) {
            Board successor = new Board(board);
            successor.make_move(player, move);
            int score = id_minimax(successor, opponent, 1, MAX_DEPTH);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;

    }

    private int id_minimax(Board board, int player, int depth, int MAX_DEPTH) {
        int score;
        if (depth == MAX_DEPTH || is_terminal(board)) {
            return evaluator.evaluate(board);
        }
        if (player == this.player) {
            score = Integer.MIN_VALUE;
            List<Board> successors = board.get_successors(player);
            for (Board succ : successors) {
                score = Math.max(score, id_minimax(succ, opponent, depth + 1, this.MAX_DEPTH));
            }
        } else {
            score = Integer.MAX_VALUE;
            List<Board> successors = board.get_successors(opponent);
            for (Board succ : successors) {
                score = Math.min(score, id_minimax(succ, this.player, depth + 1, this.MAX_DEPTH));
            }
        }
        return score;
    }

    private boolean is_terminal(Board board) {
        return board.is_full() || board.is_winner(player) || board.is_winner(opponent);
    }

}
