package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.Collections;
import java.util.EnumSet;

public enum StateError {
    PROCESS_MODEL_NO_TOKEN,
    START_EVENT_NO_TOKEN,
    LINK_IN_NO_TOKEN,
    NODE_TIME_ANOMALY,
    NUMERIC_INVARIANTS_VIOLATION;

    public static boolean haveUnfitErrors(EnumSet<StateError> errors) {
        return errors != null && !Collections.disjoint(UNFIT_ERRORS, errors);
    }

    private static final EnumSet<StateError> UNFIT_ERRORS = EnumSet.of(
        PROCESS_MODEL_NO_TOKEN, START_EVENT_NO_TOKEN, LINK_IN_NO_TOKEN);
}
