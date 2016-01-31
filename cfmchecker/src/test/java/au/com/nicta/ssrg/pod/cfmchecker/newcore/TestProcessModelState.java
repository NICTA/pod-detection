package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestProcessModelState {
    @Before
    public void createModel() {
        model = new ProcessModel("test-model",
                                 null,
                                 null,
                                 new NumericInvariants(1, 3),
                                 null);
        linkIn = new Link(null, model);
        linkOut = new Link(model, null);
    }

    @Test
    public void onExecutedKeepsAtMostOneTokenConsumedFromIncomingLinkAndUpdateLastExecTime()
        throws InterruptedException {

        ProcessModelState modelState = model.createState();
        LinkState linkStateOut = linkOut.createState();

        // Incoming link with initial token.
        LinkState linkStateIn = linkIn.createState().produceToken();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);

        // Will pass numeric invariants check.
        when(
            stateContext.getSubContextCount(modelState)
        ).thenReturn(2);

        when(
            stateContext.getLinkStatesIn(modelState)
        ).thenReturn(Collections.singletonList(linkStateIn));

        when(
            stateContext.getLinkStatesOut(modelState)
        ).thenReturn(Collections.singletonList(linkStateOut));

        StateChange firstStateChange = modelState.onExecuted(stateContext);
        StateChange secondStateChange = null;

        if (firstStateChange.hasNodeStateChanged()) {
            // Sleeps for 10ms to get an obvious last exec time change.
            Thread.sleep(10);
            secondStateChange = firstStateChange.getNodeState()
                                                .onExecuted(stateContext);
        }

        assertThat(
            "after first exec, node state changed",
            firstStateChange.hasNodeStateChanged());
        assertThat(
            "after first exec, node last exec time updated",
            firstStateChange.getNodeState().getLastExecTime(),
            greaterThan(modelState.getLastExecTime()));
        assertThat(
            "after first exec, node kept token",
            ((ProcessModelState)firstStateChange.getNodeState()).hasToken());
        assertThat(
            "after first exec, error count unchanged",
            firstStateChange.getNodeState().getErrorCount(),
            equalTo(modelState.getErrorCount()));
        assertThat(
            "after first exec, incoming link state changed",
            firstStateChange.hasLinkStatesInChanged());
        assertThat(
            "after first exec, number of tokens consumed from incoming link",
            firstStateChange.getLinkStatesIn().get(0).getTokenConsumed(),
            equalTo(1));
        assertThat(
            "after first exec, outgoing link state unchanged",
            !firstStateChange.hasLinkStatesOutChanged());
        assertThat(
            "after first exec, no errors",
            !firstStateChange.hasErrors());

        assertThat(
            "after second exec, node state changed",
            secondStateChange.hasNodeStateChanged());
        assertThat(
            "after second exec, node last exec time updated",
            secondStateChange.getNodeState().getLastExecTime(),
            greaterThan(firstStateChange.getNodeState().getLastExecTime()));
        assertThat(
            "after second exec, error count unchanged",
            secondStateChange.getNodeState().getErrorCount(),
            equalTo(firstStateChange.getNodeState().getErrorCount()));
        assertThat(
            "after second exec, incoming link state unchanged",
            !secondStateChange.hasLinkStatesInChanged());
        assertThat(
            "after second exec, no errors",
            !secondStateChange.hasErrors());
    }

    @Test
    public void onExecutedWithNoIncomingTokenReportsErrorsAndUpdateLastExecTime() {
        ProcessModelState modelState = model.createState();
        LinkState linkStateOut = linkOut.createState();
        LinkState linkStateIn = linkIn.createState();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);

        // Will pass numeric invariants check.
        when(
            stateContext.getSubContextCount(modelState)
        ).thenReturn(2);

        when(
            stateContext.getLinkStatesIn(modelState)
        ).thenReturn(Collections.singletonList(linkStateIn));

        when(
            stateContext.getLinkStatesOut(modelState)
        ).thenReturn(Collections.singletonList(linkStateOut));

        StateChange stateChange = modelState.onExecuted(stateContext);

        assertThat("node state has changed", stateChange.hasNodeStateChanged());
        assertThat("node last exec time updated",
                   stateChange.getNodeState().getLastExecTime(),
                   greaterThan(modelState.getLastExecTime()));
        assertThat("node has kept token",
                   ((ProcessModelState)stateChange.getNodeState()).hasToken());
        assertThat("node error count increased",
                   stateChange.getNodeState().getErrorCount(),
                   equalTo(modelState.getErrorCount() + 1));
        assertThat("incoming link state has changed",
                   stateChange.hasLinkStatesInChanged());
        assertThat("number of tokens missing from incoming link",
                   stateChange.getLinkStatesIn().get(0).getTokenMissing(),
                   equalTo(1));
        assertThat("has LINK_IN_NO_TOKEN error",
                   stateChange.hasError(StateError.LINK_IN_NO_TOKEN));
    }

    @Test
    public void onExecutedWithTooManySubContextsReportsError() {
        ProcessModelState modelState = model.createState();
        LinkState linkStateOut = linkOut.createState();

        // No incoming tokens will trigger other errors.
        LinkState linkStateIn = linkIn.createState();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);

        // Will fail numeric invariants check.
        when(
            stateContext.getSubContextCount(modelState)
        ).thenReturn(5);

        when(
            stateContext.getLinkStatesIn(modelState)
        ).thenReturn(Collections.singletonList(linkStateIn));

        when(
            stateContext.getLinkStatesOut(modelState)
        ).thenReturn(Collections.singletonList(linkStateOut));

        StateChange stateChange = modelState.onExecuted(stateContext);

        assertThat("has errors", stateChange.hasErrors());
        assertThat(
            "has NUMERIC_INVARIANTS_VIOLATION error",
            stateChange.hasError(StateError.NUMERIC_INVARIANTS_VIOLATION));
        assertThat("has other errors",
                   stateChange.getErrors().size(),
                   greaterThan(1));
    }

    @Test
    public void onPulledWithTokenKeptReleasesToken() {
        ProcessModelState modelState = new ProcessModelState(model, 0, 0, true);
        LinkState linkStateOut = linkOut.createState();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);

        // Will pass numeric invariants check.
        when(
            stateContext.getSubContextCount(modelState)
        ).thenReturn(2);

        when(
            stateContext.haveAllSubContextsCompleted(modelState)
        ).thenReturn(true);

        StateChange stateChange =
            modelState.onPulled(linkStateOut, stateContext);
        assertThat("node state has changed",
                   stateChange.hasNodeStateChanged());
        assertThat("node token has been released",
                   !((ProcessModelState)stateChange.getNodeState()).hasToken());
        assertThat("outgoing link state has changed",
                   stateChange.hasLinkStatesOutChanged());
        assertThat("number of tokens produced to outgoing link",
                   stateChange.getLinkStatesOut().get(0).getTokenProduced(),
                   equalTo(1));
        assertThat("no errors", !stateChange.hasErrors());
    }

    @Test
    public void onPulledWithNoTokenKeptReportsError() {
        ProcessModelState modelState =
            new ProcessModelState(model, 0, 0, false);
        LinkState linkStateOut = linkOut.createState();

        ProcessStateContext stateContext = mock(ProcessStateContext.class);

        // Will pass numeric invariants check.
        when(
            stateContext.getSubContextCount(modelState)
        ).thenReturn(2);

        StateChange stateChange =
            modelState.onPulled(linkStateOut, stateContext);
        assertThat("node state has changed",
                   stateChange.hasNodeStateChanged());
        assertThat("node error count increased",
                   stateChange.getNodeState().getErrorCount(),
                   equalTo(modelState.getErrorCount() + 1));
        assertThat("outgoing link state has changed",
            stateChange.hasLinkStatesOutChanged());
        assertThat("number of tokens produced to outgoing link",
            stateChange.getLinkStatesOut().get(0).getTokenProduced(),
            equalTo(1));
        assertThat("has PROCESS_MODEL_NO_TOKEN error",
                   stateChange.hasError(StateError.PROCESS_MODEL_NO_TOKEN));
    }

    @Test
    public void onPulledWithTooFewSubContextsReportsError() {
        LinkState linkStateOut = linkOut.createState();

        // No kept token will trigger other errors.
        ProcessModelState modelState =
            new ProcessModelState(model, 0, 0, false);

        ProcessStateContext stateContext = mock(ProcessStateContext.class);

        // Will fail numeric invariants check.
        when(
            stateContext.getSubContextCount(modelState)
        ).thenReturn(0);

        when(
            stateContext.haveAllSubContextsCompleted(modelState)
        ).thenReturn(true);

        StateChange stateChange =
            modelState.onPulled(linkStateOut, stateContext);

        assertThat("has errors", stateChange.hasErrors());
        assertThat(
            "has NUMERIC_INVARIANTS_VIOLATION error",
            stateChange.hasError(StateError.NUMERIC_INVARIANTS_VIOLATION));
        assertThat("has other errors",
                   stateChange.getErrors().size(),
                   greaterThan(1));
    }

    @Test
    public void onPulledWhenNotAllSubContextsCompletedReportsError() {
        LinkState linkStateOut = linkOut.createState();

        // No kept token will trigger other errors.
        ProcessModelState modelState =
            new ProcessModelState(model, 0, 0, false);

        ProcessStateContext stateContext = mock(ProcessStateContext.class);

        when(
            stateContext.getSubContextCount(modelState)
        ).thenReturn(2);

        // Will fail numeric invariants check.
        when(
            stateContext.haveAllSubContextsCompleted(modelState)
        ).thenReturn(false);

        StateChange stateChange =
            modelState.onPulled(linkStateOut, stateContext);

        assertThat("has errors", stateChange.hasErrors());
        assertThat(
            "has NUMERIC_INVARIANTS_VIOLATION error",
            stateChange.hasError(StateError.NUMERIC_INVARIANTS_VIOLATION));
        assertThat("has other errors",
            stateChange.getErrors().size(),
            greaterThan(1));
    }

    private ProcessModel model;
    private Link linkIn;
    private Link linkOut;
}
