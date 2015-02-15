package com.swingsane.business.scanning;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.SaneOption;
import au.com.southsky.jfreesane.SanePasswordProvider;
import au.com.southsky.jfreesane.SaneSession;
import au.com.southsky.jfreesane.SaneStatus;
import au.com.southsky.jfreesane.ScanListener;

import com.swingsane.business.notification.ConsoleNotificationImpl;
import com.swingsane.business.notification.INotification;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.model.Scanner;

public class ScanJob {

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(ScanJob.class);

  /**
   * Event Listener List for Scan Events.
   */
  private EventListenerList listenerList = new EventListenerList();

  /**
   * Scan Event.
   */
  private ScanEvent scanEvent;

  /**
   * Status notification with console output as default implementation.
   */
  private INotification notification = new ConsoleNotificationImpl();

  private Scanner scanner;

  private SwingWorker<Void, Void> worker;

  private int pagesToScan = Integer.MAX_VALUE;

  private IScanService scanService;

  private boolean useADF;

  private String batchPrefix;

  public ScanJob(IScanService scanServiceImpl, Scanner scanner) {
    scanService = scanServiceImpl;
    this.scanner = scanner;
  }

  public final void acquire(final ScanListener rateLimitedScanListener) {

    worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {

        getNotificaiton().message(Localizer.localize("ScanStartingMessage"));

        String hostAddrStr = scanner.getRemoteAddress();
        int port = scanner.getRemotePortNumber();

        SaneSession session = null;
        SaneDevice device = null;

        try {
          session = SaneSession.withRemoteSane(InetAddress.getByName(hostAddrStr), port);
          session.setPasswordProvider(getPasswordProvider());
          device = session.getDevice(scanner.getName());

          try {
            if (!(device.isOpen())) {
              device.open();
            }
          } catch (IOException e) {
            getNotificaiton().message(e.getLocalizedMessage());
            LOG.warn(e, e);
            return null;
          }

          try {
            scanService.configure(device, scanner);
          } catch (IOException ex) {
            getNotificaiton().message(ex.getLocalizedMessage());
            LOG.warn(ex, ex);
            // continue if we can't set a setting.
          }

          int pageCount = 1;
          while (!isCancelled()) {
            try {
              BufferedImage image = device.acquireImage(rateLimitedScanListener);
              getNotificaiton().message(
                  String.format(Localizer.localize("ScannedPageMessage"), pageCount));
              fireScanEvent(image, pageCount);
            } catch (SaneException e) {
              if (e.getStatus() == SaneStatus.STATUS_NO_DOCS) {
                break;
              } else {
                getNotificaiton().message(e.getLocalizedMessage());
                LOG.warn(e, e);
                break;
              }
            } catch (IOException e) {
              getNotificaiton().message(e.getLocalizedMessage());
              LOG.warn(e, e);
              break;
            }
            if (!useADF && (pageCount >= pagesToScan)) {
              break;
            }
            pageCount++;
          }
        } catch (UnknownHostException e) {
          getNotificaiton().message(e.getLocalizedMessage());
          LOG.warn(e, e);
          return null;
        } catch (IOException e) {
          getNotificaiton().message(e.getLocalizedMessage());
          LOG.warn(e, e);
          return null;
        } finally {
          try {
            if ((device != null) && device.isOpen()) {
              device.close();
            }
          } catch (IOException e) {
            getNotificaiton().message(e.getLocalizedMessage());
            LOG.warn(e, e);
          }
          if (session != null) {
            session.close();
          }
        }
        return null;
      }

      @Override
      protected void done() {

        try {
          get();
          getNotificaiton().message(Localizer.localize("ScanCompleteMessage"));
        } catch (InterruptedException e) {
          getNotificaiton().message(e.getLocalizedMessage());
          LOG.warn(e, e);
        } catch (ExecutionException e) {
          getNotificaiton().message(e.getLocalizedMessage());
          LOG.warn(e, e);
        } catch (CancellationException e) {
          getNotificaiton().message(Localizer.localize("ScanCancelledMessage"));
        }

        fireScanEvent(null, 0);

      }
    };

    worker.execute();

  }

  public final void addScanListener(final ScanEventListener listener) {
    listenerList.add(ScanEventListener.class, listener);
  }

  public final void cancel() {
    worker.cancel(true);
  }

  public final void checkOptions() throws Exception {

    String hostAddr = scanner.getRemoteAddress();
    int port = scanner.getRemotePortNumber();
    SaneSession session = null;
    SaneDevice device = null;

    try {
      session = SaneSession.withRemoteSane(InetAddress.getByName(hostAddr), port);
      session.setPasswordProvider(getPasswordProvider());
      device = session.getDevice(scanner.getName());
      if (!(device.isOpen())) {
        device.open();
      }
      scanService.configure(device, scanner);
      scanService.setScannerOptions(device, scanner);
    } catch (UnknownHostException e) {
      LOG.warn(e, e);
      throw e;
    } catch (SaneException e) {
      LOG.warn(e, e);
      throw e;
    } catch (IOException e) {
      LOG.warn(e, e);
      throw e;
    } finally {
      try {
        if ((device != null) && device.isOpen()) {
          device.close();
        }
      } catch (IOException e) {
        LOG.warn(e, e);
      }
      if (session != null) {
        try {
          session.close();
        } catch (IOException e) {
          LOG.warn(e, e);
        }
      }
    }

  }

  private void fireScanEvent(final BufferedImage bufferedImage, int pageNumber) {

    Object[] listeners = listenerList.getListenerList();

    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ScanEventListener.class) {
        if (scanEvent == null) {
          scanEvent = new ScanEvent(this);
        }
        scanEvent.setAcquiredImage(bufferedImage);
        scanEvent.setPagesToScan(pagesToScan);
        scanEvent.setPageNumber(pageNumber);
        scanEvent.setBatchPrefix(getBatchPrefix());
        ((ScanEventListener) listeners[i + 1]).eventOccurred(scanEvent);
      }
    }

  }

  private String getBatchPrefix() {
    return batchPrefix;
  }

  /**
   * Returns an instance of INotification if set.
   *
   * @return an instance of INotification if set.
   */
  public final INotification getNotificaiton() {
    return notification;
  }

  private SanePasswordProvider getPasswordProvider() {
    return scanService.getPasswordProvider();
  }

  public final boolean isActive() {
    return !(worker.isDone());
  }

  /**
   * @param listener
   *          a scan listener
   */
  public final void removeScanEventListener(final ScanEventListener listener) {
    listenerList.remove(ScanEventListener.class, listener);
  }

  public final void setBatchPrefix(String batchPrefix) {
    this.batchPrefix = batchPrefix;
  }

  public final void setButtonValue(String key) {
    String hostAddr = scanner.getRemoteAddress();
    int port = scanner.getRemotePortNumber();
    SaneSession session = null;
    SaneDevice device = null;

    try {
      session = SaneSession.withRemoteSane(InetAddress.getByName(hostAddr), port);
      session.setPasswordProvider(getPasswordProvider());
      device = session.getDevice(scanner.getName());

      if (!(device.isOpen())) {
        device.open();
      }

      SaneOption saneOption = device.getOption(key);
      saneOption.setButtonValue(); // TODO: BUG??? doesn't do anything???

    } catch (UnknownHostException e) {
      LOG.warn(e, e);
    } catch (SaneException e) {
      LOG.warn(e, e);
    } catch (IOException e) {
      LOG.warn(e, e);
    } finally {
      try {
        if ((device != null) && device.isOpen()) {
          device.close();
        }
      } catch (IOException e) {
        LOG.warn(e, e);
      }
      if (session != null) {
        try {
          session.close();
        } catch (IOException e) {
          LOG.warn(e, e);
        }
      }
    }

  }

  /**
   * Sets an instance of INotification.
   *
   * @param notificationImpl
   *          an instance of INotification
   */
  public final void setNotificaiton(final INotification notificationImpl) {
    notification = notificationImpl;
  }

  public final void setPagesToScan(int pagesToScan) {
    this.pagesToScan = pagesToScan;
  }

  public final void setUseADF(boolean useADF) {
    this.useADF = useADF;
  }

}
