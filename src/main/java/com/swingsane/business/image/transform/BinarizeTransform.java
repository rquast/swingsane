package com.swingsane.business.image.transform;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.swingsane.business.image.ImageBinarize;
import com.swingsane.gui.panel.BinarizeTransformSettingsPanel;
import com.swingsane.gui.panel.ITransformSettingsPanel;
import com.swingsane.preferences.IPreferredDefaults;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class BinarizeTransform implements IImageTransform {

  private int luminanceThreshold;

  private File sourceImageFile;
  private File outputImageFile;

  private static final ImageTransformType imageTransformType = ImageTransformType.BINARIZE;

  @Override
  public void configure(IPreferredDefaults preferredDefaultsImpl) throws Exception {
    luminanceThreshold = preferredDefaultsImpl.getDefaultLuminanceThreshold();
  }

  public final int getLuminanceThreshold() {
    return luminanceThreshold;
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
    BinarizeTransformSettingsPanel transformSettingsPanel = new BinarizeTransformSettingsPanel();
    transformSettingsPanel.setTransform(this);
    return transformSettingsPanel;
  }

  public final void setLuminanceThreshold(int luminanceThreshold) {
    this.luminanceThreshold = luminanceThreshold;
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
  public final void transform() throws IOException {
    BufferedImage bufferedImage = ImageIO.read(sourceImageFile);
    ImageIO.write(ImageBinarize.binarizeImage(bufferedImage, luminanceThreshold, false), "PNG",
        outputImageFile);
  }

}
