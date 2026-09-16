package test;

import game.Game;
import players.MCTSPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// MCTS protiv MCTS (isti broj iteracija), po GAMES_PER_CONFIG partija jer je MCTS nedeterministicki
public class MCTSTest extends BenchmarkTest<MCTSTestResult> {
    private static final List<Integer> ITERATION_COUNTS =
            List.of(10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100_000);
    private static final int GAMES_PER_CONFIG = 100;

    public MCTSTest() {
        super("mcts_results.csv");
    }

    static void main() throws Exception {
        new MCTSTest().run();
    }

    @Override
    protected List<Future<?>> submitTasks(ExecutorService executor) {
        List<Future<?>> tasks = new ArrayList<>();
        for (int iterations : ITERATION_COUNTS) {
            tasks.add(executor.submit(() -> playBatch(iterations)));
        }
        return tasks;
    }

    @Override
    protected Comparator<MCTSTestResult> sortComparator() {
        return Comparator.comparingInt(MCTSTestResult::iterations);
    }

    @Override
    protected String csvHeader() {
        return MCTSTestResult.csvHeader();
    }

    @Override
    protected String resultNoun() {
        return "serija";
    }

    private void playBatch(int iterations) {
        int player1Wins = 0;
        int player2Wins = 0;
        int draws = 0;
        long totalNanos = 0;

        for (int i = 0; i < GAMES_PER_CONFIG; i++) {
            MCTSPlayer player1 = new MCTSPlayer(1, iterations);
            MCTSPlayer player2 = new MCTSPlayer(2, iterations);

            long start = System.nanoTime();
            int winner = new Game(player1, player2).simulate(false);
            totalNanos += System.nanoTime() - start;

            if (winner == 0) {
                draws++;
            } else if (winner == 1) {
                player1Wins++;
            } else {
                player2Wins++;
            }
        }

        double avgTimeSeconds = (totalNanos / (double) GAMES_PER_CONFIG) / 1_000_000_000.0;
        addResult(new MCTSTestResult("MCTS", "MCTS", iterations,
                GAMES_PER_CONFIG, player1Wins, player2Wins, draws, avgTimeSeconds));
    }
}
