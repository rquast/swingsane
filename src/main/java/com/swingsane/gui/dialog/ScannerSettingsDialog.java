package com.swingsane.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.swingsane.i18n.Localizer;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class ScannerSettingsDialog extends JDialog {

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(ScannerSettingsDialog.class);

  private static final int BOUNDS_WIDTH = 400;
  private static final int BOUNDS_HEIGHT = 260;

  private final Dimension bounds = new Dimension(BOUNDS_WIDTH, BOUNDS_HEIGHT);

  private JTextField remoteAddressTextField;
  private JSpinner portNumberSpinner;

  private int dialogResult = JOptionPane.CANCEL_OPTION;
  private JTextField descriptionTextField;

  public ScannerSettingsDialog(Component parent) {
    initComponents();
    pack();
    setLocationRelativeTo(parent);
  }

  private void cancelButtonActionPerformed(ActionEvent e) {
    dispose();
  }

  public final String getDescription() {
    return descriptionTextField.getText().trim();
  }

  public final int getDialogResult() {
    return dialogResult;
  }

  public final int getPortNumber() {
    return (Integer) portNumberSpinner.getValue();
  }

  public final String getRemoteAddress() {
    return remoteAddressTextField.getText().trim();
  }

  private void initComponents() {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setResizable(false);
    setBounds(0, 0, bounds.width, bounds.height);
    setPreferredSize(bounds);
    setSize(bounds);
    setMinimumSize(bounds);

    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
    getContentPane().add(contentPane, BorderLayout.CENTER);
    GridBagLayout gbl_contentPane = new GridBagLayout();
    gbl_contentPane.columnWidths = new int[] { 0, 0 };
    gbl_contentPane.rowHeights = new int[] { 0, 0, 0 };
    gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_contentPane.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    contentPane.setLayout(gbl_contentPane);

    JPanel containerPanel = new JPanel();
    GridBagConstraints gbc_containerPanel = new GridBagConstraints();
    gbc_containerPanel.insets = new Insets(0, 0, 5, 0);
    gbc_containerPanel.fill = GridBagConstraints.BOTH;
    gbc_containerPanel.gridx = 0;
    gbc_containerPanel.gridy = 0;
    contentPane.add(containerPanel, gbc_containerPanel);
    GridBagLayout gbl_containerPanel = new GridBagLayout();
    gbl_containerPanel.columnWidths = new int[] { 116, 0, 0 };
    gbl_containerPanel.rowHeights = new int[] { 0, 15, 0, 0 };
    gbl_containerPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
    gbl_containerPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
    containerPanel.setLayout(gbl_containerPanel);

    JLabel descriptionLabel = new JLabel(Localizer.localize("DescriptionLabelText"));
    descriptionLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
    gbc_descriptionLabel.anchor = GridBagConstraints.EAST;
    gbc_descriptionLabel.insets = new Insets(0, 0, 5, 5);
    gbc_descriptionLabel.gridx = 0;
    gbc_descriptionLabel.gridy = 0;
    containerPanel.add(descriptionLabel, gbc_descriptionLabel);

    JPanel descriptionTextAreaPanel = new JPanel();
    GridBagConstraints gbc_descriptionTextAreaPanel = new GridBagConstraints();
    gbc_descriptionTextAreaPanel.insets = new Insets(0, 0, 5, 0);
    gbc_descriptionTextAreaPanel.fill = GridBagConstraints.BOTH;
    gbc_descriptionTextAreaPanel.gridx = 1;
    gbc_descriptionTextAreaPanel.gridy = 0;
    containerPanel.add(descriptionTextAreaPanel, gbc_descriptionTextAreaPanel);
    GridBagLayout gbl_descriptionTextAreaPanel = new GridBagLayout();
    gbl_descriptionTextAreaPanel.columnWidths = new int[] { 114, 0 };
    gbl_descriptionTextAreaPanel.rowHeights = new int[] { 0, 19, 0 };
    gbl_descriptionTextAreaPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_descriptionTextAreaPanel.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
    descriptionTextAreaPanel.setLayout(gbl_descriptionTextAreaPanel);

    JLabel descriptionTipLabel = new JLabel(Localizer.localize("DescriptionTipLabelText"));
    descriptionTipLabel.setIcon(new ImageIcon(ScannerSettingsDialog.class
        .getResource("/com/famfamfam/silk/lightbulb.png")));
    descriptionTipLabel.setFont(new Font("Dialog", Font.BOLD, 9));
    GridBagConstraints gbc_descriptionTipLabel = new GridBagConstraints();
    gbc_descriptionTipLabel.anchor = GridBagConstraints.SOUTH;
    gbc_descriptionTipLabel.insets = new Insets(0, 0, 5, 0);
    gbc_descriptionTipLabel.gridx = 0;
    gbc_descriptionTipLabel.gridy = 0;
    descriptionTextAreaPanel.add(descriptionTipLabel, gbc_descriptionTipLabel);

    descriptionTextField = new JTextField();
    GridBagConstraints gbc_descriptionTextField = new GridBagConstraints();
    gbc_descriptionTextField.anchor = GridBagConstraints.NORTH;
    gbc_descriptionTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_descriptionTextField.gridx = 0;
    gbc_descriptionTextField.gridy = 1;
    descriptionTextAreaPanel.add(descriptionTextField, gbc_descriptionTextField);
    descriptionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    descriptionTextField.setFont(UIManager.getFont("TextField.font"));
    descriptionTextField.setColumns(10);

    JLabel remoteAddressLabel = new JLabel(Localizer.localize("RemoteAddressLabelText"));
    GridBagConstraints gbc_remoteAddressLabel = new GridBagConstraints();
    gbc_remoteAddressLabel.insets = new Insets(0, 0, 5, 5);
    gbc_remoteAddressLabel.anchor = GridBagConstraints.EAST;
    gbc_remoteAddressLabel.gridx = 0;
    gbc_remoteAddressLabel.gridy = 1;
    containerPanel.add(remoteAddressLabel, gbc_remoteAddressLabel);
    remoteAddressLabel.setFont(UIManager.getFont("Label.font"));

    remoteAddressTextField = new JTextField();
    GridBagConstraints gbc_remoteAddressTextField = new GridBagConstraints();
    gbc_remoteAddressTextField.insets = new Insets(0, 0, 5, 0);
    gbc_remoteAddressTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_remoteAddressTextField.gridx = 1;
    gbc_remoteAddressTextField.gridy = 1;
    containerPanel.add(remoteAddressTextField, gbc_remoteAddressTextField);
    remoteAddressTextField.setFont(UIManager.getFont("TextField.font"));
    remoteAddressTextField.setHorizontalAlignment(SwingConstants.TRAILING);
    try {
      remoteAddressTextField.setText(InetAddress.getLocalHost().getHostAddress());
    } catch (UnknownHostException ex) {
      LOG.warn(ex, ex);
    }
    remoteAddressTextField.setColumns(10);

    JLabel portNumberLabel = new JLabel(Localizer.localize("PortNumberLabelText"));
    GridBagConstraints gbc_portNumberLabel = new GridBagConstraints();
    gbc_portNumberLabel.anchor = GridBagConstraints.EAST;
    gbc_portNumberLabel.insets = new Insets(0, 0, 0, 5);
    gbc_portNumberLabel.gridx = 0;
    gbc_portNumberLabel.gridy = 2;
    containerPanel.add(portNumberLabel, gbc_portNumberLabel);
    portNumberLabel.setFont(UIManager.getFont("Label.font"));

    portNumberSpinner = new JSpinner();
    GridBagConstraints gbc_portNumberSpinner = new GridBagConstraints();
    gbc_portNumberSpinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_portNumberSpinner.gridx = 1;
    gbc_portNumberSpinner.gridy = 2;
    containerPanel.add(portNumberSpinner, gbc_portNumberSpinner);
    portNumberSpinner.setFont(UIManager.getFont("Spinner.font"));
    portNumberSpinner.setModel(new SpinnerNumberModel(6566, 1, 65535, 1));

    JPanel buttonPanel = new JPanel();
    GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
    gbc_buttonPanel.fill = GridBagConstraints.BOTH;
    gbc_buttonPanel.gridx = 0;
    gbc_buttonPanel.gridy = 1;
    contentPane.add(buttonPanel, gbc_buttonPanel);
    GridBagLayout gbl_buttonPanel = new GridBagLayout();
    gbl_buttonPanel.columnWidths = new int[] { 0, 0, 0, 0 };
    gbl_buttonPanel.rowHeights = new int[] { 0, 0 };
    gbl_buttonPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_buttonPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    buttonPanel.setLayout(gbl_buttonPanel);

    JButton okButton = new JButton(Localizer.localize("OK"));
    okButton.setIcon(new ImageIcon(ScannerSettingsDialog.class
        .getResource("/com/famfamfam/silk/tick.png")));
    okButton.setFont(UIManager.getFont("Button.font"));
    okButton.setMargin(new Insets(1, 5, 1, 5));
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        okButtonActionPerformed(e);
      }
    });
    GridBagConstraints gbc_okButton = new GridBagConstraints();
    gbc_okButton.insets = new Insets(0, 0, 0, 5);
    gbc_okButton.gridx = 1;
    gbc_okButton.gridy = 0;
    buttonPanel.add(okButton, gbc_okButton);

    JButton cancelButton = new JButton(Localizer.localize("Cancel"));
    cancelButton.setIcon(new ImageIcon(ScannerSettingsDialog.class
        .getResource("/com/famfamfam/silk/cross.png")));
    cancelButton.setFont(UIManager.getFont("Button.font"));
    cancelButton.setMargin(new Insets(1, 5, 1, 5));
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButtonActionPerformed(e);
      }
    });
    GridBagConstraints gbc_cancelButton = new GridBagConstraints();
    gbc_cancelButton.gridx = 2;
    gbc_cancelButton.gridy = 0;
    buttonPanel.add(cancelButton, gbc_cancelButton);
  }

  private void okButtonActionPerformed(ActionEvent e) {
    dialogResult = JOptionPane.OK_OPTION;
    dispose();
  }

  public final void setDescription(String description) {
    descriptionTextField.setText(description);
  }

  public final void setRemoteAddress(String remoteAddress) {
    remoteAddressTextField.setText(remoteAddress);
  }

  public final void setRemotePortNumber(Integer portNumber) {
    portNumberSpinner.setValue(portNumber);
  }
}
