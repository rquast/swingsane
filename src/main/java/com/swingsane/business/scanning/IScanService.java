package com.swingsane.business.scanning;

import java.io.IOException;

import javax.jmdns.ServiceInfo;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.SanePasswordProvider;

import com.swingsane.preferences.model.SaneServiceIdentity;
import com.swingsane.preferences.model.Scanner;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public interface IScanService {

  void configure(SaneDevice saneDevice, Scanner scanner) throws IOException;

  Scanner create(SaneDevice saneDevice, ServiceInfo serviceInfo, String hostAddress)
      throws IOException, SaneException;

  Scanner create(SaneDevice saneDevice, String hostAddress, int portNumber, String description)
      throws IOException, SaneException;

  SanePasswordProvider getPasswordProvider();

  SaneServiceIdentity getSaneServiceIdentity();

  void setPasswordProvider(SanePasswordProvider passwordProvider);

  void setSaneServiceIdentity(SaneServiceIdentity saneService);

  void setScannerOptions(SaneDevice saneDevice, Scanner scanner) throws IOException, SaneException;

}
