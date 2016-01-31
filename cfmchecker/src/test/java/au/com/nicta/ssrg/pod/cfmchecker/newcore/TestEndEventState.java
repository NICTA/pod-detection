package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEndEventState {
    @Before
    public void createModel() {
        event = new EndEvent();
        linkIn = new Link(null, event);
    }

    @Test
    public void onExeuctedWithIncomingTokenConsumesToken() {
        EndEventState eventState = event.createState();

        // Incoming link with initial token.
        LinkState linkStateIn = linkIn.createState().produceToken();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);
        when(
            stateContext.getLinkStatesIn(eventState)).
        thenReturn(
            Arrays.asList(linkStateIn));

        StateChange stateChange = eventState.onExecuted(stateContext);

        assertThat("incoming link state has changed",
                   stateChange.hasLinkStatesInChanged());
        assertThat("number of tokens comsumed from incoming link",
                   stateChange.getLinkStatesIn().get(0).getTokenConsumed(),
                   equalTo(1));
        assertThat("no errors", !stateChange.hasErrors());
    }

    private EndEvent event;
    private Link linkIn;
}
