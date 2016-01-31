package au.com.nicta.ssrg.pod.cfmchecker.io;

import org.apache.commons.lang3.StringUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexLogInfoExtractor
        implements LogInfoExtractor {

    public RegexLogInfoExtractor(
            String processIDRegex,
            int processIDRegexGroupIndex,
            String defaultRootProcessID,
            List<ActivityPattern> activityPatterns,
            String timeRegex,
            int timeRegexGroupIndex,
            String timeFormatPattern) {
        if (processIDRegex == null) {
            this.processIDPattern = null;
        } else {
            this.processIDPattern = Pattern.compile(processIDRegex);
        }
        this.processIDRegexGroupIndex = processIDRegexGroupIndex;
        this.defaultRootProcessID = defaultRootProcessID;
        this.activityPatterns = new ArrayList<>(activityPatterns);
        this.timePattern = Pattern.compile(timeRegex);
        this.timeRegexGroupIndex = timeRegexGroupIndex;
        this.dateFormat = new SimpleDateFormat(timeFormatPattern);
    }

    @Override
    public List<String> extractProcessIDs(String log, String rootID) {
        List<String> extractedIDs = new ArrayList<>();
        if (!StringUtils.isBlank(rootID)) {
            extractedIDs.add(rootID);
        } else if (defaultRootProcessID != null) {
            extractedIDs.add(defaultRootProcessID);
        }
        if (processIDPattern != null) {
            Matcher matcher = processIDPattern.matcher(log);
            while (matcher.find()) {
                extractedIDs.add(matcher.group(processIDRegexGroupIndex));
            }
        }
        return extractedIDs;
    }

    @Override
    public List<String> extractProcessIDs(String log) {
        return extractProcessIDs(log, null);
    }

    @Override
    public String extractActivityName(String log) {
        for (ActivityPattern activityPattern : activityPatterns) {
            Matcher matcher  = activityPattern.getPattern().matcher(log);
            if (matcher.matches()) {
                return activityPattern.getActivityName();
            }
        }
        return null;
    }

    @Override
    public Date extractTime(String log) {
        Matcher matcher = timePattern.matcher(log);
        if (matcher.find()) {
            String matchedTime = matcher.group(timeRegexGroupIndex);
            return dateFormat.parse(matchedTime, new ParsePosition(0));
        }
        return null;
    }

    private Pattern processIDPattern;
    private int processIDRegexGroupIndex;
    private String defaultRootProcessID;
    private List<ActivityPattern> activityPatterns;
    private Pattern timePattern;
    private int timeRegexGroupIndex;
    private SimpleDateFormat dateFormat;
}
