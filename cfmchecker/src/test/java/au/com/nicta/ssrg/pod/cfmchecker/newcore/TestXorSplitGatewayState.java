package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestXorSplitGatewayState {
    @Before
    public void createModel() {
        gateway = new XorSplitGateway();
        linkIn = new Link(null, gateway);
        firstLinkOut = new Link(gateway, null);
        secondLinkOut = new Link(gateway, null);
    }

    @Test
    public void onPulledWithIncomingTokenMovesTokenToPulledOutgoingLink() {
        XorSplitGatewayState gatewayState = gateway.createState();
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
                   hasSize(1));
        assertThat("changed outgoing link is the pulled link",
                   stateChange.getLinkStatesOut().get(0).getLink(),
                   is(theInstance(secondLinkStateOut.getLink())));
        assertThat("number of tokens produced for changed outgoing link",
                   stateChange.getLinkStatesOut().get(0).getTokenProduced(),
                   equalTo(1));

        assertThat("node state has not changed",
                   !stateChange.hasNodeStateChanged());
        assertThat("no errors", !stateChange.hasErrors());
    }

    @Test
    public void onPulledWithNoIncomingTokenChangesNothing() {
        XorSplitGatewayState gatewayState = gateway.createState();
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

    private XorSplitGateway gateway;
    private Link linkIn;
    private Link firstLinkOut;
    private Link secondLinkOut;
}
