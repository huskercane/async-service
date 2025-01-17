package com.terrydu.asyncservice.api;

public class HttpResponse {

  final String threadLocalTenantName;
  final String response;

  public HttpResponse(String response, String t) {
    this.threadLocalTenantName = t;
    this.response = response;
  }

  public String getThreadLocalTenantName() {
    return threadLocalTenantName;
  }

  public String getResponse() {
    return response;
  }
}
