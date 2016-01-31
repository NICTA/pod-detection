package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NumericInvariants {
    @JsonCreator
    public NumericInvariants(
        @JsonProperty("minInstanceCount") int minInstanceCount,
        @JsonProperty("maxInstanceCount") int maxInstanceCount) {

        this.minInstanceCount = minInstanceCount;
        this.maxInstanceCount = maxInstanceCount;
    }

    public NumericInvariants() {
        this.minInstanceCount = 0;
        this.maxInstanceCount = Integer.MAX_VALUE;
    }

    public int getMinInstanceCount() {
        return minInstanceCount;
    }

    public int getMaxInstanceCount() {
        return maxInstanceCount;
    }

    private int minInstanceCount;
    private int maxInstanceCount;
}
