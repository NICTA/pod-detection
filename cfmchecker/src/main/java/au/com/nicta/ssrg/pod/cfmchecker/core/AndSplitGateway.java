package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.util.List;

public class AndSplitGateway extends Gateway {
    public class State extends Node.State {
        @Override
        public AndSplitGateway getNode() {
            return AndSplitGateway.this;
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
        return false;
    }

    @Override
    public boolean pull(
            Link.State pullLinkState,
            Node.State nodeState,
            List<Link.State> linkStatesIn,
            List<Link.State> linkStatesOut,
            ProcessContext context) {
        Link.State linkStateIn = linkStatesIn.get(0);
        if (!linkStateIn.hasRemaining()) {
            context.pull(linkStateIn.getLink());
            if (!linkStateIn.hasRemaining()) {
                return false;
            }
        }
        linkStateIn.consume();
        for (Link.State linkStateOut : linkStatesOut) {
            linkStateOut.produce();
        }
        return true;
    }
}
