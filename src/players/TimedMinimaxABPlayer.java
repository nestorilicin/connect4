package players;

import evaluation.Evaluator;
import game.Board;

// iterative deepening dok ne istekne timelimit
public class TimedMinimaxABPlayer extends Player {
    private final Evaluator evaluator;
    private final int opponent;
    private final long timeLimitMillis;
    private long nodesVisited = 0;
    private long deadline;
    
    private static class TimeUpException extends RuntimeException {
    }

    public TimedMinimaxABPlayer(int player, Evaluator evaluator, long timeLimitMillis) {
        super(player);
        this.evaluator = evaluator;
        this.opponent = 3 - player;
        this.timeLimitMillis = timeLimitMillis;
    }

    @Override
    public int getMove(Board board) {
        deadline = System.currentTimeMillis() + timeLimitMillis;
        int[] validMoves = Player.centerFirst(board);
        int bestMove = validMoves[0];
        int maxPossibleDepth = board.getRows() * board.getCols();

        for (int maxDepth = 1; maxDepth <= maxPossibleDepth; maxDepth++) {
            try {
                int bestScore = Integer.MIN_VALUE;
                int bestMoveAtDepth = validMoves[0];
                for (int move : validMoves) {
                    Board successor = new Board(board);
                    successor.make_move(player, move);
                    int score = search(successor, opponent, maxDepth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMoveAtDepth = move;
                    }
                }
                bestMove = bestMoveAtDepth;
            } catch (TimeUpException timeUp) {
                break;
            }
        }
        return bestMove;
    }

    private int search(Board board, int currentPlayer, int maxDepth, int depth, int alpha, int beta) {
        if (System.currentTimeMillis() >= deadline) throw new TimeUpException();
        nodesVisited++;
        if (isTerminal(board)) {
            return terminalScore(board, depth);
        }
        if (depth == maxDepth) {
            return evaluator.evaluate(board);
        }

        int[] validMoves = Player.centerFirst(board);
        int score;
        if (currentPlayer == player) {
            score = Integer.MIN_VALUE;
            for (int move : validMoves) {
                Board successor = new Board(board);
                successor.make_move(currentPlayer, move);
                score = Math.max(score, search(successor, opponent, maxDepth, depth + 1, alpha, beta));
                alpha = Math.max(alpha, score);
                if (alpha >= beta) break;
            }
        } else {
            score = Integer.MAX_VALUE;
            for (int move : validMoves) {
                Board successor = new Board(board);
                successor.make_move(currentPlayer, move);
                score = Math.min(score, search(successor, player, maxDepth, depth + 1, alpha, beta));
                beta = Math.min(beta, score);
                if (alpha >= beta) break;
            }
        }
        return score;
    }

    private boolean isTerminal(Board board) {
        return board.is_full() || board.is_winner(player) || board.is_winner(opponent);
    }
    
    private int terminalScore(Board board, int depth) {
        if (board.is_winner(player)) return Evaluator.WIN_SCORE - depth;
        if (board.is_winner(opponent)) return -Evaluator.WIN_SCORE + depth;
        return 0;
    }

    public long getNodesVisited() {
        return nodesVisited;
    }
}
