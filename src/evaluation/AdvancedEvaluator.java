package evaluation;

import game.Board;

// window kao kod ThreatEvaluator plus bonus za fork
public class AdvancedEvaluator extends Evaluator {
    private static final int OPEN_THREE = 60;
    private static final int CLOSED_THREE = 15;
    private static final int TWO = 6;
    private static final int ONE = 1;
    private static final double CENTER_WEIGHT = 2.0;
    private static final int FORK_BONUS = 10_000;
    private static final int PARITY_BONUS = 60;

    public AdvancedEvaluator(int player) {
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
        int windowScore = sign * value;
        if (pieces == 3 && emptyCount == 1) {
            int ownerId = sign > 0 ? player : opponent;
            windowScore += sign * parityAdjustment(board, emptyRow, ownerId);
        }
        return windowScore;
    }

    // Neparan red od dna pogoduje prvom igracu, paran drugom (Allis, odd/even threats)
    private int parityAdjustment(Board board, int emptyRow, int ownerId) {
        boolean rowIsOdd = (board.getRows() - emptyRow) % 2 == 1;
        boolean favorsOwner = rowIsOdd == (ownerId == 1);
        return favorsOwner ? PARITY_BONUS : -PARITY_BONUS;
    }

    // Dve ili vise kolona koje odmah pobedjuju = protivnik ne moze blokirati obe
    @Override
    protected int extraScore(Board board) {
        int playerForks = countImmediateWins(board, player);
        int opponentForks = countImmediateWins(board, opponent);
        int score = 0;
        if (playerForks >= 2) score += FORK_BONUS;
        if (opponentForks >= 2) score -= FORK_BONUS;
        return score;
    }

    private int countImmediateWins(Board board, int who) {
        int count = 0;
        for (int col : board.get_valid_moves()) {
            Board copy = new Board(board);
            copy.make_move(who, col);
            if (copy.is_winner(who)) count++;
        }
        return count;
    }

    private boolean isPlayable(Board board, int row, int col) {
        return row == board.getRows() - 1 || board.getCell(row + 1, col) != 0;
    }
}
