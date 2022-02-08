package com.datn.doffice.exceptions;

public class MailHandleException extends BaseException {

  private static final long serialVersionUID = 5264416645456329125L;

  public MailHandleException() {
    super();
  }

  public MailHandleException(String msg, Exception e) {
    super(msg, e);
  }
}
