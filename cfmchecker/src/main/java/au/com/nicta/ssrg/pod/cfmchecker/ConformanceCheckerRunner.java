package au.com.nicta.ssrg.pod.cfmchecker;

import au.com.nicta.ssrg.pod.cfmchecker.io.ProcessLogEvent;
import au.com.nicta.ssrg.pod.cfmchecker.io.ProcessRepository;
import au.com.nicta.ssrg.pod.cfmchecker.newcore.ConformanceChecker;
import au.com.nicta.ssrg.pod.cfmchecker.newcore.ConformanceCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
public class ConformanceCheckerRunner implements CommandLineRunner {
    @Async
    @SuppressWarnings("unchecked")
    public void run(String... args) throws IOException, InterruptedException {
        processEventQueue =
            (BlockingQueue<ProcessLogEvent>)context.getBean("processEventQueue");
        ProcessLogEvent event;
        while ((event = processEventQueue.take()) != null) {
            ConformanceCheckResult result = conformanceChecker.checkConformance(event);
            for (ProcessRepository processRepository : processRepositories) {
                processRepository.storeConformanceResult(result);
            }
        }
    }

    @Autowired
    private ConformanceChecker conformanceChecker;

    @Autowired
    private List<ProcessRepository> processRepositories;

    @Autowired
    ApplicationContext context;

    private BlockingQueue<ProcessLogEvent> processEventQueue;
}
