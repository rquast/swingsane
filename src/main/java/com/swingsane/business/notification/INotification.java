package com.swingsane.business.notification;

public interface INotification {

  void addAbortListener();

  Exception getException();

  boolean isInterrupted();

  void message(String message);

  void setException(Exception exception);

  void setInterrupted(boolean interrupted);

}
