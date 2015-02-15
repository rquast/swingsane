package com.swingsane.preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import com.google.common.io.Files;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.model.ApplicationPreferences;
import com.swingsane.preferences.model.Scanner;
import com.thoughtworks.xstream.XStream;

public class SwingSanePreferencesImpl implements ISwingSanePreferences {

  private XStream xstream;

  private ApplicationPreferences applicationPreferences = new ApplicationPreferences();

  private static final File tmpDir = Files.createTempDir();

  private static final String preferencesPath = System.getProperty("user.home") + File.separator
      + ".swingsane";

  private static final String preferencesFileName = "preferences.xml";

  @Override
  public final void cleanUp() {
    tmpDir.delete();
  }

  @Override
  public final ApplicationPreferences getApplicationPreferences() {
    return applicationPreferences;
  }

  private File getPreferencesFile() throws IOException {
    File preferencesDir = new File(preferencesPath);
    if (!(preferencesDir.exists())) {
      if (!(preferencesDir.mkdirs()) || !(preferencesDir.canWrite())) {
        throw new IOException(Localizer.localize("CannotOpenPreferencesDirectoryMessage"));
      }
    }
    return new File(preferencesDir.getAbsolutePath() + File.separator + preferencesFileName);
  }

  @Override
  public final File getTempDirectory() {
    return tmpDir;
  }

  @Override
  public final synchronized void load() throws IOException, ClassNotFoundException {

    tmpDir.deleteOnExit();
    xstream = XStreamUtility.getXStream();

    File preferencesFile = getPreferencesFile();
    if (!(preferencesFile.exists())) {
      save();
    }

    ObjectInputStream s = null;
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(preferencesFile);
      s = xstream.createObjectInputStream(new InputStreamReader(fis, "UTF-8"));
      applicationPreferences = ((ApplicationPreferences) s.readObject());
      for (Scanner scanner : applicationPreferences.getScannerList()) {
        PreferencesUtils.restoreCircularReferences(scanner);
      }
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (s != null) {
        s.close();
      }
      if (fis != null) {
        fis.close();
      }
    }

  }

  @Override
  public final synchronized void save() throws IOException {
    synchronized (SwingSanePreferencesImpl.class) {
      File preferencesFile = getPreferencesFile();
      String rootNodeName = "swingSane";
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          preferencesFile), "UTF-8"));
      out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      ObjectOutputStream oos;
      oos = xstream.createObjectOutputStream(out, rootNodeName);
      oos.writeObject(applicationPreferences);
      oos.close();
    }
  }

}
