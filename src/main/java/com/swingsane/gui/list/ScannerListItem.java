package com.swingsane.gui.list;

import javax.swing.JComponent;

import com.swingsane.business.scanning.ScanEventListener;
import com.swingsane.business.scanning.ScanJob;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.model.Scanner;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class ScannerListItem extends JComponent {

  public enum ScannerStatus {
    IDLE, BUSY
  }

  private ScanJob scanJob;

  private ScanEventListener scanEventListener;

  private Scanner scanner;

  private ScannerStatus status = ScannerStatus.IDLE;

  public ScannerListItem(Scanner scanner) {
    this.scanner = scanner;
  }

  public final void cancel() {
    if ((scanJob != null) && scanJob.isActive()) {
      scanJob.cancel();
    }
  }

  public final Scanner getScanner() {
    return scanner;
  }

  private String getScannerAddress() {
    return scanner.getRemoteAddress() + ":" + scanner.getRemotePortNumber();
  }

  private String getScannerModel() {
    // de-uglify SANE descriptions by replacing underscores with spaces.
    return (scanner.getVendor() + " " + scanner.getModel()).replace("_", " ");
  }

  public final ScannerStatus getScannerStatus() {
    return status;
  }

  private String getStatus() {
    switch (status) {
    case IDLE:
      return Localizer.localize("IdleScannerStatusText");
    case BUSY:
      return Localizer.localize("BusyScannerStatusText");
    default:
      break;
    }
    return null;
  }

  public final boolean isActive() {
    if (scanJob == null) {
      return false;
    }
    return scanJob.isActive();
  }

  public final void removeListeners() {
    scanJob.removeScanEventListener(scanEventListener);
  }

  public final void setScanEventListener(ScanEventListener scanEventListener) {
    this.scanEventListener = scanEventListener;
  }

  public final void setScanJob(ScanJob scanJob) {
    this.scanJob = scanJob;
  }

  public final void setScannerStatus(ScannerStatus scannerStatus) {
    status = scannerStatus;
  }

  @Override
  public final String toString() {
    if ((scanner.getDescription() != null) && (scanner.getDescription().trim().length() > 0)) {
      return scanner.getDescription() + " (" + getStatus() + ")";
    } else {
      return getScannerModel() + " - " + scanner.getName() + " - " + getScannerAddress() + " ("
          + getStatus() + ")";
    }
  }

}
