package com.swingsane.gui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.imgscalr.Scalr.Rotation;

import com.swingsane.business.image.transform.IImageTransform;
import com.swingsane.business.image.transform.RotateTransform;
import com.swingsane.i18n.Localizer;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class RotateTransformSettingsPanel extends JPanel implements ITransformSettingsPanel {

  private JComboBox<Rotation> rotationComboBox;

  private RotateTransform transform;

  public RotateTransformSettingsPanel() {
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
        .localize("RotationSettingsBorderTitle")), new EmptyBorder(5, 5, 5, 5)));
    GridBagConstraints gbc_containerPanel = new GridBagConstraints();
    gbc_containerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_containerPanel.anchor = GridBagConstraints.NORTH;
    gbc_containerPanel.gridx = 0;
    gbc_containerPanel.gridy = 0;
    add(containerPanel, gbc_containerPanel);
    GridBagLayout gbl_containerPanel = new GridBagLayout();
    gbl_containerPanel.columnWidths = new int[] { 0, 0 };
    gbl_containerPanel.rowHeights = new int[] { 24, 0 };
    gbl_containerPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_containerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    containerPanel.setLayout(gbl_containerPanel);

    rotationComboBox = new JComboBox<Rotation>();
    rotationComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        rotationSelectionActionPerformed(e);
      }
    });
    rotationComboBox.setFont(UIManager.getFont("ComboBox.font"));
    rotationComboBox.setModel(new DefaultComboBoxModel<Rotation>(new Rotation[] { Rotation.CW_90,
        Rotation.CW_180, Rotation.CW_270 }));
    GridBagConstraints gbc_rotationComboBox = new GridBagConstraints();
    gbc_rotationComboBox.anchor = GridBagConstraints.NORTH;
    gbc_rotationComboBox.gridx = 0;
    gbc_rotationComboBox.gridy = 0;
    containerPanel.add(rotationComboBox, gbc_rotationComboBox);
  }

  @Override
  public final void restoreSettings() {
    rotationComboBox.setSelectedItem(transform.getRotation());
  }

  private void rotationSelectionActionPerformed(ActionEvent e) {
    transform.setRotation((Rotation) rotationComboBox.getSelectedItem());
  }

  @Override
  public final void setTransform(IImageTransform transform) {
    this.transform = (RotateTransform) transform;
    restoreSettings();
  }

}
