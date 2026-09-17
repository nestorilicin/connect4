package test;

import evaluation.AdvancedEvaluator;
import evaluation.Evaluator;
import evaluation.SimpleEvaluator;
import evaluation.ThreatEvaluator;
import game.Game;
import players.MinimaxABPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.IntFunction;

// tri evaluatora svaki protiv svakog
public class EvaluatorTest extends BenchmarkTest<EvaluatorMatchResult> {
    private static final int MIN_DEPTH = 1;
    private static final int MAX_DEPTH = 9;

    private record EvaluatorSpec(String name, IntFunction<Evaluator> factory) {
    }

    public EvaluatorTest() {
        super("evaluator_results.csv");
    }

    static void main() throws Exception {
        new EvaluatorTest().run();
    }

    @Override
    protected List<Future<?>> submitTasks(ExecutorService executor) {
        List<EvaluatorSpec> evaluators = List.of(
                new EvaluatorSpec("SimpleEvaluator", SimpleEvaluator::new),
                new EvaluatorSpec("ThreatEvaluator", ThreatEvaluator::new),
                new EvaluatorSpec("AdvancedEvaluator", AdvancedEvaluator::new)
        );

        List<Future<?>> tasks = new ArrayList<>();
        for (int depth = MIN_DEPTH; depth <= MAX_DEPTH; depth++) {
            int finalDepth = depth;
            for (EvaluatorSpec first : evaluators) {
                for (EvaluatorSpec second : evaluators) {
                    if (first == second) continue;
                    tasks.add(executor.submit(() -> runMatch(first, second, finalDepth)));
                }
            }
        }
        return tasks;
    }

    @Override
    protected Comparator<EvaluatorMatchResult> sortComparator() {
        return Comparator.comparingInt(EvaluatorMatchResult::depth)
                .thenComparing(EvaluatorMatchResult::evaluator1)
                .thenComparing(EvaluatorMatchResult::evaluator2);
    }

    @Override
    protected String csvHeader() {
        return EvaluatorMatchResult.csvHeader();
    }

    private void runMatch(EvaluatorSpec first, EvaluatorSpec second, int depth) {
        MinimaxABPlayer player1 = new MinimaxABPlayer(1, first.factory().apply(1), depth);
        MinimaxABPlayer player2 = new MinimaxABPlayer(2, second.factory().apply(2), depth);
        Game game = new Game(player1, player2);

        long start = System.nanoTime();
        int winner = game.simulate(false);
        double elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0;
        long nodesVisited1 = player1.getNodesVisited();
        long nodesVisited2 = player2.getNodesVisited();

        addResult(new EvaluatorMatchResult(first.name(), second.name(), depth,
                winner, nodesVisited1, nodesVisited2, elapsedSeconds));
    }
}
