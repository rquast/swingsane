package com.swingsane.business.scanning;

import java.awt.image.BufferedImage;
import java.util.EventObject;

@SuppressWarnings("serial")
public class ScanEvent extends EventObject {

  private BufferedImage bufferedImage;
  private int pagesToScan;
  private int pageNumber;
  private String batchPrefix;

  public ScanEvent(Object source) {
    super(source);
  }

  public final String getBatchPrefix() {
    return batchPrefix;
  }

  public final BufferedImage getBufferedImage() {
    return bufferedImage;
  }

  public final int getPageNumber() {
    return pageNumber;
  }

  public final int getPagesToScan() {
    return pagesToScan;
  }

  public final void setAcquiredImage(BufferedImage acquiredImage) {
    bufferedImage = acquiredImage;
  }

  public final void setBatchPrefix(String batchPrefix) {
    this.batchPrefix = batchPrefix;
  }

  public final void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }

  public final void setPagesToScan(int pagesToScan) {
    this.pagesToScan = pagesToScan;
  }

}
