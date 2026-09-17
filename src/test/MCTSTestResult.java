package test;

public record MCTSTestResult(String player1, String player2, int iterations,
                              int games, int player1Wins, int player2Wins, int draws,
                              double avgTimeSeconds) implements BenchmarkResult {

    public static String csvHeader() {
        return "Player1,Player2,Iterations,Games,Player1Wins,Player2Wins,Draws,AvgTimeElapsed";
    }

    @Override
    public String toString() {
        return Csv.row(player1, player2, iterations, games, player1Wins, player2Wins, draws, avgTimeSeconds);
    }
}
