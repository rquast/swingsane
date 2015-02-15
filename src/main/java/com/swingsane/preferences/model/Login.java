package com.swingsane.preferences.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("login")
public class Login {

  private String username;

  private String password;

  public final String getPassword() {
    return password;
  }

  public final String getUsername() {
    return username;
  }

  public final void setPassword(String password) {
    this.password = password;
  }

  public final void setUsername(String username) {
    this.username = username;
  }

}
