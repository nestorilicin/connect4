import evaluation.SimpleEvaluator;
import game.Game;
import players.MinimaxPlayer;

void main() {
    int p1 = 1;
    int p2 = 2;
    SimpleEvaluator evaluator1 = new SimpleEvaluator(p1);
    SimpleEvaluator evaluator2 = new SimpleEvaluator(p2);
    MinimaxPlayer player1 = new MinimaxPlayer(p1, evaluator1, 8);
    MinimaxPlayer player2 = new MinimaxPlayer(p2, evaluator2, 8);

    Game game = new Game(player1, player2);
    game.simulate();
}
