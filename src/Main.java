import evaluation.AdvancedEvaluator;
import evaluation.Evaluator;
import game.Game;
import players.MinimaxABPlayer;
import players.Player;

void main() {
    int p1 = 1;
    int p2 = 2;
    Evaluator evaluator1 = new AdvancedEvaluator(p1);
    Evaluator evaluator2 = new AdvancedEvaluator(p2);
    Player player1 = new MinimaxABPlayer(p1, evaluator1, 8);
    Player player2 = new MinimaxABPlayer(p2, evaluator2, 8);

    Game game = new Game(player1, player2);
    game.simulate();
}
