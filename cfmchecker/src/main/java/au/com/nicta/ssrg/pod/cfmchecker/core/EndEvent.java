package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.util.List;

public class EndEvent extends Event {
    public class State extends Node.State {
        @Override
        public EndEvent getNode() {
            return EndEvent.this;
        }
    }

    @Override
    public State newState() {
        return new State();
    }

    @Override
    public boolean execute(
            Node.State nodeState,
            List<Link.State> linkStatesIn,
            List<Link.State> linkStatesOut,
            ProcessContext context) {
        boolean isSuccess = linkStatesIn.get(0).hasRemaining();
        linkStatesIn.get(0).consume();
        return isSuccess;
    }

    @Override
    public boolean pull(
            Link.State pullLinkState,
            Node.State nodeState,
            List<Link.State> linkStatesIn,
            List<Link.State> linkStatesOut,
            ProcessContext context) {
        return false;
    }
}
