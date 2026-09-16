package test;

import evaluation.AdvancedEvaluator;
import evaluation.Evaluator;
import evaluation.SimpleEvaluator;
import evaluation.ThreatEvaluator;
import game.Game;
import players.MinimaxABPlayer;
import players.Player;
import players.RandomPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.IntFunction;

// MinimaxAB protiv RandomPlayer-a, po GAMES_PER_CONFIG partija za oba redosleda poteza
public class MinimaxVsRandomTest extends BenchmarkTest<RandomMatchResult> {
    private static final int MIN_DEPTH = 1;
    private static final int MAX_DEPTH = 6;
    private static final int GAMES_PER_CONFIG = 100;

    private record EvaluatorSpec(String name, IntFunction<Evaluator> factory) {
    }

    public MinimaxVsRandomTest() {
        super("minimax_vs_random_results.csv");
    }

    static void main() throws Exception {
        new MinimaxVsRandomTest().run();
    }

    @Override
    protected List<Future<?>> submitTasks(ExecutorService executor) {
        List<EvaluatorSpec> evaluators = List.of(
                new EvaluatorSpec("SimpleEvaluator", SimpleEvaluator::new),
                new EvaluatorSpec("ThreatEvaluator", ThreatEvaluator::new),
                new EvaluatorSpec("AdvancedEvaluator", AdvancedEvaluator::new)
        );

        List<Future<?>> tasks = new ArrayList<>();
        for (EvaluatorSpec evaluator : evaluators) {
            for (int depth = MIN_DEPTH; depth <= MAX_DEPTH; depth++) {
                int finalDepth = depth;
                tasks.add(executor.submit(() -> playBatch(evaluator, finalDepth, true)));
                tasks.add(executor.submit(() -> playBatch(evaluator, finalDepth, false)));
            }
        }
        return tasks;
    }

    @Override
    protected Comparator<RandomMatchResult> sortComparator() {
        return Comparator.comparing(RandomMatchResult::evaluator)
                .thenComparingInt(RandomMatchResult::depth)
                .thenComparing(RandomMatchResult::player1);
    }

    @Override
    protected String csvHeader() {
        return RandomMatchResult.csvHeader();
    }

    @Override
    protected String resultNoun() {
        return "serija";
    }

    // minimaxFirst=true: MinimaxAB je player1, Random je player2; inace obrnuto
    private void playBatch(EvaluatorSpec evaluatorSpec, int depth, boolean minimaxFirst) {
        int minimaxWins = 0;
        int randomWins = 0;
        int draws = 0;
        long totalNanos = 0;

        for (int i = 0; i < GAMES_PER_CONFIG; i++) {
            int minimaxNumber = minimaxFirst ? 1 : 2;
            int randomNumber = minimaxFirst ? 2 : 1;
            Player minimax = new MinimaxABPlayer(minimaxNumber, evaluatorSpec.factory().apply(minimaxNumber), depth);
            Player random = new RandomPlayer(randomNumber);
            Player player1 = minimaxFirst ? minimax : random;
            Player player2 = minimaxFirst ? random : minimax;

            long start = System.nanoTime();
            int winner = new Game(player1, player2).simulate(false);
            totalNanos += System.nanoTime() - start;

            if (winner == 0) {
                draws++;
            } else if (winner == minimaxNumber) {
                minimaxWins++;
            } else {
                randomWins++;
            }
        }

        double avgTimeSeconds = (totalNanos / (double) GAMES_PER_CONFIG) / 1_000_000_000.0;
        String player1Name = minimaxFirst ? "MinimaxAB" : "Random";
        String player2Name = minimaxFirst ? "Random" : "MinimaxAB";
        addResult(new RandomMatchResult(player1Name, player2Name, depth,
                evaluatorSpec.name(), GAMES_PER_CONFIG, minimaxWins, randomWins, draws, avgTimeSeconds));
    }
}
