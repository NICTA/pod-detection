package au.com.nicta.ssrg.pod.cfmchecker.newcore;

import java.util.List;

public interface ProcessEvent {
    String getActivityName();
    List<String> getProcessInstanceIDs();
    void tag(ProcessEventTag tag);
    void setActivityID(int id);
}
