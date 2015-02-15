package com.swingsane.business.discovery;

import java.util.EventListener;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public interface DiscoveryListener extends EventListener {

  void discoveryEventOccurred(DiscoveryEvent devt);

}
