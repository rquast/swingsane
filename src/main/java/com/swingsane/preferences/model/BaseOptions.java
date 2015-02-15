package com.swingsane.preferences.model;

public abstract class BaseOptions implements Option {

  private String name;
  private String description;
  private int constraintType;

  private Constraints constraints;

  private OptionsOrderValuePair optionsOrderValuePair;

  public final Constraints getConstraints() {
    return constraints;
  }

  public final int getConstraintType() {
    return constraintType;
  }

  public final String getDescription() {
    return description;
  }

  @Override
  public final String getName() {
    return name;
  }

  @Override
  public final OptionsOrderValuePair getOptionsOrderValuePair() {
    return optionsOrderValuePair;
  }

  public final void setConstraints(Constraints constraints) {
    this.constraints = constraints;
  }

  public final void setConstraintType(int constraintType) {
    this.constraintType = constraintType;
  }

  public final void setDescription(String description) {
    this.description = description;
  }

  public final void setName(String name) {
    this.name = name;
  }

  @Override
  public final void setOptionsOrderValuePair(OptionsOrderValuePair optionsOrderValuePair) {
    this.optionsOrderValuePair = optionsOrderValuePair;
  }

}
