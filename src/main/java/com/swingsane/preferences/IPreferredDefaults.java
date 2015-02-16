package com.swingsane.preferences;

import org.imgscalr.Scalr.Rotation;

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

  Rotation DEFAULT_ROTATION = Rotation.CW_90;

  int DEFAULT_LUMINANCE_THRESHOLD = 165;

  double DEFAULT_DESKEW_THRESHOLD = 2.0d;

  ColorMode getColor();

  double getDefaultDeskewThreshold();

  int getDefaultLuminanceThreshold();

  Rotation getDefaultRotation();

  int getResolution();

  void setColor(ColorMode color);

  void setResolution(int resolution);

  void update(Scanner scanner);

}
