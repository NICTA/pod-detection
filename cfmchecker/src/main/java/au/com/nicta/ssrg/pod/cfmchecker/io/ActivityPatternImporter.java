package au.com.nicta.ssrg.pod.cfmchecker.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ActivityPatternImporter {
    public ActivityPatternImporter(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<ActivityPattern> parse() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
            sourceFile,
            new TypeReference<List<ActivityPattern>>() {});
    }

    private File sourceFile;
}
