package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.util.List;

public class XorJoinGateway extends Gateway {
    public class State extends Node.State {
        @Override
        public XorJoinGateway getNode() {
            return XorJoinGateway.this;
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
        if (!move(nodeState, linkStatesIn, linkStatesOut, context)) {
            for (Link.State linkStateIn : linkStatesIn) {
                context.pull(linkStateIn.getLink());
            }
            return move(nodeState, linkStatesIn, linkStatesOut, context);
        }
        return true;
    }

    private boolean move(
            Node.State nodeState,
            List<Link.State> linkStatesIn,
            List<Link.State> linkStatesOut,
            ProcessContext context) {
        boolean isSuccess = false;
        for (Link.State linkStateIn : linkStatesIn) {
            if (linkStateIn.hasRemaining()) {
                linkStateIn.consume();
                linkStatesOut.get(0).produce();
                isSuccess = true;
            }
        }
        return isSuccess;
    }
}
