package com.swingsane.preferences;

import java.util.Locale;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSizeName;

import org.apache.log4j.Logger;
import org.imgscalr.Scalr.Rotation;

import com.swingsane.business.options.KnownSaneOptions;
import com.swingsane.preferences.model.Scanner;

public class PreferredDefaultsImpl implements IPreferredDefaults {

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(PreferredDefaultsImpl.class);

  private ColorMode color = ColorMode.BLACK_AND_WHITE;
  private String pageSize;
  private int pagesToScan = 1;
  private int resolution = DEFAULT_RESOLUTION;
  private Source source = Source.AUTOMATIC_DOCUMENT_FEEDER;

  @Override
  public final ColorMode getColor() {
    return color;
  }

  @Override
  public double getDefaultDeskewThreshold() {
    return IPreferredDefaults.DEFAULT_DESKEW_THRESHOLD;
  }

  @Override
  public int getDefaultLuminanceThreshold() {
    return IPreferredDefaults.DEFAULT_LUMINANCE_THRESHOLD;
  }

  @Override
  public Rotation getDefaultRotation() {
    return IPreferredDefaults.DEFAULT_ROTATION;
  }

  @Override
  public final int getResolution() {
    return resolution;
  }

  private boolean isA4PaperSize() {

    String timezone = System.getProperty("user.timezone");
    if ((timezone != null) && (timezone.length() > 0)) {
      return !System.getProperty("user.timezone").startsWith("America");
    }

    try {
      PrintService pservice = PrintServiceLookup.lookupDefaultPrintService();
      Object obj = pservice.getDefaultAttributeValue(Media.class);
      if (obj instanceof MediaSizeName) {
        MediaSizeName mediaSizeName = (MediaSizeName) obj;
        return mediaSizeName.equals(MediaSizeName.ISO_A4);
      }

    } catch (Exception ex) {
      LOG.info(ex.getLocalizedMessage());
    }

    String country = Locale.getDefault().getCountry();
    if ((country.equals("US")) || (country.equals("CA"))) {
      return false;
    }

    // default to true
    return true;
  }

  @Override
  public final void setColor(ColorMode color) {
    this.color = color;
  }

  @Override
  public final void setResolution(int resolution) {
    this.resolution = resolution;
  }

  private void udpateBlackThreshold(Scanner scanner) {
    KnownSaneOptions.setBlackThreshold(scanner, 0);
  }

  @Override
  public final void update(Scanner scanner) {
    updateResolution(scanner);
    updateSource(scanner);
    updatePagesSize(scanner);
    updateColorMode(scanner);
    udpateBlackThreshold(scanner);
    updateUsingDefaultBlackThreshold(scanner);
    updateDuplexScanning(scanner);
    updateAutoCrop(scanner);
    updateADFAutoScan(scanner);
    updatePagesToScan(scanner);
    updateUseCustomOptions(scanner);
  }

  private void updateADFAutoScan(Scanner scanner) {
    KnownSaneOptions.setBatchScan(scanner, true);
  }

  private void updateAutoCrop(Scanner scanner) {
    KnownSaneOptions.setAutoCrop(scanner, false);
  }

  private void updateColorMode(Scanner scanner) {
    KnownSaneOptions.setDefaultColorMode(scanner, color);
  }

  private void updateDuplexScanning(Scanner scanner) {
    KnownSaneOptions.setDuplex(scanner, false);
  }

  private void updatePagesSize(Scanner scanner) {
    if (pageSize != null) {
      KnownSaneOptions.setDefaultScanArea(scanner, pageSize);
    } else if (isA4PaperSize()) {
      KnownSaneOptions.setDefaultScanArea(scanner, "A4");
    } else {
      KnownSaneOptions.setDefaultScanArea(scanner, "Letter");
    }
  }

  private void updatePagesToScan(Scanner scanner) {
    scanner.setPagesToScan(pagesToScan);
  }

  private void updateResolution(Scanner scanner) {
    KnownSaneOptions.setResolution(scanner, resolution);
  }

  private void updateSource(Scanner scanner) {
    KnownSaneOptions.setDefaultSource(scanner, source);
  }

  private void updateUseCustomOptions(Scanner scanner) {
    scanner.setUsingCustomOptions(false);
  }

  private void updateUsingDefaultBlackThreshold(Scanner scanner) {
    KnownSaneOptions.setUsingDefaultBlackThreshold(scanner, true);
  }

}
