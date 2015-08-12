package au.com.nicta.ssrg.pod.cfmchecker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import au.com.nicta.ssrg.pod.cfmchecker.core.ConformanceChecker;
import au.com.nicta.ssrg.pod.cfmchecker.core.LogInfoExtractor;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessLogEvent;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessModel;
import au.com.nicta.ssrg.pod.cfmchecker.io.ActivityPatternImporter;
import au.com.nicta.ssrg.pod.cfmchecker.io.ProcessEsRepository;
import au.com.nicta.ssrg.pod.cfmchecker.io.ProcessModelImporter;
import au.com.nicta.ssrg.pod.cfmchecker.io.ProcessRepository;
import au.com.nicta.ssrg.pod.cfmchecker.io.RegexLogInfoExtractor;

@SpringBootApplication
public class App {
    public static void main(String[] args ) {
        SpringApplication.run(App.class, args);
    }

    @Bean(name="processModelSourceFile")
    public File getProcessModelSourceFile() {
        return new File("process-model.json");
    }

    @Bean(name="processModel")
    public ProcessModel getProcessModel(File processModelSourceFile)
            throws IOException {
        ProcessModelImporter importer =
            new ProcessModelImporter(processModelSourceFile);
        return importer.parse();
    }

    @Bean(name="conformanceChecker")
    public ConformanceChecker getConformanceChecker(ProcessModel processModel) {
        return new ConformanceChecker(processModel);
    }

    @Bean(name="activityPatternSourceFile")
    public File getActivityPatternSourceFile() {
        return new File("activity-pattern.json");
    }

    @Bean(name="activityPatternImporter")
    public ActivityPatternImporter getActivityPatternImporter(
            File activityPatternSourceFile) {
        return new ActivityPatternImporter(activityPatternSourceFile);
    }

    @Bean(name="logInfoExtractor")
    public LogInfoExtractor getLogInfoExtractor(
            ActivityPatternImporter activityPatternImporter)
            throws IOException {
        return new RegexLogInfoExtractor(
            null,
            0,
            "asgard-001",
            activityPatternImporter.parse(),
            "",
            0,
            "yyyy-MM-dd HH:mm:ss.SSS");
    }

    @Bean(name="processActivityInstanceFile")
    public File getProcessActivityInstanceFile() {
        return new File("process-activity-instance-export.txt");
    }

    @Bean(name="processLogEventFile")
    public File getProcessLogEventFile() {
        return new File("process-log-event-export.txt");
    }

    @Bean(name="processEventQueue")
    public BlockingQueue<ProcessLogEvent> getProcessEventQueue() {
        return new ArrayBlockingQueue<ProcessLogEvent>(300, true);
    }

    @Bean(name="processRepository")
    public ProcessRepository getProcessRepository() {
        return new ProcessEsRepository(
            "elasticsearch_pod",
            "conformance",
            "logEvent");
    }
}
