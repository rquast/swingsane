package com.swingsane.business.image.transform;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.swingsane.gui.dialog.CropImageDialog;
import com.swingsane.gui.panel.CropTransformSettingsPanel;
import com.swingsane.gui.panel.ITransformSettingsPanel;
import com.swingsane.preferences.IPreferredDefaults;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class CropTransform implements IImageTransform {

  private Rectangle bounds = new Rectangle();

  private ArrayList<ChangeListener> listenerList = new ArrayList<ChangeListener>();

  private File sourceImageFile;
  private File outputImageFile;

  private Component parent;

  private boolean readyToTransform = false;

  private static final ImageTransformType imageTransformType = ImageTransformType.CROP;

  public final void addChangeListener(final ChangeListener listener) {
    listenerList.add(listener);
  }

  @Override
  public final void configure(IPreferredDefaults preferredDefaultsImpl) throws Exception {
    if (!isReadyToTransform()) {
      CropImageDialog cropImageDialog = new CropImageDialog(parent);
      cropImageDialog.setTransform(this);
      cropImageDialog.loadImage();
      cropImageDialog.setModal(true);
      cropImageDialog.setVisible(true);
      if (cropImageDialog.getDialogResult() == JOptionPane.OK_OPTION) {
        setReadyToTransform(true);
      }
    }
  }

  private void fireChangeEvent(ChangeEvent e) {
    for (ChangeListener changeListener : listenerList) {
      changeListener.stateChanged(e);
    }
  }

  public final Rectangle getBounds() {
    return bounds;
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
    CropTransformSettingsPanel transformSettingsPanel = new CropTransformSettingsPanel();
    transformSettingsPanel.setTransform(this);
    return transformSettingsPanel;
  }

  public final boolean isReadyToTransform() {
    return readyToTransform;
  }

  public final void setBounds(Rectangle bounds) {
    this.bounds = bounds;
  }

  public final void setHeight(int height) {
    bounds.height = height;
    ChangeEvent event = new ChangeEvent(bounds);
    fireChangeEvent(event);
  }

  @Override
  public final void setOutputImageFile(File outputImageFile) {
    this.outputImageFile = outputImageFile;
  }

  public final void setParentComponent(Component parentComponent) {
    parent = parentComponent;
  }

  public final void setReadyToTransform(boolean readyToTransform) {
    this.readyToTransform = readyToTransform;
  }

  @Override
  public final void setSourceImageFile(File sourceImageFile) {
    this.sourceImageFile = sourceImageFile;
  }

  public final void setWidth(int width) {
    bounds.width = width;
    ChangeEvent event = new ChangeEvent(bounds);
    fireChangeEvent(event);
  }

  public final void setX(int x) {
    bounds.x = x;
    ChangeEvent event = new ChangeEvent(bounds);
    fireChangeEvent(event);
  }

  public final void setY(int y) {
    bounds.y = y;
    ChangeEvent event = new ChangeEvent(bounds);
    fireChangeEvent(event);
  }

  @Override
  public final String toString() {
    return imageTransformType.toString();
  }

  @Override
  public final void transform() throws IOException {
    if (!isReadyToTransform()) {
      return;
    }
    BufferedImage bufferedImage = ImageIO.read(sourceImageFile).getSubimage(bounds.x, bounds.y,
        bounds.width, bounds.height);
    ImageIO.write(bufferedImage, "PNG", outputImageFile);
  }

}
