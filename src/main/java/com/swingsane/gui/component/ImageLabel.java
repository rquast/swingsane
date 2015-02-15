package com.swingsane.gui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;

import com.swingsane.gui.panel.PreviewPanel;

@SuppressWarnings("serial")
public class ImageLabel extends JLabel {

  private PreviewPanel viewer;

  public ImageLabel() {
    setDoubleBuffered(true);
  }

  @Override
  public final void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (viewer != null) {
      viewer.renderImagePreview((Graphics2D) g);
    }
  }

  public final void setViewer(PreviewPanel viewer) {
    this.viewer = viewer;
    revalidate();
  }

}
