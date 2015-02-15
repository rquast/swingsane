package com.swingsane.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class FilenameExtensionFilter implements FilenameFilter {

  private ArrayList<String> extensions = new ArrayList<String>();

  @Override
  public final boolean accept(File dir, String name) {
    if (new File(dir, name).isDirectory()) {
      return false;
    }
    name = name.toLowerCase();
    return extensions.contains(getExtension(name));
  }

  public final void addExtension(String extension) {
    extensions.add(extension);
  }

  private String getExtension(String name) {
    if (name != null) {
      int i = name.lastIndexOf('.');
      if ((i > 0) && (i < (name.length() - 1))) {
        return name.substring(i + 1).toLowerCase();
      }
    }
    return null;
  }

}
