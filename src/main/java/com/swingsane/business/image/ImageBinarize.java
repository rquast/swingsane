package com.swingsane.business.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.apache.log4j.Logger;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public final class ImageBinarize {

  public static BufferedImage binarizeImage(BufferedImage image, int luminanceCutOff,
      boolean despeckle) {

    BufferedImage newImg = new BufferedImage(image.getWidth(), image.getHeight(),
        BufferedImage.TYPE_BYTE_BINARY);

    // just copy the image if it is already a binary image.
    if (image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
      Graphics2D g2d = newImg.createGraphics();
      g2d.drawImage(image, 0, 0, null);
      newImg.flush();
      g2d.dispose();
      return newImg;
    }

    WritableRaster raster = newImg.getRaster();

    int imageWidth = image.getWidth();
    int imageHeight = image.getHeight();

    for (int y = 0; y < imageHeight; y++) {
      for (int x = 0; x < imageWidth; x++) {

        if (isBlack(image, x, y, luminanceCutOff)) {
          raster.setSample(x, y, 0, 0);
        } else {
          raster.setSample(x, y, 0, 1);
        }

        if (despeckle) {

          if ((x < 4) || (x >= (imageWidth - 4))) {
            continue;
          }

          int sum = 0;

          for (int offset = 0; offset < 5; offset++) {
            if (offset == 2) {
              continue;
            }
            int xpos = (x - offset);
            sum += raster.getSample(xpos, y, 0);
          }

          int xpos = (x - 2);

          if (xpos > 0) {
            if (sum == 4) {
              if (raster.getSample(xpos, y, 0) != WHITE) {
                raster.setSample(xpos, y, 0, WHITE);
              }
            } else if (sum == 0) {
              if (raster.getSample(xpos, y, 0) != BLACK) {
                raster.setSample(xpos, y, 0, BLACK);
              }
            }
          }

        }

      }
    }

    if (despeckle) {

      int heightMax = imageHeight - 6;
      for (int y = 6; y < heightMax; y++) {
        for (int x = 0; x < imageWidth; x++) {

          int sum = 0;

          for (int offset = 0; offset < 7; offset++) {
            if (offset == 3) {
              continue;
            }
            int ypos = (y - offset);
            sum += raster.getSample(x, ypos, 0);
          }

          int ypos = (y - 3);

          if (ypos > 0) {
            if (sum == 6) {
              if (raster.getSample(x, ypos, 0) != WHITE) {
                raster.setSample(x, ypos, 0, WHITE);
              }
            } else if (sum == 0) {
              if (raster.getSample(x, ypos, 0) != BLACK) {
                raster.setSample(x, ypos, 0, BLACK);
              }
            }
          }

        }
      }

    }

    newImg.flush();

    return newImg;
  }

  public static boolean isBlack(BufferedImage image, int x, int y, int luminanceCutOff) {

    // return white on areas outside of image boundaries
    if ((x < 0) || (y < 0) || (x > (image.getWidth() - 1)) || (y > (image.getHeight() - 1))) {
      return false;
    }

    if (image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
      WritableRaster raster = image.getRaster();
      int pixelRGBValue = raster.getSample(x, y, 0);
      return pixelRGBValue == 0;
    }

    int pixelRGBValue;
    int r;
    int g;
    int b;
    double luminance = 0.0;

    try {
      pixelRGBValue = image.getRGB(x, y);
      r = (pixelRGBValue >> 16) & 0xff;
      g = (pixelRGBValue >> 8) & 0xff;
      b = (pixelRGBValue >> 0) & 0xff;
      luminance = (r * 0.299) + (g * 0.587) + (b * 0.114);
    } catch (Exception e) {
      LOG.error(e, e);
    }

    return (luminance < luminanceCutOff);
  }

  public static final int BLACK = 0;

  public static final int WHITE = 1;

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(ImageBinarize.class);

  private ImageBinarize() {
  }

}
