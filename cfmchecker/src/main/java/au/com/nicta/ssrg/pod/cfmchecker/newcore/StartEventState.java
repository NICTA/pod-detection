package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class StartEventState extends NodeState {
    public StartEventState(StartEvent event, int errorCount, boolean hasToken) {
        super(event, errorCount, 0);
        this.hasToken = hasToken;
    }

    public StartEventState(StartEvent event) {
        super(event);
        this.hasToken = true;
    }

    @Override
    public StartEvent getNode() {
        return (StartEvent)super.getNode();
    }

    @Override
    public List<LinkState> shouldPullBeforeExecuted(ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<LinkState> shouldPullBeforePulled(ProcessStateContext context) {
        return null;
    }

    @Override
    public StateChange onExecuted(ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StateChange onPulled(LinkState linkStateOutPulled,
                                ProcessStateContext context) {
        if (hasToken) {
            return new StateChange(
                releaseToken(),
                null,
                Arrays.asList(
                    context.getLinkStatesOut(this).get(0).produceToken()));
        }

        return new StateChange(
            incrementErrorCount(),
            EnumSet.of(StateError.START_EVENT_NO_TOKEN));
    }

    @Override
    public boolean respondsToPull() { return true; }

    private StartEventState releaseToken() {
        return new StartEventState(getNode(), getErrorCount(), false);
    }

    private StartEventState incrementErrorCount() {
        return new StartEventState(getNode(), getErrorCount() + 1, hasToken);
    }

    private boolean hasToken;
}
