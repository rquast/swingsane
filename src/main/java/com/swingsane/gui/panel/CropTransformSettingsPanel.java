package com.swingsane.gui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.swingsane.business.image.transform.CropTransform;
import com.swingsane.business.image.transform.IImageTransform;
import com.swingsane.i18n.Localizer;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class CropTransformSettingsPanel extends JPanel implements ITransformSettingsPanel {

  private CropTransform transform;
  private JSpinner xSpinner;
  private JSpinner ySpinner;
  private JSpinner widthSpinner;
  private JSpinner heightSpinner;

  public CropTransformSettingsPanel() {
    initComponents();
  }

  @Override
  public final IImageTransform getTransform() {
    return transform;
  }

  private void heightSpinnerStateChanged(ChangeEvent e) {
    transform.setHeight((Integer) heightSpinner.getValue());
  }

  private void initComponents() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 32, 0 };
    gridBagLayout.rowHeights = new int[] { 24, 0 };
    gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);

    JPanel containerPanel = new JPanel();
    containerPanel.setBorder(new CompoundBorder(new TitledBorder(Localizer
        .localize("CropSettingsBorderTitle")), new EmptyBorder(5, 5, 5, 5)));
    GridBagConstraints gbc_containerPanel = new GridBagConstraints();
    gbc_containerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_containerPanel.anchor = GridBagConstraints.NORTH;
    gbc_containerPanel.gridx = 0;
    gbc_containerPanel.gridy = 0;
    add(containerPanel, gbc_containerPanel);
    GridBagLayout gbl_containerPanel = new GridBagLayout();
    gbl_containerPanel.columnWidths = new int[] { 0, 100, 10, 0, 100, 0, 0 };
    gbl_containerPanel.rowHeights = new int[] { 24, 0, 0 };
    gbl_containerPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 1.0,
        Double.MIN_VALUE };
    gbl_containerPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
    containerPanel.setLayout(gbl_containerPanel);

    JLabel xLabel = new JLabel(Localizer.localize("CropXLabelText"));
    xLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_xLabel = new GridBagConstraints();
    gbc_xLabel.insets = new Insets(0, 0, 5, 5);
    gbc_xLabel.anchor = GridBagConstraints.EAST;
    gbc_xLabel.gridx = 0;
    gbc_xLabel.gridy = 0;
    containerPanel.add(xLabel, gbc_xLabel);

    xSpinner = new JSpinner();
    xSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        xSpinnerStateChanged(e);
      }
    });
    xSpinner.setFont(UIManager.getFont("Spinner.font"));
    GridBagConstraints gbc_xSpinner = new GridBagConstraints();
    gbc_xSpinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_xSpinner.insets = new Insets(0, 0, 5, 5);
    gbc_xSpinner.gridx = 1;
    gbc_xSpinner.gridy = 0;
    containerPanel.add(xSpinner, gbc_xSpinner);

    ySpinner = new JSpinner();
    ySpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        ySpinnerStateChanged(e);
      }
    });

    JLabel yLabel = new JLabel(Localizer.localize("CropYLabelText"));
    yLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_yLabel = new GridBagConstraints();
    gbc_yLabel.anchor = GridBagConstraints.EAST;
    gbc_yLabel.insets = new Insets(0, 0, 5, 5);
    gbc_yLabel.gridx = 3;
    gbc_yLabel.gridy = 0;
    containerPanel.add(yLabel, gbc_yLabel);
    ySpinner.setFont(UIManager.getFont("Spinner.font"));
    GridBagConstraints gbc_ySpinner = new GridBagConstraints();
    gbc_ySpinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_ySpinner.insets = new Insets(0, 0, 5, 5);
    gbc_ySpinner.gridx = 4;
    gbc_ySpinner.gridy = 0;
    containerPanel.add(ySpinner, gbc_ySpinner);

    JLabel widthLabel = new JLabel(Localizer.localize("CropWidthLabelText"));
    widthLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_widthLabel = new GridBagConstraints();
    gbc_widthLabel.anchor = GridBagConstraints.EAST;
    gbc_widthLabel.insets = new Insets(0, 0, 0, 5);
    gbc_widthLabel.gridx = 0;
    gbc_widthLabel.gridy = 1;
    containerPanel.add(widthLabel, gbc_widthLabel);

    widthSpinner = new JSpinner();
    widthSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        widthSpinnerStateChanged(e);
      }
    });
    widthSpinner.setFont(UIManager.getFont("Spinner.font"));
    GridBagConstraints gbc_widthSpinner = new GridBagConstraints();
    gbc_widthSpinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_widthSpinner.insets = new Insets(0, 0, 0, 5);
    gbc_widthSpinner.gridx = 1;
    gbc_widthSpinner.gridy = 1;
    containerPanel.add(widthSpinner, gbc_widthSpinner);

    heightSpinner = new JSpinner();
    heightSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        heightSpinnerStateChanged(e);
      }
    });

    JLabel heightLabel = new JLabel(Localizer.localize("CropHeightLabelText"));
    heightLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_heightLabel = new GridBagConstraints();
    gbc_heightLabel.anchor = GridBagConstraints.EAST;
    gbc_heightLabel.insets = new Insets(0, 0, 0, 5);
    gbc_heightLabel.gridx = 3;
    gbc_heightLabel.gridy = 1;
    containerPanel.add(heightLabel, gbc_heightLabel);
    heightSpinner.setFont(UIManager.getFont("Spinner.font"));
    GridBagConstraints gbc_heightSpinner = new GridBagConstraints();
    gbc_heightSpinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_heightSpinner.insets = new Insets(0, 0, 0, 5);
    gbc_heightSpinner.gridx = 4;
    gbc_heightSpinner.gridy = 1;
    containerPanel.add(heightSpinner, gbc_heightSpinner);
  }

  @Override
  public final void restoreSettings() {
    Rectangle2D bounds = transform.getBounds();
    xSpinner.setValue((int) bounds.getX());
    ySpinner.setValue((int) bounds.getY());
    widthSpinner.setValue((int) bounds.getWidth());
    heightSpinner.setValue((int) bounds.getHeight());
  }

  @Override
  public final void setTransform(IImageTransform transform) {
    this.transform = (CropTransform) transform;
    restoreSettings();
  }

  private void widthSpinnerStateChanged(ChangeEvent e) {
    transform.setWidth((Integer) widthSpinner.getValue());
  }

  private void xSpinnerStateChanged(ChangeEvent e) {
    transform.setX((Integer) xSpinner.getValue());
  }

  private void ySpinnerStateChanged(ChangeEvent e) {
    transform.setY((Integer) ySpinner.getValue());
  }

}
