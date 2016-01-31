package au.com.nicta.ssrg.pod.cfmchecker.newcore;

public class XorSplitGateway extends Node {
    @Override
    public XorSplitGatewayState createState() {
        return new XorSplitGatewayState(this);
    }
}
