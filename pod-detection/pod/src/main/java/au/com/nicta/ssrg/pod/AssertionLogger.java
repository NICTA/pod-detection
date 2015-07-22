package au.com.nicta.ssrg.pod;

import org.apache.logging.log4j.*;

public class AssertionLogger {
    public AssertionLogger(Assertion assertion) {
        this.assertion = assertion;
        logger = LogManager.getLogger(this.assertion.getClass().getName());
    }

    public void recordAssertion(String format, Object ... args) {
        Level level = Level.INFO;
        switch (assertion.getState()) {
            case FAIL:
                level = Level.WARN;
                break;
            case ERROR:
                level = Level.ERROR;
                break;
        }
        Step step = assertion.getStep();
        Process process = step.getProcess();
        ThreadContext.put(PROCESS_NAME, process.getName());
        ThreadContext.put(PROCESS_TYPE, process.getType());
        ThreadContext.put(STEP_NAME, step.getName());
        ThreadContext.put(ASSERTION_NAME, assertion.getName());
        ThreadContext.put(ASSERTION_STATE, assertion.getState().toString());
        logger.log(level, ASSERTION_MARKER, format, args);
        ThreadContext.remove(PROCESS_NAME);
        ThreadContext.remove(PROCESS_TYPE);
        ThreadContext.remove(STEP_NAME);
        ThreadContext.remove(ASSERTION_NAME);
        ThreadContext.remove(ASSERTION_STATE);
    }

    public void info(String format, Object ... args) {
        logger.info(format, args);
    }

    protected AssertionLogger() { }

    protected static final Marker DETECTION_MARKER = MarkerManager.getMarker("POD-Detection");
    protected static final Marker ASSERTION_MARKER = MarkerManager.getMarker("Assertion", DETECTION_MARKER);
    protected Assertion assertion;
    private Logger logger;
    private static final String PROCESS_NAME = "process.name";
    private static final String PROCESS_TYPE = "process.type";
    private static final String STEP_NAME = "step.name";
    private static final String ASSERTION_NAME = "assertion.name";
    private static final String ASSERTION_STATE = "assertion.state";
}
