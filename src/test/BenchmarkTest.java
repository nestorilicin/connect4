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

// Template method: run() je fiksan, podklase daju zadatke, poredak i CSV zaglavlje
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

    // Podklasa gradi ugnjezdene petlje konfiguracija i submituje zadatke
    protected abstract List<Future<?>> submitTasks(ExecutorService executor);

    protected abstract Comparator<R> sortComparator();

    protected abstract String csvHeader();

    // "partija" ili "serija", samo za poruku po zavrsetku
    protected String resultNoun() {
        return "partija";
    }

    // Dodaje rezultat i odmah ga ispisuje; results je vec thread-safe lista
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
