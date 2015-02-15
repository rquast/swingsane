package com.swingsane.preferences.model;

import com.swingsane.i18n.Localizer;
import com.swingsane.util.RandomGUID;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("scanner")
public class Scanner extends ScannerOptions {

  private String guid;
  private String description;
  private String serviceName;
  private String remoteAddress;
  private int remotePortNumber;
  private String vendor;
  private String model;
  private String name;
  private String type;
  private int pagesToScan;
  private boolean usingCustomOptions;
  private String batchPrefix = Localizer.localize("ScanFileNamePrefix") + "_"
      + Localizer.localize("TimeStampToken") + "_" + Localizer.localize("PageNumberToken") + "_"
      + Localizer.localize("PageCountToken");

  public Scanner() {
    setGuid((new RandomGUID()).toString());
  }

  public final String getBatchPrefix() {
    return batchPrefix;
  }

  public final String getDescription() {
    return description;
  }

  public final String getGuid() {
    return guid;
  }

  public final String getModel() {
    return model;
  }

  public final String getName() {
    return name;
  }

  public final int getPagesToScan() {
    return pagesToScan;
  }

  public final String getRemoteAddress() {
    return remoteAddress;
  }

  public final int getRemotePortNumber() {
    return remotePortNumber;
  }

  public final String getServiceName() {
    return serviceName;
  }

  public final String getType() {
    return type;
  }

  public final String getVendor() {
    return vendor;
  }

  public final boolean isUsingCustomOptions() {
    return usingCustomOptions;
  }

  public final void setBatchPrefix(String batchPrefix) {
    this.batchPrefix = batchPrefix;
  }

  public final void setDescription(String description) {
    this.description = description;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public final void setModel(String model) {
    this.model = model;
  }

  public final void setName(String name) {
    this.name = name;
  }

  public final void setPagesToScan(int pagesToScan) {
    this.pagesToScan = pagesToScan;
  }

  public final void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  public final void setRemotePortNumber(int remotePortNumber) {
    this.remotePortNumber = remotePortNumber;
  }

  public final void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public final void setType(String type) {
    this.type = type;
  }

  public final void setUsingCustomOptions(boolean usingCustomOptions) {
    this.usingCustomOptions = usingCustomOptions;
  }

  public final void setVendor(String vendor) {
    this.vendor = vendor;
  }

}
