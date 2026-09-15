package players;

import game.Board;

public class MCTSPlayer extends Player{
    public MCTSPlayer(int player) {
        super(player);
    }

    @Override
    public int getMove(Board board) {
        return 0;
    }
}
