package au.com.nicta.ssrg.pod.cfmchecker.io;

import java.util.Date;
import java.util.List;

public interface LogInfoExtractor {
    List<String> extractProcessIDs(String log);
    List<String> extractProcessIDs(String log, String rootID);
    String extractActivityName(String log);
    Date extractTime(String log);
}
