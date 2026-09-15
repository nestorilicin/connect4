package evaluation;

import game.Board;

public class SimpleEvaluator extends Evaluator {

    public SimpleEvaluator(int player) {
        super(player);
    }

    @Override
    public int evaluate(Board board) {
        if (board.is_winner(player)) return WIN_SCORE;

        if (board.is_winner(opponent)) return -WIN_SCORE;

        if (board.is_full()) return 0;

        int score = 0;

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                // horizontal
                if (col + 3 < board.getCols()) {
                    score += evaluateWindow(board, row, col, 0, 1);
                }
                // vertical
                if (row + 3 < board.getRows()) {
                    score += evaluateWindow(board, row, col, 1, 0);
                }
                // diag \
                if (row + 3 < board.getRows() && col + 3 < board.getCols()) {
                    score += evaluateWindow(board, row, col, 1, 1);
                }
                // diag / 
                if (row - 3 >= 0 && col + 3 < board.getCols()) {
                    score += evaluateWindow(board, row, col, -1, 1);
                }
            }
        }
        return score;
    }

    private int evaluateWindow(Board board, int row, int col, int rowDirection, int colDirection) {
        int playerPieces = 0;
        int opponentPieces = 0;
        for (int i = 0; i < 4; i++) {
            int cell = board.getCell(row + i * rowDirection, col + i * colDirection);
            if (cell == player) {
                playerPieces++;
            }
            if (cell == opponent) {
                opponentPieces++;
            }
        }
        if (playerPieces > 0 && opponentPieces > 0) return 0;
        return switch (playerPieces) {
            case 1 -> 1;
            case 2 -> 10;
            case 3 -> 100;
            case 4 -> WIN_SCORE;
            default -> switch (opponentPieces) {
                case 1 -> -1;
                case 2 -> -10;
                case 3 -> -100;
                case 4 -> -WIN_SCORE;
                default -> 0;
            };
        };

    }
}
