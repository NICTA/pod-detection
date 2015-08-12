package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.nicta.ssrg.pod.cfmchecker.core.ConformanceException.ErrorCode;

public class Activity extends Node {
    public static class TimeChecker {
        public static class AnomalyInterval {
            @JsonCreator
            public AnomalyInterval(
                    @JsonProperty("start") long start,
                    @JsonProperty("end") long end) {
                this.start = start;
                this.end = end;
            }

            public long start() {
                return start;
            }

            public long end() {
                return end;
            }

            private long start;
            private long end;
        }

        @JsonCreator
        public TimeChecker(
                @JsonProperty("ais") List<AnomalyInterval> anomalyIntervals) {
            if (anomalyIntervals != null) {
                this.anomalyIntervals = anomalyIntervals;
            } else {
                this.anomalyIntervals = new ArrayList<>();
            }
        }

        public boolean checkSpan(String activity, long span) {
            for (AnomalyInterval ai : anomalyIntervals) {
                if (span > ai.start() && span < ai.end()) {
                    System.out.printf(
                        "Time anomaly found: activity=%s, duration=%d.%n",
                        activity,
                        span);
                    return false;
                }
            }
            return true;
        }

        private List<AnomalyInterval> anomalyIntervals;
    }

    public class State extends Node.State {
        @Override
        public Activity getNode() {
            return Activity.this;
        }

        public void start() {
            startTimes.add(new Date());
            endTimes.add(null);
        }

        public void end() {
            int index = endTimes.size() - 1;

            if (index < 0) {
                return;
            }

            endTimes.remove(index);
            endTimes.add(new Date());

            long span =
                endTimes.get(index).getTime() - startTimes.get(index).getTime();
            System.out.printf(
                "Activity (id=%d, name=%s) executed for %d ms.%n",
                Activity.this.getID(),
                Activity.this.getName(),
                span);
            boolean isValid = Activity.this.timeChecker.checkSpan(
                Activity.this.getName(), span);
            if (!isValid) {
                throw new ConformanceException(ErrorCode.TIME_ANOMALY);
            }
        }

        private List<Date> startTimes = new ArrayList<>();
        private List<Date> endTimes = new ArrayList<>();
    }

    @JsonCreator
    public Activity(
            @JsonProperty("name") String name,
            @JsonProperty("tc") TimeChecker timeChecker) {
        this.name = name;
        if (timeChecker != null) {
            this.timeChecker = timeChecker;
        } else {
            this.timeChecker = new TimeChecker(null);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public State newState() {
        return new State();
    }

    @Override
    public boolean pull(
            Link.State pullLinkState,
            Node.State nodeState,
            List<Link.State> linkStatesIn,
            List<Link.State> linkStatesOut,
            ProcessContext context) {
        return false;
    }

    @Override
    public boolean execute(
            Node.State nodeState,
            List<Link.State> linkStatesIn,
            List<Link.State> linkStatesOut,
            ProcessContext context) {
        Link.State linkStateIn = linkStatesIn.get(0);
        boolean isSuccess = linkStateIn.hasRemaining();
        if (!isSuccess) {
            context.pull(linkStateIn.getLink());
            isSuccess = linkStateIn.hasRemaining();
        }
        linkStatesIn.get(0).consume();
        linkStatesOut.get(0).produce();
        return isSuccess;
    }

    private String name;
    private TimeChecker timeChecker;
}
