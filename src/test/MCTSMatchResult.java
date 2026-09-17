package test;

public record MCTSMatchResult(String player1, String player2, int iterations,
                               int games, int mctsWins, int randomWins, int draws,
                               double avgTimeSeconds) implements BenchmarkResult {

    public static String csvHeader() {
        return "Player1,Player2,Iterations,Games,MCTSWins,RandomWins,Draws,AvgTimeElapsed";
    }

    @Override
    public String toString() {
        return Csv.row(player1, player2, iterations, games, mctsWins, randomWins, draws, avgTimeSeconds);
    }
}
