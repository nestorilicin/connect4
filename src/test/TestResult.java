package test;

// Jedan red benchmark rezultata (Minimax/MinimaxAB self-play), toString() daje CSV liniju
public record TestResult(String player1, String player2, int depth, String evaluator,
                          int winner, long nodesVisited, double timeElapsedSeconds) implements BenchmarkResult {

    public static String csvHeader() {
        return "Player1,Player2,Depth,Evaluator,Winner,NodesVisited,TimeElapsed";
    }

    @Override
    public String toString() {
        return Csv.row(player1, player2, depth, evaluator, winner, nodesVisited, timeElapsedSeconds);
    }
}
