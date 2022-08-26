package io.github.drofmij.threadlord;

import java.io.Closeable;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ThreadLord - handles simple thread management for a set of Minion objects and
 * bundles the results into a list - very useful for heavy IO operations such as
 * reading or writing from hundreds or thousands of files locally or in cloud
 * data store such as S3 - thread pool can be single use or can be reused
 * multiple times for different workloads in the same application.
 *
 * @author drofmij@gmail.com
 * @param <T> Return type that each Minion will produce
 */
public class ThreadLord<T> implements Closeable {

    private final List<Minion<T>> minions;

    private final ExecutorService executor;
    private final boolean runOnce;
    private final int numThreads;

    private int statusFrequencyPercent;
    private double percent;
    private boolean statusOut;

    /**
     * Initializes the ThreadLord with specified number of threads defaults runonce to true and statusOut to true
     *
     * @param numThreads number of worker threads - try 20 or 25 to start
     */

    public ThreadLord(int numThreads) {
        this(numThreads, true, true);

    }

    /**
     * Initializes the ThreadLord with specified number of threads, sets runonce and statusOut as specified
     *
     * @param numThreads
     * @param runOnce
     * @param statusOut
     */
    public ThreadLord(int numThreads, boolean runOnce, boolean statusOut) {
        this.statusOut = statusOut;
        this.minions = new ArrayList<>();
        this.runOnce = runOnce;
        this.numThreads = numThreads;
        this.executor = Executors.newFixedThreadPool(numThreads);
        this.statusFrequencyPercent = 1;
    }


    public void setStatusFrequencyPercent(int statusFrequencyPercent) {
        this.statusFrequencyPercent = statusFrequencyPercent;
    }

    public void setStatusOut(boolean statusOut) {
        this.statusOut = statusOut;
    }

    /**
     * add a minion to the list - implement Minion abstract class work() method
     * - can implement abstract class inline see examples.
     *
     * @param minion worker to be run
     */
    public void add(Minion<T> minion) {
        minions.add(minion);
    }

    /**
     * start the thread pool, run each of the Minion objects, and gather the
     * results that are returned - if runOnce thread executor will be shutdown
     * else thread executor will remain active until shutdown() is called.
     *
     * @return list of T objects produced by Minions
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public List<T> run() throws InterruptedException, ExecutionException, IOException {
        if(statusOut) {
            System.out.println("ThreadLord processing " + minions.size() + " minions with " + numThreads + " threads.");
        }
        List<T> results = new ArrayList<>();
        List<Future<T>> resultFutures = executor.invokeAll(minions);
        for (Future future : resultFutures) {
            results.add((T) future.get());
            handleStatus(resultFutures.indexOf(future) + 1, resultFutures.size());
        }

        if (runOnce) {
            close();
        } else {
            percent = 0.0;
            minions.clear();
        }
        return results;
    }

    /**
     * Handle status output if statusOut is true, outputs every statusFrequencyPercent
     *
     * @param current
     * @param total
     */
    private void handleStatus(int current, int total) {
        if (statusOut) {
            if (current % statusFrequencyPercent == 0 && percent < (current / total) * 100d) {
                System.out.println(new DecimalFormat("#.00").format((current / total) * 100d) + " %");
            }
            percent = (current / total) * 100d;
        }
    }

    /**
     * shutdown the thread executor
     */
    @Override
    public void close() throws IOException {
        executor.shutdown();
    }
}
