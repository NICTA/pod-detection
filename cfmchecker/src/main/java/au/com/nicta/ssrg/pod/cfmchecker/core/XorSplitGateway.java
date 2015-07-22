package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.util.List;

public class XorSplitGateway extends Gateway {
    public class State extends Node.State {
        @Override
        public XorSplitGateway getNode() {
            return XorSplitGateway.this;
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
        }
        if (linkStateIn.hasRemaining()) {
            linkStateIn.consume();
            pullLinkState.produce();
            return true;
        }
        return false;
    }
}
