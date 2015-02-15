package com.swingsane.util;

import java.awt.Component;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.swingsane.i18n.Localizer;

/**
 * @author Roland Quast (rquast@formreturn.com)
 *
 */
public final class Misc {

  public static void openURL(String url) {
    String osName = System.getProperty("os.name");

    try {
      if (osName.startsWith("Mac OS")) {
        Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
        Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
        openURL.invoke(null, new Object[] { url });
      } else if (osName.startsWith("Windows")) {
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
      } else { // assume Unix or Linux
        String[] browsers = { "chromium-browser", "google-chrome", "google-chrome-stable",
            "chrome", "/opt/google/chrome/chrome", "firefox", "opera", "konqueror", "epiphany",
            "mozilla", "netscape" };
        String browser = null;
        for (int count = 0; (count < browsers.length) && (browser == null); count++) {
          if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0) {
            browser = browsers[count];
          }
        }
        if (browser == null) {
          throw new Exception(Localizer.localize("LaunchWebBrowserNotFound"));
        } else {
          Runtime.getRuntime().exec(new String[] { browser, url });
        }
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage());
    }

  }

  public static boolean showConfirmDialog(final Component parent, final String title,
      final String message, final String confirmButtonText, final String cancelButtonText) {

    String options[] = new String[] { confirmButtonText, cancelButtonText };

    int result = JOptionPane.showOptionDialog(parent, message, title, JOptionPane.DEFAULT_OPTION,
        JOptionPane.WARNING_MESSAGE, null, options, options[0]);

    return result == 0;

  }

  public static void showErrorMsg(final Component parent, final String message) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JOptionPane.showMessageDialog(parent, message, Localizer.localize("ErrorMessageTitle"),
            JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  public static void showSuccessMsg(final Component parent, final String message) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JOptionPane.showMessageDialog(parent, message, Localizer.localize("SuccessMessageTitle"),
            JOptionPane.INFORMATION_MESSAGE);
      }
    });
  }

  private Misc() {
  }

}
