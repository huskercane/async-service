package async.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private String baseUrl = null;
    private int threadCount = 0;

    public static void main(String[] args) {
        App app = new App();
        app.parseInputs(args);
        app.start();
    }

    public void parseInputs(String[] args) {
        for (int i=0; i < args.length; i++) {
            if ("-url".equalsIgnoreCase(args[i])) {
                i++;
                if (i >= args.length) {
                    logger.error("Missing input after -url");
                    System.exit(1);
                }
                baseUrl = args[i];

            } else if ("-count".equalsIgnoreCase(args[i])) {
                i++;
                if (i >= args.length) {
                    logger.error("Missing input after -count");
                    System.exit(1);
                }
                try {
                    threadCount = Integer.parseInt(args[i]);
                } catch (NumberFormatException ex) {
                    logger.error("ERROR - the argument after '-count' is not a number!");
                    System.exit(1);
                }
            }
        }

        if (baseUrl == null || threadCount == 0) {
            logger.error("Error - missing inputs");
            System.exit(1);
        }

        if (!baseUrl.toLowerCase().startsWith("http")) {
            logger.error("Error - the url is invalid");
            System.exit(1);
        }
    }

    public void start() {
        ExecutorService executor= Executors.newFixedThreadPool(threadCount);
        try {
            for (int i = 0; i < threadCount; i++) {
                String tenantName = "Customer" + i;
                executor.execute(new ApiCaller(baseUrl + tenantName));
            }
        } catch (Exception err){
            err.printStackTrace();
        }
        executor.shutdown(); // once you are done with ExecutorService
    }

}

class ApiCaller implements Runnable {
    String url;

    public ApiCaller(String url) {
        this.url = url;
    }
    private static final Logger logger = LoggerFactory.getLogger(ApiCaller.class);
    public void run() {
        logger.info("Calling {}", url);
        final HttpUriRequest request = new HttpGet(url);

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse httpResponse = httpclient.execute(request)) {
                int responseCode = httpResponse.getCode();
                if (responseCode != HttpStatus.SC_OK) {
                    logger.error("Got a bad response after calling {}", url);
                } else {
                    HttpEntity entity = httpResponse.getEntity();
                    String response = readEntity(entity);
                    logger.info("Got response after calling {} : {}", url,  response);
                }
            }
        } catch (IOException ex) {
            logger.error("ERROR in ApiCaller calling {}. Details: {}", url, ex.getMessage());
        }
    }

    private String readEntity(HttpEntity entity) {
        try {
            return EntityUtils.toString(entity);
        } catch (ParseException | IOException ex) {
            logger.error("Error parsing response after calling {}", url);
        }

        return "<ERROR>";
    }

}
