package com.swingsane.gui.panel;

import com.swingsane.business.image.transform.IImageTransform;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public interface ITransformSettingsPanel {

  IImageTransform getTransform();

  void restoreSettings();

  void setTransform(IImageTransform transform);

}
