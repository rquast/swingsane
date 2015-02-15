package com.swingsane.preferences.model;

import java.util.List;

public class Constraints {

  private Integer minimumInteger;

  private Integer maximumInteger;

  private Integer quantumInteger;

  private Double minimumFixed;

  private Double maximumFixed;

  private Double quantumFixed;

  private List<Integer> integerList;

  private List<String> stringList;

  private List<Double> fixedList;

  public final List<Double> getFixedList() {
    return fixedList;
  }

  public final List<Integer> getIntegerList() {
    return integerList;
  }

  public final Double getMaximumFixed() {
    return maximumFixed;
  }

  public final Integer getMaximumInteger() {
    return maximumInteger;
  }

  public final Double getMinimumFixed() {
    return minimumFixed;
  }

  public final Integer getMinimumInteger() {
    return minimumInteger;
  }

  public final Double getQuantumFixed() {
    return quantumFixed;
  }

  public final Integer getQuantumInteger() {
    return quantumInteger;
  }

  public final List<String> getStringList() {
    return stringList;
  }

  public final void setFixedList(List<Double> fixedList) {
    this.fixedList = fixedList;
  }

  public final void setIntegerList(List<Integer> integerList) {
    this.integerList = integerList;
  }

  public final void setMaximumFixed(Double maximumFixed) {
    this.maximumFixed = maximumFixed;
  }

  public final void setMaximumInteger(Integer maximumInteger) {
    this.maximumInteger = maximumInteger;
  }

  public final void setMinimumFixed(Double minimumFixed) {
    this.minimumFixed = minimumFixed;
  }

  public final void setMinimumInteger(Integer minimumInteger) {
    this.minimumInteger = minimumInteger;
  }

  public final void setQuantumFixed(Double quantumFixed) {
    this.quantumFixed = quantumFixed;
  }

  public final void setQuantumInteger(Integer quantumInteger) {
    this.quantumInteger = quantumInteger;
  }

  public final void setStringList(List<String> stringList) {
    this.stringList = stringList;
  }

}
