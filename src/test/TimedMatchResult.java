package test;

// Jedna serija partija (tajmirani MinimaxAB protiv tajmiranog MCTS-a), toString() daje CSV liniju
public record TimedMatchResult(String player1, String player2, long timeLimitMs,
                                int games, int minimaxWins, int mctsWins, int draws,
                                double avgTimeSeconds) implements BenchmarkResult {

    public static String csvHeader() {
        return "Player1,Player2,TimeLimitMs,Games,MinimaxABWins,MCTSWins,Draws,AvgTimeElapsed";
    }

    @Override
    public String toString() {
        return Csv.row(player1, player2, timeLimitMs, games, minimaxWins, mctsWins, draws, avgTimeSeconds);
    }
}
