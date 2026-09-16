package test;

import evaluation.ThreatEvaluator;
import game.Game;
import players.MCTSPlayer;
import players.MinimaxABPlayer;
import players.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// MinimaxAB (AdvancedEvaluator) protiv MCTS, po GAMES_PER_ORDER partija za oba redosleda poteza
public class MinimaxVsMctsTest extends BenchmarkTest<MinimaxMctsMatchResult> {
    private static final List<Integer> DEPTHS = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    private static final List<Integer> ITERATION_COUNTS = List.of(100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100_000);
    private static final int GAMES_PER_ORDER = 50;

    public MinimaxVsMctsTest() {
        super("minimax_vs_mcts_results.csv");
    }

    static void main() throws Exception {
        new MinimaxVsMctsTest().run();
    }

    @Override
    protected List<Future<?>> submitTasks(ExecutorService executor) {
        List<Future<?>> tasks = new ArrayList<>();
        for (int depth : DEPTHS) {
            for (int iterations : ITERATION_COUNTS) {
                tasks.add(executor.submit(() -> playBatch(depth, iterations, true)));
                tasks.add(executor.submit(() -> playBatch(depth, iterations, false)));
            }
        }
        return tasks;
    }

    @Override
    protected Comparator<MinimaxMctsMatchResult> sortComparator() {
        return Comparator.comparingInt(MinimaxMctsMatchResult::depth)
                .thenComparingInt(MinimaxMctsMatchResult::iterations)
                .thenComparing(MinimaxMctsMatchResult::player1);
    }

    @Override
    protected String csvHeader() {
        return MinimaxMctsMatchResult.csvHeader();
    }

    @Override
    protected String resultNoun() {
        return "serija";
    }

    // minimaxFirst=true: MinimaxAB je player1, MCTS je player2; inace obrnuto
    private void playBatch(int depth, int iterations, boolean minimaxFirst) {
        int minimaxWins = 0;
        int mctsWins = 0;
        int draws = 0;
        long totalNanos = 0;

        for (int i = 0; i < GAMES_PER_ORDER; i++) {
            int minimaxNumber = minimaxFirst ? 1 : 2;
            int mctsNumber = minimaxFirst ? 2 : 1;
            Player minimax = new MinimaxABPlayer(minimaxNumber, new ThreatEvaluator(minimaxNumber), depth);
            Player mcts = new MCTSPlayer(mctsNumber, iterations);
            Player player1 = minimaxFirst ? minimax : mcts;
            Player player2 = minimaxFirst ? mcts : minimax;

            long start = System.nanoTime();
            int winner = new Game(player1, player2).simulate(false);
            totalNanos += System.nanoTime() - start;

            if (winner == 0) {
                draws++;
            } else if (winner == minimaxNumber) {
                minimaxWins++;
            } else {
                mctsWins++;
            }
        }

        double avgTimeSeconds = (totalNanos / (double) GAMES_PER_ORDER) / 1_000_000_000.0;
        String player1Name = minimaxFirst ? "MinimaxAB" : "MCTS";
        String player2Name = minimaxFirst ? "MCTS" : "MinimaxAB";
        addResult(new MinimaxMctsMatchResult(player1Name, player2Name, depth, iterations,
                GAMES_PER_ORDER, minimaxWins, mctsWins, draws, avgTimeSeconds));
    }
}
