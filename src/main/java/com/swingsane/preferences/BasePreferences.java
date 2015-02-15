package com.swingsane.preferences;

import java.io.IOException;

public interface BasePreferences {

  void load() throws IOException, ClassNotFoundException;

  void save() throws IOException;

}
