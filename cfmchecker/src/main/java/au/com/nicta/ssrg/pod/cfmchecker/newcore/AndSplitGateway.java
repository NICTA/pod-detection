package au.com.nicta.ssrg.pod.cfmchecker.newcore;

public class AndSplitGateway extends Node {
    @Override
    public AndSplitGatewayState createState() {
        return new AndSplitGatewayState(this);
    }
}
