package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.Arrays;
import java.util.List;

public class XorJoinGatewayState extends NodeState {
    public XorJoinGatewayState(XorJoinGateway gateway) {
        super(gateway);
    }

    @Override
    public XorJoinGateway getNode() {
        return (XorJoinGateway)super.getNode();
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
        LinkState linkStateOut = getLinkStateOut(context);

        for (LinkState linkStateIn : linkStatesIn) {
            if (linkStateIn.hasTokenRemaining()) {
                linkStateIn = linkStateIn.consumeToken();
                linkStateOut = linkStateOut.produceToken();
                return new StateChange(null,
                                       Arrays.asList(linkStateIn),
                                       Arrays.asList(linkStateOut));
            }
        }

        return null;
    }

    @Override
    public boolean respondsToPull() { return true; }

    private LinkState getLinkStateOut(ProcessStateContext context) {
        return context.getLinkStatesOut(this).get(0);
    }
}
