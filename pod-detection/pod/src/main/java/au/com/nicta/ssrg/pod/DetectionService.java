package au.com.nicta.ssrg.pod;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.restlet.*;
import org.restlet.data.Protocol;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.*;

public class DetectionService extends Component {
    public static void main(String[] args) throws Exception {
        DetectionServiceContext.init();
        DetectionService service = new DetectionService();
        service.getServers().add(Protocol.HTTP, DetectionServiceContext.getCurrent().getPort());
        service.getDefaultHost().attachDefault(new DetectionApplication());
        service.start();
    }
}
