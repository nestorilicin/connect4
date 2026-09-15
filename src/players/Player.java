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
}
