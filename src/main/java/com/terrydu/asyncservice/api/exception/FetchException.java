package com.terrydu.asyncservice.api.exception;

public class FetchException extends RuntimeException{

  public FetchException() {
    super();
  }

  public FetchException(String message) {
    super(message);
  }

  public FetchException(String message, Throwable cause) {
    super(message, cause);
  }

  public FetchException(Throwable cause) {
    super(cause);
  }

  protected FetchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
