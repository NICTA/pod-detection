package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.util.List;

public class AndJoinGateway extends Gateway {
    public class State extends Node.State {
        @Override
        public AndJoinGateway getNode() {
            return AndJoinGateway.this;
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
        for (Link.State linkStateIn : linkStatesIn) {
            if (!linkStateIn.hasRemaining()) {
                context.pull(linkStateIn.getLink());
                if (!linkStateIn.hasRemaining()) {
                    return false;
                }
            }
        }
        for (Link.State linkStateIn: linkStatesIn) {
            linkStateIn.consume();
        }
        linkStatesOut.get(0).produce();
        return true;
    }
}
