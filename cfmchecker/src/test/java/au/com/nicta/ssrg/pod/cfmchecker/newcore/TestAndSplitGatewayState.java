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

public class TestAndSplitGatewayState {
    @Before
    public void createModel() {
        gateway = new AndSplitGateway();
        linkIn = new Link(null, gateway);
        firstLinkOut = new Link(gateway, null);
        secondLinkOut = new Link(gateway, null);
    }

    @Test
    public void onPulledWithIncomingTokenSplitsTokenToAllOutgoingLinks() {
        AndSplitGatewayState gatewayState = gateway.createState();
        LinkState firstLinkStateOut = firstLinkOut.createState();
        LinkState secondLinkStateOut = secondLinkOut.createState();

        // Incoming link has initial token.
        LinkState linkStateIn = linkIn.createState().produceToken();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);
        when(
            stateContext.getLinkStatesIn(gatewayState)).
        thenReturn(
            Arrays.asList(linkStateIn));
        when(
            stateContext.getLinkStatesOut(gatewayState)).
        thenReturn(
            Arrays.asList(firstLinkStateOut, secondLinkStateOut));

        StateChange stateChange =
            gatewayState.onPulled(secondLinkStateOut, stateContext);


        assertThat("incoming link state has changed",
                   stateChange.hasLinkStatesInChanged());
        assertThat("number of tokens consumed from incoming link",
                   stateChange.getLinkStatesIn().get(0).getTokenConsumed(),
                   equalTo(1));

        assertThat("outgoing link states have changed",
                   stateChange.hasLinkStatesOutChanged());
        assertThat("number of changed outgoing link states",
                   stateChange.getLinkStatesOut(),
                   hasSize(2));
        assertThat("number of tokens produced for each outgoing link",
                   stateChange.getLinkStatesOut()
                              .stream()
                              .map(ls -> ls.getTokenProduced())
                              .collect(Collectors.toList()),
                   everyItem(equalTo(1)));

        assertThat("node state has not changed",
                   !stateChange.hasNodeStateChanged());
        assertThat("no errors", !stateChange.hasErrors());
    }

    @Test
    public void onPulledWithNoIncomingTokenChangesNothing() {
        AndSplitGatewayState gatewayState = gateway.createState();
        LinkState firstLinkStateOut = firstLinkOut.createState();
        LinkState secondLinkStateOut = secondLinkOut.createState();
        LinkState linkStateIn = linkIn.createState();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);
        when(
            stateContext.getLinkStatesIn(gatewayState)).
        thenReturn(
            Arrays.asList(linkStateIn));
        when(
            stateContext.getLinkStatesOut(gatewayState)).
        thenReturn(
            Arrays.asList(firstLinkStateOut, secondLinkStateOut));

        StateChange stateChange =
            gatewayState.onPulled(secondLinkStateOut, stateContext);

        assertNull("no state change", stateChange);
    }

    private AndSplitGateway gateway;
    private Link linkIn;
    private Link firstLinkOut;
    private Link secondLinkOut;
}
