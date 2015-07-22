package au.com.nicta.ssrg.pod.cfmchecker;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import au.com.nicta.ssrg.pod.cfmchecker.core.ConformanceChecker;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessLogEvent;
import au.com.nicta.ssrg.pod.cfmchecker.io.ProcessRepository;

@Component
public class ConformanceCheckerRunner implements CommandLineRunner {
    @Async
    public void run(String... args) throws IOException, InterruptedException {
        processEventQueue = (BlockingQueue<ProcessLogEvent>)
            context.getBean("processEventQueue");
        ProcessLogEvent event;
        while ((event = processEventQueue.take()) != null) {
            conformanceChecker.checkConformance(event);
            System.out.printf("Checked process event: %s%n", event.toString());
            for (ProcessRepository processRepository : processRepositories) {
                processRepository.storeLogEvent(event);
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
