package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestStartEventState {
    @Before
    public void createModel() {
        event = new StartEvent();
        linkOut = new Link(event, null);
    }

    @Test
    public void onPulledGeneratesTokenOnlyOnce() {
        StartEventState eventState = event.createState();
        LinkState linkStateOut = linkOut.createState();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);
        when(
            stateContext.getLinkStatesOut(eventState)).
        thenReturn(
            Arrays.asList(linkStateOut));

        StateChange firstStateChange =
            eventState.onPulled(linkStateOut, stateContext);
        StateChange secondStateChange = null;

        if (firstStateChange.hasNodeStateChanged() &&
            firstStateChange.hasLinkStatesOutChanged()
        ) {
            secondStateChange = firstStateChange.
                                getNodeState().
                                onPulled(
                                    firstStateChange.getLinkStatesOut().get(0),
                                    stateContext);
        }

        assertThat(
            "after first pull, node state changed",
            firstStateChange.hasNodeStateChanged());
        assertThat(
            "after first pull, outgoing link state changed",
            firstStateChange.hasLinkStatesOutChanged());
        assertThat(
            "after first pull, number of token on outgoing link",
            firstStateChange.getLinkStatesOut().get(0).getTokenProduced(),
            equalTo(1));
        assertThat(
            "after first pull, no errors",
            !firstStateChange.hasErrors());

        assertThat(
            "after second pull, node state changed",
            secondStateChange.hasNodeStateChanged());
        assertThat(
            "after second pull, node error count increased",
            secondStateChange.getNodeState().getErrorCount(),
            greaterThan(firstStateChange.getNodeState().getErrorCount()));
        assertThat(
            "after second pull, outgoing link state unchanged",
            !secondStateChange.hasLinkStatesOutChanged());
        assertThat(
            "after second pull, has START_EVENT_NO_TOKEN error",
            secondStateChange.hasError(StateError.START_EVENT_NO_TOKEN));
    }

    private StartEvent event;
    private Link linkOut;
}
