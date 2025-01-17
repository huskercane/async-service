package com.terrydu.asyncservice.api;

import static org.asynchttpclient.Dsl.config;

import com.terrydu.asyncservice.api.exception.FetchException;
import io.reactivex.rxjava3.core.Observable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HttpService {

  private static final Logger logger = LoggerFactory.getLogger(HttpService.class);

  private String responseFromHttpCall(String httpsUrl) throws IOException {
    URL myUrl = new URL(httpsUrl);
    HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
    conn.setConnectTimeout(120000);
    conn.setReadTimeout(120000);
    InputStream is = conn.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);

    String inputLine;

    StringBuilder stringBuilder = new StringBuilder();

    while ((inputLine = br.readLine()) != null) {
      stringBuilder.append(inputLine);
    }

    return stringBuilder.toString();
  }

  interface Operation {

    void run(String x);
  }

  private void responseFromAsyncHttpCall(String httpsUrl, Operation xx){
    AsyncHttpClient client = Dsl.asyncHttpClient(config().setIoThreadsCount(50));
    Request getRequest = Dsl.get(httpsUrl).build();

    client.executeRequest(getRequest, new AsyncCompletionHandler<String>() {

      @Override
      public String onCompleted(Response response) throws Exception {
        String responseBody = response.getResponseBody();
        xx.run(responseBody);

        client.close();

        return responseBody;
      }
    });
  }

  /**
   * Fetches data from a API
   *
   * @param tenantName tenant name of entity to delete.
   * @param httpsUrl   url to fetch.
   * @return Observable that will receive completion, or exception if error occurs.
   */
  public Observable<HttpResponse> fetchDataSync(String tenantName, String httpsUrl) {
    return Observable.create(inSource -> {
      logger.info("Calling Terry URL, tenant: {} on thread {}", tenantName, Thread.currentThread().getName());
      String response = "<ERROR>";
      try {
        response = responseFromHttpCall(httpsUrl);
      } catch (IOException e) {
        logger.error("Error calling HttpService", e);
        inSource.onError(new FetchException("Error Fetching Data from the URL", e));
      }
      HttpResponse value = new HttpResponse(response, tenantName);
      inSource.onNext(value);
      inSource.onComplete();
    });
  }

  /**
   * Fetches data from a API
   *
   * @param tenantName tenant name of entity to delete.
   * @param httpsUrl   url to fetch.
   * @return Observable that will receive completion, or exception if error occurs.
   */
  public Observable<HttpResponse> fetchData(String tenantName, String httpsUrl) {
    return Observable.create(inSource -> {
      logger.info("Calling Terry URL, tenant: {}", tenantName);
      responseFromAsyncHttpCall(httpsUrl, x -> {
            HttpResponse value = new HttpResponse(x, tenantName);
            inSource.onNext(value);
            inSource.onComplete();
          }
      );
    });
  }

}
