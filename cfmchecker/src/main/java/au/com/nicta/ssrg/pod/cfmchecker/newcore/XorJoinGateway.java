package au.com.nicta.ssrg.pod.cfmchecker.newcore;

public class XorJoinGateway extends Node {
    @Override
    public XorJoinGatewayState createState() {
        return new XorJoinGatewayState(this);
    }
}
