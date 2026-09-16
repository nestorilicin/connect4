package players;

import game.Board;

import java.util.List;
import java.util.Random;

public class RandomPlayer extends Player{
    Random random = new Random();
    public RandomPlayer(int player) {
        super(player);
        
    }

    @Override
    public int getMove(Board board) {
        List<Integer> validMoves = board.get_valid_moves();
        int randomIndex = random.nextInt(validMoves.size());
        
        return validMoves.get(randomIndex);
    }
}
