package au.com.nicta.ssrg.pod;

import java.io.File;
import java.util.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.*;

public class DetectionServiceContext {
    public static void init() {
        String workingDirectory = System.getProperty("user.dir");
        File configXmlFile = new File(workingDirectory, SPRING_CONFIG_FILE_NAME);
        ApplicationContext context;
        if (configXmlFile.exists()) {
            context = new FileSystemXmlApplicationContext(configXmlFile.getPath());
        } else {
            context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE_NAME);
        }
        current = context.getBean("detectionServiceContext", DetectionServiceContext.class);
        current.appContext = context;
    }

    public static DetectionServiceContext getCurrent() {
        return current;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<Process> getProcesses() {
        return this.processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public Assertion getAssertion(String name) {
        return appContext.getBean(name, Assertion.class);
    }

    private static final String SPRING_CONFIG_FILE_NAME = "spring-pod.xml";
    private static DetectionServiceContext current;
    private ApplicationContext appContext;
    private int port;
    private List<Process> processes;
}
