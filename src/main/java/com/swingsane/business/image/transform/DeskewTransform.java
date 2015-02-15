package com.swingsane.business.image.transform;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.swingsane.business.image.ImageDeskew;
import com.swingsane.gui.panel.DeskewTransformSettingsPanel;
import com.swingsane.gui.panel.ITransformSettingsPanel;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class DeskewTransform implements IImageTransform {

  private static final double DEFAULT_DESKEW_THRESHOLD = 2.0d;

  private double deskewThreshold = DEFAULT_DESKEW_THRESHOLD;

  private File sourceImageFile;
  private File outputImageFile;

  private static final ImageTransformType imageTransformType = ImageTransformType.DESKEW;

  @Override
  public void configure() {
  }

  public final double getDeskewThreshold() {
    return deskewThreshold;
  }

  @Override
  public final File getOutputImageFile() {
    return outputImageFile;
  }

  @Override
  public final File getSourceImageFile() {
    return sourceImageFile;
  }

  @Override
  public final ITransformSettingsPanel getTransformSettingsPanel() {
    DeskewTransformSettingsPanel transformSettingsPanel = new DeskewTransformSettingsPanel();
    transformSettingsPanel.setTransform(this);
    return transformSettingsPanel;
  }

  public final BufferedImage rotate(BufferedImage image, double angle, int cx, int cy) {

    int width = image.getWidth(null);
    int height = image.getHeight(null);

    int minX, minY, maxX, maxY;
    minX = minY = maxX = maxY = 0;

    int[] corners = { 0, 0, width, 0, width, height, 0, height };

    double theta = Math.toRadians(angle);
    for (int i = 0; i < corners.length; i += 2) {
      int x = (int) (((Math.cos(theta) * (corners[i] - cx)) - (Math.sin(theta) * (corners[i + 1] - cy))) + cx);
      int y = (int) ((Math.sin(theta) * (corners[i] - cx))
          + (Math.cos(theta) * (corners[i + 1] - cy)) + cy);

      if (x > maxX) {
        maxX = x;
      }

      if (x < minX) {
        minX = x;
      }

      if (y > maxY) {
        maxY = y;
      }

      if (y < minY) {
        minY = y;
      }

    }

    cx = (cx - minX);
    cy = (cy - minY);

    BufferedImage bufferedImage = new BufferedImage((maxX - minX), (maxY - minY), image.getType());
    Graphics2D graphics2d = bufferedImage.createGraphics();
    graphics2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    graphics2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

    graphics2d.setBackground(Color.white);
    graphics2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

    AffineTransform at = new AffineTransform();
    at.rotate(theta, cx, cy);

    graphics2d.setTransform(at);
    graphics2d.drawImage(image, -minX, -minY, null);
    graphics2d.dispose();

    return bufferedImage;

  }

  public final void setDeskewThreshold(double deskewThreshold) {
    this.deskewThreshold = deskewThreshold;
  }

  @Override
  public final void setOutputImageFile(File outputImageFile) {
    this.outputImageFile = outputImageFile;
  }

  @Override
  public final void setSourceImageFile(File sourceImageFile) {
    this.sourceImageFile = sourceImageFile;
  }

  @Override
  public final String toString() {
    return imageTransformType.toString();
  }

  @Override
  public final void transform() throws Exception {
    BufferedImage bufferedImage = ImageIO.read(sourceImageFile);
    ImageDeskew imageDeskew = new ImageDeskew(bufferedImage);
    double angle = imageDeskew.getSkewAngle();
    ImageIO.write(
        rotate(bufferedImage, -angle, bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2),
        "PNG", outputImageFile);
  }

}
