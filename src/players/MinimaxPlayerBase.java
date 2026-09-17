package players;

import evaluation.Evaluator;
import game.Board;

// Template method: getMove() je fiksan, podklase daju search()
public abstract class MinimaxPlayerBase extends Player {
    protected final Evaluator evaluator;
    protected final int maxDepth;
    protected final int opponent;
    private long nodesVisited = 0;

    protected MinimaxPlayerBase(int player, Evaluator evaluator, int maxDepth) {
        super(player);
        this.evaluator = evaluator;
        this.maxDepth = maxDepth;
        this.opponent = 3 - player;
    }

    @Override
    public final int getMove(Board board) {
        int[] validMoves = orderedMoves(board);
        int bestMove = validMoves[0];
        int bestScore = Integer.MIN_VALUE;

        for (int move : validMoves) {
            Board successor = new Board(board);
            successor.make_move(player, move);
            int score = search(successor, opponent, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    
    protected final int[] orderedMoves(Board board) {
        return Player.centerFirst(board);
    }
    
    protected final int search(Board board, int currentPlayer, int depth, int alpha, int beta) {
        nodesVisited++;
        return searchNode(board, currentPlayer, depth, alpha, beta);
    }

    protected abstract int searchNode(Board board, int currentPlayer, int depth, int alpha, int beta);

    protected boolean isTerminal(Board board) {
        return board.is_full() || board.is_winner(player) || board.is_winner(opponent);
    }
    
    protected final int terminalScore(Board board, int depth) {
        if (board.is_winner(player)) return Evaluator.WIN_SCORE - depth;
        if (board.is_winner(opponent)) return -Evaluator.WIN_SCORE + depth;
        return 0;
    }

    public long getNodesVisited() {
        return nodesVisited;
    }
}
