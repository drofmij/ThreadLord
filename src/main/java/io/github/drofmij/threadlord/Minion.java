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

    private static float total;
    private static float done;
    private static int statusFrequency;
    private static double percent;

    private final boolean statusOut;

    /**
     * default constructor, statusOut set to false
     */
    public Minion() {
        this(false);
    }

    /**
     * if statusOut is true percentage will be output to system.out
     *
     * @param statusOut
     */
    public Minion(boolean statusOut) {
        this.statusOut = statusOut;
    }

    /**
     * call() over rides call() in super() in order to output status percentage
     * if statusOut is true.
     *
     * @return
     * @throws Exception
     */
    @Override
    public T call() throws Exception {
        if (statusOut) {
            synchronized (this.getClass()) {
                done++;
            }
            if (done % statusFrequency == 0 && percent < (done / total) * 100d) {
                System.out.println(new DecimalFormat("#.00").format((done / total) * 100d) + " %");
            }
            percent = (done / total) * 100d;
        }
        return work();
    }

    /**
     * initializes status #s at class level defaults frequency to output when
     * 100 minions are complete
     *
     * @param total minions to be processed
     */
    protected static void init(int total) {
        init(total, 100);
    }

    /**
     * initializes status #s at class level for status output frequency and
     * total number of minions to be processed
     *
     * @param total
     * @param statusFrequency
     */
    protected static void init(int total, int statusFrequency) {
        Minion.total = total;
        Minion.statusFrequency = statusFrequency;
        Minion.done = 0;
        Minion.percent = 0;
    }

    /**
     * Implement work() to handle each unit of work you want to accomplish and
     * return the result.
     *
     * @return result
     */
    protected abstract T work();
}
