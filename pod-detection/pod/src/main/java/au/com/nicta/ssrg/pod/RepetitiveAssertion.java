package au.com.nicta.ssrg.pod;

import java.util.*;
import org.apache.logging.log4j.*;

public abstract class RepetitiveAssertion extends Assertion {
    public abstract void init(Map<String, Object> attributes);

    public final void start() {
        worker = new Worker();
        worker.start();
    }

    public void setMaxRepetition(int maxRepetition) {
        this.maxRepetition = maxRepetition;
    }

    public void setInitialDelay(int initialDelay) {
        this.initialDelay = initialDelay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    protected abstract void work();

    protected class Worker extends Thread {
        public void run() {
            if (initialDelay > 0) {
                try {
                    Thread.sleep(initialDelay);
                } catch (InterruptedException ex) { }
            }
            while (runCounter < maxRepetition) {
                ++runCounter;
                work();
                if (runCounter < maxRepetition) {
                    try {
                        Thread.sleep(delay);
                    } catch(InterruptedException ex) { }
                }
            }
        }
    }

    protected int maxRepetition = 5;
    protected int initialDelay = 0;
    protected int delay = 5 * 1000;
    protected int runCounter = 0;
    protected Worker worker;
}
