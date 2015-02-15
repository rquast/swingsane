package com.swingsane.preferences.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("applicationPreferences")
public class ApplicationPreferences {

  private ArrayList<Scanner> scannerList;

  private HashMap<String, Login> saneLogins;

  private SaneServiceIdentity saneServiceIdentity;

  public final HashMap<String, Login> getSaneLogins() {
    if (saneLogins == null) {
      saneLogins = new HashMap<String, Login>();
    }
    return saneLogins;
  }

  public final SaneServiceIdentity getSaneServiceIdentity() {
    if (saneServiceIdentity == null) {
      saneServiceIdentity = new SaneServiceIdentity();
    }
    return saneServiceIdentity;
  }

  public final ArrayList<Scanner> getScannerList() {
    if (scannerList == null) {
      scannerList = new ArrayList<Scanner>();
    }
    return scannerList;
  }

  public final void setSaneService(SaneServiceIdentity saneService) {
    saneServiceIdentity = saneService;
  }

}
