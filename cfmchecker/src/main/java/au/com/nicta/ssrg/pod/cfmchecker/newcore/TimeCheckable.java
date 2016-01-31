package au.com.nicta.ssrg.pod.cfmchecker.newcore;

/**
 * Node types with this interface can be timed and detected for time anomaly.
 * The bahaviors of this interface should be delegated to a time checker
 * configured with the implementing node type.
 * TODO: change may apply to move the time tracking to node state objects and use
 * a timer for periodic verification.
 */
public interface TimeCheckable {
    StateError checkTimeSpan(long span);
}
