package com.swingsane.preferences.model;

public abstract class GroupOptions extends ScannerOptions implements Option {

  private String name;
  private String description;

  private OptionsOrderValuePair optionsOrderValuePair;

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
