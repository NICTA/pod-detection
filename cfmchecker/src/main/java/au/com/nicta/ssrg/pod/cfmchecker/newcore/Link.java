package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {
    @JsonCreator
    public Link(@JsonProperty("source") Node source,
                @JsonProperty("target") Node target) {
        this.source = source;
        this.target = target;
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }

    public LinkState createState() {
        return new LinkState(this, 0, 0, 0);
    }

    private Node source;
    private Node target;
}
