package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class ActivityState extends NodeState {
    public ActivityState(Activity activity) {
        super(activity);
    }

    public ActivityState(Activity activity, int errorCount, long lastExecTime) {
        super(activity, errorCount, lastExecTime);
    }

    @Override
    public Activity getNode() {
        return (Activity)super.getNode();
    }

    @Override
    public List<LinkState> shouldPullBeforeExecuted(ProcessStateContext context) {
        LinkState linkStateIn = getLinkStateIn(context);

        if (!linkStateIn.hasTokenRemaining()) {
            return Arrays.asList(linkStateIn);
        }

        return null;
    }

    @Override
    public List<LinkState> shouldPullBeforePulled(ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StateChange onExecuted(ProcessStateContext context) {
        LinkState linkStateIn = getLinkStateIn(context);
        LinkState linkStateOut = getLinkStateOut(context);

        ActivityState nodeState = this.updateLastExecTime();
        EnumSet<StateError> errors = null;
        if (!linkStateIn.hasTokenRemaining()) {
            nodeState = nodeState.incrementErrorCount();
            errors = EnumSet.of(StateError.LINK_IN_NO_TOKEN);
        }

        return new StateChange(nodeState,
                               Arrays.asList(linkStateIn.consumeToken()),
                               Arrays.asList(linkStateOut.produceToken()),
                               errors);
    }

    @Override
    public StateChange onPulled(LinkState linkStateOutPulled,
                                ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean respondsToPull() { return false; }

    private ActivityState incrementErrorCount() {
        return new ActivityState(getNode(),
                                 getErrorCount() + 1,
                                 getLastExecTime());
    }

    private ActivityState updateLastExecTime() {
        return new ActivityState(getNode(),
                                 getErrorCount(),
                                 newLastExecTime());
    }

    private LinkState getLinkStateIn(ProcessStateContext context) {
        return context.getLinkStatesIn(this).get(0);
    }

    private LinkState getLinkStateOut(ProcessStateContext context) {
        return context.getLinkStatesOut(this).get(0);
    }
}
