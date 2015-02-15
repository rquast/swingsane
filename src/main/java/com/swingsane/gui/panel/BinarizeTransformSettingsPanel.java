package com.swingsane.gui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.swingsane.business.image.transform.BinarizeTransform;
import com.swingsane.business.image.transform.IImageTransform;
import com.swingsane.i18n.Localizer;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class BinarizeTransformSettingsPanel extends JPanel implements ITransformSettingsPanel {

  private BinarizeTransform transform;
  private JSpinner luminanceSpinner;

  public BinarizeTransformSettingsPanel() {
    initComponents();
  }

  @Override
  public final IImageTransform getTransform() {
    return transform;
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
        .localize("LuminanceSettingsBorderTitle")), new EmptyBorder(5, 5, 5, 5)));
    GridBagConstraints gbc_containerPanel = new GridBagConstraints();
    gbc_containerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_containerPanel.anchor = GridBagConstraints.NORTH;
    gbc_containerPanel.gridx = 0;
    gbc_containerPanel.gridy = 0;
    add(containerPanel, gbc_containerPanel);
    GridBagLayout gbl_containerPanel = new GridBagLayout();
    gbl_containerPanel.columnWidths = new int[] { 0, 0, 0 };
    gbl_containerPanel.rowHeights = new int[] { 24, 0 };
    gbl_containerPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
    gbl_containerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    containerPanel.setLayout(gbl_containerPanel);

    JLabel luminanceLabel = new JLabel(Localizer.localize("LuminanceThresholdLabelText"));
    luminanceLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_luminanceLabel = new GridBagConstraints();
    gbc_luminanceLabel.insets = new Insets(0, 0, 0, 5);
    gbc_luminanceLabel.anchor = GridBagConstraints.EAST;
    gbc_luminanceLabel.gridx = 0;
    gbc_luminanceLabel.gridy = 0;
    containerPanel.add(luminanceLabel, gbc_luminanceLabel);

    luminanceSpinner = new JSpinner();
    luminanceSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        luminanceStateChanged(e);
      }
    });
    luminanceSpinner.setModel(new SpinnerNumberModel(165, 0, 255, 1));
    luminanceSpinner.setFont(UIManager.getFont("Spinner.font"));
    GridBagConstraints gbc_luminanceSpinner = new GridBagConstraints();
    gbc_luminanceSpinner.anchor = GridBagConstraints.WEST;
    gbc_luminanceSpinner.gridx = 1;
    gbc_luminanceSpinner.gridy = 0;
    containerPanel.add(luminanceSpinner, gbc_luminanceSpinner);
  }

  private void luminanceStateChanged(ChangeEvent e) {
    transform.setLuminanceThreshold((Integer) luminanceSpinner.getValue());
  }

  @Override
  public final void restoreSettings() {
    luminanceSpinner.setValue(transform.getLuminanceThreshold());
  }

  @Override
  public final void setTransform(IImageTransform transform) {
    this.transform = (BinarizeTransform) transform;
    restoreSettings();
  }

}
