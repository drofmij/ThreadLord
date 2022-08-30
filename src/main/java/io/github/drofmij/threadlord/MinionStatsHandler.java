package io.github.drofmij.threadlord;


public class MinionStatsHandler {
    private int total;
    private int done;
    private float lastPercent = 0f;

    public MinionStatsHandler() {
    }

    public void setTotal(int total) {
        this.total = total;
        this.done = 0;
        this.lastPercent = 0f;
    }

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
