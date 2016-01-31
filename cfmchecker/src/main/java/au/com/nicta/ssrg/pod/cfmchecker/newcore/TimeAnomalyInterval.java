package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeAnomalyInterval {
    @JsonCreator
    public TimeAnomalyInterval(@JsonProperty("start") long start,
                               @JsonProperty("end") long end) {
        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    private long start;
    private long end;
}
