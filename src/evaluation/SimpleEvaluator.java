package evaluation;

import game.Board;

public class SimpleEvaluator extends Evaluator {
    public SimpleEvaluator(int player) {
        super(player);
    }

    @Override
    protected int centerScore(Board board) {
        int center = board.getCols() / 2;
        int score = 0;
        for (int r = 0; r < board.getRows(); r++) {
            int cell = board.getCell(r, center);
            if (cell == player) score += 3;
            else if (cell == opponent) score -= 3;
        }
        return score;
    }

    @Override
    protected int evaluateWindow(Board board, int row, int col, int rowDirection, int colDirection) {
        int playerPieces = 0;
        int opponentPieces = 0;
        for (int i = 0; i < 4; i++) {
            int cell = board.getCell(row + i * rowDirection, col + i * colDirection);
            if (cell == player) {
                playerPieces++;
            } else if (cell == opponent) {
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
