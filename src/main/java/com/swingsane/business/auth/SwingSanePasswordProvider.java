package com.swingsane.business.auth;

import java.util.HashMap;

import au.com.southsky.jfreesane.SanePasswordProvider;

import com.swingsane.preferences.model.Login;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class SwingSanePasswordProvider extends SanePasswordProvider {

  public static final String MARKER_MD5 = "$MD5$";

  private HashMap<String, Login> logins;

  public SwingSanePasswordProvider(HashMap<String, Login> logins) {
    this.logins = logins;
  }

  @Override
  public final boolean canAuthenticate(String resource) {
    return logins.containsKey(parseResourceString(resource));
  }

  @Override
  public final String getPassword(String resource) {
    return logins.get(parseResourceString(resource)).getPassword();
  }

  @Override
  public final String getUsername(String resource) {
    return logins.get(parseResourceString(resource)).getUsername();
  }

  private String parseResourceString(String resource) {
    return resource.contains(MARKER_MD5) ? resource.substring(0, resource.indexOf(MARKER_MD5))
        : resource;
  }

}
