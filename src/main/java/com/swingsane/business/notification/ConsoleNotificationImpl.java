package com.swingsane.business.notification;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class ConsoleNotificationImpl implements INotification {

  private boolean interrupted = false;
  private Exception exception;

  @Override
  public void addAbortListener() {
  }

  @Override
  public final Exception getException() {
    return exception;
  }

  @Override
  public final boolean isInterrupted() {
    return interrupted;
  }

  @Override
  public final void message(final String message) {
    System.out.println(message);
  }

  @Override
  public final void setException(Exception exception) {
    this.exception = exception;
  }

  @Override
  public final void setInterrupted(boolean interrupted) {
    this.interrupted = interrupted;
  }

}
