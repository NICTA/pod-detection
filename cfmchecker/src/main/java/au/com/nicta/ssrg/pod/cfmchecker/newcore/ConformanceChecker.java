package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public class ConformanceChecker {
    public ConformanceChecker(ProcessModel rootModel) {
        this.rootModel = rootModel;
        virtualContext = new VirtualProcessContext(rootModel);
    }

    public ConformanceCheckResult checkConformance(ProcessEvent event) {
        // The process models must satisfy the following requirements:
        // 1. They must start with an activity. That is, their start event must
        //    connects directly to an activity.
        // 2. They must end with an activity. That is, their end event must
        //    connects directly to an activity.

        // The process model to check the event against should be extracted
        // from the event.
        // For now a single root model is used.
        ProcessModel eventModel = rootModel;

        List<String> piids = event.getProcessInstanceIDs();
        if (piids.size() == 0) {
            // Unable to find any process instance IDs.
            event.tag(ProcessEventTag.UNFIT);
            return new ConformanceCheckResult(event);
        }

        // Finds the path to the event activity in target model tree.
        ImmutablePair<LinkedList<ProcessModel>, Activity> pathToActivity =
            eventModel.findPathToActivity(event.getActivityName());

        if (pathToActivity == null) {
            event.tag(ProcessEventTag.UNFIT);
            return new ConformanceCheckResult(event);
        }

        // Correlates process instance IDs with process models on the model tree
        // path leading to the event activity.
        LinkedList<ImmutablePair<ProcessModel, String>> modelPiidSeq =
            matchPathWithPiids(pathToActivity.getLeft(), piids);
        if (modelPiidSeq == null) {
            event.tag(ProcessEventTag.UNFIT);
            return new ConformanceCheckResult(event);
        }

        Activity activity = pathToActivity.getRight();
        event.setActivityID(activity.getID());

        // Correlates process instance IDs with process instances.
        LinkedList<ProcessContext> contexts = new LinkedList<>();
        ProcessContext lastContext = null;
        ProcessContext currentContext = virtualContext;
        ProcessModel currentModel = null;
        String currentPiid = null;
        int depth = 0;
        for (ImmutablePair<ProcessModel, String> pair : modelPiidSeq) {
            ++depth;
            currentModel = pair.getLeft();
            currentPiid = pair.getRight();
            lastContext = currentContext;

            if (!currentContext.hasSubContext(currentModel, currentPiid)) {
                currentContext = null;
                break;
            }

            currentContext = currentContext.getSubContext(currentModel,
                                                          currentPiid);
            contexts.add(currentContext);
        }

        if (depth < modelPiidSeq.size()) {
            // Cannot find enough running process instances to start checking or
            // create new process instance.
            event.tag(ProcessEventTag.UNFIT);
            return new ConformanceCheckResult(event);
        }

        StartEvent startEvent =
            currentModel.getStartEventByLinkedNode(activity);

        if (currentContext == null) {
            // Tries to create new process instance.

            if (startEvent == null) {
                // Not starting activity. Cannot create new process instance.
                event.tag(ProcessEventTag.UNFIT);
                return new ConformanceCheckResult(event);
            }

            ProcessInstance newInstance =
                new ProcessInstance(currentPiid, currentModel);
            lastContext.addSubContext(currentModel, currentPiid, newInstance);
            contexts.add(newInstance);

            EnumSet<StateError> modelErrors =
                lastContext.executeNode(currentModel);
            EnumSet<StateError> activityErrors =
                newInstance.executeNode(activity);
            EnumSet<StateError> errors =
                mergeErrors(modelErrors, activityErrors);

            mapErrorsToTags(errors, event);
            return new ConformanceCheckResult(
                event, pathToActivity.getLeft(), contexts);
        }

        if (!currentContext.isNodeActive(activity)) {
            if (startEvent != null) {
                // Replaces the running process instance with a new one. (Restart)

                ProcessInstance newInstance =
                    new ProcessInstance(currentPiid, currentModel);
                lastContext.removeSubContext(currentModel, currentPiid);
                lastContext.addSubContext(
                    currentModel, currentPiid, newInstance);

                EnumSet<StateError> modelErrors =
                    lastContext.executeNode(currentModel);
                EnumSet<StateError> activityErrors =
                    newInstance.executeNode(activity);
                EnumSet<StateError> errors =
                    mergeErrors(modelErrors, activityErrors);

                mapErrorsToTags(errors, event);
                event.tag(ProcessEventTag.RESTART);
            } else {
                // Executes the activity in the running process instance.
                EnumSet<StateError> errors = currentContext.executeNode(activity);
                mapErrorsToTags(errors, event);
            }
        }

        // Checks the completion of the root process instance.
        // TODO: remove this check and the COMPLETED tag. Completion status
        // should be read directly from contexts.
        if (contexts.size() > 0 && contexts.getFirst().isCompleted()) {
            event.tag(ProcessEventTag.COMPLETED);
        }

        // TODO: garbage collection of completed process instances.

        return new ConformanceCheckResult(
            event, pathToActivity.getLeft(), contexts);
    }

    private LinkedList<ImmutablePair<ProcessModel, String>> matchPathWithPiids(
        List<ProcessModel> path,
        List<String> ids) {

        if (path.size() != ids.size()) {
            // Unable to match path and process instance IDs.
            return null;
        }

        LinkedList<ImmutablePair<ProcessModel, String>> modelPiidSeq =
            new LinkedList<ImmutablePair<ProcessModel, String>>();
        int idIndex = 0;
        for (ProcessModel model : path) {
            modelPiidSeq.add(new ImmutablePair<>(model, ids.get(idIndex)));
            ++idIndex;
        }

        return modelPiidSeq;
    }

    private void mapErrorsToTags(EnumSet<StateError> errors,
                                 ProcessEvent event) {
        if (errors == null) {
            return;
        }
        if (StateError.haveUnfitErrors(errors)) {
            event.tag(ProcessEventTag.UNFIT);
        }
        if (errors.contains(StateError.NUMERIC_INVARIANTS_VIOLATION)) {
            event.tag(ProcessEventTag.INVARIANT_VIOLATION);
        }
        if (errors.contains(StateError.NODE_TIME_ANOMALY)) {
            event.tag(ProcessEventTag.TIME_ANOMALY);
        }
    }

    private EnumSet<StateError> mergeErrors(EnumSet<StateError> oneSet,
                                            EnumSet<StateError> anotherSet) {
        EnumSet<StateError> errors = EnumSet.noneOf(StateError.class);
        if (oneSet != null) {
            errors.addAll(oneSet);
        }
        if (anotherSet != null) {
            errors.addAll(anotherSet);
        }
        return errors;
    }

    private VirtualProcessContext virtualContext;
    private ProcessModel rootModel;
}
