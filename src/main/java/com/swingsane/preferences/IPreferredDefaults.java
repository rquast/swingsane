package com.swingsane.preferences;

import com.swingsane.preferences.model.Scanner;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public interface IPreferredDefaults {

  public enum ColorMode {
    BLACK_AND_WHITE, COLOR, GRAYSCALE
  }

  public enum Source {
    AUTOMATIC_DOCUMENT_FEEDER, FLATBED
  }

  int DEFAULT_RESOLUTION = 300;

  ColorMode getColor();

  int getResolution();

  void setColor(ColorMode color);

  void setResolution(int resolution);

  void update(Scanner scanner);

}
