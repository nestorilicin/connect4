package players;

import game.Board;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Player {
    protected int player;

    public Player(int player) {
        this.player = player;
    }

    public int getPlayer() {
        return this.player;
    }

    public abstract int getMove(Board board);

    protected static List<Integer> centerFirst(Board board) {
        List<Integer> valid = board.get_valid_moves();
        double center = (board.getCols() - 1) / 2.0;
        List<Integer> ordered = new ArrayList<>(valid);
        ordered.sort(Comparator.comparingDouble(a -> Math.abs(a - center)));
        return ordered;
    }
}
