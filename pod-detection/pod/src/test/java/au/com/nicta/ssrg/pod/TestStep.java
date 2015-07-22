package au.com.nicta.ssrg.pod;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TestStep {
    @BeforeClass
    public static void init() {
        AssertionLoggerCreator.setCurrentCreator(new NumbAssertionLoggerCreator());
    }

    @Test
    public void testStart() {
        String[] assertionKeys = new String[] { "assertion1", "assertion2" };
        List<AssertionForTest> assertions = new ArrayList<AssertionForTest>(assertionKeys.length);
        Map<String, Assertion> keyAssertionMap = new HashMap<String, Assertion>(2);

        for (String key : assertionKeys) {
            AssertionForTest assertion = new AssertionForTest();
            assertions.add(assertion);
            keyAssertionMap.put(key, assertion);
        }

        StepForTest step = new StepForTest();
        step.setDetectionServiceContext(new DetectionServiceContextForTest(keyAssertionMap));
        step.setAssertionKeys(Arrays.asList(assertionKeys));
        step.start(new HashMap<String, Object>());

        EnumSet<CompletedAssertionAction> allRequiredActions = EnumSet.allOf(CompletedAssertionAction.class);
        for (int i = 0; i < assertionKeys.length; ++i) {
            assertEquals(
                String.format("Assertion's name is not equal to its assertion key. (Index=%d)", i),
                assertionKeys[i],
                assertions.get(i).getName());
            assertTrue(
                String.format(
                    "Assertion did not complete all required actions. (Index=%d, Required=%s, Completed=%s)",
                    i,
                    allRequiredActions.toString(),
                    assertions.get(i).getCompletedAssertionActions().toString()),
                assertions.get(i).hasCompletedAllRequiredActions(allRequiredActions));
        }
    }

    private class DetectionServiceContextForTest extends DetectionServiceContext {
        public DetectionServiceContextForTest(Map<String, Assertion> assertionsForTest) {
            this.assertionsForTest = assertionsForTest;
        }

        @Override
        public Assertion getAssertion(String name) {
            return assertionsForTest.get(name);
        }

        private Map<String, Assertion> assertionsForTest;
    }

    private class StepForTest extends Step {
        public void setDetectionServiceContext(DetectionServiceContext detectionServiceContext) {
            this.detectionServiceContext = detectionServiceContext;
        }

        @Override
        protected DetectionServiceContext getDetectionServiceContext() {
            return detectionServiceContext;
        }

        private DetectionServiceContext detectionServiceContext;
    }

    private class AssertionForTest extends Assertion {
        @Override
        public void init(Map<String, Object> attributes) {
            completedAssertionActions.add(CompletedAssertionAction.INIT);
        }

        @Override
        public void start() {
            completedAssertionActions.add(CompletedAssertionAction.START);
        }

        public Boolean hasCompletedAllRequiredActions(EnumSet<CompletedAssertionAction> allRequiredActions) {
            return completedAssertionActions.containsAll(allRequiredActions);
        }

        public EnumSet<CompletedAssertionAction> getCompletedAssertionActions() {
            return completedAssertionActions;
        }

        private EnumSet<CompletedAssertionAction> completedAssertionActions = EnumSet.noneOf(CompletedAssertionAction.class);
    }

    private enum CompletedAssertionAction {
        INIT,
        START
    }
}
