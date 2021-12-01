package com.terrydu.asyncservice.api.jersey.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NOTE: Whenever you add a new Controller class, be sure to update JerseyConfig.java!
 */
// This is relative to http://hostname/api/jersey
@Path("/ping")
public class PingController {
  private static final Logger logger = LoggerFactory.getLogger(PingController.class);

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public String ping() {
    logger.info("Handling request for '/api/jersey/ping' on thread {}", Thread.currentThread().getName());
    return "pong";
  }
}
