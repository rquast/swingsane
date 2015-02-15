package com.swingsane.business.discovery;

import java.util.ArrayList;
import java.util.EventObject;

import com.swingsane.preferences.model.Scanner;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class DiscoveryEvent extends EventObject {

  private ArrayList<Scanner> discoveredScanners;

  public DiscoveryEvent(Object source) {
    super(source);
  }

  public final ArrayList<Scanner> getDiscoveredScanners() {
    return discoveredScanners;
  }

  public final void setDiscoveredScanners(ArrayList<Scanner> discoveredScanners) {
    this.discoveredScanners = discoveredScanners;
  }

}
