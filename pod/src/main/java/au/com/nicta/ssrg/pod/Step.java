package au.com.nicta.ssrg.pod;

import java.util.*;

public class Step {
    public void start(Map<String, Object> attributes) {
        DetectionServiceContext serviceContext = getDetectionServiceContext();
        for (String assertionKey : assertionKeys) {
            Assertion assertion = serviceContext.getAssertion(assertionKey);
            String injectedName = assertion.getName();
            if (injectedName == null || injectedName.trim().equals("")) {
                assertion.setName(assertionKey);
            }
            assertion.setStep(this);
            assertion.init(attributes);
            assertion.start();
        }
    }

    protected DetectionServiceContext getDetectionServiceContext() {
        return DetectionServiceContext.getCurrent();
    }

    public void setAssertionKeys(List<String> assertionKeys) {
        this.assertionKeys = assertionKeys;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    private String name;
    private List<String> assertionKeys;
    private Process process;
}
