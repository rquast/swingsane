package com.swingsane.preferences;

import java.io.File;

import com.swingsane.preferences.model.ApplicationPreferences;

public interface ISwingSanePreferences extends BasePreferences {

  void cleanUp();

  ApplicationPreferences getApplicationPreferences();

  File getTempDirectory();

}
