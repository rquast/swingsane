package com.swingsane.business.image.transform;

import java.io.File;

import com.swingsane.gui.panel.ITransformSettingsPanel;
import com.swingsane.preferences.IPreferredDefaults;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public interface IImageTransform {

  void configure(IPreferredDefaults preferredDefaultsImpl) throws Exception;

  File getOutputImageFile();

  File getSourceImageFile();

  ITransformSettingsPanel getTransformSettingsPanel();

  void setOutputImageFile(File outputImageFile);

  void setSourceImageFile(File sourceImageFile);

  @Override
  String toString();

  void transform() throws Exception;

}
