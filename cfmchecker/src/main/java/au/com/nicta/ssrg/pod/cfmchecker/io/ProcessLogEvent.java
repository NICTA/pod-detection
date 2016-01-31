package au.com.nicta.ssrg.pod.cfmchecker.io;

import au.com.nicta.ssrg.pod.cfmchecker.newcore.ProcessEvent;
import au.com.nicta.ssrg.pod.cfmchecker.newcore.ProcessEventTag;

import java.util.*;

public class ProcessLogEvent implements ProcessEvent {
    public ProcessLogEvent(
            String log,
            LogInfoExtractor logInfoExtractor,
            Date defaultTime,
            String defaultRootProcessInstanceID) {
        this.log = log;
        this.tags = EnumSet.noneOf(ProcessEventTag.class);

        this.processInstanceIDs = logInfoExtractor.
            extractProcessIDs(this.log, defaultRootProcessInstanceID);
        this.activityName = logInfoExtractor.extractActivityName(this.log);
        this.time = logInfoExtractor.extractTime(this.log);

        if (this.time == null) {
            this.time = defaultTime;
        }
    }

    @Override
    public String getActivityName() {
        return activityName;
    }

    @Override
    public List<String> getProcessInstanceIDs() {
        return processInstanceIDs;
    }

    @Override
    public void tag(ProcessEventTag tag) {
        if (tag != null) {
            tags.add(tag);
        }
    }

    public boolean hasTag(ProcessEventTag tag) {
        return tags.contains(tag);
    }

    @Override
    public void setActivityID(int id) {
        activityID = id;
    }

    public String getLog() {
        return log;
    }

    public Integer getActivityID() {
        return activityID;
    }

    public Date getTime() {
        return time;
    }

    public Collection<ProcessEventTag> getTags() {
        return tags;
    }

    public boolean hasActivityErrorTags() {
        for (ProcessEventTag tag : tags) {
            if (activityErrorTags.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    private static Set<ProcessEventTag> activityErrorTags = EnumSet.of(
        ProcessEventTag.TIME_ANOMALY,
        ProcessEventTag.INVARIANT_VIOLATION,
        ProcessEventTag.UNFIT);
    private String log;
    private String activityName;
    private Integer activityID;
    private Date activityStartTime;
    private Date activityLastExecutionTime;
    private List<String> processInstanceIDs;
    private Date time;
    private Set<ProcessEventTag> tags;
}
