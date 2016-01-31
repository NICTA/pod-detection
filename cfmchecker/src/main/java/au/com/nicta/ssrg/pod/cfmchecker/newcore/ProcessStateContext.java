package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.List;

public interface ProcessStateContext {

    /**
     * Gets state objects of incoming links to the node.
     * @param  nodeState The state object of the node.
     * @return           The state objects of incoming links. Or null if no
     *                   incoming links.
     */
    List<LinkState> getLinkStatesIn(NodeState nodeState);

    /**
     * Gets state objects of outgoing links from the node.
     * @param  nodeState The state object of the node.
     * @return           The state objects of outgoing links. Or null if no
     *                   outgoing links.
     */
    List<LinkState> getLinkStatesOut(NodeState nodeState);

    /**
     * Gets the number of process contexts associated with the process model.
     * @param  modelState The state object of the process model.
     * @return            The number of process contexts associated.
     */
    int getSubContextCount(ProcessModelState modelState);

    /**
     * Checks the completion of all process contexts of the process model.
     * @param  modelState The state object of the process model.
     * @return            True if all completed; otherwise false.
     */
    boolean haveAllSubContextsCompleted(ProcessModelState state);
}
