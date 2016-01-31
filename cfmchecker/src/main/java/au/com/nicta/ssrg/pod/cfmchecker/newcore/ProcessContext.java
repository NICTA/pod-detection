package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.EnumSet;
import java.util.Map;

public interface ProcessContext {

    String getID();

    boolean hasSubContext(ProcessModel model, String contextID);

    ProcessContext getSubContext(ProcessModel model, String contextID);

    void addSubContext(ProcessModel model,
                       String contextID,
                       ProcessContext context);

    void removeSubContext(ProcessModel model, String contextID);

    EnumSet<StateError> executeNode(Node node);

    boolean isNodeActive(Node node);

    boolean isCompleted();

    Map<Node, NodeState> getNodeStateMapView();
}
