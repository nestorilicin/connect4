package test;

import evaluation.ThreatEvaluator;
import game.Game;
import players.MCTSPlayer;
import players.Player;
import players.TimedMinimaxABPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// Tajmirani MinimaxAB (AdvancedEvaluator) protiv tajmiranog MCTS-a, isti limit po potezu
public class TimedMinimaxVsMctsTest extends BenchmarkTest<TimedMatchResult> {
    private static final List<Long> TIME_LIMITS_MS = List.of(200L, 300L, 400L,600L,700L,800L,900L, 1000L);
    private static final int GAMES_PER_ORDER = 50;

    public TimedMinimaxVsMctsTest() {
        super("timed_minimax_vs_mcts_results.csv");
    }

    static void main() throws Exception {
        new TimedMinimaxVsMctsTest().run();
    }

    @Override
    protected List<Future<?>> submitTasks(ExecutorService executor) {
        List<Future<?>> tasks = new ArrayList<>();
        for (long timeLimitMs : TIME_LIMITS_MS) {
            tasks.add(executor.submit(() -> playBatch(timeLimitMs, true)));
            tasks.add(executor.submit(() -> playBatch(timeLimitMs, false)));
        }
        return tasks;
    }

    @Override
    protected Comparator<TimedMatchResult> sortComparator() {
        return Comparator.comparingLong(TimedMatchResult::timeLimitMs)
                .thenComparing(TimedMatchResult::player1);
    }

    @Override
    protected String csvHeader() {
        return TimedMatchResult.csvHeader();
    }

    @Override
    protected String resultNoun() {
        return "serija";
    }

    // minimaxFirst=true: TimedMinimaxAB je player1, MCTS je player2; inace obrnuto
    private void playBatch(long timeLimitMs, boolean minimaxFirst) {
        int minimaxWins = 0;
        int mctsWins = 0;
        int draws = 0;
        long totalNanos = 0;

        for (int i = 0; i < GAMES_PER_ORDER; i++) {
            int minimaxNumber = minimaxFirst ? 1 : 2;
            int mctsNumber = minimaxFirst ? 2 : 1;
            Player minimax = new TimedMinimaxABPlayer(minimaxNumber, new ThreatEvaluator(minimaxNumber), timeLimitMs);
            Player mcts = new MCTSPlayer(mctsNumber, timeLimitMs);
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
        String player1Name = minimaxFirst ? "TimedMinimaxAB" : "MCTS";
        String player2Name = minimaxFirst ? "MCTS" : "TimedMinimaxAB";
        addResult(new TimedMatchResult(player1Name, player2Name, timeLimitMs,
                GAMES_PER_ORDER, minimaxWins, mctsWins, draws, avgTimeSeconds));
    }
}
