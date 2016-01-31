package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TestConformanceChecker {
    public static class ProcessEventCommon implements ProcessEvent {
        @Override
        public void tag(ProcessEventTag tag) {
            tags.add(tag);
        }

        @Override
        public void setActivityID(int id) {
            activityID = id;
        }

        @Override
        public String getActivityName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> getProcessInstanceIDs() {
            throw new UnsupportedOperationException();
        }

        public boolean hasTags() {
            return !tags.isEmpty();
        }

        public boolean hasTagsOtherThan(ProcessEventTag tag) {
            if (tags.contains(tag)) {
                return tags.size() > 1;
            } else {
                return !tags.isEmpty();
            }
        }

        public int getActivityID() {
            return activityID;
        }

        public EnumSet<ProcessEventTag> getTags() {
            return tags;
        }

        private int activityID;
        private EnumSet<ProcessEventTag> tags =
            EnumSet.noneOf(ProcessEventTag.class);
    }

    @Test
    public void checkConformanceAgainstModelTreeWithoutErrorsGivesNoErrorTagsToEvents() {
        HashMap<String, ConformanceCheckResult> namedResults =
            checkConformanceAgainstModelTreeWithoutErrors();

        for (Map.Entry<String, ConformanceCheckResult> namedResult :
             namedResults.entrySet()) {

            ProcessEventCommon eventCommon =
                (ProcessEventCommon)namedResult.getValue().getEvent();
            assertTrue(namedResult.getKey() + " executed with no errors",
                       !eventCommon.hasTagsOtherThan(ProcessEventTag.COMPLETED));
        }
    }

    private LinkedHashMap<String, ConformanceCheckResult> checkConformanceAgainstModelTreeWithoutErrors() {
        ProcessModel model =
            ProcessModelFactoryForTest.createSimpleModelTreeOfDepthThree();
        ConformanceChecker checker = new ConformanceChecker(model);
        LinkedHashMap<String, ConformanceCheckResult> namedResults =
            new LinkedHashMap<>();

        {
            String resultName = "start pi: root";
            String activityName = "root-act-1";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "start pi: root first-leaf";
            String activityName = "first-leaf-act-1";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "first-leaf")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "end pi: root first-leaf";
            String activityName = "first-leaf-act-2";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "first-leaf")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "triggers: root-act-2";
            String activityName = "root-act-2";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "start pi: root mid-1";
            String activityName = "mid-act-1";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-1")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "start pi: root mid-2";
            String activityName = "mid-act-1";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-2")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "start pi: root mid-2 second-leaf-1";
            String activityName = "second-leaf-act-1";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-2", "second-leaf-1")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "start pi: root mid-2 second-leaf-2";
            String activityName = "second-leaf-act-1";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-2", "second-leaf-2")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "start pi: root mid-1 second-leaf-1";
            String activityName = "second-leaf-act-1";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-1", "second-leaf-1")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "start pi: root mid-1 second-leaf-2";
            String activityName = "second-leaf-act-1";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-1", "second-leaf-2")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "end pi: root mid-1 second-leaf-2";
            String activityName = "second-leaf-act-2";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-1", "second-leaf-2")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "end pi: root mid-1 second-leaf-1";
            String activityName = "second-leaf-act-2";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-1", "second-leaf-1")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "end pi: root mid-1";
            String activityName = "mid-act-2";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-1")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }


        {
            String resultName = "end pi: root mid-2 second-leaf-1";
            String activityName = "second-leaf-act-2";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-2", "second-leaf-1")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "end pi: root mid-2 second-leaf-2";
            String activityName = "second-leaf-act-2";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-2", "second-leaf-2")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "end pi: root mid-2";
            String activityName = "mid-act-2";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root", "mid-2")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        {
            String resultName = "end pi: root";
            String activityName = "root-act-3";
            ProcessEvent event = spyProcessEventCommon();
            doReturn(activityName).when(event).getActivityName();
            doReturn(
                Arrays.asList("root")
            ).when(event).getProcessInstanceIDs();
            ConformanceCheckResult result = checker.checkConformance(event);
            namedResults.put(resultName, result);
        }

        return namedResults;
    }

    private ProcessEventCommon spyProcessEventCommon() {
        ProcessEventCommon event = new ProcessEventCommon();
        return spy(event);
    }
}
