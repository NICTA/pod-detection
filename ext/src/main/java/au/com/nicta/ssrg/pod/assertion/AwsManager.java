package au.com.nicta.ssrg.pod.assertion;

import com.amazonaws.auth.*;
import com.amazonaws.services.autoscaling.*;
import com.amazonaws.services.ec2.*;
import com.amazonaws.services.elasticloadbalancing.*;
import java.io.File;

public final class AwsManager {
    public static AwsManager getCurrent() {
        return current;
    }

    public AWSCredentialsProvider getCredentialProvider() {
        return credentialsProvider;
    }

    public AmazonAutoScaling createAmazonAutoScaling() {
        return new AmazonAutoScalingClient(credentialsProvider);
    }

    public AmazonAutoScalingAsync createAmazonAutoScalingAsync() {
        return new AmazonAutoScalingAsyncClient(credentialsProvider);
    }

    public AmazonElasticLoadBalancingAsync createAmazonElasticLoadBalancingAsync() {
        return new AmazonElasticLoadBalancingAsyncClient(credentialsProvider);
    }

    public AmazonEC2Async createAmazonEC2Async() {
        return new AmazonEC2AsyncClient(credentialsProvider);
    }

    private AwsManager() {
        String workingDirectory = System.getProperty("user.dir");
        File configFile = new File(workingDirectory, AWS_CREDENTIALS_CONFIG_FILE_NAME);
        if (configFile.exists()) {
            credentialsProvider = new PropertiesFileCredentialsProvider(AWS_CREDENTIALS_CONFIG_FILE_NAME);
        } else {
            credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
        }
    }

    private static final String AWS_CREDENTIALS_CONFIG_FILE_NAME = "AwsCredentials.properties";
    private static AwsManager current = new AwsManager();
    private AWSCredentialsProvider credentialsProvider;
}
