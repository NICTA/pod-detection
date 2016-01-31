package au.com.nicta.ssrg.pod.cfmchecker.io;

import au.com.nicta.ssrg.pod.cfmchecker.newcore.ConformanceCheckResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;
import java.io.StringWriter;

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

    public void storeConformanceResult(ConformanceCheckResult result) {
        try {
            String source = jsonifyConformanceResult(result);
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
