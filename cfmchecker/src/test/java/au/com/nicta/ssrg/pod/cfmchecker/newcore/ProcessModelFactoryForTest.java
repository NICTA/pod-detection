package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessModelFactoryForTest {
    public static ProcessModel createSimpleModelTreeOfDepthThree() {
        ProcessModel firstLeafModel = null;
        {
            List<Node> nodes = new ArrayList<>();
            StartEvent startEvent = new StartEvent();
            EndEvent endEvent = new EndEvent();
            Activity act1 = new Activity("first-leaf-act-1", null);
            Activity act2 = new Activity("first-leaf-act-2", null);

            nodes.add(startEvent);
            nodes.add(endEvent);
            nodes.add(act1);
            nodes.add(act2);

            List<Link> links = new ArrayList<>();
            links.add(new Link(startEvent, act1));
            links.add(new Link(act1, act2));
            links.add(new Link(act2, endEvent));

            firstLeafModel = new ProcessModel("first-leaf-model",
                nodes,
                links,
                null,
                null);
        }

        ProcessModel secondLeafModel = null;
        {
            List<Node> nodes = new ArrayList<>();
            StartEvent startEvent = new StartEvent();
            EndEvent endEvent = new EndEvent();
            Activity act1 = new Activity("second-leaf-act-1", null);
            Activity act2 = new Activity("second-leaf-act-2", null);

            nodes.add(startEvent);
            nodes.add(endEvent);
            nodes.add(act1);
            nodes.add(act2);

            List<Link> links = new ArrayList<>();
            links.add(new Link(startEvent, act1));
            links.add(new Link(act1, act2));
            links.add(new Link(act2, endEvent));

            secondLeafModel = new ProcessModel("second-leaf-model",
                nodes,
                links,
                null,
                null);
        }

        ProcessModel midModel = null;
        {
            List<Node> nodes = new ArrayList<>();
            StartEvent startEvent = new StartEvent();
            EndEvent endEvent = new EndEvent();
            Activity act1 = new Activity("mid-act-1", null);
            Activity act2 = new Activity("mid-act-2", null);

            nodes.add(startEvent);
            nodes.add(endEvent);
            nodes.add(act1);
            nodes.add(act2);
            nodes.add(secondLeafModel);

            List<Link> links = new ArrayList<>();
            links.add(new Link(startEvent, act1));
            links.add(new Link(act1, secondLeafModel));
            links.add(new Link(secondLeafModel, act2));
            links.add(new Link(act2, endEvent));

            midModel = new ProcessModel("mid-model",
                nodes,
                links,
                null,
                null);
        }

        ProcessModel rootModel = null;
        {
            List<Node> nodes = new ArrayList<>();
            StartEvent startEvent = new StartEvent();
            EndEvent endEvent = new EndEvent();
            Activity act1 = new Activity("root-act-1", null);
            Activity act2 = new Activity("root-act-2", null);
            Activity act3 = new Activity("root-act-3", null);

            nodes.add(startEvent);
            nodes.add(endEvent);
            nodes.add(act1);
            nodes.add(act2);
            nodes.add(act3);
            nodes.add(firstLeafModel);
            nodes.add(midModel);

            List<Link> links = new ArrayList<>();
            links.add(new Link(startEvent, act1));
            links.add(new Link(act1, firstLeafModel));
            links.add(new Link(firstLeafModel, act2));
            links.add(new Link(act2, midModel));
            links.add(new Link(midModel, act3));
            links.add(new Link(act3, endEvent));

            rootModel = new ProcessModel("root-model",
                nodes,
                links,
                null,
                null);
        }

        return rootModel;
    }

    public static ProcessModel createModelThatHasConsecutiveGateways() {
        StartEvent startEvent = new StartEvent();
        EndEvent endEvent1 = new EndEvent();
        EndEvent endEvent2 = new EndEvent();
        Activity act1 = new Activity("act-1", null);
        Activity act2 = new Activity("act-2", null);
        Activity act3 = new Activity("act-3", null);
        AndSplitGateway andSplitGateway = new AndSplitGateway();
        XorSplitGateway xorSplitGateway = new XorSplitGateway();
        AndJoinGateway andJoinGateway = new AndJoinGateway();

        List<Node> nodes = new ArrayList<>();
        nodes.add(startEvent);
        nodes.add(endEvent1);
        nodes.add(endEvent2);
        nodes.add(act1);
        nodes.add(act2);
        nodes.add(act3);
        nodes.add(andSplitGateway);
        nodes.add(xorSplitGateway);
        nodes.add(andJoinGateway);

        List<Link> links = new ArrayList<>();
        links.add(new Link(startEvent, act1));
        links.add(new Link(act1, andSplitGateway));
        links.add(new Link(andSplitGateway, xorSplitGateway));
        links.add(new Link(andSplitGateway, andJoinGateway));
        links.add(new Link(xorSplitGateway, act3));
        links.add(new Link(xorSplitGateway, andJoinGateway));
        links.add(new Link(act3, endEvent1));
        links.add(new Link(andJoinGateway, act2));
        links.add(new Link(act2, endEvent2));

        return new ProcessModel("consecutive-gateways-model",
                                nodes,
                                links,
                                null,
                                null);
    }

    public static ProcessModel createModelTreeThatHasTimeCheckersAndNumericInvariants() {
        TimeChecker tc = new TimeChecker(Arrays.asList(
            new TimeAnomalyInterval(3000, Long.MAX_VALUE)
        ));

        ProcessModel subModel = null;
        {
            StartEvent startEvent = new StartEvent();
            EndEvent endEvent = new EndEvent();
            Activity act1 = new Activity("sub-act-1", tc);
            Activity act2 = new Activity("sub-act-2", tc);

            List<Node> nodes = new ArrayList<>();
            nodes.add(startEvent);
            nodes.add(endEvent);
            nodes.add(act1);
            nodes.add(act2);

            List<Link> links = new ArrayList<>();
            links.add(new Link(startEvent, act1));
            links.add(new Link(act1, act2));
            links.add(new Link(act2, endEvent));

            subModel = new ProcessModel("sub-model",
                                        nodes,
                                        links,
                                        new NumericInvariants(2, 3),
                                        tc);
        }

        ProcessModel rootModel = null;
        {
            StartEvent startEvent = new StartEvent();
            EndEvent endEvent = new EndEvent();
            Activity act1 = new Activity("root-act-1", tc);
            Activity act2 = new Activity("root-act-2", tc);

            List<Node> nodes = new ArrayList<>();
            nodes.add(startEvent);
            nodes.add(endEvent);
            nodes.add(act1);
            nodes.add(act2);
            nodes.add(subModel);

            List<Link> links = new ArrayList<>();
            links.add(new Link(startEvent, act1));
            links.add(new Link(act1, subModel));
            links.add(new Link(subModel, act2));
            links.add(new Link(act2, endEvent));

            rootModel = new ProcessModel("root-model",
                                         nodes,
                                         links,
                                         null,
                                         null);
        }

        return rootModel;
    }
}
