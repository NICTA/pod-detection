package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestActivityState {
    @Before
    public void createModel() {
        activity = new Activity("TestActivity", null);
        linkIn = new Link(null, activity);
        linkOut = new Link(activity, null);
    }

    @Test
    public void onExecutedWithIncomingTokenMovesTokenToOutgoingLinkAndUpdatesNodeLastExecTime() {
        ActivityState activityState = activity.createState();
        LinkState linkStateOut = linkOut.createState();

        // Incoming link with an initial token.
        LinkState linkStateIn = linkIn.createState().produceToken();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);
        when(
            stateContext.getLinkStatesIn(activityState)).
        thenReturn(
            Arrays.asList(linkStateIn));
        when(
            stateContext.getLinkStatesOut(activityState)).
        thenReturn(
            Arrays.asList(linkStateOut));

        StateChange stateChange = activityState.onExecuted(stateContext);

        assertThat("incoming link state has changed",
                   stateChange.hasLinkStatesInChanged());
        assertThat("number of tokens consumed from incoming link",
                   stateChange.getLinkStatesIn().get(0).getTokenConsumed(),
                   equalTo(1));

        assertThat("outgoing link state has changed",
                   stateChange.hasLinkStatesOutChanged());
        assertThat("number of tokens produced to outgoing link",
                   stateChange.getLinkStatesOut().get(0).getTokenProduced(),
                   equalTo(1));

        assertThat("node state has changed",
                   stateChange.hasNodeStateChanged());
        assertThat("node last exec time increased",
                   stateChange.getNodeState().getLastExecTime(),
                   greaterThan(activityState.getLastExecTime()));

        assertThat("no errors", !stateChange.hasErrors());
    }

    @Test
    public void onExecutedWithNoIncomingTokenIncreaseErrorCountAndUpdatesNodeLastExecTime() {
        ActivityState activityState = activity.createState();
        LinkState linkStateIn = linkIn.createState();
        LinkState linkStateOut = linkOut.createState();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);
        when(
            stateContext.getLinkStatesIn(activityState)).
        thenReturn(
            Arrays.asList(linkStateIn));
        when(
            stateContext.getLinkStatesOut(activityState)).
        thenReturn(
            Arrays.asList(linkStateOut));

        StateChange stateChange = activityState.onExecuted(stateContext);

        assertThat("incoming link state has changed",
                   stateChange.hasLinkStatesInChanged());
        assertThat("number of token missing from incoming link",
                   stateChange.getLinkStatesIn().get(0).getTokenMissing(),
                   equalTo(1));

        assertThat("outgoing link state has changed",
                   stateChange.hasLinkStatesOutChanged());
        assertThat("number of tokens produced to outgoing link",
                   stateChange.getLinkStatesOut().get(0).getTokenProduced(),
                   equalTo(1));

        assertThat("node state has changed",
                   stateChange.hasNodeStateChanged());
        assertThat("node error count increased",
                   stateChange.getNodeState().getErrorCount(),
                   equalTo(activityState.getErrorCount() + 1));
        assertThat("node last exec time increased",
                   stateChange.getNodeState().getLastExecTime(),
                   greaterThan(activityState.getLastExecTime()));

        assertThat("has LINK_IN_NO_TOKEN error",
                   stateChange.hasError(StateError.LINK_IN_NO_TOKEN));
    }

    private Activity activity;
    private Link linkIn;
    private Link linkOut;
}
