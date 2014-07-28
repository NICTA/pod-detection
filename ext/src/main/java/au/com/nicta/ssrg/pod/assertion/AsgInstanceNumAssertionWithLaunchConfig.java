package au.com.nicta.ssrg.pod.assertion;

import au.com.nicta.ssrg.pod.*;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.autoscaling.AmazonAutoScalingAsync;
import com.amazonaws.services.autoscaling.model.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.logging.log4j.*;

public class AsgInstanceNumAssertionWithLaunchConfig extends RepetitiveAssertion {
    @Override
    public void init(Map<String, Object> attributes) {
        asgName = (String)attributes.get("asgName");
        amiID = (String)attributes.get("amiID");
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setMinInstanceNum(int minInstanceNum) {
        this.minInstanceNum = minInstanceNum;
    }

    public void setLaunchConfigName(String launchConfigName) {
        this.launchConfigName = launchConfigName;
    }

    @Override
    protected void work() {
        ThreadContext.put("amiID", amiID);
        ThreadContext.put("minInstanceNum", Integer.toString(minInstanceNum));
        assertionLogger.info("Checking instance number of ASG {} using launch configuration {}.", asgName, launchConfigName);
        try {
            int instanceNum = check();
            if (instanceNum >= minInstanceNum) {
                assertPass(
                    "Assertion passed: ASG {} has {} instances using launch configuration {}.",
                    asgName,
                    instanceNum,
                    launchConfigName);
            } else {
                assertFail(
                    "Assertion failed: ASG {} has {} instances using launch configuration {}.",
                    asgName,
                    instanceNum,
                    launchConfigName);
            }
        } catch (TimeoutException ex) {
            if (future != null) { future.cancel(true); }
            assertError("Check of instance number of ASG {} failed due to timeout.", asgName);
        } catch (InterruptedException | CancellationException ex) {
            assertError(
                "Check of instance number of ASG {} using launch configuration {} was interrupted/cancelled unexpectedly. {}",
                asgName,
                launchConfigName,
                ex.getMessage());
        } catch (ExecutionException | AmazonClientException ex) {
            assertError(
                "Check of instance number of ASG {} using launch configuration {} failed due to exception. {}",
                asgName,
                launchConfigName,
                ex.getMessage());
        }
        ThreadContext.remove("amiID");
        ThreadContext.remove("minInstanceNum");
    }

    protected int check() throws InterruptedException, ExecutionException, TimeoutException {
        int instanceNum = 0;
        DescribeAutoScalingGroupsRequest request = new DescribeAutoScalingGroupsRequest();
        ArrayList<String> asgNames = new ArrayList<String>();
        asgNames.add(asgName);
        request.setAutoScalingGroupNames(asgNames);
        AmazonAutoScalingAsync client = AwsManager.getCurrent().createAmazonAutoScalingAsync();
        future = client.describeAutoScalingGroupsAsync(request);
        DescribeAutoScalingGroupsResult result = future.get(timeout, TimeUnit.MILLISECONDS);
        List<AutoScalingGroup> groups = result.getAutoScalingGroups();
        if (!groups.isEmpty()) {
            List<Instance> instances = groups.get(0).getInstances();
            for (int i = 0; i < instances.size(); ++i) {
                Instance instance = instances.get(i);
                LifecycleState state = LifecycleState.fromValue(instance.getLifecycleState());
                if (instance.getLaunchConfigurationName().equals(launchConfigName) && state.equals(LifecycleState.InService)) {
                    ++instanceNum;
                }
            }
        }
        return instanceNum;
    }

    private Future<DescribeAutoScalingGroupsResult> future;
    private int timeout = 5 * 1000;
    private String asgName;
    private String amiID;
    private int minInstanceNum;
    private String launchConfigName;
}
