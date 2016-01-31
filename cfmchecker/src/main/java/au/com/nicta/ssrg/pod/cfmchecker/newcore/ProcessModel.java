package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

public class ProcessModel extends Node implements TimeCheckable {
    @JsonCreator
    public ProcessModel(
        @JsonProperty("name") String name,
        @JsonProperty("nodes") Collection<Node> nodes,
        @JsonProperty("links") Collection<Link> links,
        @JsonProperty("invariants") NumericInvariants numericInvariants,
        @JsonProperty("tc") TimeChecker timeChecker) {

        this.name = name;
        this.nodes = nodes == null ? new ArrayList<>() : new ArrayList<>(nodes);
        this.links = links == null ? new ArrayList<>() : new ArrayList<>(links);
        this.timeChecker =
            timeChecker == null ? new TimeChecker() : timeChecker;
        this.numericInvariants =
            numericInvariants == null ? new NumericInvariants() : numericInvariants;

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

            if (target != null) {
                List<Link> targetLinksIn = nodeLinksInMap.get(target);
                if (targetLinksIn == null) {
                    targetLinksIn = new ArrayList<>();
                    nodeLinksInMap.put(target, targetLinksIn);
                }
                targetLinksIn.add(link);
            }

            if (source != null) {
                List<Link> sourceLinksOut = nodeLinksOutMap.get(source);
                if (sourceLinksOut == null) {
                    sourceLinksOut = new ArrayList<>();
                    nodeLinksOutMap.put(source, sourceLinksOut);
                }
                sourceLinksOut.add(link);
            }
        }
    }

    @Override
    public ProcessModelState createState() {
        return new ProcessModelState(this);
    }

    @Override
    public StateError checkTimeSpan(long span) {
        return timeChecker.checkTimeSpan(span);
    }

    public String getName() {
        return name;
    }

    public List<Node> getNodesView() {
        return Collections.unmodifiableList(nodes);
    }

    public List<Link> getLinksView() {
        return Collections.unmodifiableList(links);
    }

    public NumericInvariants getNumericInvariants() {
        return numericInvariants;
    }

    public Activity getActivity(String name) {
        return nameActivityMap.get(name);
    }

    public Collection<Activity> getActivitiesView() {
        return Collections.unmodifiableCollection(nameActivityMap.values());
    }

    public Collection<ProcessModel> getSubModelsView() {
        return Collections.unmodifiableCollection(subModels);
    }

    public List<Link> getLinksOutView(Node node) {
        return Collections.unmodifiableList(nodeLinksOutMap.get(node));
    }

    public List<Link> getLinksInView(Node node) {
        return Collections.unmodifiableList(nodeLinksInMap.get(node));
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
            Node target = link.getTarget();
            if (target instanceof EndEvent) {
                EndEvent targetEndEvent = (EndEvent)target;
                if (endEvents.contains(targetEndEvent)) {
                    return targetEndEvent;
                }
            }
        }
        return null;
    }

    public ImmutablePair<LinkedList<ProcessModel>, Activity> findPathToActivity(
        String activityName) {

        LinkedList<ProcessModel> path = new LinkedList<>();
        if (traceActivityInModelTree(activityName, this, path)) {
            return new ImmutablePair<>(
                path,
                path.getLast().nameActivityMap.get(activityName));
        }
        return null;
    }

    private boolean traceActivityInModelTree(
            String activityName,
            ProcessModel currentModel,
            LinkedList<ProcessModel> path) {
        if (currentModel.nameActivityMap.containsKey(activityName)) {
            path.addFirst(currentModel);
            return true;
        }
        for (ProcessModel subModel : currentModel.subModels) {
            if (traceActivityInModelTree(activityName, subModel, path)) {
                path.addFirst(currentModel);
                return true;
            }
        }
        return false;
    }

    private String name;
    private List<Node> nodes;
    private List<Link> links;
    private TimeChecker timeChecker;
    private NumericInvariants numericInvariants;
    private Collection<ProcessModel> subModels;
    private Map<String, Activity> nameActivityMap;
    private StartEvent startEvent;
    private Set<EndEvent> endEvents;
    private Map<Node, List<Link>> nodeLinksOutMap;
    private Map<Node, List<Link>> nodeLinksInMap;
}
