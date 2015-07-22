package au.com.nicta.ssrg.pod.cfmchecker.core;

public class ConformanceException extends RuntimeException {
    public static enum ErrorCode {
        EVENT_UNFIT,
        INVARIANT_VIOLATION,
        TIME_ANOMALY
    }

    public ConformanceException(ErrorCode code) {
        this.code = code;
    }

    public ErrorCode code() {
        return code;
    }

    public Node.State nodeState() {
        return nodeState;
    }

    public void nodeState(Node.State nodeState) {
        this.nodeState = nodeState;
    }

    public ProcessInstance instance() {
        return instance;
    }

    public void instance(ProcessInstance instance) {
        this.instance = instance;
    }

    private ErrorCode code;
    private Node.State nodeState;
    private ProcessInstance instance;
}
