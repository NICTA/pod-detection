package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.junit.Test;

import java.util.EnumSet;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestProcessInstance {
    @Test
    public void executeNodePullsRecursively() {
        ProcessModel model =
            ProcessModelFactoryForTest.createModelThatHasConsecutiveGateways();
        Activity act1 = model.findPathToActivity("act-1").getRight();
        Activity act2 = model.findPathToActivity("act-2").getRight();
        EndEvent endEvent = (EndEvent)model.getEndEventByLinkedNode(act2);

        ProcessInstance instance = new ProcessInstance("test-instance", model);

        // Executes "act-1" activity to provide token for consecutive gateways.
        EnumSet<StateError> errorsForAct1Exec =
            instance.executeNode(model.findPathToActivity("act-1").getRight());

        // Executes "act-2" causes the gateways to pull recursively and move
        // the token to the incoming link of "act-2". Then the token should be
        // moved by "act-2" and finally consumed by the end event.
        EnumSet<StateError> errorsForAct2Exec = instance.executeNode(act2);

        NodeState endEventState = instance.getNodeStateMapView().get(endEvent);
        LinkState endEventLinkInState =
            instance.getLinkStatesIn(endEventState).get(0);

        assertNull("no errors for \"act-1\" execution", errorsForAct1Exec);
        assertNull("no errors for \"act-2\" execution", errorsForAct2Exec);
        assertThat("number of tokens produced by \"act-2\"",
                   endEventLinkInState.getTokenProduced(),
                   equalTo(1));
        assertThat("number of tokens consumed by end event",
                   endEventLinkInState.getTokenConsumed(),
                   equalTo(1));
    }

    @Test
    public void executeNodeCollectsTimeAnomalyAndNumericInvariantsErrors()
        throws InterruptedException {

        ProcessModel rootModel =
            ProcessModelFactoryForTest.
            createModelTreeThatHasTimeCheckersAndNumericInvariants();
        Activity rootAct1 = rootModel.findPathToActivity("root-act-1").getRight();
        Activity rootAct2 = rootModel.findPathToActivity("root-act-2").getRight();
        Activity subAct1 = rootModel.findPathToActivity("sub-act-1").getRight();
        ProcessModel subModel =
            rootModel.findPathToActivity("sub-act-1").getLeft().getLast();

        ProcessInstance rootInstance =
            new ProcessInstance("root-instance", rootModel);

        // Executes "act-1" to start root process instance.
        rootInstance.executeNode(rootAct1);

        // Executes "sub-act-1" to start only one sub process instance. This
        // will violate the sub process model's numeric invariants.
        String subContextID = "sub-instance";

        ProcessInstance subInstance =
            new ProcessInstance(subContextID, subModel);
        rootInstance.addSubContext(subModel, subContextID, subInstance);
        rootInstance.executeNode(subModel);
        subInstance.executeNode(subAct1);

        // Executes "root-act-2" after sleeping for 3 seconds. This will trigger
        // multiple errors about time anomaly and numeric invariants for the sub
        // process model.
        Thread.sleep(3500);
        EnumSet<StateError> errors = rootInstance.executeNode(rootAct2);

        assertNotNull("has errors", errors);
        assertTrue("has time anomaly error",
                   errors.contains(StateError.NODE_TIME_ANOMALY));
        assertTrue("has numeric invariants error",
                   errors.contains(StateError.NUMERIC_INVARIANTS_VIOLATION));
    }
}
