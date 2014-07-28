package au.com.nicta.ssrg.pod;

public class AssertionLoggerCreator {
    public static AssertionLogger create(Assertion assertion) {
        return currentCreator.createInstance(assertion);
    }

    public static void setCurrentCreator(AssertionLoggerCreator creator) {
        currentCreator = creator;
    }

    protected AssertionLogger createInstance(Assertion assertion) {
        return new AssertionLogger(assertion);
    }

    private static AssertionLoggerCreator currentCreator = new AssertionLoggerCreator();
}
