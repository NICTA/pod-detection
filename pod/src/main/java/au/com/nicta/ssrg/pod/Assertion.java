package au.com.nicta.ssrg.pod;

import java.util.Map;

public abstract class Assertion {
    public Assertion() {
        assertionLogger = AssertionLoggerCreator.create(this);
    }

    public abstract void init(Map<String, Object> attributes);
    public abstract void start();

    public void setStep(Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public AssertionState getState() {
        return state;
    }

    protected void assertPass(String format, Object ... args) {
        state = AssertionState.PASS;
        assertionLogger.recordAssertion(format, args);
    }

    protected void assertFail(String format, Object ... args) {
        state = AssertionState.FAIL;
        assertionLogger.recordAssertion(format, args);
    }

    protected void assertError(String format, Object ... args) {
        state = AssertionState.ERROR;
        assertionLogger.recordAssertion(format, args);
    }

    protected AssertionState state = AssertionState.NEUTRAL;
    protected AssertionLogger assertionLogger;
    private Step step;
    private String name;
}
