package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TimeChecker {
    @JsonCreator
    public TimeChecker(
        @JsonProperty("ais") List<TimeAnomalyInterval> anomalyIntervals) {

        this.anomalyIntervals = new ArrayList<>();
        if (anomalyIntervals != null) {
            this.anomalyIntervals.addAll(anomalyIntervals);
        }
    }

    public TimeChecker() {
        this.anomalyIntervals = new ArrayList<>();
    }

    public StateError checkTimeSpan(long span) {
        for (TimeAnomalyInterval interval : anomalyIntervals) {
            if (span > interval.getStart() && span < interval.getEnd()) {
                return StateError.NODE_TIME_ANOMALY;
            }
        }
        return null;
    }

    private List<TimeAnomalyInterval> anomalyIntervals;
}
