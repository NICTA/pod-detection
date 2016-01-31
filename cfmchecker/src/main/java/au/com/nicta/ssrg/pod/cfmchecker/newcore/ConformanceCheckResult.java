package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.ArrayList;
import java.util.List;

public class ConformanceCheckResult {
    public ConformanceCheckResult(
        ProcessEvent event,
        List<ProcessModel> models,
        List<ProcessContext> contexts) {

        this.event = event;
        this.models = models == null ? new ArrayList<>() : models;

        if (contexts == null) {
            contextSnapshots = new ArrayList<>();
        } else {
            contextSnapshots = new ArrayList<>(contexts.size());
            for (ProcessContext context : contexts) {
                contextSnapshots.add(new ProcessContextSnapshot(context));
            }
        }
    }

    public ConformanceCheckResult(ProcessEvent event) {
        this.event = event;
        this.models = new ArrayList<>();
        this.contextSnapshots = new ArrayList<>();
    }

    public ProcessEvent getEvent() {
        return event;
    }

    public List<ProcessModel> getModels() {
        return models;
    }

    public List<ProcessContextSnapshot> getContextSnapshots() {
        return contextSnapshots;
    }

    private ProcessEvent event;
    private List<ProcessModel> models;
    private List<ProcessContextSnapshot> contextSnapshots;
}
