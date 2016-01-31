package au.com.nicta.ssrg.pod.cfmchecker.newcore;

public class AndJoinGateway extends Node {
    @Override
    public AndJoinGatewayState createState() {
        return new AndJoinGatewayState(this);
    }
}
