package evaluation;

import game.Board;
import game.Direction;

// Template method
public abstract class Evaluator {
    protected final int player;
    protected final int opponent;
    public static final int WIN_SCORE = 1_000_000;

    protected Evaluator(int player) {
        this.player = player;
        this.opponent = 3 - player;
    }

    public final int evaluate(Board board) {
        if (board.is_winner(player)) return WIN_SCORE;
        if (board.is_winner(opponent)) return -WIN_SCORE;
        if (board.is_full()) return 0;
        return centerScore(board) + windowScore(board) + extraScore(board);
    }

    protected abstract int centerScore(Board board);

    protected abstract int evaluateWindow(Board board, int row, int col, int rowDirection, int colDirection);
    
    protected int extraScore(Board board) {
        return 0;
    }

    private int windowScore(Board board) {
        int score = 0;
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                for (Direction dir : Direction.values()) {
                    if (fitsWindow(board, row, col, dir)) {
                        score += evaluateWindow(board, row, col, dir.dr, dir.dc);
                    }
                }
            }
        }
        return score;
    }

    private boolean fitsWindow(Board board, int row, int col, Direction dir) {
        int endRow = row + 3 * dir.dr;
        int endCol = col + 3 * dir.dc;
        return endRow >= 0 && endRow < board.getRows() && endCol >= 0 && endCol < board.getCols();
    }
}
