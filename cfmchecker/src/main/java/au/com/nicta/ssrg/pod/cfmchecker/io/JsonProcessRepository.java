package au.com.nicta.ssrg.pod.cfmchecker.io;

import au.com.nicta.ssrg.pod.cfmchecker.newcore.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public abstract class JsonProcessRepository
        implements ProcessRepository {
    public JsonProcessRepository() {
        jsonFactory = new JsonFactory();
        jsonFactory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }

    protected String jsonifyConformanceResult(ConformanceCheckResult result) throws IOException {
        StringWriter writer = new StringWriter();
        ProcessLogEvent event = (ProcessLogEvent)result.getEvent();

        try (JsonGenerator generator = jsonFactory.createGenerator(writer)) {
            generator.writeStartObject();

            // TODO: will be removed.
            generator.writeStringField(
                "processInstanceID",
                event.getProcessInstanceIDs().get(0));

            generator.writeArrayFieldStart("processInstanceIDs");
            for (String piid : event.getProcessInstanceIDs()) {
                generator.writeString(piid);
            }
            generator.writeEndArray();

            generator.writeObjectField(
                "activityID",
                event.getActivityID());
            generator.writeNumberField(
                "activityStatus",
                event.hasActivityErrorTags() ?
                    ProcessActivityInstanceStatus.ERROR.value() :
                    ProcessActivityInstanceStatus.OK.value()
            );

            // TODO: will be removed.
            generator.writeObjectField("activityStartTime", null);
            generator.writeObjectField("activityLastExecutionTime", null);

            generator.writeStringField("log", event.getLog());
            generator.writeObjectField(
                "logTime",
                event.getTime() == null ?
                    null :
                    event.getTime().getTime()
            );

            generator.writeArrayFieldStart("tags");
            for (ProcessEventTag tag : event.getTags()) {
                generator.writeString(tag.value());
            }
            generator.writeEndArray();

            // TODO: will be removed.
            writeActivityStatesSnapshot(result, generator);

            generator.writeArrayFieldStart("modelIDs");
            for (ProcessModel model : result.getModels()) {
                generator.writeNumber(model.getID());
            }
            generator.writeEndArray();

            generator.writeArrayFieldStart("processInstanceStates");
            for (ProcessContextSnapshot pcSnapshot :
                 result.getContextSnapshots()) {

                generator.writeStartArray(pcSnapshot.getNodeStateMap().size());
                for (NodeState nodeState : pcSnapshot.getNodeStateMap().values()) {
                    if (nodeState instanceof ActivityState) {
                        writeActivityState(generator, event, (ActivityState)nodeState);
                    }
                }
                generator.writeEndArray();
            }
            generator.writeEndArray();

            generator.writeEndObject();
            generator.flush();
            return writer.toString();
        }
    }

    private void writeActivityStatesSnapshot(ConformanceCheckResult result,
                                             JsonGenerator generator) throws IOException {
        if (result.getContextSnapshots().size() == 0) {
            return;
        }

        ProcessLogEvent event = (ProcessLogEvent)result.getEvent();
        Map<Node, NodeState> stateMap = result.getContextSnapshots()
                                              .get(0)
                                              .getNodeStateMap();

        generator.writeArrayFieldStart("activityStatesSnapshot");
        for (NodeState nodeState : stateMap.values()) {
            if (nodeState instanceof ActivityState) {
                writeActivityState(generator, event, (ActivityState)nodeState);
            }
        }
        generator.writeEndArray();
    }

    private void writeActivityState(JsonGenerator generator,
                                    ProcessLogEvent event,
                                    ActivityState activityState)
                                    throws IOException {
        generator.writeStartObject();

        int activityID = activityState.getNode().getID();
        generator.writeNumberField("id", activityID);

        generator.writeFieldName("state");
        if (activityState.getErrorCount() > 0) {
            generator.writeNumber(
                ProcessModelActivityState.ERROR.value());

        } else if (event.getActivityID().equals(activityID)) {
            if (event.hasTag(ProcessEventTag.UNFIT) &&
                    !event.hasTag(ProcessEventTag.TIME_ANOMALY)) {
                generator.writeNumber(
                    ProcessModelActivityState.ERROR.value());
            } else {
                generator.writeNumber(
                    ProcessModelActivityState.ACTIVE.value());
            }

        } else {
            if (activityState.getLastExecTime() == 0) {
                generator.writeNumber(
                    ProcessModelActivityState.INACTIVE.value());
            } else {
                generator.writeNumber(
                    ProcessModelActivityState.COMPLETED.value());
            }
        }

        generator.writeEndObject();
    }

    private JsonFactory jsonFactory;
}
