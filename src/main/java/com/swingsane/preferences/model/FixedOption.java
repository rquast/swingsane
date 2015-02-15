package com.swingsane.preferences.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("fixedOption")
public class FixedOption extends BaseOptions {

  private double value;

  private List<Double> valueList;

  public final double getValue() {
    return value;
  }

  public final List<Double> getValueList() {
    return valueList;
  }

  public final void setValue(double value) {
    this.value = value;
  }

  public final void setValueList(List<Double> valueList) {
    this.valueList = valueList;
  }

}
