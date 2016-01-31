package au.com.nicta.ssrg.pod.cfmchecker.io;

import au.com.nicta.ssrg.pod.cfmchecker.newcore.ProcessModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ProcessModelImporter {
    public ProcessModelImporter(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public ProcessModel parse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(sourceFile, ProcessModel.class);
    }

    private File sourceFile;
}
