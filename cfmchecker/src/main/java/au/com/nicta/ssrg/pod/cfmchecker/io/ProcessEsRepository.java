package au.com.nicta.ssrg.pod.cfmchecker.io;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.stereotype.Component;

import au.com.nicta.ssrg.pod.cfmchecker.core.Activity;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessEventTag;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessInstance;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessLogEvent;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessModel;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessEsRepository
        extends JsonProcessRepository
        implements AutoCloseable {
    public ProcessEsRepository(
            String esCluster,
            String esIndex,
            String logEventEsType) {
        super();
        this.esIndex = esIndex;
        this.logEventEsType = logEventEsType;
        esNode = NodeBuilder.nodeBuilder().clusterName(esCluster).node();
    }

    public void storeLogEvent(ProcessLogEvent event) {
        try {
            String source = convertEventToJson(event);
            Client esClient = esNode.client();
            IndexResponse response = esClient.
                prepareIndex(esIndex, logEventEsType).
                setSource(source).
                execute().
                actionGet();
            if (response.isCreated()) {
                System.out.printf(
                    "Successfully stored log event. event=%s%n",
                    source);
            } else {
                StringWriter writer = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(writer, response.getHeaders());
                System.out.printf(
                    "Failed to store log event. event=%s, headers=%s",
                    source,
                    writer.toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        esNode.close();
    }

    private Node esNode;
    private String esIndex;
    private String logEventEsType;
}
