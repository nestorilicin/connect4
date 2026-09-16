package evaluation;

import game.Board;

// window skor, bonus za centar i prepoznaje pretnje drugog igraca
public class ThreatEvaluator extends Evaluator {
    private static final int OPEN_THREE = 60;
    private static final int CLOSED_THREE = 15;
    private static final int TWO = 6;
    private static final int ONE = 1;
    private static final double CENTER_WEIGHT = 2.0;

    public ThreatEvaluator(int player) {
        super(player);
    }

    @Override
    protected int centerScore(Board board) {
        double center = (board.getCols() - 1) / 2.0;
        int score = 0;
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                int cell = board.getCell(r, c);
                if (cell == 0) continue;
                int weight = (int) Math.round(CENTER_WEIGHT - Math.abs(c - center));
                if (weight <= 0) continue;
                score += (cell == player) ? weight : -weight;
            }
        }
        return score;
    }

    @Override
    protected int evaluateWindow(Board board, int row, int col, int rowDirection, int colDirection) {
        int playerPieces = 0;
        int opponentPieces = 0;
        int emptyRow = -1;
        int emptyCol = -1;
        int emptyCount = 0;
        for (int i = 0; i < 4; i++) {
            int r = row + i * rowDirection;
            int c = col + i * colDirection;
            int cell = board.getCell(r, c);
            if (cell == player) {
                playerPieces++;
            } else if (cell == opponent) {
                opponentPieces++;
            } else {
                emptyRow = r;
                emptyCol = c;
                emptyCount++;
            }
        }
        if (playerPieces > 0 && opponentPieces > 0) return 0;
        int pieces = Math.max(playerPieces, opponentPieces);
        int sign = playerPieces > 0 ? 1 : (opponentPieces > 0 ? -1 : 0);
        if (sign == 0) return 0;
        boolean playableNow = emptyCount == 1 && isPlayable(board, emptyRow, emptyCol);
        int value = switch (pieces) {
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> playableNow ? OPEN_THREE : CLOSED_THREE;
            case 4 -> WIN_SCORE;
            default -> 0;
        };
        return sign * value;
    }

    private boolean isPlayable(Board board, int row, int col) {
        return row == board.getRows() - 1 || board.getCell(row + 1, col) != 0;
    }
}
