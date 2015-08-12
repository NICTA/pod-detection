package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.util.List;

public class StartEvent extends Event {
    public class State extends Node.State {
        @Override
        public StartEvent getNode() {
            return StartEvent.this;
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
        linkStatesOut.get(0).produce();
        return true;
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
