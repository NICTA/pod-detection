package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.Arrays;
import java.util.List;

public class XorSplitGatewayState extends NodeState {
    public XorSplitGatewayState(XorSplitGateway gateway) {
        super(gateway);
    }

    @Override
    public XorSplitGateway getNode() {
        return (XorSplitGateway)super.getNode();
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
            return new StateChange(
                null,
                Arrays.asList(linkStateIn.consumeToken()),
                Arrays.asList(linkStateOutPulled.produceToken()));
        }
        return null;
    }

    @Override
    public boolean respondsToPull() { return true; }

    private LinkState getLinkStateIn(ProcessStateContext context) {
        return context.getLinkStatesIn(this).get(0);
    }
}
