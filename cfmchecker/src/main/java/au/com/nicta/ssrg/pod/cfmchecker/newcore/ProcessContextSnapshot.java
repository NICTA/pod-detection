package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.HashMap;
import java.util.Map;

public class ProcessContextSnapshot {
    public ProcessContextSnapshot(ProcessContext context) {
        id = context.getID();
        nodeStateMap = new HashMap<>(context.getNodeStateMapView());
    }

    public String getID() {
        return id;
    }

    public Map<Node, NodeState> getNodeStateMap() {
        return nodeStateMap;
    }

    private String id;
    private Map<Node, NodeState> nodeStateMap;
}
