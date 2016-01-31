package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndSplitGatewayState extends NodeState {
    public AndSplitGatewayState(AndSplitGateway gateway) {
        super(gateway);
    }

    @Override
    public AndSplitGateway getNode() {
        return (AndSplitGateway)super.getNode();
    }

    @Override
    public List<LinkState> shouldPullBeforeExecuted(ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<LinkState> shouldPullBeforePulled(ProcessStateContext context) {
        LinkState linkStateIn = getLinkStateIn(context);
        if (linkStateIn.hasTokenRemaining()) {
            return null;
        }
        return Arrays.asList(linkStateIn);
    }

    @Override
    public StateChange onExecuted(ProcessStateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StateChange onPulled(LinkState linkStateOutPulled,
                                ProcessStateContext context) {
        LinkState linkStateIn = getLinkStateIn(context);

        if (linkStateIn.hasTokenRemaining()) {
            List<LinkState> linkStatesOut = context.getLinkStatesOut(this);
            List<LinkState> changedLinkStatesOut = new ArrayList<>();

            linkStateIn = linkStateIn.consumeToken();
            for (LinkState linkStateOut : linkStatesOut) {
                changedLinkStatesOut.add(linkStateOut.produceToken());
            }

            return new StateChange(null,
                                   Arrays.asList(linkStateIn),
                                   changedLinkStatesOut);
        }

        return null;
    }

    @Override
    public boolean respondsToPull() { return true; }

    private LinkState getLinkStateIn(ProcessStateContext context) {
        return context.getLinkStatesIn(this).get(0);
    }
}
