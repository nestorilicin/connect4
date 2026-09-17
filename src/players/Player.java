package players;

import game.Board;

public abstract class Player {
    protected int player;

    public Player(int player) {
        this.player = player;
    }

    public int getPlayer() {
        return this.player;
    }

    public abstract int getMove(Board board);
    
    protected static int[] centerFirst(Board board) {
        int[] moves = board.get_valid_moves();
        double center = (board.getCols() - 1) / 2.0;
        for (int i = 1; i < moves.length; i++) {
            int move = moves[i];
            double key = Math.abs(move - center);
            int j = i - 1;
            while (j >= 0 && Math.abs(moves[j] - center) > key) {
                moves[j + 1] = moves[j];
                j--;
            }
            moves[j + 1] = move;
        }
        return moves;
    }
}
