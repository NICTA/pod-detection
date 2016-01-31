package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAndJoinGatewayState {
    @Before
    public void createModel() {
        gateway = new AndJoinGateway();
        firstLinkIn = new Link(null, gateway);
        secondLinkIn = new Link(null, gateway);
        linkOut = new Link(gateway, null);
    }

    @Test
    public void onPulledWithTokenOnEveryIncomingLinkMergesTokensToOneOnOutgoingLink() {
        AndJoinGatewayState gatewayState = gateway.createState();
        LinkState linkStateOut = linkOut.createState();

        // Incoming links have initial token.
        LinkState firstLinkStateIn = firstLinkIn.createState().produceToken();
        LinkState secondLinkStateIn = secondLinkIn.createState().produceToken();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);
        when(
            stateContext.getLinkStatesIn(gatewayState)).
        thenReturn(
            Arrays.asList(firstLinkStateIn, secondLinkStateIn));
        when(
            stateContext.getLinkStatesOut(gatewayState)).
        thenReturn(
            Arrays.asList(linkStateOut));

        StateChange stateChange =
            gatewayState.onPulled(linkStateOut, stateContext);

        assertThat("incoming link states have changed",
                   stateChange.hasLinkStatesInChanged());
        assertThat("number of changed incoming link states",
                   stateChange.getLinkStatesIn(),
                   hasSize(2));
        assertThat("number of tokens comsumed from each incoming link",
                   stateChange.getLinkStatesIn()
                              .stream()
                              .map(ls -> ls.getTokenConsumed())
                              .collect(Collectors.toList()),
                   everyItem(equalTo(1)));

        assertThat("outgoing link state has changed",
                   stateChange.hasLinkStatesOutChanged());
        assertThat("number of tokens produced to outgoing link",
                   stateChange.getLinkStatesOut().get(0).getTokenProduced(),
                   equalTo(1));

        assertThat("node state has not changed",
                   !stateChange.hasNodeStateChanged());
        assertThat("no errors", !stateChange.hasErrors());
    }

    @Test
    public void onPulledWhenAtLeastOneIncomingLinkHasNoTokenChangesNothing() {
        AndJoinGatewayState gatewayState = gateway.createState();
        LinkState linkStateOut = linkOut.createState();

        // One incoming link does not have initial token.
        LinkState firstLinkStateIn = firstLinkIn.createState().produceToken();
        LinkState secondLinkStateIn = secondLinkIn.createState();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);
        when(
            stateContext.getLinkStatesIn(gatewayState)).
        thenReturn(
            Arrays.asList(firstLinkStateIn, secondLinkStateIn));
        when(
            stateContext.getLinkStatesOut(gatewayState)).
        thenReturn(
            Arrays.asList(linkStateOut));

        StateChange stateChange =
            gatewayState.onPulled(linkStateOut, stateContext);

        assertNull("no state change", stateChange);
    }

    private AndJoinGateway gateway;
    private Link firstLinkIn;
    private Link secondLinkIn;
    private Link linkOut;
}
