package au.com.nicta.ssrg.pod.cfmchecker.io;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivityPattern {
    @JsonCreator
    public ActivityPattern(
            @JsonProperty("activityName") String activityName,
            @JsonProperty("pattern") String pattern) {
        this.activityName = activityName;
        this.pattern = Pattern.compile(pattern);
    }

    public String getActivityName() {
        return activityName;
    }

    public Pattern getPattern() {
        return pattern;
    }

    private String activityName;
    private Pattern pattern;
}
