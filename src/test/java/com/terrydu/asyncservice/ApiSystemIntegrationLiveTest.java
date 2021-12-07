package com.terrydu.asyncservice;

//import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

//import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@WireMockTest(httpPort = 8083)
class ApiSystemIntegrationLiveTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiSystemIntegrationLiveTest.class);
    private static final String SERVICE_URL = "http://localhost:8083/api/ping";

    private static final String SPRING_MVC = "http://localhost:8083/api/mvc/async/tenant/ZzssDD";
    private static final String JERSEY = "http://localhost:8083/api/jersey/async/tenant/ZzssDD";
    private static final String SERVLET = "http://localhost:8083/api/servlet/async/tenant\\?name=ZzssDD";

    @Disabled("Only run this when the stand-alone service is running!")
    @Test
    void getPing() throws IOException {
//        stubFor(get("/api/ping").willReturn(badRequest()));

        final HttpUriRequest request = new HttpGet(SERVICE_URL);

        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

//    @Test
//    void testWireMockSanity() throws IOException {
//        stubFor(get("/api/ping").willReturn(ok()));
//
//        final HttpUriRequest request = new HttpGet(SERVICE_URL);
//
//        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
//
//        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
//    }
    @Test
    void getSpringMvc() throws IOException {
        final HttpUriRequest request = new HttpGet(SPRING_MVC);

        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }
    @Test
    void testSpringMvcWireMock() throws IOException {
//        stubFor(get("/api/terrydu-wait15").willReturn(ok()));
        final HttpUriRequest request = new HttpGet(SPRING_MVC);

        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }
    @Test
    void getjersey() throws IOException {
        final HttpUriRequest request = new HttpGet(JERSEY);

        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }
    @Test
    void getAsyncServlet() throws IOException {
        final HttpUriRequest request = new HttpGet(SERVLET);

        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }
}
