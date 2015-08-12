package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ProcessInstance implements ProcessContext {
    public ProcessInstance(
        String id,
        ProcessModel model) {

        this.id = id;
        this.model = model;
        this.activeNode = null;
        this.isCompleted = false;

        this.nodeStateMap = new HashMap<>();
        for (Node node : this.model.getNodes()) {
            this.nodeStateMap.put(node, node.newState());
        }

        this.linkStateMap = new HashMap<>();
        for (Link link : this.model.getLinks()) {
            this.linkStateMap.put(link, link.newState());
        }
    }

    public String getID() {
        return id;
    }

    public Node.State getNodeState(Node node) {
        return nodeStateMap.get(node);
    }

    public boolean start(StartEvent startEvent, Node startNode) {
        return execute(startEvent) & execute(startNode);
    }

    public boolean isNodeActive(Node node) {
        return activeNode == node;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public boolean pull(Link link) {
        Node node = link.getSource();
        Node.State nodeState = nodeStateMap.get(node);
        nodeState.updateLastPullTime();

        List<Link> linksIn = model.getLinksIn(node);
        List<Link> linksOut = model.getLinksOut(node);

        List<Link.State> linkStatesIn = getLinkStates(linksIn);
        List<Link.State> linkStatesOut = getLinkStates(linksOut);
        Link.State linkState = linkStateMap.get(link);

        return node.pull(
            linkState, nodeState, linkStatesIn, linkStatesOut, this);
    }

    @Override
    public boolean execute(Node node) {
        Node.State nodeState = nodeStateMap.get(node);
        nodeState.updateLastExecutionTime();

        List<Link> linksIn = model.getLinksIn(node);
        List<Link> linksOut = model.getLinksOut(node);

        List<Link.State> linkStatesIn = getLinkStates(linksIn);
        List<Link.State> linkStatesOut = getLinkStates(linksOut);

        if (node.execute(nodeState, linkStatesIn, linkStatesOut, this)) {
            updateActiveNode(node, nodeState);

            if (!(node instanceof EndEvent)) {
                EndEvent endEvent = model.getEndEventByLinkedNode(node);
                if (endEvent != null) {
                    if (execute(endEvent)) {
                        isCompleted = true;
                        return true;
                    }
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator;
        try {
            generator = factory.createGenerator(writer);
            generator.writeStartObject();
            generator.writeStringField("id", id);
            generator.writeBooleanField("isCompleted", isCompleted);
            generator.writeFieldName("activeNode");
            generator.writeRawValue(
                activeNode == null ?
                    null :
                    activeNode.toString());
            generator.writeArrayFieldStart("nodeStates");
            for (Node.State nodeState : nodeStateMap.values()) {
                generator.writeRawValue(nodeState.toString());
            }
            generator.writeEndArray();
            generator.writeArrayFieldStart("linkStates");
            for (Link.State linkState : linkStateMap.values()) {
                generator.writeRawValue(linkState.toString());
            }
            generator.writeEndArray();
            generator.writeEndObject();
            generator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }

    private void updateActiveNode(Node node, Node.State nodeState) {
        if (!(node instanceof Activity) && !(node instanceof ProcessModel)) {
            return;
        }

        Node lastActiveNode = activeNode;
        activeNode = node;

        if (activeNode instanceof Activity) {
            ((Activity.State)nodeState).start();
        }

        if (lastActiveNode instanceof Activity) {
            try {
                ((Activity.State)nodeStateMap.get(lastActiveNode)).end();
            } catch (ConformanceException ex) {
                ex.nodeState(nodeState);
                throw ex;
            }
        }
    }

    private List<Link.State> getLinkStates(Collection<Link> links) {
        if (links == null) { return null; }
        List<Link.State> linkStates = new ArrayList<>(links.size());
        for (Link link : links) {
            linkStates.add(linkStateMap.get(link));
        }
        return linkStates;
    }

    private String id;
    private ProcessModel model;
    private Node activeNode;
    private Map<Link, Link.State> linkStateMap;
    private Map<Node, Node.State> nodeStateMap;
    private boolean isCompleted;
}
