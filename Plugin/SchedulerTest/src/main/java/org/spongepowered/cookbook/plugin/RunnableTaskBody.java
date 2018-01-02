package org.spongepowered.cookbook.plugin;

public class RunnableTaskBody implements Runnable {
    String label;
    long period;
    long counter = 0;
    long lastnano = 0;
    TimeBase basis;
    boolean verbosity = false;

    public enum TimeBase {
        TICK,
        WALLCLOCK
    }

    RunnableTaskBody(String s, long period, TimeBase base) {
        this.label = s;
        this.period = period;
        this.basis = base;
    }

    RunnableTaskBody setVerbose(boolean verboseOn) {
        this.verbosity = verboseOn;
        return this;
    }

    @Override
    public void run() {
        long mils = System.currentTimeMillis();
        long nanos = System.nanoTime();
        String expected = null;

        if (this.basis == TimeBase.TICK) {
            expected = "" + (50 * this.period);
        } else if (this.basis == TimeBase.WALLCLOCK) {
            expected = "" + this.period;
        }

        if (this.verbosity) {
            System.out.println(this.label + "\t" + nanos + "\t" + mils +
                    "\t" + (mils - this.counter) + " (Expected: " + expected + ") \t" + (nanos - this.lastnano));
        }
        this.counter = mils;
        this.lastnano = nanos;
    }
}


