package au.com.nicta.ssrg.pod.cfmchecker.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({
    @Type(name="model", value=ProcessModel.class),
    @Type(name="activity", value=Activity.class),
    @Type(name="event-end", value=EndEvent.class),
    @Type(name="event-start", value=StartEvent.class),
    @Type(name="gateway-xor-join", value=XorJoinGateway.class),
    @Type(name="gateway-xor-split", value=XorSplitGateway.class)
})
@JsonIdentityInfo(generator=PropertyGenerator.class, property="id")
public abstract class Node {
    public abstract class State {
        public abstract Node getNode();

        public Date getFirstExecutionTime() {
            return firstExecutionTime;
        }

        public Date getLastExecutionTime() {
            return lastExecutionTime;
        }

        public Date getLastPullTime() {
            return lastPullTime;
        }

        public void updateLastExecutionTime() {
            lastExecutionTime = new Date();
            if (firstExecutionTime == null) {
                firstExecutionTime = lastExecutionTime;
            }
        }

        public void updateLastPullTime() {
            lastPullTime = new Date();
        }

        public int errorCount() {
            return errorCount;
        }

        public void incrementErrorCount() {
            ++errorCount;
        }

        @Override
        public String toString() {
            StringWriter writer = new StringWriter();
            JsonFactory factory = new JsonFactory();
            try (JsonGenerator generator = factory.createGenerator(writer)) {
                generator.writeStartObject();
                generator.writeFieldName("node");
                generator.writeRawValue(getNode().toString());
                generator.writeObjectField(
                    "lastExecutionTime",
                    lastExecutionTime == null ?
                        null :
                        lastExecutionTime.getTime());
                generator.writeObjectField(
                    "lastPullTime",
                    lastPullTime == null ?
                        null :
                        lastPullTime.getTime());
                generator.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return writer.toString();
        }

        private Date firstExecutionTime;
        private Date lastExecutionTime;
        private Date lastPullTime;
        private int errorCount = 0;
    }

    public abstract State newState();

    public abstract boolean execute(
        Node.State nodeState,
        List<Link.State> linkStatesIn,
        List<Link.State> linkStatesOut,
        ProcessContext context);

    public abstract boolean pull(
        Link.State pullLinkState,
        Node.State nodeState,
        List<Link.State> linkStatesIn,
        List<Link.State> linkStatesOut,
        ProcessContext context);

    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator generator = factory.createGenerator(writer)) {
            generator.writeStartObject();
            generator.writeNumberField("id", id);
            generator.writeEndObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }

    @JsonProperty(required=true)
    private int id;
}
