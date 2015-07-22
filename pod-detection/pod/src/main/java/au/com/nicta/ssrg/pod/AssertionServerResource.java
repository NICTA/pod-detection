package au.com.nicta.ssrg.pod;

import org.restlet.representation.*;
import org.restlet.resource.*;

public class AssertionServerResource extends ServerResource {
    public static final String CONTEXT_KEY_OF_STEP = "__NICTA_POD_DETECTION_STEP__";

    @Override
    protected Representation get() throws ResourceException {
        Step step = (Step)getContext().getAttributes().get(CONTEXT_KEY_OF_STEP);
        step.start(getRequestAttributes());
        return new StringRepresentation("OK");
    }
}
