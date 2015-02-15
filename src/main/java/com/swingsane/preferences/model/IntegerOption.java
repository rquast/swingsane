package com.swingsane.preferences.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("integerOption")
public class IntegerOption extends BaseOptions {

  private int value;

  private List<Integer> valueList;

  public final int getValue() {
    return value;
  }

  public final List<Integer> getValueList() {
    return valueList;
  }

  public final void setValue(int value) {
    this.value = value;
  }

  public final void setValueList(List<Integer> valueList) {
    this.valueList = valueList;
  }

}
