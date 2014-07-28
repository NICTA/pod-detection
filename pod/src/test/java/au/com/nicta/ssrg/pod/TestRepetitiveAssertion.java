package au.com.nicta.ssrg.pod;

import java.util.Map;
import org.junit.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestRepetitiveAssertion {
    @BeforeClass
    public static void init() {
        AssertionLoggerCreator.setCurrentCreator(new NumbAssertionLoggerCreator());
    }

    @Test
    public void testRepetition() {
        RepetitiveAssertionForTest assertion = createAssertionForTest(5, 0, 0);
        assertion.start();
        assertion.join();
        assertTrue(
            String.format(
                "Assertion doesn't repeat enough times. (MaxRepetition=%d, ActualRepetition=%d)",
                assertion.getMaxRepetition(),
                assertion.getWorkCounter()),
            assertion.getMaxRepetition() == assertion.getWorkCounter());
    }

    @Test
    public void testDelay() {
        RepetitiveAssertionForTest assertion = createAssertionForTest(5, 0, 1000);
        long start = System.currentTimeMillis();
        assertion.start();
        assertion.join();
        long actualDelay = System.currentTimeMillis() - start;
        long totalDelay = assertion.getTotalDelay();
        long delta = totalDelay / 5;
        assertThat(
            String.format("The actual delay is not accurate. (Delay=%d, Repetition=%d, Actual=%d)", assertion.getDelay(), assertion.getMaxRepetition(), actualDelay),
            actualDelay,
            allOf(greaterThanOrEqualTo(totalDelay - delta), lessThanOrEqualTo(totalDelay + delta)));
    }

    @Test
    public void testInitialDelay() {
        RepetitiveAssertionForTest assertion = createAssertionForTest(1, 2000, 0);
        long start = System.currentTimeMillis();
        assertion.start();
        assertion.join();
        long actualInitialDelay = System.currentTimeMillis() - start;
        long delta = assertion.getInitialDelay() / 10;
        assertThat(
            String.format("The actual initial delay is not accurate. (InitialDelay=%d, Actual=%d)", assertion.getInitialDelay(), actualInitialDelay),
            actualInitialDelay,
            allOf(greaterThanOrEqualTo(assertion.getInitialDelay() - delta), lessThanOrEqualTo(assertion.getInitialDelay() + delta)));
    }

    private RepetitiveAssertionForTest createAssertionForTest(int maxRepetition, int initialDelay, int delay) {
        RepetitiveAssertionForTest assertion = new RepetitiveAssertionForTest();
        assertion.setMaxRepetition(maxRepetition);
        assertion.setInitialDelay(initialDelay);
        assertion.setDelay(delay);
        return assertion;
    }

    private class RepetitiveAssertionForTest extends RepetitiveAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        public int getWorkCounter() {
            return workCounter;
        }

        public int getMaxRepetition() {
            return maxRepetition;
        }

        public int getDelay() {
            return delay;
        }

        public int getInitialDelay() {
            return initialDelay;
        }

        public int getTotalDelay() {
            return delay * maxRepetition + initialDelay;
        }

        @Override
        public void init(Map<String, Object> attributes) { }

        @Override
        protected void work() {
            ++workCounter;
        }

        private int workCounter = 0;
    }
}
