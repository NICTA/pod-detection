package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.EnumSet;
import java.util.List;

public class StateChange {
    public StateChange(NodeState nodeState) {
        this.nodeState = nodeState;
        this.linkStatesIn = null;
        this.linkStatesOut = null;
        this.errors = null;
    }

    public StateChange(EnumSet<StateError> errors) {
        this.nodeState = null;
        this.linkStatesIn = null;
        this.linkStatesOut = null;
        this.errors = errors;
    }

    public StateChange(NodeState nodeState, EnumSet<StateError> errors) {
        this.nodeState = nodeState;
        this.linkStatesIn = null;
        this.linkStatesOut = null;
        this.errors = errors;
    }

    public StateChange(NodeState nodeState,
                       List<LinkState> linkStatesIn,
                       List<LinkState> linkStatesOut) {
        this.nodeState = nodeState;
        this.linkStatesIn = linkStatesIn;
        this.linkStatesOut = linkStatesOut;
        this.errors = null;
    }

    public StateChange(NodeState nodeState,
                       List<LinkState> linkStatesIn,
                       List<LinkState> linkStatesOut,
                       EnumSet<StateError> errors) {
        this.nodeState = nodeState;
        this.linkStatesIn = linkStatesIn;
        this.linkStatesOut = linkStatesOut;
        this.errors = errors;
    }

    public NodeState getNodeState() { return nodeState; }
    public List<LinkState> getLinkStatesIn() { return linkStatesIn; }
    public List<LinkState> getLinkStatesOut() { return linkStatesOut; }
    public EnumSet<StateError> getErrors() { return errors; }

    public boolean hasNodeStateChanged() { return nodeState != null; }

    public boolean hasLinkStatesInChanged() {
        return !(linkStatesIn == null || linkStatesIn.isEmpty());
    }

    public boolean hasLinkStatesOutChanged() {
        return !(linkStatesOut == null || linkStatesOut.isEmpty());
    }

    public boolean hasErrors() {
        return !(errors == null || errors.isEmpty());
    }

    public boolean hasError(StateError error) {
        if (errors == null) {
            return false;
        }
        return errors.contains(error);
    }

    private NodeState nodeState;
    private List<LinkState> linkStatesIn;
    private List<LinkState> linkStatesOut;
    private EnumSet<StateError> errors;
}
