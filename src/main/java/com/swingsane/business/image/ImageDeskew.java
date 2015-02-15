package com.swingsane.business.image;

import java.awt.image.BufferedImage;

import com.swingsane.i18n.Localizer;

/**
 * ImageDeskew (JDeskew)
 *
 * A Java port of this project: http://www.codeproject.com/Articles/13615/How-to-deskew-an-image
 *
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class ImageDeskew {

  // representation of a line in the image
  public class HoughLine {

    // count of points in the line
    public int count = 0;

    // index in matrix.
    public int index = 0;

    // the line is represented as all x, y that solve y * cos(alpha) - x *
    // sin(alpha) = d
    public double alpha;
    public double d;

  }

  private static final int BLACK = 0;

  // the source image
  private BufferedImage sourceImage;
  // the range of angles to search for lines
  private double cAlphaStart = -20.0d;
  private double cAlphaStep = 0.2d;

  private int cSteps = 40 * 5;
  // pre-calculation of sin and cos
  private double[] cSinA;

  private double[] cCosA;
  // range of d
  private double cDMin;
  private double cDStep = 1.0d;

  private int cDCount;

  // count of points that fit in a line
  private int[] cHMatrix;

  private BufferedImage binarizedImage;

  private int luminanceThreshold = 165;

  private boolean despeckle = true;

  // constructor
  public ImageDeskew(BufferedImage sourceImage) throws Exception {
    this.sourceImage = sourceImage;
    binarizedImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(),
        BufferedImage.TYPE_BYTE_BINARY);
    if (sourceImage.getType() == BufferedImage.TYPE_BYTE_BINARY) {
      binarizedImage = sourceImage.getSubimage(0, 0, sourceImage.getWidth(),
          sourceImage.getHeight());
    } else {
      binarizedImage = ImageBinarize.binarizeImage(this.sourceImage, luminanceThreshold, despeckle);
    }
  }

  // Hough Transformation
  private void calc() throws Exception {

    int hMin = 2; // (int) ((this.cImage.getHeight()) / 4.0);
    int hMax = (sourceImage.getHeight()) - 2; // (int) ((this.cImage.getHeight()) * 3.0 /
    // 4.0);

    init();

    if (hMin >= hMax) {
      throw new Exception(Localizer.localize("HoughMaxLessThanHoughMinMessageText"));
    }

    for (int y = hMin; y < hMax; y++) {
      for (int x = 1; x < (sourceImage.getWidth() - 2); x++) {
        // only lower edges are considered
        if (isBlack(x, y)) {
          if (!isBlack(x, (y + 1))) {
            calc(x, y);
          }
        }
      }
    }

  }

  // calculate all lines through the point (x,y)
  private void calc(int x, int y) throws Exception {
    double d;
    int dIndex;
    int index;

    for (int alpha = 0; alpha < (cSteps - 1); alpha++) {
      d = (y * cCosA[alpha]) - (x * cSinA[alpha]);
      dIndex = (int) (d - cDMin);
      index = (dIndex * cSteps) + alpha;
      cHMatrix[index] += 1;
    }
  }

  public final double getAlpha(int index) {
    return cAlphaStart + (index * cAlphaStep);
  }

  public final BufferedImage getBinarizedImage() {
    return binarizedImage;
  }

  // calculate the skew angle of the image cImage
  public final double getSkewAngle() throws Exception {
    ImageDeskew.HoughLine[] hl;
    double sum = 0.0d;
    double count = 0.0d;

    // perform Hough Transformation
    calc();

    // top 20 of the detected lines in the image
    hl = getTop(20);

    if (hl.length >= 20) {

      // average angle of the lines
      for (int i = 0; i < 19; i++) {
        sum += hl[i].alpha;
        count += 1.0;
      }

      return (sum / count);

    } else {
      return 0.0d;
    }

  }

  // calculate the count lines in the image with most points
  private ImageDeskew.HoughLine[] getTop(int count) {

    ImageDeskew.HoughLine[] hl;
    hl = new ImageDeskew.HoughLine[count];
    for (int i = 0; i < count; i++) {
      hl[i] = new ImageDeskew.HoughLine();
    }

    ImageDeskew.HoughLine tmp;
    int j = 0;
    int alphaIndex;
    int dIndex;

    for (int i = 0; i < (count - 1); i++) {
      hl[i] = new ImageDeskew.HoughLine();
    }

    for (int i = 0; i < (cHMatrix.length - 1); i++) {
      if (cHMatrix[i] > hl[count - 1].count) {
        hl[count - 1].count = cHMatrix[i];
        hl[count - 1].index = i;
        j = count - 1;
        while ((j > 0) && (hl[j].count > hl[j - 1].count)) {
          tmp = hl[j];
          hl[j] = hl[j - 1];
          hl[j - 1] = tmp;
          j -= 1;
        }
      }
    }

    for (int i = 0; i < (count - 1); i++) {
      dIndex = hl[i].index / cSteps; // integer division, no
      // remainder
      alphaIndex = hl[i].index - (dIndex * cSteps);
      hl[i].alpha = getAlpha(alphaIndex);
      hl[i].d = dIndex + cDMin;
    }

    return hl;

  }

  private void init() {

    double angle;

    // pre-calculation of sin and cos
    cSinA = new double[cSteps - 1];
    cCosA = new double[cSteps - 1];

    for (int i = 0; i < (cSteps - 1); i++) {
      angle = (getAlpha(i) * Math.PI) / 180.0;
      cSinA[i] = Math.sin(angle);
      cCosA[i] = Math.cos(angle);
    }

    // range of d
    cDMin = -sourceImage.getWidth();
    cDCount = (int) ((2.0 * ((sourceImage.getWidth() + sourceImage.getHeight()))) / cDStep);
    cHMatrix = new int[cDCount * cSteps];

  }

  private boolean isBlack(int x, int y) {
    return (binarizedImage.getRaster().getSample(x, y, 0) == BLACK);
  }

}
