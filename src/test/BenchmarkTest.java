package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Template method
public abstract class BenchmarkTest<R extends BenchmarkResult> {
    private final String csvPath;
    private final List<R> results = Collections.synchronizedList(new ArrayList<>());

    protected BenchmarkTest(String csvPath) {
        this.csvPath = csvPath;
    }

    public final void run() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<?>> tasks = submitTasks(executor);

        for (Future<?> task : tasks) {
            task.get();
        }
        executor.shutdown();

        List<R> sorted = new ArrayList<>(results);
        sorted.sort(sortComparator());

        writeCsv(sorted);
        System.out.println("Gotovo, " + sorted.size() + " " + resultNoun() + " upisano u " + csvPath);
    }

    protected abstract List<Future<?>> submitTasks(ExecutorService executor);

    protected abstract Comparator<R> sortComparator();

    protected abstract String csvHeader();

    protected String resultNoun() {
        return "partija";
    }

    protected final void addResult(R result) {
        results.add(result);
        logProgress(result);
    }

    private synchronized void logProgress(R result) {
        System.out.println(result);
    }

    private void writeCsv(List<R> sortedResults) throws IOException {
        try (PrintWriter writer = new PrintWriter(Path.of(csvPath).toFile())) {
            writer.println(csvHeader());
            for (R result : sortedResults) {
                writer.println(result);
            }
        }
    }
}
