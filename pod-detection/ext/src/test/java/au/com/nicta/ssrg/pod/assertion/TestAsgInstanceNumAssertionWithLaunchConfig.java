package au.com.nicta.ssrg.pod.assertion;

import au.com.nicta.ssrg.pod.*;
import java.util.*;
import com.amazonaws.*;
import java.util.concurrent.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TestAsgInstanceNumAssertionWithLaunchConfig {
    @BeforeClass
    public static void init() {
        AssertionLoggerCreator.setCurrentCreator(new NumbAssertionLoggerCreator());
    }

    @Before
    public void prepareForAssertionInit() {
        attributesForAssertionInit = new HashMap<String, Object>(2);
        attributesForAssertionInit.put("asgName", "test-asg-name");
        attributesForAssertionInit.put("amiID", "test-ami-id");
    }

    @Test
    public void TestAssertionPass() {
        int assigned = 2;
        int minInstanceNum = 2;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setCheckReturnValue(assigned);
        assertion.setMinInstanceNum(minInstanceNum);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%d, MinInstanceNum=%d)",
                AssertionState.PASS,
                assertion.getState(),
                assigned,
                minInstanceNum),
            AssertionState.PASS,
            assertion.getState());
    }

    @Test
    public void TestAssertionFail() {
        int assigned = 1;
        int minInstanceNum = 2;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setCheckReturnValue(assigned);
        assertion.setMinInstanceNum(minInstanceNum);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%d, MinInstanceNum=%d)",
                AssertionState.FAIL,
                assertion.getState(),
                assigned,
                minInstanceNum),
            AssertionState.FAIL,
            assertion.getState());
    }

    @Test
    public void TestTimeoutException() {
        AssertionForTestWithTimeoutExceptionThrown assertion = new AssertionForTestWithTimeoutExceptionThrown();
        setDefaultAssertionProperties(assertion);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when TimeoutException thrown. (State=%s, Actual=%s)",
                AssertionState.ERROR,
                assertion.getState()),
            AssertionState.ERROR,
            assertion.getState());
    }

    @Test
    public void TestInterruptedException() {
        AssertionForTestWithInterruptedExceptionThrown assertion = new AssertionForTestWithInterruptedExceptionThrown();
        setDefaultAssertionProperties(assertion);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when InterruptedException thrown. (State=%s, Actual=%s)",
                AssertionState.ERROR,
                assertion.getState()),
            AssertionState.ERROR,
            assertion.getState());
    }

    @Test
    public void TestCancellationException() {
        AssertionForTestWithCancellationExceptionThrown assertion = new AssertionForTestWithCancellationExceptionThrown();
        setDefaultAssertionProperties(assertion);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when CancellationException thrown. (State=%s, Actual=%s)",
                AssertionState.ERROR,
                assertion.getState()),
            AssertionState.ERROR,
            assertion.getState());
    }

    @Test
    public void TestExecutionException() {
        AssertionForTestWithExecutionExceptionThrown assertion = new AssertionForTestWithExecutionExceptionThrown();
        setDefaultAssertionProperties(assertion);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when ExecutionException thrown. (State=%s, Actual=%s)",
                AssertionState.ERROR,
                assertion.getState()),
            AssertionState.ERROR,
            assertion.getState());
    }

    @Test
    public void TestAmazonClientException() {
        AssertionForTestWithAmazonClientExceptionThrown assertion = new AssertionForTestWithAmazonClientExceptionThrown();
        setDefaultAssertionProperties(assertion);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when AmazonClientException thrown. (State=%s, Actual=%s)",
                AssertionState.ERROR,
                assertion.getState()),
            AssertionState.ERROR,
            assertion.getState());
    }

    @Test
    public void TestAmazonServiceException() {
        AssertionForTestWithAmazonServiceExceptionThrown assertion = new AssertionForTestWithAmazonServiceExceptionThrown();
        setDefaultAssertionProperties(assertion);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when AmazonServiceException thrown. (State=%s, Actual=%s)",
                AssertionState.ERROR,
                assertion.getState()),
            AssertionState.ERROR,
            assertion.getState());
    }

    private class AssertionForTestWithAssignedCheckResult extends AsgInstanceNumAssertionWithLaunchConfig {
        public void setCheckReturnValue(int value) {
            checkReturnValue = value;
        }

        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected int check() {
            return checkReturnValue;
        }

        private int checkReturnValue;
    }

    private class AssertionForTestWithTimeoutExceptionThrown extends AsgInstanceNumAssertionWithLaunchConfig {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected int check() throws TimeoutException {
            throw new TimeoutException();
        }
    }

    private class AssertionForTestWithInterruptedExceptionThrown extends AsgInstanceNumAssertionWithLaunchConfig {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected int check() throws InterruptedException {
            throw new InterruptedException();
        }
    }

    private class AssertionForTestWithCancellationExceptionThrown extends AsgInstanceNumAssertionWithLaunchConfig {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected int check() {
            throw new CancellationException();
        }
    }

    private class AssertionForTestWithExecutionExceptionThrown extends AsgInstanceNumAssertionWithLaunchConfig {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected int check() throws ExecutionException {
            throw new ExecutionException(null);
        }
    }

    private class AssertionForTestWithAmazonClientExceptionThrown extends AsgInstanceNumAssertionWithLaunchConfig {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected int check() {
            throw new AmazonClientException("amazon-client-exception-for-test");
        }
    }

    private class AssertionForTestWithAmazonServiceExceptionThrown extends AsgInstanceNumAssertionWithLaunchConfig {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected int check() {
            throw new AmazonServiceException("amazon-service-exception-for-test");
        }
    }

    private void setDefaultAssertionProperties(AsgInstanceNumAssertionWithLaunchConfig assertion) {
        assertion.setDelay(0);
        assertion.setInitialDelay(0);
        assertion.setMaxRepetition(3);
        assertion.setTimeout(1000);
        assertion.setMinInstanceNum(2);
        assertion.setLaunchConfigName("test-launch-config");
    }

    private Map<String, Object> attributesForAssertionInit;
}
