package com.swingsane.preferences.model;

import com.swingsane.i18n.Localizer;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@XStreamAlias("optionsOrderValuePair")
public class OptionsOrderValuePair {

  public enum SaneOptionType {
    STRING, BOOLEAN, INTEGER, FIXED, GROUP, BUTTON
  };

  private String key;

  private SaneOptionType saneOptionType;

  private boolean active = true;

  public final String getKey() {
    return key;
  }

  public final SaneOptionType getSaneOptionType() {
    return saneOptionType;
  }

  private String getStatus() {
    if (active) {
      return " (" + Localizer.localize("OptionActiveStatusText") + ")";
    } else {
      return " (" + Localizer.localize("OptionInactiveStatusText") + ")";
    }
  }

  public final boolean isActive() {
    return active;
  }

  public final void setActive(boolean active) {
    this.active = active;
  }

  public final void setKey(String key) {
    this.key = key;
  }

  public final void setSaneOptionType(SaneOptionType saneOptionType) {
    this.saneOptionType = saneOptionType;
  }

  @Override
  public final String toString() {
    return key + getStatus();
  }

}
