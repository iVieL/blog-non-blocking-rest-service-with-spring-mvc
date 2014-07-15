package se.callista.springmvc.asynch.pattern.router.nonblocking.callback;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.ning.http.client.AsyncHttpClient;
import se.callista.springmvc.asynch.common.log.LogHelper;
import se.callista.springmvc.asynch.common.log.LogHelperFactory;

import javax.annotation.PostConstruct;

@RestController
public class RouterNonBlockingCallbackController {

    private LogHelper LOG;

    private AsyncHttpClient   asyncHttpClient    = new AsyncHttpClient();

    @Autowired
    private LogHelperFactory logFactory;

    @Value("${sp.non_blocking.url}")
    private String SP_NON_BLOCKING_URL;

    @PostConstruct
    public void initAfterInject() {
        LOG = logFactory.getLog(RouterNonBlockingCallbackController.class, "router");
    }

    /**
     * Sample usage: curl "http://localhost:9080/route-non-blocking?minMs=1000&maxMs=2000"
     *
     * @param minMs
     * @param maxMs
     * @return
     * @throws IOException
     */
    @RequestMapping("/route-non-blocking")
    public DeferredResult<String> nonBlockingRouter(
        @RequestParam(value = "minMs", required = false, defaultValue = "0") int minMs,
        @RequestParam(value = "maxMs", required = false, defaultValue = "0") int maxMs) throws IOException {

        LOG.logStartNonBlocking();

        DeferredResult<String> deferredResult = new DeferredResult<String>();

        asyncHttpClient.prepareGet(SP_NON_BLOCKING_URL + "?minMs=" + minMs + "&maxMs=" + maxMs).execute(
            new RouterCallback(LOG, deferredResult));

        LOG.logLeaveThreadNonBlocking();

        // Return to let go of the precious thread we are holding on to...
        return deferredResult;
    }
}