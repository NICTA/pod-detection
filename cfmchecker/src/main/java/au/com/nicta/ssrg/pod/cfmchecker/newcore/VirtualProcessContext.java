package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class VirtualProcessContext implements ProcessContext {
    public VirtualProcessContext(ProcessModel... models) {
        subContexts = new HashMap<>();
        for (int i = 0; i < models.length; ++i) {
            subContexts.put(models[i], new HashMap<>());
        }
    }

    @Override
    public String getID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSubContext(ProcessModel model, String contextID) {
        return subContexts.containsKey(model) &&
            subContexts.get(model).containsKey(contextID);
    }

    @Override
    public ProcessContext getSubContext(ProcessModel model, String contextID) {
        if (hasSubContext(model, contextID)) {
            return subContexts.get(model).get(contextID);
        }
        return null;
    }

    @Override
    public void addSubContext(ProcessModel model,
                              String contextID,
                              ProcessContext context) {
        if (!subContexts.containsKey(model)) {
            subContexts.put(model, new HashMap<>());
        }
        subContexts.get(model).put(contextID, context);
    }

    @Override
    public void removeSubContext(ProcessModel model, String contextID) {
        subContexts.get(model).remove(contextID);
    }

    @Override
    public EnumSet<StateError> executeNode(Node node) {
        return null;
    }

    @Override
    public boolean isNodeActive(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCompleted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Node, NodeState> getNodeStateMapView() {
        throw new UnsupportedOperationException();
    }

    private Map<ProcessModel, Map<String, ProcessContext>> subContexts;
}
