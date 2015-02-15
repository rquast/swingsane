package com.swingsane.preferences.device;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.swingsane.preferences.model.Scanner;

public class ScannerPreferencesTest {

  private static final String DEFAULT_SANE_SERVICE_NAME = "saned";

  private static final int DEFAULT_SANE_PORT = 6566;

  private static final String MOCK_REMOTE_ADDRESS = "192.168.1.169";

  private static final String MOCK_SCANNER_MODEL = "GT-S50";

  private static final String MOCK_VENDOR = "Epson";

  private static final String MOCK_NAME = "epkowa:interpreter:002:008";

  @Test
  public void testGetSetModel() throws Exception {
    Scanner scannerPreferences = new Scanner();
    scannerPreferences.setModel(MOCK_SCANNER_MODEL);
    assertEquals(MOCK_SCANNER_MODEL, scannerPreferences.getModel());
  }

  @Test
  public void testGetSetName() throws Exception {
    Scanner scannerPreferences = new Scanner();
    scannerPreferences.setName(MOCK_NAME);
    assertEquals(MOCK_NAME, scannerPreferences.getName());
  }

  @Test
  public void testGetSetRemoteAddress() throws Exception {
    Scanner scannerPreferences = new Scanner();
    scannerPreferences.setRemoteAddress(MOCK_REMOTE_ADDRESS);
    assertEquals(MOCK_REMOTE_ADDRESS, scannerPreferences.getRemoteAddress());
  }

  @Test
  public void testGetSetRemotePortNumber() throws Exception {
    Scanner scannerPreferences = new Scanner();
    scannerPreferences.setRemotePortNumber(DEFAULT_SANE_PORT);
    assertEquals(DEFAULT_SANE_PORT, scannerPreferences.getRemotePortNumber());
  }

  @Test
  public void testGetSetServiceName() throws Exception {
    Scanner scannerPreferences = new Scanner();
    scannerPreferences.setServiceName(DEFAULT_SANE_SERVICE_NAME);
    assertEquals(DEFAULT_SANE_SERVICE_NAME, scannerPreferences.getServiceName());
  }

  @Test
  public void testGetSetVendor() throws Exception {
    Scanner scannerPreferences = new Scanner();
    scannerPreferences.setVendor(MOCK_VENDOR);
    assertEquals(MOCK_VENDOR, scannerPreferences.getVendor());
  }

}
