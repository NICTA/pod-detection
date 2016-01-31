package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestXorJoinGatewayState {
    @Before
    public void createModel() {
        gateway = new XorJoinGateway();
        firstLinkIn = new Link(null, gateway);
        secondLinkIn = new Link(null, gateway);
        linkOut = new Link(gateway, null);
    }

    @Test
    public void onPulledWithTokenOnEveryIncomingLinkMovesOnlyOneTokenToOutgoingLink() {
        XorJoinGatewayState gatewayState = gateway.createState();
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
                   hasSize(1));
        assertThat("number of tokens comsumed for changed incoming link",
                   stateChange.getLinkStatesIn().get(0).getTokenConsumed(),
                   equalTo(1));

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
    public void onPulledWithNoTokenOnAnyIncomingLinkChangesNothing() {
        XorJoinGatewayState gatewayState = gateway.createState();
        LinkState linkStateOut = linkOut.createState();
        LinkState firstLinkStateIn = firstLinkIn.createState();
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

    private XorJoinGateway gateway;
    private Link firstLinkIn;
    private Link secondLinkIn;
    private Link linkOut;
}
