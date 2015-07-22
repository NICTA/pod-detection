package au.com.nicta.ssrg.pod.cfmchecker.core;

public interface ProcessContext {
    boolean pull(Link link);
    boolean execute(Node node);
}
