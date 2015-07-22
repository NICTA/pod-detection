package au.com.nicta.ssrg.pod;

import java.util.Map;

public class Process {
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String name) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setUrlStepMap(Map<String, Step> urlStepMap) {
        this.urlStepMap = urlStepMap;
    }

    public Map<String, Step> getUrlStepMap() {
        return urlStepMap;
    }

    private String name;
    private String type;
    private Map<String, Step> urlStepMap;
}
