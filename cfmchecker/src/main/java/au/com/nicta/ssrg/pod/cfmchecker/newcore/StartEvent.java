package au.com.nicta.ssrg.pod.cfmchecker.newcore;

public class StartEvent extends Node {
    @Override
    public StartEventState createState() {
        return new StartEventState(this);
    }
}
