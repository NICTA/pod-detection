package au.com.nicta.ssrg.pod.cfmchecker.io;

import au.com.nicta.ssrg.pod.cfmchecker.newcore.ConformanceCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

@Component
public class ProcessFileRepository
    extends JsonProcessRepository
    implements AutoCloseable {

    @Autowired
    public ProcessFileRepository(File processLogEventFile)
            throws IOException {
        this.processLogEventWriter = new FileWriter(processLogEventFile);
    }

    public void storeConformanceResult(ConformanceCheckResult result) {
        try {
            processLogEventWriter.write(jsonifyConformanceResult(result));
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
