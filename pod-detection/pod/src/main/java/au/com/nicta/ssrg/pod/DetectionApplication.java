package au.com.nicta.ssrg.pod;

import java.util.*;
import org.apache.logging.log4j.*;
import org.restlet.*;
import org.restlet.resource.Finder;
import org.restlet.routing.*;

public class DetectionApplication extends Application {
    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        List<Process> processes = DetectionServiceContext.getCurrent().getProcesses();
        for (Process process : processes) {
            Set<Map.Entry<String, Step>> urlStepMapEntries = process.getUrlStepMap().entrySet();
            for (Map.Entry<String, Step> entry : urlStepMapEntries) {
                Step step = entry.getValue();
                step.setProcess(process);
                Context context = new Context();
                context.getAttributes().put(
                    AssertionServerResource.CONTEXT_KEY_OF_STEP,
                    step);
                Finder finder = new Finder(context, AssertionServerResource.class);
                router.attach(entry.getKey(), finder);
            }
        }
        return router;
    }
}
