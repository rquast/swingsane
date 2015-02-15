package com.swingsane.business.image.transform;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;

import com.swingsane.gui.panel.ITransformSettingsPanel;
import com.swingsane.gui.panel.RotateTransformSettingsPanel;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class RotateTransform implements IImageTransform {

  private Rotation rotation = Rotation.CW_90;
  private File sourceImageFile;
  private File outputImageFile;

  private static final ImageTransformType imageTransformType = ImageTransformType.ROTATE;

  @Override
  public void configure() {
  }

  @Override
  public final File getOutputImageFile() {
    return outputImageFile;
  }

  public final Rotation getRotation() {
    return rotation;
  }

  @Override
  public final File getSourceImageFile() {
    return sourceImageFile;
  }

  @Override
  public final ITransformSettingsPanel getTransformSettingsPanel() {
    RotateTransformSettingsPanel transformSettingsPanel = new RotateTransformSettingsPanel();
    transformSettingsPanel.setTransform(this);
    return transformSettingsPanel;
  }

  @Override
  public final void setOutputImageFile(File outputImageFile) {
    this.outputImageFile = outputImageFile;
  }

  public final void setRotation(Rotation rotation) {
    this.rotation = rotation;
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
  public final void transform() throws IOException {
    BufferedImage bufferedImage = Scalr.rotate(ImageIO.read(sourceImageFile), rotation);
    ImageIO.write(bufferedImage, "PNG", outputImageFile);
  }

}
