package com.swingsane.business.discovery;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.SanePasswordProvider;
import au.com.southsky.jfreesane.SaneSession;

import com.swingsane.business.notification.ConsoleNotificationImpl;
import com.swingsane.business.notification.INotification;
import com.swingsane.business.scanning.IScanService;
import com.swingsane.business.scanning.ScanServiceImpl;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.model.SaneServiceIdentity;
import com.swingsane.preferences.model.Scanner;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class DiscoveryJob {

  /**
   * Event Listener List for Discovery Events.
   */
  private EventListenerList listenerList = new EventListenerList();

  /**
   * Discovery Event.
   */
  private DiscoveryEvent discoveryEvent;

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(DiscoveryJob.class);

  /**
   * SANE service name.
   */
  public static final String SANE_SERVICE_NAME = "_sane-port._tcp.local.";

  /**
   * Status notification with console output as default implementation.
   */
  private INotification notification = new ConsoleNotificationImpl();

  private SwingWorker<ArrayList<Scanner>, Void> worker;

  private IScanService scanService;

  /**
   * Constructor for SaneDiscovery.
   *
   * @param scanService
   */
  public DiscoveryJob(IScanService scanService) {
    this.scanService = scanService;
  }

  /**
   * @param listener
   *          a discovery listener
   */
  public final void addDiscoveryListener(final DiscoveryListener listener) {
    listenerList.add(DiscoveryListener.class, listener);
  }

  public final void cancel() {
    worker.cancel(true);
  }

  private void detectScanners(JmDNS jmdns, InetAddress address,
      ArrayList<Scanner> discoveredScanners, ServiceInfo serviceInfo) throws IOException,
      SaneException {

    getNotificaiton().message(
        String.format(Localizer.localize("QueryingServerMessage"), serviceInfo.getName() + " ("
            + address.getHostAddress() + ":" + serviceInfo.getPort())
            + ")");

    if (worker.isCancelled()) {
      return;
    }

    SaneSession session = null;

    try {
      session = SaneSession.withRemoteSane(address, serviceInfo.getPort());
      session.setPasswordProvider(getPasswordProvider());
      List<SaneDevice> devices = session.listDevices();

      if ((devices != null) && (devices.size() > 0)) {
        getNotificaiton().message(
            String.format(devices.size() > 1 ? Localizer.localize("FoundDevicesMessage")
                : Localizer.localize("FoundDeviceMessage"), devices.size()));
        for (SaneDevice device : devices) {
          if (worker.isCancelled()) {
            return;
          }
          try {
            Scanner scanner = scanService.create(device, serviceInfo, address.getHostAddress());
            discoveredScanners.add(scanner);
          } catch (Exception ex) {
            getNotificaiton().message(
                address.getHostAddress() + ":" + serviceInfo.getPort() + " - "
                    + ex.getLocalizedMessage());
          }
        }
      }

    } catch (SocketException ex) {
      getNotificaiton()
          .message(
              address.getHostAddress() + ":" + serviceInfo.getPort() + " - "
                  + ex.getLocalizedMessage());
    } finally {
      if (session != null) {
        session.close();
      }
      try {
        jmdns.unregisterAllServices();
      } catch (Exception e) {
        getNotificaiton().message(e.getLocalizedMessage());
        LOG.error(e, e);
      }
      try {
        jmdns.close();
      } catch (IOException e) {
        getNotificaiton().message(e.getLocalizedMessage());
        LOG.error(e, e);
      }
    }
  }

  /**
   * Perform discovery of SANE scanners.
   *
   * @param scanService
   *          an instance of {@link ScanServiceImpl}
   */
  public final synchronized void discover() {

    worker = new SwingWorker<ArrayList<Scanner>, Void>() {

      @Override
      protected ArrayList<Scanner> doInBackground() throws Exception {

        getNotificaiton().message(Localizer.localize("DiscoverSanedServersMessage"));

        JmDNS jmdns = JmDNS.create(getLocalAddress());
        ServiceInfo[] serviceInfoArr = jmdns.list(getSaneServiceIdentity().getServiceName());

        ArrayList<Scanner> discoveredScanners = new ArrayList<Scanner>();

        for (ServiceInfo serviceInfo : serviceInfoArr) {

          if (isCancelled()) {
            return discoveredScanners;
          }

          // try IPV4 addresses before IPV6 ones.
          for (Inet4Address address : serviceInfo.getInet4Addresses()) {
            detectScanners(jmdns, address, discoveredScanners, serviceInfo);
          }

          if (discoveredScanners.size() <= 0) {
            for (Inet6Address address : serviceInfo.getInet6Addresses()) {
              detectScanners(jmdns, address, discoveredScanners, serviceInfo);
            }
          }

        }

        return discoveredScanners;

      }

      @Override
      protected void done() {

        ArrayList<Scanner> discoveredScanners = null;

        try {
          discoveredScanners = get();
          getNotificaiton().message(Localizer.localize("DiscoveryCompleteMessage"));
        } catch (InterruptedException e) {
          getNotificaiton().message(e.getLocalizedMessage());
          LOG.error(e, e);
        } catch (ExecutionException e) {
          getNotificaiton().message(e.getLocalizedMessage());
          LOG.error(e, e);
        } catch (CancellationException e) {
          getNotificaiton().message(Localizer.localize("DiscoveryCancelledMessage"));
        } finally {
          fireDiscoveryEvent(discoveredScanners);
        }

      }

    };

    worker.execute();

  }

  public final synchronized void discover(final InetAddress address, final int portNumber,
      final String description) throws IOException, SaneException {

    worker = new SwingWorker<ArrayList<Scanner>, Void>() {

      @Override
      protected ArrayList<Scanner> doInBackground() throws Exception {

        getNotificaiton().message(
            String.format(Localizer.localize("QueryingServerMessage"), address.getHostName() + " ("
                + address.getHostAddress() + ":" + portNumber)
                + ")");

        ArrayList<Scanner> discoveredScanners = new ArrayList<Scanner>();
        SaneSession session = null;

        try {
          session = SaneSession.withRemoteSane(address, portNumber);
          session.setPasswordProvider(getPasswordProvider());
          List<SaneDevice> devices = session.listDevices();

          if ((devices != null) && (devices.size() > 0)) {
            getNotificaiton().message(
                String.format(devices.size() > 1 ? Localizer.localize("FoundDevicesMessage")
                    : Localizer.localize("FoundDeviceMessage"), devices.size()));
            for (SaneDevice device : devices) {
              try {
                Scanner scanner = scanService.create(device, address.getHostAddress(), portNumber,
                    description);
                discoveredScanners.add(scanner);
              } catch (Exception ex) {
                getNotificaiton().message(
                    address.getHostAddress() + ":" + portNumber + " - " + ex.getLocalizedMessage());
              }
            }
          }

        } catch (SocketException ex) {
          getNotificaiton().message(
              address.getHostAddress() + ":" + portNumber + " - " + ex.getLocalizedMessage());
        } finally {
          if (session != null) {
            session.close();
          }
        }

        return discoveredScanners;

      }

      @Override
      protected void done() {

        ArrayList<Scanner> discoveredScanners = null;

        try {
          discoveredScanners = get();
          getNotificaiton().message(Localizer.localize("DiscoveryCompleteMessage"));
        } catch (InterruptedException e) {
          getNotificaiton().message(e.getLocalizedMessage());
          LOG.error(e, e);
        } catch (ExecutionException e) {
          getNotificaiton().message(e.getLocalizedMessage());
          LOG.error(e, e);
        } catch (CancellationException e) {
          getNotificaiton().message(Localizer.localize("DiscoveryCancelledMessage"));
        } finally {
          fireDiscoveryEvent(discoveredScanners);
        }

      }

    };

    worker.execute();

  }

  /**
   * @param discoveredScanners
   *          an {@link ArrayList} of discovered {@link Scanner}(s)
   */
  private void fireDiscoveryEvent(final ArrayList<Scanner> discoveredScanners) {

    Object[] listeners = listenerList.getListenerList();

    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == DiscoveryListener.class) {
        if (discoveryEvent == null) {
          discoveryEvent = new DiscoveryEvent(this);
          discoveryEvent.setDiscoveredScanners(discoveredScanners);
        }
        ((DiscoveryListener) listeners[i + 1]).discoveryEventOccurred(discoveryEvent);
      }
    }

  }

  public final InetAddress getLocalAddress() throws SocketException {
    for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
        .hasMoreElements();) {
      final NetworkInterface networkInterface = interfaces.nextElement();
      if (networkInterface.isLoopback()) {
        continue;
      }
      for (final InterfaceAddress interfaceAddr : networkInterface.getInterfaceAddresses()) {
        final InetAddress inetAddr = interfaceAddr.getAddress();
        if (!(inetAddr instanceof Inet4Address)) {
          continue;
        }
        return inetAddr;
      }
    }
    return null;
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

  private SaneServiceIdentity getSaneServiceIdentity() {
    return scanService.getSaneServiceIdentity();
  }

  public final boolean isActive() {
    return !(worker.isDone());
  }

  /**
   * @param listener
   *          a discovery listener
   */
  public final void removeDiscoveryListener(final DiscoveryListener listener) {
    listenerList.remove(DiscoveryListener.class, listener);
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

}
