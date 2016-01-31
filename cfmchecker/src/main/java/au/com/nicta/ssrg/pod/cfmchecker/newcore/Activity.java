package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Activity extends Node implements TimeCheckable {
    @JsonCreator
    public Activity(@JsonProperty("name") String name,
                    @JsonProperty("tc") TimeChecker timeChecker) {
        this.name = name;
        this.timeChecker =
            timeChecker == null ? new TimeChecker() : timeChecker;
    }

    public String getName() {
        return name;
    }

    @Override
    public ActivityState createState() {
        return new ActivityState(this);
    }

    @Override
    public StateError checkTimeSpan(long span) {
        return timeChecker.checkTimeSpan(span);
    }

    private String name;
    private TimeChecker timeChecker;
}
