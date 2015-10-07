package au.com.nicta.ssrg.pod.cfmchecker.io;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
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
        extends JsonProcessRepository {
    public ProcessEsRepository(
            String esCluster,
            String esIndex,
            String logEventEsType) {
        super();
        this.esIndex = esIndex;
        this.logEventEsType = logEventEsType;
        esTransportAddresss = new InetSocketTransportAddress("localhost", 9300);
        esSettings = ImmutableSettings.settingsBuilder().put("cluster.name", esCluster).build();
    }

    public void storeLogEvent(ProcessLogEvent event) {
        try {
            String source = convertEventToJson(event);
            try (Client esClient = getEsClient()) {
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
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Client getEsClient() {
        return new TransportClient(esSettings).
            addTransportAddress(esTransportAddresss);
    }

    private String esIndex;
    private String logEventEsType;
    private Settings esSettings;
    private InetSocketTransportAddress esTransportAddresss;
}
