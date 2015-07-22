package au.com.nicta.ssrg.pod.assertion;

import au.com.nicta.ssrg.pod.*;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.model.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.logging.log4j.*;

public class InstanceStateAssertion extends RepetitiveAssertion {
    @Override
    public void init(Map<String, Object> attributes) {
        asgName = (String)attributes.get("asgName");
        amiID = (String)attributes.get("amiID");
        instanceID = (String)attributes.get("instanceID");
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setStateText(String stateText) {
        this.stateText = stateText;
    }

    @Override
    protected void work() {
        String instanceState = new String();
        assertionLogger.info("Checking state of instance {} against \"{}\".", instanceID, stateText);
        try {
             instanceState = check();
        } catch (TimeoutException ex) {
            if (future != null) { future.cancel(true); }
            assertError(
                "Checking state of instance {} against \"{}\" failed due to timeout.",
                instanceID,
                stateText);
        } catch (InterruptedException | CancellationException ex) {
            assertError(
                "Checking state of instance {} against \"{}\" was interrupted/cancelled unexpectedly. {}",
                instanceID,
                stateText,
                ex.getMessage());
        } catch (ExecutionException | AmazonClientException ex) {
            assertError(
                "Checking state of instance {} against \"{}\" failed due to exception. {}",
                instanceID,
                stateText,
                ex.getMessage());
        }

        if (state.equals(AssertionState.NEUTRAL)) {
            if (instanceState.equals(stateText)) {
                assertPass("The state \"{}\" of instance {} is correct.", stateText, instanceID);
            } else {
                assertFail("The state \"{}\" of instance {} is incorrect.", stateText, instanceID);
            }
        }
    }

    protected String check() throws InterruptedException, ExecutionException, TimeoutException {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        HashSet<String> instanceIDs = new HashSet<String>();
        instanceIDs.add(instanceID);
        request.setInstanceIds(instanceIDs);
        AmazonEC2Async client = AwsManager.getCurrent().createAmazonEC2Async();
        future = client.describeInstancesAsync(request);
        String instanceState = new String();
        DescribeInstancesResult result = future.get(timeout, TimeUnit.MILLISECONDS);
        List<Reservation> reservations = result.getReservations();
        Set<Instance> instances = new HashSet<Instance>();
        for (Reservation reservation : reservations) {
            instances.addAll(reservation.getInstances());
        }
        for (Instance instance : instances) {
            instanceState = instance.getState().getName();
        }

        return instanceState;
    }

    private Future<DescribeInstancesResult> future;
    private int timeout = 5 * 1000;
    private String asgName;
    private String amiID;
    private String elbName;
    private String instanceID;
    private String stateText;
}
