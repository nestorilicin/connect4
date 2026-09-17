package game;

import players.Player;

public class Game {
    private final Player player1;
    private final Player player2;
    private final Board board;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        board = new Board();
    }

    public void simulate() {
        simulate(true);
    }

    
    public int simulate(boolean withPrint) {
        while (true) {
            board.make_move(player1.getPlayer(), player1.getMove(board));
            if (withPrint) board.print();
            if (board.is_winner(player1.getPlayer())) {
                if (withPrint) System.out.println("Player 1 pobedio");
                return player1.getPlayer();
            }
            board.make_move(player2.getPlayer(), player2.getMove(board));
            if (withPrint) board.print();
            if (board.is_winner(player2.getPlayer())) {
                if (withPrint) System.out.println("Player 2 pobedio");
                return player2.getPlayer();
            }
            if (board.is_full()) {
                if (withPrint) System.out.println("Nereseno");
                return 0;
            }
        }
    }
}
