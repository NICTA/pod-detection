package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.Arrays;
import java.util.List;

public class EndEventState extends NodeState {
    public EndEventState(EndEvent event) {
        super(event);
    }

    @Override
    public EndEvent getNode() {
        return (EndEvent)super.getNode();
    }

    @Override
    public List<LinkState> shouldPullBeforeExecuted(ProcessStateContext context) {
        return null;
    }

    @Override
    public List<LinkState> shouldPullBeforePulled(ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StateChange onExecuted(ProcessStateContext context) {
        return new StateChange(
            null,
            Arrays.asList(
                context.getLinkStatesIn(this).get(0).consumeToken()),
            null);
    }

    @Override
    public StateChange onPulled(LinkState linkStateOutPulled,
                                ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean respondsToPull() { return false; }
}
