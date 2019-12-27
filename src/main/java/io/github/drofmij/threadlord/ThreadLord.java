package io.github.drofmij.threadlord;

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
public class ThreadLord<T> {

    private final List<Minion<T>> minions;
    private final ExecutorService executor;
    private final boolean runOnce;

    /**
     * Initializes the ThreadLord with specified number of threads and sets
     * runOnce flag.
     *
     * @param numThreads number of worker threads - try 20 or 25 to start
     * @param runOnce if true thread executor will remain running until
     * shutdown() is called if false thread executor will shutdown during run()
     * call once the workers complete
     */
    public ThreadLord(int numThreads, boolean runOnce) {
        this.minions = new ArrayList<>();
        this.runOnce = runOnce;
        this.executor = Executors.newFixedThreadPool(numThreads);
    }

    /**
     * add a minion to the list - implement Minion abstract class work() method
     * - can implement abstract class inline see examples.
     *
     * @param minion worker to be run
     */
    public void addMinion(Minion<T> minion) {
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
    public List<T> run() throws InterruptedException, ExecutionException {
        Minion.init(minions.size());
        List<T> results = new ArrayList<>();
        List<Future<T>> resultFutures = executor.invokeAll(minions);
        for (Future future : resultFutures) {
            results.add((T) future.get());
        }

        if (runOnce) {
            shutdown();
        } else {
            minions.clear();
        }
        return results;
    }

    /**
     * shutdown the thread executor
     */
    public void shutdown() {
        executor.shutdown();
    }
}
