package io.github.drofmij.threadlord;


/**
 * handles stats for a group of Minion objects. 1 instantiation of this object will be shared between the ThreadLord and the
 * group of Minion objects to be run.
 */
public class MinionStatsHandler {
    private int total;
    private int done;
    private float lastPercent = 0f;

    /**
     * plain old default constructor
     */
    public MinionStatsHandler() {
    }

    /**
     * init this stats object with the total Minions that will be processed
     *
     * @param total total number of Minions to be processed
     */
    public void init(int total) {
        this.total = total;
        this.done = 0;
        this.lastPercent = 0f;
    }

    /**
     * update stats for 1 unit of work
     */
    public void update() {
        synchronized (this) {
            done++;
        }
        int percent = Float.valueOf(done / Float.valueOf(total) * 100.0F).intValue();
        if (lastPercent < percent) {
            System.out.println(percent + "%  - " + done + " / " + total);
            lastPercent = percent;
        }
    }
}
