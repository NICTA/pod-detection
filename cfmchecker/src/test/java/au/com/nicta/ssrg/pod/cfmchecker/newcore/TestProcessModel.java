package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class TestProcessModel {
    @Before
    public void createModel() {
        model = ProcessModelFactoryForTest.createSimpleModelTreeOfDepthThree();
    }

    @Test
    public void findPathToActivityReturnsProcessModelTreePathAndActivity() {
        String activityName = "second-leaf-act-2";

        ImmutablePair<LinkedList<ProcessModel>, Activity> pathAndActivity =
            model.findPathToActivity(activityName);

        assertNotNull("path is found", pathAndActivity);
        assertThat("model names are in root-to-leaf order",
                   pathAndActivity.getLeft()
                                  .stream()
                                  .map(m -> m.getName())
                                  .collect(Collectors.toList()),
                   contains("root-model", "mid-model", "second-leaf-model"));
        assertEquals("activity name is matched",
                     pathAndActivity.getRight().getName(),
                     activityName);
    }

    @Test
    public void findPathToActivityReturnsNullWhenNotFound() {
        String activityName = "no-such-activity";

        ImmutablePair<LinkedList<ProcessModel>, Activity> pathAndActivity =
            model.findPathToActivity(activityName);

        assertNull("no path found", pathAndActivity);
    }

    private ProcessModel model;
}
