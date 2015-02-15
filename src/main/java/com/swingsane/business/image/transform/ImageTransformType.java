package com.swingsane.business.image.transform;

import com.swingsane.i18n.Localizer;

public enum ImageTransformType {
  DESKEW, BINARIZE, ROTATE, CROP;

  @Override
  public String toString() {
    switch (this) {
    case DESKEW:
      return Localizer.localize("DeskewTransformName");
    case BINARIZE:
      return Localizer.localize("BinarizeTransformName");
    case ROTATE:
      return Localizer.localize("RotateTransformName");
    case CROP:
      return Localizer.localize("CropTransformName");
    default:
      throw new IllegalArgumentException();
    }
  }

}
