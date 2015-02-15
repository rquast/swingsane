package com.swingsane.preferences.model;

import com.swingsane.business.discovery.DiscoveryJob;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@XStreamAlias("saneServiceIdentity")
public class SaneServiceIdentity {

  private String serviceName = DiscoveryJob.SANE_SERVICE_NAME + "";

  public final String getServiceName() {
    if (serviceName == null) {
      serviceName = DiscoveryJob.SANE_SERVICE_NAME + "";
    }
    return serviceName;
  }

  public final void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

}
