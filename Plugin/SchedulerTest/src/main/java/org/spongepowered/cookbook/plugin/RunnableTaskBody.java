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
        label = s;
        this.period = period;
        basis = base;
    }

    RunnableTaskBody setVerbose(boolean verboseOn) {
        verbosity = verboseOn;
        return this;
    }

    public void run() {
        long mils = System.currentTimeMillis();
        long nanos = System.nanoTime();
        String expected = null;

        if (basis == TimeBase.TICK) {
            expected = "" + (50 * period);
        }
        else if (basis == TimeBase.WALLCLOCK) {
            expected = "" + period;
        }

        if ( verbosity ) {
            System.out.println(label + "\t" + nanos + "\t" + mils +
                    "\t" + (mils - counter) + " (Expected: " + expected + ") \t" + (nanos - lastnano));
        }
        counter = mils;
        lastnano = nanos;
    }
}


