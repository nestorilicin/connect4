package test;

import evaluation.AdvancedEvaluator;
import evaluation.Evaluator;
import evaluation.SimpleEvaluator;
import evaluation.ThreatEvaluator;
import game.Game;
import players.MinimaxABPlayer;
import players.MinimaxPlayer;
import players.MinimaxPlayerBase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.IntFunction;

// Minimax vs MinimaxAB na razlicitim dubinama
public class MinimaxTest extends BenchmarkTest<TestResult> {
    private static final int MIN_DEPTH = 1;
    private static final int MAX_DEPTH = 7;
    
    private interface PlayerFactory {
        MinimaxPlayerBase create(int player, Evaluator evaluator, int maxDepth);
    }

    private record AlgorithmSpec(String name, PlayerFactory factory) {
    }

    private record EvaluatorSpec(String name, IntFunction<Evaluator> factory) {
    }

    public MinimaxTest() {
        super("minimax_results.csv");
    }

    static void main() throws Exception {
        new MinimaxTest().run();
    }

    @Override
    protected List<Future<?>> submitTasks(ExecutorService executor) {
        List<AlgorithmSpec> algorithms = List.of(
                new AlgorithmSpec("Minimax", MinimaxPlayer::new),
                new AlgorithmSpec("MinimaxAB", MinimaxABPlayer::new)
        );
        List<EvaluatorSpec> evaluators = List.of(
                new EvaluatorSpec("SimpleEvaluator", SimpleEvaluator::new),
                new EvaluatorSpec("ThreatEvaluator", ThreatEvaluator::new),
                new EvaluatorSpec("AdvancedEvaluator", AdvancedEvaluator::new)
        );

        List<Future<?>> tasks = new ArrayList<>();
        for (AlgorithmSpec algorithm : algorithms) {
            for (EvaluatorSpec evaluator : evaluators) {
                for (int depth = MIN_DEPTH; depth <= MAX_DEPTH; depth++) {
                    int finalDepth = depth;
                    tasks.add(executor.submit(() -> runMatch(algorithm, evaluator, finalDepth)));
                }
            }
        }
        return tasks;
    }

    @Override
    protected Comparator<TestResult> sortComparator() {
        return Comparator.comparing(TestResult::player1)
                .thenComparingInt(TestResult::depth)
                .thenComparing(TestResult::evaluator);
    }

    @Override
    protected String csvHeader() {
        return TestResult.csvHeader();
    }

    private void runMatch(AlgorithmSpec algorithm, EvaluatorSpec evaluatorSpec, int depth) {
        Evaluator evaluator1 = evaluatorSpec.factory().apply(1);
        Evaluator evaluator2 = evaluatorSpec.factory().apply(2);
        MinimaxPlayerBase player1 = algorithm.factory().create(1, evaluator1, depth);
        MinimaxPlayerBase player2 = algorithm.factory().create(2, evaluator2, depth);
        Game game = new Game(player1, player2);

        long start = System.nanoTime();
        int winner = game.simulate(false);
        double elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0;
        long nodesVisited = player1.getNodesVisited() + player2.getNodesVisited();

        addResult(new TestResult(algorithm.name(), algorithm.name(), depth,
                evaluatorSpec.name(), winner, nodesVisited, elapsedSeconds));
    }
}
