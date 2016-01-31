package au.com.nicta.ssrg.pod.cfmchecker.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping(value="/activity")
public class ActivityController {
    @Autowired
    @SuppressWarnings("unchecked")
    public ActivityController(ApplicationContext context) {
        processEventQueue = (BlockingQueue<ProcessLogEvent>)
            context.getBean("processEventQueue");
    }

    @RequestMapping("exec")
    public void executeActivityByLog(
            @RequestParam(required=true) String log,
            @RequestParam(required=false) String procInstID) {
        ProcessLogEvent event =
            new ProcessLogEvent(log, logInfoExtractor, new Date(), procInstID);
        if (!processEventQueue.offer(event)) {
            throw new ProcessEventQueueException();
        }
    }

    @ExceptionHandler(ProcessEventQueueException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void handleProcessEventQueueException(
            ProcessEventQueueException ex) {}

    @Autowired
    private LogInfoExtractor logInfoExtractor;

    private BlockingQueue<ProcessLogEvent> processEventQueue;

    private class ProcessEventQueueException extends RuntimeException {}
}
