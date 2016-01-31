package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.*;

public class ProcessInstance implements ProcessContext, ProcessStateContext {
    public ProcessInstance(String id,
                           ProcessModel model) {
        this.id = id;
        this.model = model;
        this.activeNode = null;
        this.isCompleted = false;

        nodeStateMap = new HashMap<>();
        for (Node node : this.model.getNodesView()) {
            nodeStateMap.put(node, node.createState());
        }

        linkStateMap = new HashMap<>();
        for (Link link : this.model.getLinksView()) {
            linkStateMap.put(link, link.createState());
        }

        subContexts = new HashMap<>();
        for (ProcessModel subModel : this.model.getSubModelsView()) {
            subContexts.put(subModel, new HashMap<>());
        }

        nodeLastActiveTimeMap = new HashMap<>();
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean hasSubContext(ProcessModel model, String contextID) {
        return subContexts.containsKey(model) &&
            subContexts.get(model).containsKey(contextID);
    }

    @Override
    public ProcessInstance getSubContext(ProcessModel model, String contextID) {
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
        subContexts.get(model).put(contextID, (ProcessInstance)context);
    }

    @Override
    public void removeSubContext(ProcessModel model, String contextID) {
        subContexts.get(model).remove(contextID);
    }

    @Override
    public EnumSet<StateError> executeNode(Node node) {
        EnumSet<StateError> errors = EnumSet.noneOf(StateError.class);

        EnumSet<StateError> execErrors = executeNode(nodeStateMap.get(node));
        if (execErrors != null) {
            errors.addAll(execErrors);
        }

        // Marks the node as active without considering its state change. That
        // is, simply tracks the last attempted node.
        // Could consider changing the behavior to only when node state changes
        // will the node be marked active.
        Node lastActiveNode = activeNode;
        activeNode = node;

        // For time anomaly checking.
        if (lastActiveNode != activeNode) {

            if (activeNode instanceof TimeCheckable) {
                nodeLastActiveTimeMap.put(activeNode,
                                          System.currentTimeMillis());
            }

            if (lastActiveNode instanceof TimeCheckable) {
                Long lastActiveTime = nodeLastActiveTimeMap.get(lastActiveNode);
                if (lastActiveTime != null) {
                    Long activeSpan =
                        System.currentTimeMillis() - lastActiveTime;
                    TimeCheckable timeCheckable = (TimeCheckable)lastActiveNode;
                    StateError timeError =
                        timeCheckable.checkTimeSpan(activeSpan);
                    if (timeError != null) {
                        errors.add(timeError);
                    }
                }
            }
        }

        // If the node is connected to the end event, executes the end event
        // and marks the process instance as completed.
        EndEvent endEvent = model.getEndEventByLinkedNode(node);
        if (endEvent != null) {
            EnumSet<StateError> endEventErrors =
                executeNode(nodeStateMap.get(endEvent));
            isCompleted = true;
            if (endEventErrors != null) {
                errors.addAll(endEventErrors);
            }
        }

        if (errors.isEmpty()) {
            return null;
        }

        return errors;
    }

    @Override
    public boolean isNodeActive(Node node) {
        return activeNode == node;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public Map<Node, NodeState> getNodeStateMapView() {
        return Collections.unmodifiableMap(nodeStateMap);
    }

    @Override
    public List<LinkState> getLinkStatesIn(NodeState nodeState) {
        Iterable<Link> linksIn = model.getLinksInView(nodeState.getNode());
        List<LinkState> linkStatesIn = new ArrayList<>();
        for (Link linkIn : linksIn) {
            linkStatesIn.add(linkStateMap.get(linkIn));
        }
        if (linkStatesIn.size() == 0) {
            return null;
        }
        return linkStatesIn;
    }

    @Override
    public List<LinkState> getLinkStatesOut(NodeState nodeState) {
        Iterable<Link> linksOut = model.getLinksOutView(nodeState.getNode());
        List<LinkState> linkStatesOut = new ArrayList<>();
        for (Link linkOut: linksOut) {
            linkStatesOut.add(linkStateMap.get(linkOut));
        }
        if (linkStatesOut.size() == 0) {
            return null;
        }
        return linkStatesOut;
    }

    @Override
    public int getSubContextCount(ProcessModelState modelState) {
        return subContexts.get(modelState.getNode()).size();
    }

    @Override
    public boolean haveAllSubContextsCompleted(ProcessModelState modelState) {
        Map<String, ProcessInstance> modelContexts =
            subContexts.get(modelState.getNode());
        for (ProcessInstance modelContext : modelContexts.values()) {
            if (!modelContext.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    private EnumSet<StateError> executeNode(NodeState nodeState) {
        // Before executing the node, checks whether its incoming links need
        // to be pulled.
        List<LinkState> linkStatesToPull =
            nodeState.shouldPullBeforeExecuted(this);
        EnumSet<StateError> errors = EnumSet.noneOf(StateError.class);
        if (linkStatesToPull != null) {
            for (LinkState linkStateToPull : linkStatesToPull) {
                // Pulls each of the node's incoming links, merging all errors.
                EnumSet<StateError> pullErrors = pullLink(linkStateToPull);
                if (pullErrors != null) {
                    errors.addAll(pullErrors);
                }
            }
        }

        // Triggers the onExecuted action on the node. Saves the result and
        // returns the errors if any.
        StateChange stateChange = nodeState.onExecuted(this);
        if (stateChange != null) {
            saveStateChange(stateChange);
            if (stateChange.hasErrors()) {
                errors.addAll(stateChange.getErrors());
            }
        }

        if (errors.size() == 0) {
            return null;
        }

        return errors;
    }

    private EnumSet<StateError> pullLink(LinkState linkStateOutPulled) {
        // From the source of the outgoing link finds the node to be pulled.
        Node nodeToPull = linkStateOutPulled.getLink().getSource();
        NodeState nodeStateToPull = nodeStateMap.get(nodeToPull);

        if (nodeStateToPull.respondsToPull()) {
            // Before pulling the node, checks whether its incoming links need
            // to be pulled.
            List<LinkState> furtherLinkStatesToPull =
                nodeStateToPull.shouldPullBeforePulled(this);
            if (furtherLinkStatesToPull != null) {
                for (LinkState furtherLinkStateToPull : furtherLinkStatesToPull) {
                    // Pulls each of the node's incoming links recursively.
                    // Stops and returns the first errors if any.
                    EnumSet<StateError> errors = pullLink(furtherLinkStateToPull);
                    if (errors != null) {
                        return errors;
                    }
                }
            }

            // Triggers the onPulled action on the node itself. Saves the result
            // and returns the errors if any.
            StateChange stateChange =
                nodeStateToPull.onPulled(linkStateOutPulled, this);
            if (stateChange != null) {
                saveStateChange(stateChange);
                if (stateChange.hasErrors()) {
                    return stateChange.getErrors();
                }
            }
        }

        return null;
    }

    private void saveStateChange(StateChange stateChange) {
        if (stateChange.hasNodeStateChanged()) {
            NodeState newNodeState = stateChange.getNodeState();
            nodeStateMap.put(newNodeState.getNode(), newNodeState);
        }
        if (stateChange.hasLinkStatesInChanged()) {
            saveLinkStates(stateChange.getLinkStatesIn());
        }
        if (stateChange.hasLinkStatesOutChanged()) {
            saveLinkStates(stateChange.getLinkStatesOut());
        }
    }

    private void saveLinkStates(List<LinkState> linkStates) {
        for (LinkState linkState : linkStates) {
            linkStateMap.put(linkState.getLink(), linkState);
        }
    }

    private String id;
    private ProcessModel model;
    private Node activeNode;
    private boolean isCompleted;
    private Map<Link, LinkState> linkStateMap;
    private Map<Node, NodeState> nodeStateMap;
    private Map<ProcessModel, Map<String, ProcessInstance>> subContexts;
    private Map<Node, Long> nodeLastActiveTimeMap;
}
