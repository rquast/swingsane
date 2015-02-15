package com.swingsane.preferences.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("stringOption")
public class StringOption extends BaseOptions {

  private String value;

  public final String getValue() {
    return value;
  }

  public final void setValue(String value) {
    this.value = value;
  }

}
