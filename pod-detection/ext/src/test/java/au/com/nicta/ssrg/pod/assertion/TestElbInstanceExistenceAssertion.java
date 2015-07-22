package au.com.nicta.ssrg.pod.assertion;

import au.com.nicta.ssrg.pod.*;
import java.util.*;
import com.amazonaws.*;
import java.util.concurrent.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TestElbInstanceExistenceAssertion {
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
    public void TestAssertionPassWhenInstanceShouldExist() {
        Boolean shouldInstanceExist = true;
        Boolean assigned = true;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setShouldInstanceExist(shouldInstanceExist);
        assertion.setCheckResult(assigned);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%B, ShouldInstanceExist=%B)",
                AssertionState.PASS,
                assertion.getState(),
                assigned,
                shouldInstanceExist),
            AssertionState.PASS,
            assertion.getState());
    }

    @Test
    public void TestAssertionPassWhenInstanceShouldNotExist() {
        Boolean shouldInstanceExist = false;
        Boolean assigned = false;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setShouldInstanceExist(shouldInstanceExist);
        assertion.setCheckResult(assigned);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%B, ShouldInstanceExist=%B)",
                AssertionState.PASS,
                assertion.getState(),
                assigned,
                shouldInstanceExist),
            AssertionState.PASS,
            assertion.getState());
    }

    @Test
    public void TestAssertionFailWhenInstanceShouldExist() {
        Boolean shouldInstanceExist = true;
        Boolean assigned = false;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setShouldInstanceExist(shouldInstanceExist);
        assertion.setCheckResult(assigned);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%B, ShouldInstanceExist=%B)",
                AssertionState.FAIL,
                assertion.getState(),
                assigned,
                shouldInstanceExist),
            AssertionState.FAIL,
            assertion.getState());
    }

    @Test
    public void TestAssertionFailWhenInstanceShouldNotExist() {
        Boolean shouldInstanceExist = false;
        Boolean assigned = true;
        AssertionForTestWithAssignedCheckResult assertion = new AssertionForTestWithAssignedCheckResult();
        setDefaultAssertionProperties(assertion);
        assertion.setShouldInstanceExist(shouldInstanceExist);
        assertion.setCheckResult(assigned);
        assertion.init(attributesForAssertionInit);
        assertion.start();
        assertion.join();
        assertEquals(
            String.format(
                "Assertion is in a wrong state when tested with assigned check result. (State=%s, Actual=%s, Assigned=%B, ShouldInstanceExist=%B)",
                AssertionState.FAIL,
                assertion.getState(),
                assigned,
                shouldInstanceExist),
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

    private class AssertionForTestWithAssignedCheckResult extends ElbInstanceExistenceAssertion {
        public void setCheckResult(Boolean value) {
            checkResult = value;
        }

        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected void check() {
            isInstancePresent = checkResult;
        }

        private Boolean checkResult;
    }

    private class AssertionForTestWithTimeoutExceptionThrown extends ElbInstanceExistenceAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected void check() throws TimeoutException {
            throw new TimeoutException();
        }
    }

    private class AssertionForTestWithInterruptedExceptionThrown extends ElbInstanceExistenceAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected void check() throws InterruptedException {
            throw new InterruptedException();
        }
    }

    private class AssertionForTestWithCancellationExceptionThrown extends ElbInstanceExistenceAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected void check() {
            throw new CancellationException();
        }
    }

    private class AssertionForTestWithExecutionExceptionThrown extends ElbInstanceExistenceAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected void check() throws ExecutionException {
            throw new ExecutionException(null);
        }
    }

    private class AssertionForTestWithAmazonClientExceptionThrown extends ElbInstanceExistenceAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected void check() {
            throw new AmazonClientException("amazon-client-exception-for-test");
        }
    }

    private class AssertionForTestWithAmazonServiceExceptionThrown extends ElbInstanceExistenceAssertion {
        public void join() {
            try {
                worker.join();
            } catch (InterruptedException ex) { }
        }

        @Override
        protected void check() {
            throw new AmazonServiceException("amazon-service-exception-for-test");
        }
    }

    private void setDefaultAssertionProperties(ElbInstanceExistenceAssertion assertion) {
        assertion.setDelay(0);
        assertion.setInitialDelay(0);
        assertion.setMaxRepetition(3);
        assertion.setTimeout(1000);
        assertion.setElbName("test-elb-name");
        assertion.setShouldInstanceExist(true);
    }

    private Map<String, Object> attributesForAssertionInit;
}
