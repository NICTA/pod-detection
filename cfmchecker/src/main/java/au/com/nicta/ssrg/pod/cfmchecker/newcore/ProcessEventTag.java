package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessEventTag {
    TIME_ANOMALY("time anomaly"),
    INVARIANT_VIOLATION("invariant violation"),
    RESTART("restart"),
    COMPLETED("completed"),
    UNFIT("unfit");

    @JsonCreator
    public static ProcessEventTag convertFromValue(String value) {
        switch (value) {
            case "unfit":
                return ProcessEventTag.UNFIT;
            case "restart":
                return ProcessEventTag.RESTART;
            case "completed":
                return ProcessEventTag.COMPLETED;
            case "invariant violation":
                return ProcessEventTag.INVARIANT_VIOLATION;
            case "time anomaly":
                return ProcessEventTag.TIME_ANOMALY;
        }

        return null;
    }

    @JsonValue
    public String value() {
        return value;
    }

    private ProcessEventTag(String value) {
        this.value = value;
    }

    private String value;
}
