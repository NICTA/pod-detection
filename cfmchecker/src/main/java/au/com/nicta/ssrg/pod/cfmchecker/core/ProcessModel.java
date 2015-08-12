package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import au.com.nicta.ssrg.pod.cfmchecker.core.ConformanceException.ErrorCode;

public class ProcessModel extends Node {
    public static class NumericInvariants {
        @JsonCreator
        public NumericInvariants(
                @JsonProperty("minInstanceCount") int minInstanceCount,
                @JsonProperty("maxInstanceCount") int maxInstanceCount) {
            this.minInstanceCount = minInstanceCount;
            this.maxInstanceCount = maxInstanceCount;
        }

        public int minInstanceCount() {
            return minInstanceCount;
        }

        public int maxInstanceCount() {
            return maxInstanceCount;
        }

        private int minInstanceCount;
        private int maxInstanceCount;
    }

    public class State extends Node.State {
        public State() {
            this.idInstanceMap = new HashMap<>();
            this.tokenKept = 0;
        }

        @Override
        public ProcessModel getNode() {
            return ProcessModel.this;
        }

        public ProcessInstance createInstance(String id) {
            ProcessInstance instance =
                new ProcessInstance(id, ProcessModel.this);
            idInstanceMap.put(id, instance);

            if (idInstanceMap.size() >
                    ProcessModel.this.invariants.maxInstanceCount()) {
                ConformanceException ex =
                    new ConformanceException(ErrorCode.INVARIANT_VIOLATION);
                ex.instance(instance);
                ex.nodeState(this);
                throw ex;
            }

            return instance;
        }

        public ProcessInstance removeInstance(String id) {
            return idInstanceMap.remove(id);
        }

        public ProcessInstance getInstance(String id) {
            return idInstanceMap.get(id);
        }

        public boolean hasInstances() {
            return idInstanceMap.size() > 0;
        }

        public void keepToken() {
            ++tokenKept;
        }

        public boolean releaseToken() {
            if (tokenKept > 0) {
                --tokenKept;
                return true;
            }
            return false;
        }

        public boolean checkInvariants() {
            int size = idInstanceMap.size();
            if (size < ProcessModel.this.invariants.minInstanceCount()) {
                return false;
            }
            if (size > ProcessModel.this.invariants.maxInstanceCount()) {
                return false;
            }
            for (ProcessInstance instance : idInstanceMap.values()) {
                if (!instance.isCompleted()) {
                    return false;
                }
            }
            return true;
        }

        private Map<String, ProcessInstance> idInstanceMap;
        private int tokenKept;
    }

    @Override
    public State newState() {
        return new State();
    }

    @JsonCreator
    public ProcessModel(
            @JsonProperty("name") String name,
            @JsonProperty("nodes") Collection<Node> nodes,
            @JsonProperty("links") Collection<Link> links,
            @JsonProperty("invariants") NumericInvariants invariants) {
        this.name = name;
        this.nodes = new ArrayList<>(nodes);
        this.links = new ArrayList<>(links);
        if (invariants != null) {
            this.invariants = invariants;
        } else {
            this.invariants = new NumericInvariants(0, Integer.MAX_VALUE);
        }

        this.subModels = new ArrayList<>();
        this.endEvents = new HashSet<>();
        this.nameActivityMap = new HashMap<>();
        for (Node node : this.nodes) {
            if (node instanceof ProcessModel) {
                this.subModels.add((ProcessModel)node);
            } else if (node instanceof Activity) {
                Activity activity = (Activity)node;
                this.nameActivityMap.put(activity.getName(), activity);
            } else if (node instanceof StartEvent) {
                this.startEvent = (StartEvent)node;
            } else if (node instanceof EndEvent) {
                this.endEvents.add((EndEvent)node);
            }
        }

        this.nodeLinksInMap = new HashMap<>(this.nodes.size());
        this.nodeLinksOutMap = new HashMap<>(this.nodes.size());
        for (Link link : this.links) {
            Node source = link.getSource();
            Node target = link.getTarget();

            List<Link> targetLinksIn = nodeLinksInMap.get(target);
            if (targetLinksIn == null) {
                targetLinksIn = new ArrayList<>();
                nodeLinksInMap.put(target, targetLinksIn);
            }
            targetLinksIn.add(link);

            List<Link> sourceLinksOut = nodeLinksOutMap.get(source);
            if (sourceLinksOut == null) {
                sourceLinksOut = new ArrayList<>();
                nodeLinksOutMap.put(source, sourceLinksOut);
            }
            sourceLinksOut.add(link);
        }
    }

    public String getName() {
        return name;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public Activity getActivity(String name) {
        return nameActivityMap.get(name);
    }

    public List<Activity> getActivities() {
        return new ArrayList<>(nameActivityMap.values());
    }

    public List<Link> getLinksOut(Node node) {
        return nodeLinksOutMap.get(node);
    }

    public List<Link> getLinksIn(Node node) {
        return nodeLinksInMap.get(node);
    }

    public StartEvent getStartEventByLinkedNode(Node node) {
        for (Link link : nodeLinksOutMap.get(startEvent)) {
            if (link.getTarget() == node) {
                return startEvent;
            }
        }
        return null;
    }

    public EndEvent getEndEventByLinkedNode(Node node) {
        for (Link link : nodeLinksOutMap.get(node)) {
            if (endEvents.contains(link.getTarget())) {
                return (EndEvent)link.getTarget();
            }
        }
        return null;
    }

    public List<Node> findNodePathToActivity(String activityName) {
        LinkedList<Node> nodePath = new LinkedList<>();
        if (traceActivityInModelTree(activityName, this, nodePath)) {
            return nodePath;
        }
        return null;
    }

    @Override
    public boolean pull(
            Link.State pullLinkState,
            Node.State nodeState,
            List<Link.State> linkStatesIn,
            List<Link.State> linkStatesOut,
            ProcessContext context) {
        ProcessModel.State modelState = (ProcessModel.State)nodeState;
        if (!modelState.checkInvariants()) {
            ConformanceException ex =
                new ConformanceException(ErrorCode.INVARIANT_VIOLATION);
            ex.nodeState(modelState);
            throw ex;
        }
        if (modelState.releaseToken()) {
            pullLinkState.produce();
        }
        return false;
    }

    @Override
    public boolean execute(
            Node.State nodeState,
            List<Link.State> linkStatesIn,
            List<Link.State> linkStatesOut,
            ProcessContext context) {
        Link.State linkStateIn = linkStatesIn.get(0);
        boolean isSuccess = linkStateIn.hasRemaining();
        if (!isSuccess) {
            context.pull(linkStateIn.getLink());
            isSuccess = linkStateIn.hasRemaining();
        }
        linkStatesIn.get(0).consume();
        ((ProcessModel.State)nodeState).keepToken();
        return isSuccess;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator;
        try {
            generator = factory.createGenerator(writer);
            generator.writeStartObject();
            generator.writeFieldName("super");
            generator.writeRawValue(super.toString());
            generator.writeArrayFieldStart("nodes");
            for (Node node : nodes) {
                generator.writeRawValue(node.toString());
            }
            generator.writeEndArray();
            generator.writeArrayFieldStart("links");
            for (Link link : links) {
                generator.writeRawValue(link.toString());
            }
            generator.writeEndArray();
            generator.writeEndObject();
            generator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }

    private boolean traceActivityInModelTree(
            String activityName,
            ProcessModel currentModel,
            LinkedList<Node> nodePath) {
        if (currentModel.nameActivityMap.containsKey(activityName)) {
            nodePath.add(getActivity(activityName));
            nodePath.addFirst(currentModel);
            return true;
        }
        for (ProcessModel subModel : currentModel.subModels) {
            if (traceActivityInModelTree(activityName, subModel, nodePath)) {
                nodePath.addFirst(subModel);
                return true;
            }
        }
        return false;
    }

    private String name;
    private List<Node> nodes;
    private List<Link> links;
    private NumericInvariants invariants;

    private Collection<ProcessModel> subModels;
    private Map<String, Activity> nameActivityMap;
    private StartEvent startEvent;
    private Set<EndEvent> endEvents;
    private Map<Node, List<Link>> nodeLinksOutMap;
    private Map<Node, List<Link>> nodeLinksInMap;
}
