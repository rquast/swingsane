package com.swingsane.gui;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.io.IOUtils;

import au.com.southsky.jfreesane.SanePasswordProvider;

import com.google.common.io.Files;
import com.swingsane.business.auth.SwingSanePasswordProvider;
import com.swingsane.business.scanning.IScanService;
import com.swingsane.business.scanning.ScanServiceImpl;
import com.swingsane.gui.window.SwingSaneWindow;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.IPreferredDefaults;
import com.swingsane.preferences.ISwingSanePreferences;
import com.swingsane.preferences.PreferredDefaultsImpl;
import com.swingsane.preferences.SwingSanePreferencesImpl;
import com.swingsane.preferences.XStreamUtility;

/**
 * SwingSane
 *
 * @author Roland Quast (roland@formreturn.com)
 */
public final class Main {

  private static void createDesktopLauncher() {
    try {
      String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()
          .getPath();
      File jarFile = new File(jarPath);
      if (!(jarFile.exists())) {
        return;
      }
      String path = jarFile.getParentFile().getCanonicalPath();
      File launcherFile = new File(path + File.separator + "SwingSane.desktop");
      if (launcherFile.exists()) {
        return;
      }
      File iconFile = new File(path + File.separator + "swingsane_512x512.png");
      InputStream iconStream = Main.class
          .getResourceAsStream("/com/swingsane/images/swingsane_512x512.png");
      Files.write(IOUtils.toByteArray(iconStream), iconFile);
      iconStream.close();
      String desktopLauncherString = "#!/usr/bin/env xdg-open\n\n";
      desktopLauncherString += "[Desktop Entry]\n";
      desktopLauncherString += "Name=SwingSane\n";
      desktopLauncherString += "Exec=java -jar " + jarPath + "\n";
      desktopLauncherString += "Path=" + path + "\n";
      desktopLauncherString += "Icon=" + path + File.separator + "swingsane_512x512.png\n";
      desktopLauncherString += "Terminal=false\n";
      desktopLauncherString += "Type=Application\n";
      desktopLauncherString += "Categories=Office;Application;";
      LOG.debug(String.format(Localizer.localize("WritingLauncherFileMessage"),
          launcherFile.getCanonicalPath()));
      Files.write(desktopLauncherString.getBytes(), launcherFile);
      try {
        String[] cmdArray = { "chmod", "+x", launcherFile.getCanonicalPath() };
        Runtime.getRuntime().exec(cmdArray);
      } catch (Exception e) {
        LOG.warn(e, e);
      }
    } catch (URISyntaxException e) {
      LOG.warn(e, e);
    } catch (IOException e) {
      LOG.warn(e, e);
    }
  }

  private static SanePasswordProvider getPasswordProvider() {
    return new SwingSanePasswordProvider(preferences.getApplicationPreferences().getSaneLogins());
  }

  private static IPreferredDefaults getPreferredDefaults() {
    return new PreferredDefaultsImpl();
  }

  private static IScanService getScanService() {
    IScanService scanService = new ScanServiceImpl();
    scanService.setPasswordProvider(getPasswordProvider());
    scanService.setSaneServiceIdentity(preferences.getApplicationPreferences()
        .getSaneServiceIdentity());
    return scanService;
  }

  /**
   * Fixes a variety of ugly default GUI settings for Swing on Linux.
   */
  private static void initLinuxLAF() {
    try {
      System.setProperty("awt.useSystemAAFontSettings", "on");
      Font oldLabelFont = UIManager.getFont("Label.font");
      UIManager.put("Label.font", oldLabelFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldButtonFont = UIManager.getFont("Button.font");
      UIManager.put("Button.font", oldButtonFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldCheckBoxFont = UIManager.getFont("CheckBox.font");
      UIManager.put("CheckBox.font", oldCheckBoxFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldRadioButtonFont = UIManager.getFont("RadioButton.font");
      UIManager.put("RadioButton.font", oldRadioButtonFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldComboBoxFont = UIManager.getFont("ComboBox.font");
      UIManager.put("ComboBox.font", oldComboBoxFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldColorChooserFont = UIManager.getFont("ColorChooser.font");
      UIManager.put("ColorChooser.font", oldColorChooserFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldListFont = UIManager.getFont("List.font");
      UIManager.put("List.font", oldListFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldOptionPaneFont = UIManager.getFont("OptionPane.font");
      UIManager.put("OptionPane.font", oldOptionPaneFont.deriveFont(Font.PLAIN, 12.0f));
      UIManager.put("OptionPane.messageFont", oldOptionPaneFont.deriveFont(Font.PLAIN, 12.0f));
      UIManager.put("OptionPane.buttonFont", oldButtonFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldPanelFont = UIManager.getFont("Panel.font");
      UIManager.put("Panel.font", oldPanelFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldProgressBarFont = UIManager.getFont("ProgressBar.font");
      UIManager.put("ProgressBar.font", oldProgressBarFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldScrollPaneFont = UIManager.getFont("ScrollPane.font");
      UIManager.put("ScrollPane.font", oldScrollPaneFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldViewportFont = UIManager.getFont("Viewport.font");
      UIManager.put("Viewport.font", oldViewportFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldTextPaneFont = UIManager.getFont("TextPane.font");
      UIManager.put("TextPane.font", oldTextPaneFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldEditorPaneFont = UIManager.getFont("EditorPane.font");
      UIManager.put("EditorPane.font", oldEditorPaneFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldToolTipFont = UIManager.getFont("ToolTip.font");
      UIManager.put("ToolTip.font", oldToolTipFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldTreeFont = UIManager.getFont("Tree.font");
      UIManager.put("Tree.font", oldTreeFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldToggleButtonFont = UIManager.getFont("ToggleButton.font");
      UIManager.put("ToggleButton.font", oldToggleButtonFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldTabbedPaneFont = UIManager.getFont("TabbedPane.font");
      UIManager.put("TabbedPane.font", oldTabbedPaneFont.deriveFont(Font.PLAIN, 13.0f));
      Font oldTableFont = UIManager.getFont("Table.font");
      UIManager.put("Table.font", oldTableFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldTextFieldFont = UIManager.getFont("TextField.font");
      UIManager.put("TextField.font", oldTextFieldFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldPasswordFieldFont = UIManager.getFont("PasswordField.font");
      UIManager.put("PasswordField.font", oldPasswordFieldFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldTextAreaFont = UIManager.getFont("TextArea.font");
      UIManager.put("TextArea.font", oldTextAreaFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldToolBarFont = UIManager.getFont("ToolBar.font");
      UIManager.put("ToolBar.font", oldToolBarFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldTableHeaderFont = UIManager.getFont("TableHeader.font");
      UIManager.put("TableHeader.font", oldTableHeaderFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldSpinnerFont = UIManager.getFont("Spinner.font");
      UIManager.put("Spinner.font", oldSpinnerFont.deriveFont(Font.PLAIN, 11.0f));
      Font oldTitledBorderFont = UIManager.getFont("TitledBorder.font");
      UIManager.put("TitledBorder.font", oldTitledBorderFont.deriveFont(Font.BOLD, 11.0f));
      Font oldMenuItemFont = UIManager.getFont("MenuItem.font");
      UIManager.put("MenuItem.font", oldMenuItemFont.deriveFont(Font.PLAIN, 12.0f));
      Font oldMenuFont = UIManager.getFont("Menu.font");
      UIManager.put("Menu.font", oldMenuFont.deriveFont(Font.PLAIN, 12.0f));
      Font oldPopupMenuFont = UIManager.getFont("PopupMenu.font");
      UIManager.put("PopupMenu.font", oldPopupMenuFont.deriveFont(Font.PLAIN, 12.0f));
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
      LOG.warn(ex, ex);
    }
  }

  private static void initLog4J() {
    ConsoleAppender console = new ConsoleAppender();
    console.setLayout(new EnhancedPatternLayout("%d %-5p [%t] %c.%M - %m%n %throwable{short}"));
    console.setThreshold(Level.DEBUG);
    console.activateOptions();
    Logger.getRootLogger().addAppender(console);
  }

  protected static void initLookAndFeel() {

    try {
      if (LINUX) {
        initLinuxLAF();
      } else {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
    } catch (Exception e) {
      LOG.warn(e, e);
    }
  }

  private static void loadPreferences() throws IOException, ClassNotFoundException {
    preferences.load();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        preferences.cleanUp();
      }
    });
  }

  public static void main(String[] args) {

    initLog4J();

    final Properties swingSaneProperties = new Properties();
    try {
      swingSaneProperties.load(Main.class
          .getResourceAsStream("/com/swingsane/swingsane.properties"));
      swingSaneProperties.getProperty("swingsane.name");
    } catch (IOException ioex) {
      LOG.error(ioex, ioex);
      swingSaneProperties.setProperty("swingsane.name", "SwingSane");
    }

    if (LINUX) {
      createDesktopLauncher();
    }

    try {
      loadPreferences();
    } catch (ClassNotFoundException e) {
      LOG.fatal(e, e);
      System.exit(0);
    } catch (IOException e) {
      LOG.fatal(e, e);
      System.exit(0);
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        initLookAndFeel();
        window = new SwingSaneWindow();
        window.setApplicationName(swingSaneProperties.getProperty("swingsane.name"));
        window.setXstream(XStreamUtility.getXStream());
        window.setPreferredDefaults(getPreferredDefaults());
        window.setPreferences(preferences);
        window.setScanService(getScanService());
        window.initialize();
        window.setVisible(true);
      }
    });

  }

  private static final ISwingSanePreferences preferences = new SwingSanePreferencesImpl();

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(Main.class);

  /**
   * Application window singleton.
   */
  private static SwingSaneWindow window;

  /**
   * Is this Windows?
   */
  public static final boolean WINDOWS = (System.getProperty("os.name").toLowerCase()
      .startsWith("windows"));

  /**
   * Is this Mac OS X?
   */
  public static final boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase()
      .startsWith("mac os x"));

  /**
   * Is this Linux?
   */
  public static final boolean LINUX = (System.getProperty("os.name").toLowerCase()
      .startsWith("linux"));

  private Main() {
  }

}
