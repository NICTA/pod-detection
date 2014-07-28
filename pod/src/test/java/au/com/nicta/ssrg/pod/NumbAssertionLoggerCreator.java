package au.com.nicta.ssrg.pod;

public class NumbAssertionLoggerCreator extends AssertionLoggerCreator {
    @Override
    protected AssertionLogger createInstance(Assertion assertion) {
        return new NumbAssertionLogger(assertion);
    }
}
