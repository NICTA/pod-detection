package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class ProcessModelState extends NodeState {
    public ProcessModelState(ProcessModel model,
                             int errorCount,
                             long lastExecTime,
                             boolean hasToken) {
        super(model, errorCount, lastExecTime);
        this.hasToken = hasToken;
    }

    public ProcessModelState(ProcessModel model) {
        super(model);
        this.hasToken = false;
    }

    @Override
    public ProcessModel getNode() {
        return (ProcessModel)super.getNode();
    }

    @Override
    public List<LinkState> shouldPullBeforeExecuted(ProcessStateContext context) {
        LinkState linkStateIn = getLinkStateIn(context);
        if (linkStateIn.hasTokenRemaining()) {
            return null;
        }
        return Arrays.asList(linkStateIn);
    }

    @Override
    public List<LinkState> shouldPullBeforePulled(ProcessStateContext context) {
        return null;
    }

    @Override
    public StateChange onExecuted(ProcessStateContext context) {
        ProcessModelState nodeState = this.updateLastExecTime();

        EnumSet<StateError> errors = EnumSet.noneOf(StateError.class);
        // Checks maximum sub-context count.
        int maxSubContextCount =
            getNode().getNumericInvariants().getMaxInstanceCount();
        if (context.getSubContextCount(this) > maxSubContextCount) {
            errors.add(StateError.NUMERIC_INVARIANTS_VIOLATION);
        }

        // Keeps at most one token.
        if (hasToken) {
            if (errors.isEmpty()) {
                return new StateChange(nodeState);
            }
            return new StateChange(nodeState, errors);
        }

        nodeState = nodeState.keepToken();
        LinkState linkStateIn = getLinkStateIn(context);
        if (linkStateIn.hasTokenRemaining()) {
            return new StateChange(nodeState,
                                   Arrays.asList(linkStateIn.consumeToken()),
                                   null,
                                   errors);
        }

        errors.add(StateError.LINK_IN_NO_TOKEN);
        return new StateChange(nodeState.incrementErrorCount(),
                               Arrays.asList(linkStateIn.consumeToken()),
                               null,
                               errors);
    }

    @Override
    public StateChange onPulled(LinkState linkStateOutPulled,
                                ProcessStateContext context) {
        EnumSet<StateError> errors = EnumSet.noneOf(StateError.class);

        // Checks minimum sub-context count.
        int minSubContextCount =
            getNode().getNumericInvariants().getMinInstanceCount();
        if (context.getSubContextCount(this) < minSubContextCount) {
            errors.add(StateError.NUMERIC_INVARIANTS_VIOLATION);
        }

        // Checks completion of every sub-context.
        if (!context.haveAllSubContextsCompleted(this)) {
            errors.add(StateError.NUMERIC_INVARIANTS_VIOLATION);
        }

        linkStateOutPulled = linkStateOutPulled.produceToken();
        if (hasToken) {
            return new StateChange(
                releaseToken(),
                null,
                Collections.singletonList(linkStateOutPulled),
                errors);
        }

        errors.add(StateError.PROCESS_MODEL_NO_TOKEN);
        return new StateChange(
            incrementErrorCount(),
            null,
            Collections.singletonList(linkStateOutPulled),
            errors);
    }

    @Override
    public boolean respondsToPull() {
        return true;
    }

    public boolean hasToken() {
        return hasToken;
    }

    private ProcessModelState releaseToken() {
        return new ProcessModelState(getNode(),
                                     getErrorCount(),
                                     getLastExecTime(),
                                     false);
    }

    private ProcessModelState keepToken() {
        return new ProcessModelState(getNode(),
                                     getErrorCount(),
                                     getLastExecTime(),
                                     true);
    }

    private ProcessModelState incrementErrorCount() {
        return new ProcessModelState(getNode(),
                                     getErrorCount() + 1,
                                     getLastExecTime(),
                                     hasToken);
    }

    private ProcessModelState updateLastExecTime() {
        return new ProcessModelState(getNode(),
                                     getErrorCount(),
                                     newLastExecTime(),
                                     hasToken);
    }

    private LinkState getLinkStateIn(ProcessStateContext context) {
        return context.getLinkStatesIn(this).get(0);
    }

    private boolean hasToken;
}
