package com.swingsane.preferences.model;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ScannerOptions {

  private HashMap<String, BooleanOption> booleanOptions = new HashMap<String, BooleanOption>();
  private HashMap<String, IntegerOption> integerOptions = new HashMap<String, IntegerOption>();
  private HashMap<String, FixedOption> fixedOptions = new HashMap<String, FixedOption>();
  private HashMap<String, StringOption> stringOptions = new HashMap<String, StringOption>();
  private HashMap<String, ButtonOption> buttonOptions = new HashMap<String, ButtonOption>();
  private HashMap<String, GroupOption> groupOptions = new HashMap<String, GroupOption>();

  /**
   * OptionsOrderValuePair also exists in the Option interface. Circular referencing helps to speed
   * the name lookup.
   */
  private ArrayList<OptionsOrderValuePair> optionOrdering = new ArrayList<OptionsOrderValuePair>();

  public final void addOption(Option option) {
    if (option == null) {
      return;
    }
    OptionsOrderValuePair vp = option.getOptionsOrderValuePair();
    if (option instanceof BooleanOption) {
      booleanOptions.put(option.getName(), (BooleanOption) option);
    } else if (option instanceof IntegerOption) {
      integerOptions.put(option.getName(), (IntegerOption) option);
    } else if (option instanceof FixedOption) {
      fixedOptions.put(option.getName(), (FixedOption) option);
    } else if (option instanceof StringOption) {
      stringOptions.put(option.getName(), (StringOption) option);
    } else if (option instanceof ButtonOption) {
      buttonOptions.put(option.getName(), (ButtonOption) option);
    } else if (option instanceof GroupOption) {
      groupOptions.put(option.getName(), (GroupOption) option);
    }
    optionOrdering.add(vp);
  }

  public final HashMap<String, BooleanOption> getBooleanOptions() {
    return booleanOptions;
  }

  public final HashMap<String, ButtonOption> getButtonOptions() {
    return buttonOptions;
  }

  public final HashMap<String, FixedOption> getFixedOptions() {
    return fixedOptions;
  }

  public final HashMap<String, GroupOption> getGroupOptions() {
    return groupOptions;
  }

  public final HashMap<String, IntegerOption> getIntegerOptions() {
    return integerOptions;
  }

  public final ArrayList<OptionsOrderValuePair> getOptionOrdering() {
    return optionOrdering;
  }

  public final HashMap<String, StringOption> getStringOptions() {
    return stringOptions;
  }

  public final void setBooleanOptions(HashMap<String, BooleanOption> booleanOptions) {
    this.booleanOptions = booleanOptions;
  }

  public final void setButtonOptions(HashMap<String, ButtonOption> buttonOptions) {
    this.buttonOptions = buttonOptions;
  }

  public final void setFixedOptions(HashMap<String, FixedOption> fixedOptions) {
    this.fixedOptions = fixedOptions;
  }

  public final void setGroupOptions(HashMap<String, GroupOption> groupOptions) {
    this.groupOptions = groupOptions;
  }

  public final void setIntegerOptions(HashMap<String, IntegerOption> integerOptions) {
    this.integerOptions = integerOptions;
  }

  public final void setOptionOrdering(ArrayList<OptionsOrderValuePair> optionOrdering) {
    this.optionOrdering = optionOrdering;
  }

  public final void setStringOptions(HashMap<String, StringOption> stringOptions) {
    this.stringOptions = stringOptions;
  }

}
