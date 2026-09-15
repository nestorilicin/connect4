package game;

import players.Player;

public class Game {
    private Player player1;
    private Player player2;
    private Board board;
    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        board = new Board();
    }
    
    public void simulate(){
        while (true) {
            board.make_move(player1.getPlayer(), player1.getMove(board));
            board.print();
            if (board.is_winner(player1.getPlayer())){
                System.out.println("Player 1 pobedio");
                break;
            }
            board.make_move(player2.getPlayer(), player2.getMove(board));
            board.print();

            if (board.is_full()) {
                System.out.println("Nereseno");
                break;
            }


            if (board.is_winner(player2.getPlayer())){
                System.out.println("Player 2 pobedio");
                break;
            }

        }
    }
}
