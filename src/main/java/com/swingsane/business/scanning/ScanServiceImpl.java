package com.swingsane.business.scanning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jmdns.ServiceInfo;

import org.apache.log4j.Logger;

import au.com.southsky.jfreesane.OptionValueConstraintType;
import au.com.southsky.jfreesane.RangeConstraint;
import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.SaneOption;
import au.com.southsky.jfreesane.SanePasswordProvider;

import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.model.BooleanOption;
import com.swingsane.preferences.model.ButtonOption;
import com.swingsane.preferences.model.Constraints;
import com.swingsane.preferences.model.FixedOption;
import com.swingsane.preferences.model.GroupOption;
import com.swingsane.preferences.model.IntegerOption;
import com.swingsane.preferences.model.Option;
import com.swingsane.preferences.model.OptionsOrderValuePair;
import com.swingsane.preferences.model.OptionsOrderValuePair.SaneOptionType;
import com.swingsane.preferences.model.SaneServiceIdentity;
import com.swingsane.preferences.model.Scanner;
import com.swingsane.preferences.model.StringOption;

public class ScanServiceImpl implements IScanService {

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(ScanServiceImpl.class);

  /**
   * Password Provider for authenticating SANE sessions with password protected backends.
   */
  private SanePasswordProvider passwordProvider;

  private SaneServiceIdentity saneServiceIdentity;

  @Override
  public final void configure(SaneDevice saneDevice, Scanner scanner) throws IOException {
    ArrayList<OptionsOrderValuePair> optionOrdering = scanner.getOptionOrdering();
    for (OptionsOrderValuePair vp : optionOrdering) {
      switch (vp.getSaneOptionType()) {
      case STRING:
        StringOption stringOption = scanner.getStringOptions().get(vp.getKey());
        setDeviceStringOption(saneDevice, stringOption);
        break;
      case INTEGER:
        IntegerOption integerOption = scanner.getIntegerOptions().get(vp.getKey());
        setDeviceIntegerOption(saneDevice, integerOption);
        break;
      case BOOLEAN:
        BooleanOption booleanOption = scanner.getBooleanOptions().get(vp.getKey());
        setDeviceBooleanOption(saneDevice, booleanOption);
        break;
      case FIXED:
        FixedOption fixedOption = scanner.getFixedOptions().get(vp.getKey());
        setDeviceFixedOption(saneDevice, fixedOption);
        break;
      case GROUP:
        // group's don't get configured.
      case BUTTON:
        // buttons don't get configured.
      default:
        break;
      }
    }
  }

  @Override
  public final Scanner create(SaneDevice saneDevice, ServiceInfo serviceInfo, String hostAddress)
      throws IOException, SaneException {

    Scanner scanner = new Scanner();
    scanner.setServiceName(serviceInfo.getName());
    scanner.setRemoteAddress(hostAddress);
    scanner.setRemotePortNumber(serviceInfo.getPort());

    try {
      saneDevice.open();
      scanner.setModel(saneDevice.getModel());
      scanner.setVendor(saneDevice.getVendor());
      scanner.setType(saneDevice.getType());
      scanner.setName(saneDevice.getName());

      try {
        setScannerOptions(saneDevice, scanner);
      } catch (IOException e) {
        LOG.warn(e, e);
      }
    } catch (IOException e) {
      LOG.warn(e, e);
      throw e;
    } catch (SaneException e) {
      LOG.warn(e, e);
      throw e;
    } finally {
      try {
        if (saneDevice.isOpen()) {
          saneDevice.close();
        }
      } catch (IOException e) {
        LOG.warn(e, e);
      }
    }

    return scanner;

  }

  @Override
  public final Scanner create(SaneDevice saneDevice, String hostAddress, int portNumber,
      String description) throws IOException, SaneException {

    Scanner scanner = new Scanner();
    scanner.setServiceName(hostAddress + ":" + portNumber);
    scanner.setRemoteAddress(hostAddress);
    scanner.setRemotePortNumber(portNumber);
    scanner.setDescription(description);

    try {
      saneDevice.open();
      scanner.setModel(saneDevice.getModel());
      scanner.setVendor(saneDevice.getVendor());
      scanner.setType(saneDevice.getType());
      scanner.setName(saneDevice.getName());

      try {
        List<SaneOption> deviceOptions = saneDevice.listOptions();
        for (SaneOption option : deviceOptions) {
          if (isOptionBlacklisted(option)) {
            continue;
          }
          scanner.addOption(optionInfo(option, 0));
        }
      } catch (IOException e) {
        LOG.warn(e, e);
      }
    } catch (IOException e) {
      LOG.warn(e, e);
      throw e;
    } catch (SaneException e) {
      LOG.warn(e, e);
      throw e;
    } finally {
      try {
        if (saneDevice.isOpen()) {
          saneDevice.close();
        }
      } catch (IOException e) {
        LOG.warn(e, e);
      }
    }

    return scanner;

  }

  @Override
  public final SanePasswordProvider getPasswordProvider() {
    return passwordProvider;
  }

  @Override
  public final SaneServiceIdentity getSaneServiceIdentity() {
    return saneServiceIdentity;
  }

  private boolean isOptionBlacklisted(final SaneOption saneOption) {
    String[] blacklist = new String[] { "monitor-button" };
    for (String optionName : blacklist) {
      if (optionName.equalsIgnoreCase(saneOption.getName())) {
        return true;
      }
    }
    return false;
  }

  private Option optionInfo(SaneOption saneOption, int recursionLevel) throws IOException,
      SaneException {

    recursionLevel++;

    if (saneOption.isActive() && saneOption.isReadable() && saneOption.isWriteable()) {

      switch (saneOption.getType()) {

      case BOOLEAN:
        BooleanOption boolOption = new BooleanOption();
        boolOption.setName(saneOption.getName());
        boolOption.setDescription(saneOption.getDescription());
        boolOption.setValue(saneOption.getBooleanValue());
        OptionsOrderValuePair booleanValuePair = new OptionsOrderValuePair();
        booleanValuePair.setKey(boolOption.getName());
        booleanValuePair.setSaneOptionType(SaneOptionType.BOOLEAN);
        booleanValuePair.setActive(saneOption.isActive());
        boolOption.setOptionsOrderValuePair(booleanValuePair);
        return boolOption;

      case INT:
        IntegerOption intOption = new IntegerOption();
        intOption.setName(saneOption.getName());
        intOption.setDescription(saneOption.getDescription());
        intOption.setConstraintType(saneOption.getConstraintType().getWireValue());
        if (saneOption.getConstraintType() == OptionValueConstraintType.RANGE_CONSTRAINT) {
          RangeConstraint rangeConstraint = saneOption.getRangeConstraints();
          Constraints constraints = new Constraints();
          constraints.setMinimumInteger(new Integer(rangeConstraint.getMinimumInteger()));
          constraints.setMaximumInteger(new Integer(rangeConstraint.getMaximumInteger()));
          constraints.setQuantumInteger(new Integer(rangeConstraint.getQuantumInteger()));
          intOption.setConstraints(constraints);
        } else if (saneOption.getConstraintType() == OptionValueConstraintType.VALUE_LIST_CONSTRAINT) {
          Constraints constraints = new Constraints();
          constraints.setIntegerList(saneOption.getIntegerValueListConstraint());
          intOption.setConstraints(constraints);
        }

        if (saneOption.getValueCount() > 1) {
          intOption.setValueList(saneOption.getIntegerArrayValue());
        } else {
          intOption.setValue(saneOption.getIntegerValue());
        }

        OptionsOrderValuePair integerValuePair = new OptionsOrderValuePair();
        integerValuePair.setKey(intOption.getName());
        integerValuePair.setSaneOptionType(SaneOptionType.INTEGER);
        integerValuePair.setActive(saneOption.isActive());
        intOption.setOptionsOrderValuePair(integerValuePair);

        return intOption;

      case GROUP:
        if (recursionLevel <= 1) { // don't let it infinitely recurse.
          GroupOption groupOption = new GroupOption();
          groupOption.setName(saneOption.getName());
          groupOption.setDescription(saneOption.getDescription());
          for (SaneOption saneOption2 : saneOption.getGroup().getOptions()) {
            groupOption.addOption(optionInfo(saneOption2, recursionLevel));
          }

          OptionsOrderValuePair groupValuePair = new OptionsOrderValuePair();
          groupValuePair.setKey(groupOption.getName());
          groupValuePair.setSaneOptionType(SaneOptionType.GROUP);
          groupValuePair.setActive(saneOption.isActive());
          groupOption.setOptionsOrderValuePair(groupValuePair);

          return groupOption;
        }
        break;

      case BUTTON:
        if (recursionLevel <= 1) { // don't let it infinitely recurse.
          ButtonOption buttonOption = new ButtonOption();
          buttonOption.setName(saneOption.getName());
          buttonOption.setDescription(saneOption.getDescription());
          for (SaneOption saneOption2 : saneOption.getGroup().getOptions()) {
            buttonOption.addOption(optionInfo(saneOption2, recursionLevel));
          }

          OptionsOrderValuePair buttonValuePair = new OptionsOrderValuePair();
          buttonValuePair.setKey(buttonOption.getName());
          buttonValuePair.setSaneOptionType(SaneOptionType.BUTTON);
          buttonValuePair.setActive(saneOption.isActive());
          buttonOption.setOptionsOrderValuePair(buttonValuePair);

          return buttonOption;
        }
        break;

      case STRING:
        StringOption stringOption = new StringOption();
        stringOption.setName(saneOption.getName());
        stringOption.setDescription(saneOption.getDescription());
        stringOption.setValue(saneOption.getStringValue());
        stringOption.setConstraintType(saneOption.getConstraintType().getWireValue());
        if (saneOption.getConstraintType() == OptionValueConstraintType.STRING_LIST_CONSTRAINT) {
          Constraints constraints = new Constraints();
          constraints.setStringList(saneOption.getStringConstraints());
          stringOption.setConstraints(constraints);
        }
        OptionsOrderValuePair stringValuePair = new OptionsOrderValuePair();
        stringValuePair.setKey(stringOption.getName());
        stringValuePair.setSaneOptionType(SaneOptionType.STRING);
        stringValuePair.setActive(saneOption.isActive());
        stringOption.setOptionsOrderValuePair(stringValuePair);
        return stringOption;

      case FIXED:
        FixedOption fixedOption = new FixedOption();
        fixedOption.setName(saneOption.getName());
        fixedOption.setDescription(saneOption.getDescription());
        fixedOption.setValue(saneOption.getFixedValue());
        fixedOption.setConstraintType(saneOption.getConstraintType().getWireValue());
        if (saneOption.getConstraintType() == OptionValueConstraintType.RANGE_CONSTRAINT) {
          RangeConstraint rangeConstraint = saneOption.getRangeConstraints();
          Constraints constraints = new Constraints();
          constraints.setMinimumFixed(new Double(rangeConstraint.getMinimumFixed()));
          constraints.setMaximumFixed(new Double(rangeConstraint.getMaximumFixed()));
          constraints.setQuantumFixed(new Double(rangeConstraint.getQuantumFixed()));
          fixedOption.setConstraints(constraints);
        } else if (saneOption.getConstraintType() == OptionValueConstraintType.VALUE_LIST_CONSTRAINT) {
          Constraints constraints = new Constraints();
          constraints.setFixedList(saneOption.getFixedValueListConstraint());
          fixedOption.setConstraints(constraints);
        }

        if (saneOption.getValueCount() > 1) {
          fixedOption.setValueList(saneOption.getFixedArrayValue());
        } else {
          fixedOption.setValue(saneOption.getFixedValue());
        }

        OptionsOrderValuePair fixedValuePair = new OptionsOrderValuePair();
        fixedValuePair.setKey(fixedOption.getName());
        fixedValuePair.setSaneOptionType(SaneOptionType.FIXED);
        fixedValuePair.setActive(saneOption.isActive());
        fixedOption.setOptionsOrderValuePair(fixedValuePair);

        return fixedOption;

      default:
        LOG.warn(String.format(Localizer.localize("InvalidOptionTypeMessage"), saneOption.getType()));
      }
    }
    return null;
  }

  private void setDeviceBooleanOption(SaneDevice saneDevice, BooleanOption booleanOption)
      throws IOException {
    if (!(booleanOption.getOptionsOrderValuePair().isActive())) {
      return;
    }
    try {
      SaneOption saneOption = saneDevice.getOption(booleanOption.getName());
      saneOption.setBooleanValue(booleanOption.getValue());
      LOG.debug(booleanOption.getName() + " - " + (booleanOption.getValue() ? "true" : "false"));
    } catch (Exception e) {
      LOG.warn(e, e);
    }
  }

  private void setDeviceFixedOption(SaneDevice saneDevice, FixedOption fixedOption)
      throws IOException {
    if (!(fixedOption.getOptionsOrderValuePair().isActive())) {
      return;
    }
    try {
      SaneOption deviceOption = saneDevice.getOption(fixedOption.getName());
      if (fixedOption.getValueList() != null) {
        deviceOption.setFixedValue(fixedOption.getValueList());
        LOG.debug(fixedOption.getName() + " - " + fixedOption.getValueList());
      } else {
        deviceOption.setFixedValue(fixedOption.getValue());
        LOG.debug(fixedOption.getName() + " - " + fixedOption.getValue());
      }
    } catch (Exception e) {
      LOG.warn(e, e);
    }
  }

  private void setDeviceIntegerOption(SaneDevice saneDevice, IntegerOption integerOption)
      throws IOException {
    if (!(integerOption.getOptionsOrderValuePair().isActive())) {
      return;
    }
    try {
      SaneOption deviceOption = saneDevice.getOption(integerOption.getName());
      if (integerOption.getValueList() != null) {
        deviceOption.setIntegerValue(integerOption.getValueList());
        LOG.debug(integerOption.getName() + " - " + integerOption.getValueList());
      } else {
        deviceOption.setIntegerValue(integerOption.getValue());
        LOG.debug(integerOption.getName() + " - " + integerOption.getValue());
      }
    } catch (Exception e) {
      LOG.warn(e, e);
    }
  }

  private void setDeviceStringOption(SaneDevice saneDevice, StringOption stringOption)
      throws IOException {
    if (!(stringOption.getOptionsOrderValuePair().isActive())) {
      return;
    }
    try {
      SaneOption saneOption = saneDevice.getOption(stringOption.getName());
      saneOption.setStringValue(stringOption.getValue());
      LOG.debug(stringOption.getName() + " - " + stringOption.getValue());
    } catch (Exception e) {
      LOG.warn(e, e);
    }
  }

  @Override
  public final void setPasswordProvider(SanePasswordProvider passwordProvider) {
    this.passwordProvider = passwordProvider;
  }

  @Override
  public final void setSaneServiceIdentity(SaneServiceIdentity identity) {
    saneServiceIdentity = identity;
  }

  @Override
  public final void setScannerOptions(SaneDevice saneDevice, Scanner scanner) throws IOException,
      SaneException {
    scanner.getOptionOrdering().clear();
    List<SaneOption> deviceOptions = saneDevice.listOptions();
    for (SaneOption option : deviceOptions) {
      if (isOptionBlacklisted(option)) {
        continue;
      }
      scanner.addOption(optionInfo(option, 0));
    }
  }

}
