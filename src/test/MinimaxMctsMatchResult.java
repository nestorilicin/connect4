package test;

// Jedna serija partija (MinimaxAB protiv MCTS), toString() daje CSV liniju
public record MinimaxMctsMatchResult(String player1, String player2, int depth, int iterations,
                                      int games, int minimaxWins, int mctsWins, int draws,
                                      double avgTimeSeconds) implements BenchmarkResult {

    public static String csvHeader() {
        return "Player1,Player2,Depth,Iterations,Games,MinimaxABWins,MCTSWins,Draws,AvgTimeElapsed";
    }

    @Override
    public String toString() {
        return Csv.row(player1, player2, depth, iterations, games, minimaxWins, mctsWins, draws, avgTimeSeconds);
    }
}
