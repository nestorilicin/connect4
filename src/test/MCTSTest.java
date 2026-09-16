package test;

import game.Game;
import players.MCTSPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// MCTS protiv MCTS (isti broj iteracija), analogno MinimaxTest-u
public class MCTSTest extends BenchmarkTest<MCTSTestResult> {
    private static final List<Integer> ITERATION_COUNTS = List.of(100, 200, 500, 1000, 2000, 5000, 10000);

    public MCTSTest() {
        super("mcts_results.csv");
    }

    public static void main(String[] args) throws Exception {
        new MCTSTest().run();
    }

    @Override
    protected List<Future<?>> submitTasks(ExecutorService executor) {
        List<Future<?>> tasks = new ArrayList<>();
        for (int iterations : ITERATION_COUNTS) {
            int finalIterations = iterations;
            tasks.add(executor.submit(() -> runMatch(finalIterations)));
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

    private void runMatch(int iterations) {
        MCTSPlayer player1 = new MCTSPlayer(1, iterations);
        MCTSPlayer player2 = new MCTSPlayer(2, iterations);
        Game game = new Game(player1, player2);

        long start = System.nanoTime();
        int winner = game.simulate(false);
        double elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0;
        long nodesCreated = player1.getNodesCreated() + player2.getNodesCreated();

        addResult(new MCTSTestResult("MCTS", "MCTS", iterations, winner, nodesCreated, elapsedSeconds));
    }
}
