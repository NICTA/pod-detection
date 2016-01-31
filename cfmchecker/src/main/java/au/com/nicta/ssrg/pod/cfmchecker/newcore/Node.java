package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({
    @Type(name="model", value=ProcessModel.class),
    @Type(name="activity", value=Activity.class),
    @Type(name="event-end", value=EndEvent.class),
    @Type(name="event-start", value=StartEvent.class),
    @Type(name="gateway-xor-join", value=XorJoinGateway.class),
    @Type(name="gateway-xor-split", value=XorSplitGateway.class),
    @Type(name="gateway-and-join", value=AndJoinGateway.class),
    @Type(name="gateway-and-split", value=AndSplitGateway.class)
})
@JsonIdentityInfo(generator=PropertyGenerator.class, property="id")
public abstract class Node {
    public Node() {
    }

    public abstract NodeState createState();

    public int getID() {
        return id;
    }

    @JsonProperty(required=true)
    private int id;
}
