package test;

// Jedna partija MCTS protiv MCTS, toString() daje CSV liniju
public record MCTSTestResult(String player1, String player2, int iterations,
                              int winner, long nodesCreated, double timeElapsedSeconds) implements BenchmarkResult {

    public static String csvHeader() {
        return "Player1,Player2,Iterations,Winner,NodesCreated,TimeElapsed";
    }

    @Override
    public String toString() {
        return Csv.row(player1, player2, iterations, winner, nodesCreated, timeElapsedSeconds);
    }
}
