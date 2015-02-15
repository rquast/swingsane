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

import com.swingsane.business.image.transform.DeskewTransform;
import com.swingsane.business.image.transform.IImageTransform;
import com.swingsane.i18n.Localizer;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class DeskewTransformSettingsPanel extends JPanel implements ITransformSettingsPanel {

  private DeskewTransform transform;
  private JSpinner deskewThresholdSpinner;

  public DeskewTransformSettingsPanel() {
    initComponents();
  }

  private void deskewThresholdStateChanged(ChangeEvent e) {
    transform.setDeskewThreshold((Double) deskewThresholdSpinner.getValue());
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
        .localize("DeskewSettingsBorderTitle")), new EmptyBorder(5, 5, 5, 5)));
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

    JLabel deskewThresholdLabel = new JLabel(Localizer.localize("DeskewThresholdLabelText"));
    deskewThresholdLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_deskewThresholdLabel = new GridBagConstraints();
    gbc_deskewThresholdLabel.insets = new Insets(0, 0, 0, 5);
    gbc_deskewThresholdLabel.anchor = GridBagConstraints.EAST;
    gbc_deskewThresholdLabel.gridx = 0;
    gbc_deskewThresholdLabel.gridy = 0;
    containerPanel.add(deskewThresholdLabel, gbc_deskewThresholdLabel);

    deskewThresholdSpinner = new JSpinner();
    deskewThresholdSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        deskewThresholdStateChanged(e);
      }
    });
    deskewThresholdSpinner.setModel(new SpinnerNumberModel(2.0d, 0.0d, 180.0d, 0.1d));
    deskewThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
    GridBagConstraints gbc_deskewThresholdSpinner = new GridBagConstraints();
    gbc_deskewThresholdSpinner.anchor = GridBagConstraints.WEST;
    gbc_deskewThresholdSpinner.gridx = 1;
    gbc_deskewThresholdSpinner.gridy = 0;
    containerPanel.add(deskewThresholdSpinner, gbc_deskewThresholdSpinner);
  }

  @Override
  public final void restoreSettings() {
    deskewThresholdSpinner.setValue(transform.getDeskewThreshold());
  }

  @Override
  public final void setTransform(IImageTransform transform) {
    this.transform = (DeskewTransform) transform;
    restoreSettings();
  }

}
