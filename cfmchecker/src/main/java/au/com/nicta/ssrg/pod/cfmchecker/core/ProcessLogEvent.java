package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ProcessLogEvent {
    public ProcessLogEvent(
            String log,
            LogInfoExtractor logInfoExtractor,
            Date defaultTime,
            String defaultRootProcInstID) {
        this.log = log;
        this.tags = EnumSet.noneOf(ProcessEventTag.class);

        this.processIDs = logInfoExtractor.
            extractProcessIDs(this.log, defaultRootProcInstID);
        this.activityName = logInfoExtractor.extractActivityName(this.log);
        this.time = logInfoExtractor.extractTime(this.log);

        if (this.time == null) {
            this.time = defaultTime;
        }
    }

    public String getLog() {
        return log;
    }

    public String getActivityName() {
        return activityName;
    }

    public List<String> getProcessIDs() {
        return processIDs;
    }

    public Integer getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public Date getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(Date activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public Date getActivityLastExecutionTime() {
        return activityLastExecutionTime;
    }

    public void setActivityLastExecutionTime(Date activityLastExecutionTime) {
        this.activityLastExecutionTime = activityLastExecutionTime;
    }

    public Date getTime() {
        return time;
    }

    public Collection<ProcessEventTag> getTags() {
        return tags;
    }

    public void addTag(ProcessEventTag tag) {
        tags.add(tag);
    }

    public boolean containsTag(ProcessEventTag tag) {
        return tags.contains(tag);
    }

    public boolean hasActivityErrorTags() {
        for (ProcessEventTag tag : tags) {
            if (activityErrorTags.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    public ProcessModel.State getModelState() {
        return modelState;
    }

    public void setModelState(ProcessModel.State modelState) {
        this.modelState = modelState;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator;
        try {
            generator = factory.createGenerator(writer);
            generator.writeStartObject();
            generator.writeStringField("log", log);
            generator.writeStringField("activityName", activityName);
            generator.writeArrayFieldStart("processIDs");
            for (String processID : processIDs) {
                generator.writeString(processID);
            }
            generator.writeEndArray();
            generator.writeArrayFieldStart("tags");
            for (ProcessEventTag tag : tags) {
                generator.writeString(tag.value());
            }
            generator.writeEndArray();
            generator.writeEndObject();
            generator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
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
    private List<String> processIDs;
    private Date time;
    private Set<ProcessEventTag> tags;
    private ProcessModel.State modelState;
}
