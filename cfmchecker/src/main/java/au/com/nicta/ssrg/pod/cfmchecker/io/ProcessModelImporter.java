package au.com.nicta.ssrg.pod.cfmchecker.io;

import java.io.File;
import java.io.IOException;

import au.com.nicta.ssrg.pod.cfmchecker.core.ProcessModel;

import com.fasterxml.jackson.databind.ObjectMapper;

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
