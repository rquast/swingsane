package com.swingsane.preferences.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("booleanOption")
public class BooleanOption extends BaseOptions {

  private boolean value;

  public final boolean getValue() {
    return value;
  }

  public final void setValue(boolean value) {
    this.value = value;
  }

}
