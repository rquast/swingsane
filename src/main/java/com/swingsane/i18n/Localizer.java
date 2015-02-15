package com.swingsane.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * An i18n localizer utility class.
 *
 * @author Roland Quast (roland@formreturn.com)
 */
public final class Localizer {

  /**
   * Returns the current locale.
   *
   * @return the current locale
   */
  public static Locale getCurrentLocale() {
    if (currentLocale == null) {
      setCurrentLocale(Locale.getDefault());
    }
    return currentLocale;
  }

  /**
   * Returns a translation for the specified key in the current locale.
   *
   * @param key
   *          a translation key listed in the messages resource file
   * @return a translation for the specified key in the current locale
   */
  public static String localize(String key) {
    try {
      return BUNDLE.getString(key);
    } catch (Exception ex) {
      LOG.warn(ex.getLocalizedMessage(), ex);
      LOG.warn("Missing translation: " + key);
      return key;
    }
  }

  /**
   * Sets the current locale and attempts to load the messages resource file for the locale.
   *
   * @param currentLocale
   *          a locale that has a messages resource
   */
  public static void setCurrentLocale(Locale currentLocale) {
    Localizer.currentLocale = currentLocale;
    try {
      ResourceBundle.getBundle(MESSAGES_RESOURCE, currentLocale);
    } catch (Exception ex) {
      ResourceBundle.getBundle(MESSAGES_RESOURCE, DEFAULT_LOCALE);
      currentLocale = DEFAULT_LOCALE;
    }
  }

  private static final String MESSAGES_RESOURCE = "com.swingsane.i18n.messages";

  private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

  private static Locale currentLocale = DEFAULT_LOCALE;

  private static final Logger LOG = Logger.getLogger(Localizer.class);

  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MESSAGES_RESOURCE,
      getCurrentLocale());

  private Localizer() {
  }

}
