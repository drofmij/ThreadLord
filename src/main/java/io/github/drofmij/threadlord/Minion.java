package io.github.drofmij.threadlord;

import java.text.DecimalFormat;
import java.util.concurrent.Callable;

/**
 * Minion - descending classes must implement the work() method - set T for the
 * type that will be returned as a result of this unit of work
 *
 * @author drofmij
 * @param <T> type of result to be returned from work()
 */
public abstract class Minion<T> implements Callable<T> {

    private MinionStatsHandler stats;

    /**
     * default constructor
     */
    public Minion() {
    }

    public void setStats(MinionStatsHandler stats) {
        this.stats = stats;
    }

    /**
     * call() over rides call() in super() in order to output status percentage
     * if statusOut is true.
     *
     * @return result object of type T
     * @throws Exception in case of error that is not handled in work()
     */
    @Override
    public T call() throws Exception {
        T result = work();
        stats.update();
        return result;
    }

    /**
     * Implement work() to handle each unit of work you want to accomplish and
     * return the result.
     *
     * @return result
     */
    protected abstract T work();
}
