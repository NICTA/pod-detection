package au.com.nicta.ssrg.pod.cfmchecker.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.nicta.ssrg.pod.cfmchecker.core.Activity;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessEventTag;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessInstance;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessLogEvent;
import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessModel;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProcessFileRepository
    extends JsonProcessRepository
    implements AutoCloseable {

    @Autowired
    public ProcessFileRepository(File processLogEventFile)
            throws IOException {
        this.processLogEventWriter = new FileWriter(processLogEventFile);
    }

    public void storeLogEvent(ProcessLogEvent event) {
        try {
            processLogEventWriter.write(convertEventToJson(event));
            processLogEventWriter.write(System.lineSeparator());
            processLogEventWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        try {
            processLogEventWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Writer processLogEventWriter;
}
