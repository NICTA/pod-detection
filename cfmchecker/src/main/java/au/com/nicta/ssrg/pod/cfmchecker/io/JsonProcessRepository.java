package au.com.nicta.ssrg.pod.cfmchecker.io;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import au.com.nicta.ssrg.pod.cfmchecker.core.Activity;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessEventTag;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessInstance;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessLogEvent;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessModel;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonProcessRepository
        implements ProcessRepository {
    public JsonProcessRepository() {
        jsonFactory = new JsonFactory();
        jsonFactory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }

    protected String convertEventToJson(ProcessLogEvent event)
            throws IOException {
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = jsonFactory.createGenerator(writer)) {
            generator.writeStartObject();
            generator.writeStringField(
                "processInstanceID",
                event.getProcessIDs().get(0));
            generator.writeObjectField(
                "activityID",
                event.getActivityID());
            generator.writeNumberField(
                "activityStatus",
                event.hasActivityErrorTags() ?
                    ProcessActivityInstanceStatus.ERROR.value() :
                    ProcessActivityInstanceStatus.OK.value());
            generator.writeObjectField(
                "activityStartTime",
                event.getActivityStartTime() == null ?
                    null :
                    event.getActivityStartTime().getTime());
            generator.writeObjectField(
                "activityLastExecutionTime",
                event.getActivityLastExecutionTime() == null ?
                    null :
                    event.getActivityLastExecutionTime().getTime());
            generator.writeStringField("log", event.getLog());
            generator.writeObjectField(
                "logTime",
                event.getTime() == null ?
                    null :
                    event.getTime().getTime());
            generator.writeArrayFieldStart("tags");
            for (ProcessEventTag tag : event.getTags()) {
                generator.writeString(tag.value());
            }
            generator.writeEndArray();
            writeActivityStatesSnapshot(event, generator);
            generator.writeEndObject();
            generator.flush();
            return writer.toString();
        }
    }

    private void writeActivityStatesSnapshot(
            ProcessLogEvent event,
            JsonGenerator generator)
            throws IOException {
        ProcessModel.State modelState = event.getModelState();
        ProcessInstance instance =
            modelState.getInstance(event.getProcessIDs().get(0));

        if (instance == null)  {
            return;
        }

        ProcessModel model = (ProcessModel)modelState.getNode();
        List<Activity> activities = model.getActivities();
        generator.writeArrayFieldStart("activityStatesSnapshot");
        for (Activity activity : activities) {
            generator.writeStartObject();
            generator.writeNumberField("id", activity.getID());
            generator.writeFieldName("state");
            if (instance.getNodeState(activity).errorCount() > 0) {
                generator.writeNumber(
                    ProcessModelActivityState.ERROR.value());
            } else if (new Integer(activity.getID()).equals(event.getActivityID())) {
                if (event.containsTag(ProcessEventTag.UNFIT) &&
                        !event.containsTag(ProcessEventTag.TIME_ANOMALY)) {
                    generator.writeNumber(
                        ProcessModelActivityState.ERROR.value());
                } else {
                    generator.writeNumber(
                        ProcessModelActivityState.ACTIVE.value());
                }
            } else {
                if (instance.getNodeState(activity).getLastExecutionTime() == null) {
                    generator.writeNumber(
                        ProcessModelActivityState.INACTIVE.value());
                } else {
                    generator.writeNumber(
                        ProcessModelActivityState.COMPLETED.value());
                }
            }
            generator.writeEndObject();
        }
        generator.writeEndArray();
    }

    private JsonFactory jsonFactory;
}
