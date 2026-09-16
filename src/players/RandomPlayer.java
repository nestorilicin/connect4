package players;

import game.Board;

import java.util.Random;

public class RandomPlayer extends Player {
    Random random = new Random();

    public RandomPlayer(int player) {
        super(player);
    }

    @Override
    public int getMove(Board board) {
        int[] validMoves = board.get_valid_moves();
        int randomIndex = random.nextInt(validMoves.length);
        return validMoves[randomIndex];
    }
}
