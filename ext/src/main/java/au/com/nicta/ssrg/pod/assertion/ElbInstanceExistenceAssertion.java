package au.com.nicta.ssrg.pod.assertion;

import au.com.nicta.ssrg.pod.*;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.autoscaling.model.*;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingAsync;
import com.amazonaws.services.elasticloadbalancing.model.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.logging.log4j.*;

public class ElbInstanceExistenceAssertion extends RepetitiveAssertion {
    @Override
    public void init(Map<String, Object> attributes) {
        asgName = (String)attributes.get("asgName");
        amiID = (String)attributes.get("amiID");
        instanceID = (String)attributes.get("instanceID");
    }

    public void setElbName(String elbName) {
        this.elbName = elbName;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setShouldInstanceExist(boolean shouldInstanceExist) {
        this.shouldInstanceExist = shouldInstanceExist;
    }

    @Override
    protected void work() {
        if (shouldInstanceExist) {
            assertionLogger.info("Checking that ELB {} has instance {}.", elbName, instanceID);
        } else {
            assertionLogger.info("Checking that ELB {} does not have instance {}.", elbName, instanceID);
        }

        try {
            check();
        } catch (TimeoutException ex) {
            if (future != null) { future.cancel(true); }
            assertError(
                "Check of existence of instance {} of ELB {} failed due to timeout.",
                instanceID,
                elbName);
        }catch (InterruptedException | CancellationException ex) {
            assertError(
                "Check of existence of instance {} of ELB {} was interrupted/cancelled unexpectedly. {}",
                instanceID,
                elbName,
                ex.getMessage());
        } catch (ExecutionException | AmazonClientException ex) {
            assertError(
                "Check of existence of instance {} of ELB {} failed due to exception. {}",
                instanceID,
                elbName,
                ex.getMessage());
        }

        if (state.equals(AssertionState.NEUTRAL)) {
            if (shouldInstanceExist) {
                if (isInstancePresent){
                    assertPass("ELB {} has instance {}.", elbName, instanceID);
                } else {
                    assertFail("ELB {} has no instance {} though it should have.", elbName, instanceID);
                }
            } else {
                if (isInstancePresent){
                    assertFail("ELB {} has instance {} though it should not have.", elbName, instanceID);
                } else {
                    assertPass("ELB {} does not have instance {}.", elbName, instanceID);
                }
            }
        }
    }

    protected void check() throws InterruptedException, ExecutionException, TimeoutException {
        isInstancePresent = false;
        DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest();
        HashSet<String> elbNames = new HashSet<String>();
        elbNames.add(elbName);
        request.setLoadBalancerNames(elbNames);
        AmazonElasticLoadBalancingAsync client = AwsManager.getCurrent().createAmazonElasticLoadBalancingAsync();
        future = client.describeLoadBalancersAsync(request);
        DescribeLoadBalancersResult result = future.get(timeout, TimeUnit.MILLISECONDS);
        List<LoadBalancerDescription> descriptions = result.getLoadBalancerDescriptions();
        if (!descriptions.isEmpty()) {
            List<com.amazonaws.services.elasticloadbalancing.model.Instance> instances = descriptions.get(0).getInstances();
            for (int i = 0; i < instances.size(); ++i) {
                if(instances.get(i).getInstanceId().equals(instanceID)) {
                    isInstancePresent = true;
                    break;
                }
            }
        }
    }

    protected boolean isInstancePresent;
    private Future<DescribeLoadBalancersResult> future;
    private int timeout = 5 * 1000;
    private String asgName;
    private String amiID;
    private String elbName;
    private String instanceID;
    private boolean shouldInstanceExist;
}
