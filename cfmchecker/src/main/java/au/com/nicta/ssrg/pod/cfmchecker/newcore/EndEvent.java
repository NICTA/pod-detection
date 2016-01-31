package au.com.nicta.ssrg.pod.cfmchecker.newcore;

public class EndEvent extends Node {
    @Override
    public EndEventState createState() {
        return new EndEventState(this);
    }
}
