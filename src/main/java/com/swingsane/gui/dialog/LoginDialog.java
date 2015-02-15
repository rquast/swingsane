package com.swingsane.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.swingsane.i18n.Localizer;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class LoginDialog extends JDialog {

  private static final int BOUNDS_WIDTH = 400;
  private static final int BOUNDS_HEIGHT = 200;

  private final Dimension bounds = new Dimension(BOUNDS_WIDTH, BOUNDS_HEIGHT);

  private JTextField usernameTextField;

  private int dialogResult = JOptionPane.CANCEL_OPTION;
  private JPasswordField passwordField;
  private JTextField resourceTextField;

  public LoginDialog(Component parent) {
    initComponents();
    pack();
    setLocationRelativeTo(parent);
  }

  private void cancelButtonActionPerformed(ActionEvent e) {
    dispose();
  }

  public final int getDialogResult() {
    return dialogResult;
  }

  public final String getPassword() {
    return new String(passwordField.getPassword());
  }

  public final String getResource() {
    return resourceTextField.getText();
  }

  public final String getUsername() {
    return usernameTextField.getText();
  }

  private void initComponents() {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(Localizer.localize("LoginDialogTitle"));
    setName("loginDialog");
    setBounds(0, 0, bounds.width, bounds.height);
    setSize(bounds);
    setPreferredSize(bounds);
    setMinimumSize(bounds);
    setResizable(false);

    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
    getContentPane().add(contentPane, BorderLayout.CENTER);
    GridBagLayout gbl_contentPane = new GridBagLayout();
    gbl_contentPane.columnWidths = new int[] { 0, 0 };
    gbl_contentPane.rowHeights = new int[] { 0, 0, 0 };
    gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_contentPane.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    contentPane.setLayout(gbl_contentPane);

    JPanel panel = new JPanel();
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.insets = new Insets(0, 0, 5, 0);
    gbc_panel.fill = GridBagConstraints.BOTH;
    gbc_panel.gridx = 0;
    gbc_panel.gridy = 0;
    contentPane.add(panel, gbc_panel);
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[] { 66, 0, 0 };
    gbl_panel.rowHeights = new int[] { 15, 0, 0, 0 };
    gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
    gbl_panel.rowWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
    panel.setLayout(gbl_panel);

    JLabel resourceLabel = new JLabel(Localizer.localize("ResourceLabelText"));
    GridBagConstraints gbc_resourceLabel = new GridBagConstraints();
    gbc_resourceLabel.insets = new Insets(0, 0, 5, 5);
    gbc_resourceLabel.anchor = GridBagConstraints.EAST;
    gbc_resourceLabel.gridx = 0;
    gbc_resourceLabel.gridy = 0;
    panel.add(resourceLabel, gbc_resourceLabel);
    resourceLabel.setFont(UIManager.getFont("Label.font"));

    resourceTextField = new JTextField();
    GridBagConstraints gbc_resourceTextField = new GridBagConstraints();
    gbc_resourceTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_resourceTextField.insets = new Insets(0, 0, 5, 0);
    gbc_resourceTextField.gridx = 1;
    gbc_resourceTextField.gridy = 0;
    panel.add(resourceTextField, gbc_resourceTextField);
    resourceTextField.setFont(UIManager.getFont("TextField.font"));
    resourceTextField.setColumns(10);

    JLabel usernameLabel = new JLabel(Localizer.localize("UserNameLabelText"));
    GridBagConstraints gbc_usernameLabel = new GridBagConstraints();
    gbc_usernameLabel.anchor = GridBagConstraints.EAST;
    gbc_usernameLabel.insets = new Insets(0, 0, 5, 5);
    gbc_usernameLabel.gridx = 0;
    gbc_usernameLabel.gridy = 1;
    panel.add(usernameLabel, gbc_usernameLabel);
    usernameLabel.setFont(UIManager.getFont("Label.font"));

    usernameTextField = new JTextField();
    GridBagConstraints gbc_usernameTextField = new GridBagConstraints();
    gbc_usernameTextField.insets = new Insets(0, 0, 5, 0);
    gbc_usernameTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_usernameTextField.gridx = 1;
    gbc_usernameTextField.gridy = 1;
    panel.add(usernameTextField, gbc_usernameTextField);
    usernameTextField.setFont(UIManager.getFont("TextField.font"));
    usernameTextField.setText("");
    usernameTextField.setColumns(10);

    JLabel passwordLabel = new JLabel(Localizer.localize("PasswordLabelText"));
    GridBagConstraints gbc_passwordLabel = new GridBagConstraints();
    gbc_passwordLabel.anchor = GridBagConstraints.EAST;
    gbc_passwordLabel.insets = new Insets(0, 0, 0, 5);
    gbc_passwordLabel.gridx = 0;
    gbc_passwordLabel.gridy = 2;
    panel.add(passwordLabel, gbc_passwordLabel);
    passwordLabel.setFont(UIManager.getFont("Label.font"));

    passwordField = new JPasswordField();
    GridBagConstraints gbc_passwordField = new GridBagConstraints();
    gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
    gbc_passwordField.gridx = 1;
    gbc_passwordField.gridy = 2;
    panel.add(passwordField, gbc_passwordField);

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
    okButton.setIcon(new ImageIcon(LoginDialog.class.getResource("/com/famfamfam/silk/tick.png")));
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
    cancelButton.setIcon(new ImageIcon(LoginDialog.class
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

  public final void setPassword(String password) {
    passwordField.setText(password);
  }

  public final void setResource(String resource) {
    resourceTextField.setEditable(false);
    resourceTextField.setEnabled(false);
    resourceTextField.setText(resource);
  }

  public final void setUsername(String username) {
    usernameTextField.setText(username);
  }

}
