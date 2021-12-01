package com.terrydu.asyncservice.api.mvc.controller;

import static com.terrydu.asyncservice.api.Constant.SERVICE_URL_15;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * http://localhost:8083/api/mvc/sync/tenant/{tenantName}
 */
@RestController
public class SyncTenantController {
  private static final Logger logger = LoggerFactory.getLogger(SyncTenantController.class);

  /**
   * NOTE: This path mapping is relative to "/api/mvc"
   */
  @GetMapping("/sync/tenant/{tenantName}")
  public String syncTenant(@PathVariable String tenantName) {
    logger.info("Tenant {} : Handling request for '/api/jersey/sync/tenant/", tenantName);
    long startTime = System.currentTimeMillis();

    ThreadLocal<String> threadLocalTenantName = new ThreadLocal<>();
    threadLocalTenantName.set(tenantName);

    // Call external API that takes 2 minutes here.
    final HttpUriRequest request = new HttpGet(SERVICE_URL_15);

    String response = "";
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      try (CloseableHttpResponse httpResponse = httpclient.execute(request)) {
        int statusCode = httpResponse.getCode();
        if (statusCode != HttpStatus.SC_OK) {
          logger.error("Tenant {}: ERROR from the external network service call! Status returned: {}", tenantName, statusCode);
        } else {
          HttpEntity entity = httpResponse.getEntity();
          try {
            response = EntityUtils.toString(entity);
          } catch (ParseException ex) {
            logger.error("Tenant {}: Error parsing response after calling {}", tenantName, SERVICE_URL_15);
          }

        }
      }
    } catch (IOException ex) {
      logger.error("Tenant {}: ERROR waiting for the external network service call!", tenantName, ex);
    }

    if (!tenantName.equals(threadLocalTenantName.get())) {
      logger.error("Tenant {}: ERROR - The value in thread local storage ({}) does not match the correct value ({})", tenantName, threadLocalTenantName.get(), tenantName);
    }

    long endTime = System.currentTimeMillis();
    long timeElapsed = endTime - startTime;
    logger.info("Tenant {}: Completing request for '/api/jersey/sync/tenant/{}' taking {} ms", tenantName, tenantName, timeElapsed);
    return threadLocalTenantName.get() + "-" + response;
  }
}
