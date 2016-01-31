package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndJoinGatewayState extends NodeState {
    public AndJoinGatewayState(AndJoinGateway gateway) {
        super(gateway);
    }

    @Override
    public AndJoinGateway getNode() {
        return (AndJoinGateway)super.getNode();
    }

    @Override
    public List<LinkState> shouldPullBeforeExecuted(ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<LinkState> shouldPullBeforePulled(ProcessStateContext context) {
        List<LinkState> linkStatesIn = context.getLinkStatesIn(this);
        for (LinkState linkStateIn : linkStatesIn) {
            if (!linkStateIn.hasTokenRemaining()) {
                return linkStatesIn;
            }
        }
        return null;
    }

    @Override
    public StateChange onExecuted(ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StateChange onPulled(LinkState linkStateOutPulled,
                                ProcessStateContext context) {
        List<LinkState> linkStatesIn = context.getLinkStatesIn(this);
        for (LinkState linkStateIn : linkStatesIn) {
            if (!linkStateIn.hasTokenRemaining()) {
                return null;
            }
        }

        List<LinkState> changedLinkStatesIn = new ArrayList<>();
        for (LinkState linkStateIn : linkStatesIn) {
            changedLinkStatesIn.add(linkStateIn.consumeToken());
        }

        return new StateChange(
            null,
            changedLinkStatesIn,
            Arrays.asList(getLinkStateOut(context).produceToken()));
    }

    @Override
    public boolean respondsToPull() { return true; }

    private LinkState getLinkStateOut(ProcessStateContext context) {
        return context.getLinkStatesOut(this).get(0);
    }
}
