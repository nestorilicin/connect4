package test;

import game.Game;
import players.MCTSPlayer;
import players.Player;
import players.RandomPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// MCTS protiv RandomPlayer-a, po GAMES_PER_CONFIG partija za oba redosleda poteza
public class MCTSVsRandomTest extends BenchmarkTest<MCTSMatchResult> {
    private static final List<Integer> ITERATION_COUNTS = List.of(10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000);
    private static final int GAMES_PER_CONFIG = 100;

    public MCTSVsRandomTest() {
        super("mcts_vs_random_results.csv");
    }

    public static void main(String[] args) throws Exception {
        new MCTSVsRandomTest().run();
    }

    @Override
    protected List<Future<?>> submitTasks(ExecutorService executor) {
        List<Future<?>> tasks = new ArrayList<>();
        for (int iterations : ITERATION_COUNTS) {
            int finalIterations = iterations;
            tasks.add(executor.submit(() -> playBatch(finalIterations, true)));
            tasks.add(executor.submit(() -> playBatch(finalIterations, false)));
        }
        return tasks;
    }

    @Override
    protected Comparator<MCTSMatchResult> sortComparator() {
        return Comparator.comparingInt(MCTSMatchResult::iterations)
                .thenComparing(MCTSMatchResult::player1);
    }

    @Override
    protected String csvHeader() {
        return MCTSMatchResult.csvHeader();
    }

    @Override
    protected String resultNoun() {
        return "serija";
    }

    // mctsFirst=true: MCTS je player1, Random je player2; inace obrnuto
    private void playBatch(int iterations, boolean mctsFirst) {
        int mctsWins = 0;
        int randomWins = 0;
        int draws = 0;
        long totalNanos = 0;

        for (int i = 0; i < GAMES_PER_CONFIG; i++) {
            int mctsNumber = mctsFirst ? 1 : 2;
            int randomNumber = mctsFirst ? 2 : 1;
            Player mcts = new MCTSPlayer(mctsNumber, iterations);
            Player random = new RandomPlayer(randomNumber);
            Player player1 = mctsFirst ? mcts : random;
            Player player2 = mctsFirst ? random : mcts;

            long start = System.nanoTime();
            int winner = new Game(player1, player2).simulate(false);
            totalNanos += System.nanoTime() - start;

            if (winner == 0) {
                draws++;
            } else if (winner == mctsNumber) {
                mctsWins++;
            } else {
                randomWins++;
            }
        }

        double avgTimeSeconds = (totalNanos / (double) GAMES_PER_CONFIG) / 1_000_000_000.0;
        String player1Name = mctsFirst ? "MCTS" : "Random";
        String player2Name = mctsFirst ? "Random" : "MCTS";
        addResult(new MCTSMatchResult(player1Name, player2Name, iterations,
                GAMES_PER_CONFIG, mctsWins, randomWins, draws, avgTimeSeconds));
    }
}
