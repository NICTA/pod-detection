package au.com.nicta.ssrg.pod.assertion;

import au.com.nicta.ssrg.pod.*;
import java.util.*;
import com.amazonaws.*;
import java.util.concurrent.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TestInstanceStateAssertion {
    @BeforeClass
    public static void init() {
        AssertionLoggerCreator.setCurrentCreator(new NumbAssertionLoggerCreator());
    }

    @Before
    public void prepareForAssertionInit() {
        attributesForAssertionInit = new HashMap<String, Object>(2);
        attributesForAssertionInit.put("asgName", "test-asg-name");
        attributesForAssertionInit.put("amiID", "test-ami-id");
        attributesForAssertionInit.put("instanceID", "test-instance-id");
    }

    @Test
    public void TestAssertionPassWhenInstanceStateIsTerminated() {
        String instanceState = INSTANCE_STATE_TERMINATED;
        String assigned = INSTANCE_STATE_TERMINATED;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setStateText(instanceState);
        assertion.setCheckResult(assigned);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%s, ShouldInstanceExist=%s)",
                AssertionState.PASS,
                assertion.getState(),
                assigned,
                instanceState),
            AssertionState.PASS,
            assertion.getState());
    }

    @Test
    public void TestAssertionPassWhenInstanceStateIsRunnig() {
        String instanceState = INSTANCE_STATE_RUNNING;
        String assigned = INSTANCE_STATE_RUNNING;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setStateText(instanceState);
        assertion.setCheckResult(assigned);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%s, ShouldInstanceExist=%s)",
                AssertionState.PASS,
                assertion.getState(),
                assigned,
                instanceState),
            AssertionState.PASS,
            assertion.getState());
    }

    @Test
    public void TestAssertionFailWhenInstanceStateIsTermiated() {
        String instanceState = INSTANCE_STATE_TERMINATED;
        String assigned = INSTANCE_STATE_RUNNING;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setStateText(instanceState);
        assertion.setCheckResult(assigned);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%s, ShouldInstanceExist=%s)",
                AssertionState.FAIL,
                assertion.getState(),
                assigned,
                instanceState),
            AssertionState.FAIL,
            assertion.getState());
    }

    @Test
    public void TestAssertionFailWhenInstanceStateIsRunning() {
        String instanceState = INSTANCE_STATE_RUNNING;
        String assigned = INSTANCE_STATE_TERMINATED;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setStateText(instanceState);
        assertion.setCheckResult(assigned);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%s, ShouldInstanceExist=%s)",
                AssertionState.FAIL,
                assertion.getState(),
                assigned,
                instanceState),
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

    private class AssertionForTestWithAssignedCheckResult extends InstanceStateAssertion {
        public void setCheckResult(String value) {
            checkResult = value;
        }

        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected String check() {
            return checkResult;
        }

        private String checkResult;
    }

    private class AssertionForTestWithTimeoutExceptionThrown extends InstanceStateAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected String check() throws TimeoutException {
            throw new TimeoutException();
        }
    }

    private class AssertionForTestWithInterruptedExceptionThrown extends InstanceStateAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected String check() throws InterruptedException {
            throw new InterruptedException();
        }
    }

    private class AssertionForTestWithCancellationExceptionThrown extends InstanceStateAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected String check() {
            throw new CancellationException();
        }
    }

    private class AssertionForTestWithExecutionExceptionThrown extends InstanceStateAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected String check() throws ExecutionException {
            throw new ExecutionException(null);
        }
    }

    private class AssertionForTestWithAmazonClientExceptionThrown extends InstanceStateAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected String check() {
            throw new AmazonClientException("amazon-client-exception-for-test");
        }
    }

    private class AssertionForTestWithAmazonServiceExceptionThrown extends InstanceStateAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected String check() {
            throw new AmazonServiceException("amazon-service-exception-for-test");
        }
    }

    private void setDefaultAssertionProperties(InstanceStateAssertion assertion) {
        assertion.setDelay(0);
        assertion.setInitialDelay(0);
        assertion.setMaxRepetition(3);
        assertion.setTimeout(1000);
        assertion.setStateText("running");
    }

    private static final String INSTANCE_STATE_TERMINATED = "terminated";
    private static final String INSTANCE_STATE_RUNNING = "running";
    private Map<String, Object> attributesForAssertionInit;
}
