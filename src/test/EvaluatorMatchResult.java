package test;

public record EvaluatorMatchResult(String evaluator1, String evaluator2, int depth,
                                   int winner, long nodesVisited1, long nodesVisited2,
                                   double timeElapsedSeconds) implements BenchmarkResult {

    public static String csvHeader() {
        return "Evaluator1,Evaluator2,Depth,Winner,NodesVisited1,NodesVisited2, TimeElapsed";
    }

    @Override
    public String toString() {
        return Csv.row(evaluator1, evaluator2, depth, winner, nodesVisited1, nodesVisited2, timeElapsedSeconds);
    }
}
