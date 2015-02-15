package com.swingsane.business.options;

import java.util.HashMap;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.swingsane.preferences.IPreferredDefaults.ColorMode;
import com.swingsane.preferences.IPreferredDefaults.Source;
import com.swingsane.preferences.model.BooleanOption;
import com.swingsane.preferences.model.Constraints;
import com.swingsane.preferences.model.FixedOption;
import com.swingsane.preferences.model.IntegerOption;
import com.swingsane.preferences.model.Scanner;
import com.swingsane.preferences.model.StringOption;

/**
 * Known Sane Options. Refactor this class to support different scanners.
 */
public final class KnownSaneOptions {

  public static SpinnerModel getBlackThresholdModel(Scanner scanner) {
    SpinnerNumberModel blackThresholdModel = new SpinnerNumberModel(0, MIN_BLACK_THRESHOLD,
        MAX_BLACK_THRESHOLD, 1);

    HashMap<String, FixedOption> fixedOptions = scanner.getFixedOptions();

    FixedOption fixedOption = fixedOptions.get(SANE_NAME_THRESHOLD);

    if (fixedOption == null) {
      return null;
    }

    Constraints constraints = fixedOption.getConstraints();
    Integer maxInt = constraints.getMaximumInteger();
    Integer minInt = constraints.getMinimumInteger();

    blackThresholdModel.setMaximum(maxInt);
    blackThresholdModel.setMinimum(minInt);
    blackThresholdModel.setStepSize(constraints.getQuantumInteger());

    blackThresholdModel.setValue(fixedOption.getValue());

    return blackThresholdModel;
  }

  public static String getColorMode(Scanner scanner) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();
    StringOption stringOption = stringOptions.get(SANE_NAME_SCAN_MODE);
    if (stringOption == null) {
      return null;
    }
    return stringOption.getValue();
  }

  public static ComboBoxModel<String> getColorModeComponent(Scanner scanner) {

    DefaultComboBoxModel<String> colorModel = new DefaultComboBoxModel<String>();

    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();

    StringOption stringOption = stringOptions.get(SANE_NAME_SCAN_MODE);

    if (stringOption == null) {
      return null;
    }

    Constraints constraints = stringOption.getConstraints();
    List<String> values = constraints.getStringList();

    for (String value : values) {
      colorModel.addElement(value);
    }

    if (values.size() > 0) {
      return colorModel;
    } else {
      return null;
    }

  }

  public static String getPageSize(Scanner scanner) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();
    StringOption stringOption = stringOptions.get(EPSON_NAME_SCAN_AREA);
    if (stringOption == null) {
      return null;
    }
    return stringOption.getValue();
  }

  public static ComboBoxModel<String> getPageSizeModel(Scanner scanner) {
    DefaultComboBoxModel<String> pageSizeModel = new DefaultComboBoxModel<String>();

    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();

    StringOption stringOption = stringOptions.get(EPSON_NAME_SCAN_AREA);

    if (stringOption == null) {
      return null;
    }

    Constraints constraints = stringOption.getConstraints();
    List<String> values = constraints.getStringList();

    for (String value : values) {
      pageSizeModel.addElement(value);
    }

    if (values.size() > 0) {
      return pageSizeModel;
    } else {
      return null;
    }

  }

  public static Integer getResolution(Scanner scanner) {
    HashMap<String, IntegerOption> integerOptions = scanner.getIntegerOptions();
    IntegerOption integerOption = integerOptions.get(SANE_NAME_SCAN_RESOLUTION);
    if (integerOption == null) {
      integerOption = integerOptions.get(SANE_NAME_SCAN_X_RESOLUTION);
    }
    if (integerOption == null) {
      return null;
    }
    return new Integer(integerOption.getValue());
  }

  public static ComboBoxModel<Integer> getResolutionModel(Scanner scanner) {

    DefaultComboBoxModel<Integer> resolutionModel = new DefaultComboBoxModel<Integer>();

    HashMap<String, IntegerOption> integerOptions = scanner.getIntegerOptions();

    IntegerOption integerOption = integerOptions.get(SANE_NAME_SCAN_RESOLUTION);

    if (integerOption == null) {
      integerOption = integerOptions.get(SANE_NAME_SCAN_X_RESOLUTION);
    }

    if (integerOption == null) {
      return null;
    }

    Constraints constraints = integerOption.getConstraints();
    List<Integer> integerList = constraints.getIntegerList();
    Integer maxInt = constraints.getMaximumInteger();
    Integer minInt = constraints.getMinimumInteger();
    Integer quantum = constraints.getQuantumInteger();

    if (integerList != null) {
      for (Integer value : integerList) {
        resolutionModel.addElement(value);
      }
    } else {
      for (int res = minInt; res >= maxInt; res += quantum) {
        resolutionModel.addElement(res);
      }
    }

    if (resolutionModel.getSize() > 0) {
      return resolutionModel;
    } else {
      return null;
    }

  }

  public static String getSource(Scanner scanner) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();
    StringOption stringOption = stringOptions.get(SANE_NAME_SCAN_SOURCE);
    if (stringOption == null) {
      stringOption = stringOptions.get(SAMSUNG_NAME_SCAN_SOURCE);
      if (stringOption == null) {
        return null;
      }
    }
    return stringOption.getValue();
  }

  public static ComboBoxModel<String> getSourceModel(Scanner scanner) {
    DefaultComboBoxModel<String> sourceModel = new DefaultComboBoxModel<String>();

    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();

    StringOption stringOption = stringOptions.get(SANE_NAME_SCAN_SOURCE);

    if (stringOption == null) {
      return null;
    }

    Constraints constraints = stringOption.getConstraints();
    List<String> values = constraints.getStringList();

    for (String value : values) {
      sourceModel.addElement(value);
    }

    if (values.size() > 0) {
      return sourceModel;
    } else {
      return null;
    }
  }

  public static boolean isAutoCropAvailable(Scanner scanner) {
    HashMap<String, BooleanOption> booleanOptions = scanner.getBooleanOptions();
    BooleanOption booleanOption = booleanOptions.get(EPSON_NAME_AUTOCROP); // epson scanners only?
    return !(booleanOption == null);
  }

  public static boolean isAutoCropSelected(Scanner scanner) {
    HashMap<String, BooleanOption> booleanOptions = scanner.getBooleanOptions();
    BooleanOption booleanOption = booleanOptions.get(EPSON_NAME_AUTOCROP); // epson scanners only?
    if (booleanOption == null) {
      return false;
    } else {
      return booleanOption.getValue();
    }
  }

  private static boolean isAutomaticDocumentFeederString(String value) {
    return value.toLowerCase().contains("adf") || value.toLowerCase().contains("document feeder");
  }

  public static boolean isBatchScanAvailable(Scanner scanner) {
    HashMap<String, BooleanOption> booleanOptions = scanner.getBooleanOptions();
    BooleanOption booleanOption = booleanOptions.get(SANE_KNOWN_OPTION_BATCH_SCAN);
    if (booleanOption == null) {
      booleanOption = booleanOptions.get(EPSON_NAME_ADF_AUTO_SCAN);
      if (booleanOption == null) {
        return false;
      }
    }
    return true;
  }

  public static boolean isBatchScanSelected(Scanner scanner) {
    HashMap<String, BooleanOption> booleanOptions = scanner.getBooleanOptions();
    BooleanOption booleanOption = booleanOptions.get(SANE_KNOWN_OPTION_BATCH_SCAN);
    if (booleanOption == null) {
      booleanOption = booleanOptions.get(EPSON_NAME_ADF_AUTO_SCAN);
      if (booleanOption == null) {
        return false;
      }
    }
    return booleanOption.getValue();
  }

  private static boolean isBinaryColorString(String value) {
    return value.toLowerCase().contains("black & white") || value.toLowerCase().contains("mono")
        || value.toLowerCase().contains("binary") || value.toLowerCase().contains("halftone")
        || value.toLowerCase().contains("thresholded") || value.toLowerCase().contains("lineart");
  }

  private static boolean isColorColorString(String value) {
    return value.toLowerCase().contains("color");
  }

  public static boolean isDuplexScanningAvailable(Scanner scanner) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();
    StringOption stringOption = stringOptions.get(SANE_KNOWN_OPTION_DUPLEX);
    if (stringOption == null) {
      stringOption = stringOptions.get(EPSON_NAME_ADF_MODE);
    }
    return !(stringOption == null);
  }

  public static boolean isDuplexScanningEnabled(Scanner scanner) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();

    StringOption stringOption = stringOptions.get(SANE_KNOWN_OPTION_DUPLEX);
    if ((stringOption != null) && (!stringOption.getOptionsOrderValuePair().isActive())) {
      return false;
    }

    if (stringOption == null) {
      stringOption = stringOptions.get(EPSON_NAME_ADF_MODE);
      if ((stringOption == null) || !stringOption.getOptionsOrderValuePair().isActive()) {
        return false;
      }
    }

    return stringOption.getValue().toLowerCase().contains("duplex");
  }

  private static boolean isFlatbedFeederString(String value) {
    return value.toLowerCase().contains("flatbed");
  }

  private static boolean isGrayscaleColorString(String value) {
    return value.toLowerCase().contains("gray") || value.toLowerCase().contains("grey");
  }

  public static boolean isUsingDefaultBlackThreshold(Scanner scanner) {
    HashMap<String, FixedOption> fixedOptions = scanner.getFixedOptions();
    FixedOption fixedOption = fixedOptions.get(SANE_NAME_THRESHOLD);
    if (fixedOption == null) {
      return false;
    }
    return fixedOption.getOptionsOrderValuePair().isActive();
  }

  public static void setAutoCrop(Scanner scanner, boolean autoCropEnabled) {
    HashMap<String, BooleanOption> booleanOptions = scanner.getBooleanOptions();
    BooleanOption booleanOption = booleanOptions.get(EPSON_NAME_AUTOCROP); // epson scanners only?
    if (booleanOption == null) {
      return;
    }
    booleanOption.setValue(autoCropEnabled);
  }

  public static void setBatchScan(Scanner scanner, boolean enabled) {
    HashMap<String, BooleanOption> booleanOptions = scanner.getBooleanOptions();
    BooleanOption booleanOption = booleanOptions.get(SANE_KNOWN_OPTION_BATCH_SCAN);
    if (booleanOption == null) {
      booleanOption = booleanOptions.get(EPSON_NAME_ADF_AUTO_SCAN);
    }
    if (booleanOption == null) {
      return;
    }
    booleanOption.setValue(enabled);
  }

  public static void setBlackThreshold(Scanner scanner, int blackThreshold) {
    HashMap<String, FixedOption> fixedOptions = scanner.getFixedOptions();

    FixedOption fixedOption = fixedOptions.get(SANE_NAME_THRESHOLD);
    if (fixedOption == null) {
      return;
    }
    fixedOption.setValue(blackThreshold);
  }

  public static void setColorMode(Scanner scanner, String colorMode) {

    HashMap<String, IntegerOption> integerOptions = scanner.getIntegerOptions();
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();

    StringOption stringOption = stringOptions.get(SANE_NAME_SCAN_MODE);

    if (stringOption == null) {
      return;
    }

    IntegerOption depth = integerOptions.get(SANE_NAME_BIT_DEPTH);

    // black and white
    if (isBinaryColorString(colorMode)) {
      if (depth != null) {
        depth.getOptionsOrderValuePair().setActive(false);
      }
      stringOption.setValue(colorMode);
      return;
    }

    Constraints depthConstraints = depth.getConstraints();

    // grayscale
    if (colorMode.toLowerCase().contains("gray") || colorMode.toLowerCase().contains("grey")) {
      if (depthConstraints != null) {
        List<Integer> values = depthConstraints.getIntegerList();
        if (values.contains(COLOR_DEPTH_8_BIT)) {
          depth.setValue(COLOR_DEPTH_8_BIT);
        } else if (values.contains(COLOR_DEPTH_12_BIT)) {
          depth.setValue(COLOR_DEPTH_12_BIT);
        } else if (values.contains(COLOR_DEPTH_16_BIT)) {
          depth.setValue(COLOR_DEPTH_16_BIT);
        } else if (values.contains(COLOR_DEPTH_24_BIT)) {
          depth.setValue(COLOR_DEPTH_24_BIT);
        }
      }
      stringOption.setValue(colorMode);
      return;
    }

    // color
    if (depthConstraints != null) {
      List<Integer> values = depthConstraints.getIntegerList();
      if (values.contains(COLOR_DEPTH_16_BIT)) {
        depth.setValue(COLOR_DEPTH_16_BIT);
      } else if (values.contains(COLOR_DEPTH_24_BIT)) {
        depth.setValue(COLOR_DEPTH_24_BIT);
      }
    }
    stringOption.setValue(colorMode);

  }

  public static void setDefaultColorMode(Scanner scanner, ColorMode color) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();
    StringOption stringOption = stringOptions.get(SANE_NAME_SCAN_MODE);
    if (stringOption == null) {
      return;
    }
    Constraints constraints = stringOption.getConstraints();
    if (constraints == null) {
      return;
    }
    List<String> values = constraints.getStringList();
    for (String value : values) {
      switch (color) {
      case BLACK_AND_WHITE:
        if (isBinaryColorString(value)) {
          setColorMode(scanner, value);
          return;
        }
        break;
      case GRAYSCALE:
        if (isGrayscaleColorString(value)) {
          setColorMode(scanner, value);
          return;
        }
        break;
      case COLOR:
        if (isColorColorString(value)) {
          setColorMode(scanner, value);
          return;
        }
        break;
      default:
        break;

      }
    }
  }

  public static void setDefaultScanArea(Scanner scanner, String defaultScanArea) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();
    StringOption stringOption = stringOptions.get(EPSON_NAME_SCAN_AREA);
    if (stringOption == null) {
      return;
    }
    Constraints constraints = stringOption.getConstraints();
    if (constraints == null) {
      return;
    }
    List<String> values = constraints.getStringList();
    for (String value : values) {
      if (value.toLowerCase().contains(defaultScanArea.toLowerCase())) {
        setScanArea(scanner, value);
        return;
      }
    }
  }

  public static void setDefaultSource(Scanner scanner, Source source) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();
    StringOption stringOption = stringOptions.get(SANE_NAME_SCAN_SOURCE);
    if (stringOption == null) {
      return;
    }
    Constraints constraints = stringOption.getConstraints();
    if (constraints == null) {
      return;
    }
    List<String> values = constraints.getStringList();
    for (String value : values) {
      switch (source) {
      case AUTOMATIC_DOCUMENT_FEEDER:
        if (isAutomaticDocumentFeederString(value)) {
          setSource(scanner, value);
          return;
        }
        break;
      case FLATBED:
        if (isFlatbedFeederString(value)) {
          setSource(scanner, value);
          return;
        }
        break;
      default:
        break;
      }
    }
  }

  public static void setDuplex(Scanner scanner, boolean isDuplex) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();

    StringOption stringOption = stringOptions.get(SANE_KNOWN_OPTION_DUPLEX);

    if (stringOption == null) {
      stringOption = stringOptions.get(EPSON_NAME_ADF_MODE);
      if (stringOption == null) {
        return;
      }
    }

    Constraints constraints = stringOption.getConstraints();
    List<String> values = constraints.getStringList();

    if (isDuplex) {
      for (String value : values) {
        if (value.toLowerCase().contains("duplex")) {
          stringOption.setValue(value);
          stringOption.getOptionsOrderValuePair().setActive(true);
          return;
        }
      }
    } else {
      for (String value : values) {
        if (!(value.toLowerCase().contains("duplex"))) {
          stringOption.setValue(value);
          return;
        }
      }
    }

  }

  public static void setResolution(Scanner scanner, Integer resolution) {
    HashMap<String, IntegerOption> integerOptions = scanner.getIntegerOptions();
    if (integerOptions.get(SANE_NAME_SCAN_X_RESOLUTION) != null) {
      integerOptions.get(SANE_NAME_SCAN_X_RESOLUTION).setValue(resolution);
      integerOptions.get(SANE_NAME_SCAN_Y_RESOLUTION).setValue(resolution);
    }
    if (integerOptions.get(SANE_NAME_SCAN_RESOLUTION) != null) {
      integerOptions.get(SANE_NAME_SCAN_RESOLUTION).setValue(resolution);
    }
  }

  public static void setScanArea(Scanner scanner, String pageSize) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();
    StringOption stringOption = stringOptions.get(EPSON_NAME_SCAN_AREA);
    if (stringOption == null) {
      return;
    }
    stringOption.setValue(pageSize);
  }

  public static void setSource(Scanner scanner, String source) {
    HashMap<String, StringOption> stringOptions = scanner.getStringOptions();
    StringOption stringOption = stringOptions.get(SANE_NAME_SCAN_SOURCE);
    if (stringOption == null) {
      stringOption = stringOptions.get(SAMSUNG_NAME_SCAN_SOURCE);
      if (stringOption == null) {
        return;
      }
    }
    stringOption.setValue(source);
  }

  public static void setUsingDefaultBlackThreshold(Scanner scanner,
      boolean usingDefaultBlackThreshold) {
    HashMap<String, FixedOption> fixedOptions = scanner.getFixedOptions();
    FixedOption fixedOption = fixedOptions.get(SANE_NAME_THRESHOLD);
    if (fixedOption == null) {
      return;
    }
    fixedOption.getOptionsOrderValuePair().setActive(usingDefaultBlackThreshold);
  }

  private static final int MAX_BLACK_THRESHOLD = 128;

  private static final int MIN_BLACK_THRESHOLD = -128;

  private static final int COLOR_DEPTH_8_BIT = 8;

  private static final int COLOR_DEPTH_12_BIT = 12;

  private static final int COLOR_DEPTH_16_BIT = 16;

  private static final int COLOR_DEPTH_24_BIT = 24;

  private static final String SANE_KNOWN_OPTION_DUPLEX = "duplex";

  private static final String SANE_KNOWN_OPTION_BATCH_SCAN = "batch-scan";

  public static final String EPSON_NAME_ADF_AUTO_SCAN = "adf-auto-scan"; // batch-scan ??

  public static final String EPSON_NAME_ADF_MODE = "adf-mode"; // duplex ??

  public static final String EPSON_NAME_AUTOCROP = "autocrop";

  public static final String EPSON_NAME_SCAN_AREA = "scan-area";

  public static final String SAMSUNG_NAME_SCAN_SOURCE = "doc-source"; // source ??

  public static final String SANE_NAME_BIT_DEPTH = "depth";

  public static final String SANE_NAME_SCAN_MODE = "mode";

  public static final String SANE_NAME_SCAN_RESOLUTION = "resolution";

  public static final String SANE_NAME_SCAN_SOURCE = "source";

  public static final String SANE_NAME_SCAN_X_RESOLUTION = "x-resolution";

  public static final String SANE_NAME_SCAN_Y_RESOLUTION = "y-resolution";

  public static final String SANE_NAME_THRESHOLD = "threshold";

  private KnownSaneOptions() {
  }

}
