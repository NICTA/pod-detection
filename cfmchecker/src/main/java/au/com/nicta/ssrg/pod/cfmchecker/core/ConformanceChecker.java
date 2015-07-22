package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import au.com.nicta.ssrg.pod.cfmchecker.core.ConformanceException.ErrorCode;

public class ConformanceChecker {
    public ConformanceChecker(ProcessModel rootModel) {
        this.rootModel = rootModel;
        this.rootModelState = rootModel.newState();
    }

    public void checkConformance(ProcessLogEvent event) {
        event.setModelState(rootModelState);

        List<Node> nodePathToActivity =
            rootModel.findNodePathToActivity(event.getActivityName());

        if (nodePathToActivity == null) {
            markProcessEventUnfit(event);
            return;
        }

        List<String> processInstanceIDs = event.getProcessIDs();

        if (!validateNodePathAndProcessInstanceIDs(
                nodePathToActivity,
                processInstanceIDs,
                event)) {
            markProcessEventUnfit(event);
            return;
        }

        Node activity =
            nodePathToActivity.get(nodePathToActivity.size() - 1);
        event.setActivityID(activity.getID());

        ProcessInstance checkedProcessInstance =
            checkProcessInstanceConformanceOnNodePath(
                nodePathToActivity,
                processInstanceIDs,
                event);

        if (checkedProcessInstance == null) {
            markProcessEventUnfit(event);
            return;
        }

        try {
            if (!checkedProcessInstance.isNodeActive(activity) &&
                    !checkedProcessInstance.execute(activity)) {
                markProcessEventUnfit(event);
            }
        } catch (ConformanceException ex) {
            if (ex.code() == ErrorCode.INVARIANT_VIOLATION) {
                event.addTag(ProcessEventTag.INVARIANT_VIOLATION);
            } else if (ex.code() == ErrorCode.TIME_ANOMALY) {
                event.addTag(ProcessEventTag.TIME_ANOMALY);
            }
            markProcessEventUnfit(event);
        }

        Node.State activityNodeState =
            checkedProcessInstance.getNodeState(activity);
        event.setActivityStartTime(
            activityNodeState.getFirstExecutionTime());
        event.setActivityLastExecutionTime(
            activityNodeState.getLastExecutionTime());
    }

    public ProcessModel getRootModel() {
        return rootModel;
    }

    public ProcessModel.State getRootModelState() {
        return rootModelState;
    }

    private void markProcessEventUnfit(ProcessLogEvent event) {
        event.addTag(ProcessEventTag.UNFIT);
    }

    private ProcessInstance checkProcessInstanceConformanceOnNodePath(
            List<Node> nodePathToActivity,
            List<String> processInstanceIDs,
            ProcessLogEvent event) {
        ProcessInstance checkedProcessInstance = null;
        ProcessModel.State modelStateToCheck = rootModelState;
        for (int i = 0; i < nodePathToActivity.size() - 1; ++i) {
            ProcessModel modelToCheck = (ProcessModel)nodePathToActivity.get(i);
            if (checkedProcessInstance != null) {
                modelStateToCheck = (ProcessModel.State)
                    checkedProcessInstance.getNodeState(modelToCheck);
            }
            checkedProcessInstance = checkProcessInstanceConformance(
                checkedProcessInstance,
                modelToCheck,
                modelStateToCheck,
                processInstanceIDs.get(i),
                nodePathToActivity.get(i + 1),
                event);
            if (checkedProcessInstance == null) {
                return null;
            }
        }
        return checkedProcessInstance;
    }

    private ProcessInstance checkProcessInstanceConformance(
            ProcessInstance parentProcessInstance,
            ProcessModel model,
            ProcessModel.State modelState,
            String processInstanceID,
            Node nodeInModelToCheck,
            ProcessLogEvent event) {
        ProcessInstance checkedProcessInstance =
            modelState.getInstance(processInstanceID);
        StartEvent startEvent =
            model.getStartEventByLinkedNode(nodeInModelToCheck);

        if (checkedProcessInstance != null) {
            if (startEvent != null &&
                    !checkedProcessInstance.isNodeActive(nodeInModelToCheck)) {
                modelState.removeInstance(processInstanceID);
                ProcessInstance instanceToStart = null;
                try {
                    instanceToStart =
                        modelState.createInstance(processInstanceID);
                } catch (ConformanceException ex) {
                    instanceToStart = ex.instance();
                    event.addTag(ProcessEventTag.INVARIANT_VIOLATION);
                }
                instanceToStart.start(startEvent, nodeInModelToCheck);
                event.addTag(ProcessEventTag.RESTART);
                return instanceToStart;
            }

            if (checkedProcessInstance.isCompleted() &&
                    !checkedProcessInstance.isNodeActive(nodeInModelToCheck)) {
                return null;
            }
            return checkedProcessInstance;
        }

        if (startEvent != null) {
            if (parentProcessInstance != null) {
                if (!modelState.hasInstances() &&
                        !parentProcessInstance.execute(model)) {
                    return null;
                }
            }
            ProcessInstance instanceToStart = null;
            try {
                instanceToStart =
                    modelState.createInstance(processInstanceID);
            } catch (ConformanceException ex) {
                instanceToStart = ex.instance();
                event.addTag(ProcessEventTag.INVARIANT_VIOLATION);
            }
            instanceToStart.start(startEvent, nodeInModelToCheck);
            return instanceToStart;
        }

        return null;
    }

    private boolean validateNodePathAndProcessInstanceIDs(
            List<Node> nodePath,
            List<String> ids,
            ProcessLogEvent event) {
        if (nodePath.size() == 0) {
            System.out.printf(
                "Unable to find event activity in process model. " +
                    "EventActivity=%s; " +
                    "Event=%s%n",
                event.getActivityName(),
                event.getLog());
            return false;
        }
        if (nodePath.size() != ids.size() + 1) {
            List<String> modelNames = new ArrayList<>(nodePath.size());
            for (int i = 0; i < nodePath.size() - 1; ++i) {
                ProcessModel model = (ProcessModel)nodePath.get(i);
                modelNames.add(model.getName());
            }
            System.out.printf(
                "Unable to match modelPath and process instance IDs. " +
                    "ModelPath=%s; " +
                    "ProcessInstanceIDs=%s; " +
                    "Event=%s%n",
                StringUtils.join(modelNames, ','),
                StringUtils.join(ids, ','),
                event.getLog());
            return false;
        }
        return true;
    }

    private ProcessModel rootModel;
    private ProcessModel.State rootModelState;
}
