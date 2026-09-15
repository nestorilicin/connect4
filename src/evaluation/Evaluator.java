package evaluation;

import game.Board;

public abstract class Evaluator {
    protected final int player;
    protected final int opponent;
    protected static final int WIN_SCORE = 1000;
    public Evaluator(int player){
        this.player = player;
        this.opponent = 3 - player;
    }
    
    public abstract int evaluate(Board board);
}
