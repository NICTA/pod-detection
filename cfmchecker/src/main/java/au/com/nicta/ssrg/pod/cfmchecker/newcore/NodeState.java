package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.List;

public abstract class NodeState {
    public NodeState(Node node, int errorCount, long lastExecTime) {
        this.node = node;
        this.errorCount = errorCount;
        this.lastExecTime = lastExecTime;
    }

    public NodeState(Node node) {
        this.node = node;
        this.errorCount = 0;
        this.lastExecTime = 0;
    }

    public Node getNode() {
        return node;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public long getLastExecTime() {
        return lastExecTime;
    }

    public long newLastExecTime() {
        return System.currentTimeMillis();
    }

    /**
     * Gets incoming links which should to be pulled before being executed.
     * This is done before the execution of the node.
     * @param  context   The managed context of the process.
     * @return           A list of incoming links should to be pulled. Or null
     *                   if no links should be pulled.
     */
    public abstract List<LinkState> shouldPullBeforeExecuted(ProcessStateContext context);

    /**
     * Gets incoming links which should to be pulled before being pulled.
     * This is done before the node itself is pulled.
     * @param  context   The managed context of the process.
     * @return           A list of incoming links should to be pulled. Or null
     *                   if no links should be pulled.
     */
    public abstract List<LinkState> shouldPullBeforePulled(ProcessStateContext context);

    /**
     * Triggered to handle the internal logic of the node.
     * @param  context   The managed context of the process.
     * @return           New state objects of the changed node and its links. Or
     *                   null if no change.
     */
    public abstract StateChange onExecuted(ProcessStateContext context);

    /**
     * Triggered when pulled by other node.
     * @param  linkStatePulled The state object of the pulled outgoing link.
     * @param  context         The managed context of the process.
     * @return                 New state objects of the changed node and its
     *                         links. Or null if no change.
     */
    public abstract StateChange onPulled(LinkState linkStateOutPulled,
                                         ProcessStateContext context);

    /**
     * States whether the node responds to pulls from other nodes.
     * @return True if will respond; otherwise false.
     */
    public abstract boolean respondsToPull();

    private Node node;
    private int errorCount;
    private long lastExecTime;
}
