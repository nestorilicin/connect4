package test;

// Jedna serija partija (MinimaxAB protiv Random), toString() daje CSV liniju
public record RandomMatchResult(String player1, String player2, int depth, String evaluator,
                                 int games, int minimaxWins, int randomWins, int draws,
                                 double avgTimeSeconds) implements BenchmarkResult {

    public static String csvHeader() {
        return "Player1,Player2,Depth,Evaluator,Games,MinimaxABWins,RandomWins,Draws,AvgTimeElapsed";
    }

    @Override
    public String toString() {
        return Csv.row(player1, player2, depth, evaluator, games, minimaxWins, randomWins, draws, avgTimeSeconds);
    }
}
