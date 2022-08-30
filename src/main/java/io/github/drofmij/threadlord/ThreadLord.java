package io.github.drofmij.threadlord;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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

    private boolean outputStatus;
    private MinionStatsHandler stats = null;

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
     * @param outputStatus
     */
    public ThreadLord(int numThreads, boolean runOnce, boolean outputStatus) {
        if(outputStatus) {
            this.stats = new MinionStatsHandler();
        }
        this.minions = new ArrayList<>();
        this.runOnce = runOnce;
        this.numThreads = numThreads;
        this.executor = Executors.newFixedThreadPool(numThreads);
    }

    /**
     * add a minion to the list - implement Minion abstract class work() method
     * - can implement abstract class inline see examples.
     *
     * @param minion worker to be run
     */
    public void add(Minion<T> minion) {
        minion.setStats(stats);
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
        if(stats != null) {
            System.out.println("ThreadLord processing " + minions.size() + " minions with " + numThreads + " threads.");
        }
        List<T> results = new ArrayList<>();
        stats.init(minions.size());
        List<Future<T>> resultFutures = executor.invokeAll(minions);
        for (Future future : resultFutures) {
            results.add((T) future.get());
        }

        if (runOnce) {
            close();
        } else {
            minions.clear();
        }
        return results;
    }

    /**
     * shutdown the thread executor
     */
    @Override
    public void close() throws IOException {
        executor.shutdown();
    }
}
