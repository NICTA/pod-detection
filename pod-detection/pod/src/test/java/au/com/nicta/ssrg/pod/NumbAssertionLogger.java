package au.com.nicta.ssrg.pod;

public class NumbAssertionLogger extends AssertionLogger {
    public NumbAssertionLogger(Assertion assertion) {
        this.assertion = assertion;
    }

    @Override
    public void recordAssertion(String format, Object ... args) { }
}
