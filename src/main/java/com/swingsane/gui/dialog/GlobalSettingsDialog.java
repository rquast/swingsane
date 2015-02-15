package com.swingsane.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.swingsane.business.discovery.DiscoveryJob;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.model.Login;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class GlobalSettingsDialog extends JDialog {

  private static final int BOUNDS_WIDTH = 550;
  private static final int BOUNDS_HEIGHT = 450;

  private final Dimension bounds = new Dimension(BOUNDS_WIDTH, BOUNDS_HEIGHT);

  private final JPanel contentPanel = new JPanel();
  private JTextField mDNSNameTextField;

  private String saneServiceName;

  private HashMap<String, Login> logins;

  private DefaultListModel<String> loginListModel = new DefaultListModel<String>();
  private JList<String> loginList;

  private int dialogResult = JOptionPane.CANCEL_OPTION;

  public GlobalSettingsDialog(Component parent) {
    initComponents();
    pack();
    setLocationRelativeTo(parent);
  }

  private void addLoginActionPerformed(ActionEvent e) {
    LoginDialog loginDialog = new LoginDialog(this);
    Login login = new Login();
    loginDialog.setUsername(login.getUsername());
    loginDialog.setPassword(login.getPassword());
    loginDialog.setModal(true);
    loginDialog.setVisible(true);
    if (loginDialog.getDialogResult() == JOptionPane.OK_OPTION) {
      login.setUsername(loginDialog.getUsername().trim());
      login.setPassword(loginDialog.getPassword().trim());
      String resource = loginDialog.getResource().trim();
      if (!(logins.containsKey(resource))) {
        logins.put(resource, login);
        loginListModel.addElement(resource);
      }
      loginList.revalidate();
    }
  }

  private void cancelActionPerformed(ActionEvent e) {
    dispose();
  }

  private void editLoginActionPerformed(ActionEvent e) {
    String resource = loginList.getSelectedValue();
    if (resource == null) {
      return;
    }
    LoginDialog loginDialog = new LoginDialog(this);

    Login login = logins.get(resource);
    loginDialog.setResource(resource);
    loginDialog.setUsername(login.getUsername());
    loginDialog.setPassword(login.getPassword());

    loginDialog.setModal(true);
    loginDialog.setVisible(true);
    if (loginDialog.getDialogResult() == JOptionPane.OK_OPTION) {
      login.setUsername(loginDialog.getUsername().trim());
      login.setPassword(loginDialog.getPassword().trim());
      loginList.revalidate();
    }
  }

  public final int getDialogResult() {
    return dialogResult;
  }

  public final HashMap<String, Login> getLogins() {
    return logins;
  }

  public final HashMap<String, Login> getSaneLogins() {
    return logins;
  }

  public final String getSaneServiceName() {
    return saneServiceName;
  }

  public final String getServiceName() {
    return mDNSNameTextField.getText();
  }

  private void initComponents() {
    setTitle(Localizer.localize("GlobalSettingsDialogTitle"));
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setResizable(false);
    setBounds(0, 0, bounds.width, bounds.height);
    setMinimumSize(bounds);
    setPreferredSize(bounds);
    setSize(bounds);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] { 222, 0 };
    gbl_contentPanel.rowHeights = new int[] { 0, 0, 0 };
    gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    contentPanel.setLayout(gbl_contentPanel);
    {
      JPanel loginPanel = new JPanel();
      loginPanel.setBorder(new CompoundBorder(new TitledBorder(Localizer
          .localize("ScannerBackendLoginsTitle")), new EmptyBorder(5, 5, 5, 5)));
      GridBagConstraints gbc_loginPanel = new GridBagConstraints();
      gbc_loginPanel.insets = new Insets(0, 0, 5, 0);
      gbc_loginPanel.fill = GridBagConstraints.BOTH;
      gbc_loginPanel.gridx = 0;
      gbc_loginPanel.gridy = 0;
      contentPanel.add(loginPanel, gbc_loginPanel);
      GridBagLayout gbl_loginPanel = new GridBagLayout();
      gbl_loginPanel.columnWidths = new int[] { 333, 0 };
      gbl_loginPanel.rowHeights = new int[] { 3, 0, 0 };
      gbl_loginPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
      gbl_loginPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
      loginPanel.setLayout(gbl_loginPanel);
      {
        JScrollPane loginScrollPane = new JScrollPane();
        GridBagConstraints gbc_loginScrollPane = new GridBagConstraints();
        gbc_loginScrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_loginScrollPane.fill = GridBagConstraints.BOTH;
        gbc_loginScrollPane.gridx = 0;
        gbc_loginScrollPane.gridy = 0;
        loginPanel.add(loginScrollPane, gbc_loginScrollPane);
        {
          loginList = new JList<String>();
          loginList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          loginList.setFont(UIManager.getFont("List.font"));
          loginScrollPane.setViewportView(loginList);
        }
      }
      {
        JPanel loginActionsPanel = new JPanel();
        GridBagConstraints gbc_loginActionsPanel = new GridBagConstraints();
        gbc_loginActionsPanel.fill = GridBagConstraints.BOTH;
        gbc_loginActionsPanel.gridx = 0;
        gbc_loginActionsPanel.gridy = 1;
        loginPanel.add(loginActionsPanel, gbc_loginActionsPanel);
        GridBagLayout gbl_loginActionsPanel = new GridBagLayout();
        gbl_loginActionsPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_loginActionsPanel.rowHeights = new int[] { 0, 0 };
        gbl_loginActionsPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_loginActionsPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        loginActionsPanel.setLayout(gbl_loginActionsPanel);
        {
          JButton addLoginButton = new JButton(Localizer.localize("Add"));
          addLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addLoginActionPerformed(e);
            }
          });
          addLoginButton.setIcon(new ImageIcon(GlobalSettingsDialog.class
              .getResource("/com/famfamfam/silk/add.png")));
          addLoginButton.setFont(UIManager.getFont("Button.font"));
          addLoginButton.setMargin(new Insets(1, 5, 1, 5));
          GridBagConstraints gbc_addLoginButton = new GridBagConstraints();
          gbc_addLoginButton.insets = new Insets(0, 0, 0, 5);
          gbc_addLoginButton.gridx = 1;
          gbc_addLoginButton.gridy = 0;
          loginActionsPanel.add(addLoginButton, gbc_addLoginButton);
        }
        {
          JButton removeLoginButton = new JButton(Localizer.localize("Remove"));
          removeLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              removeLoginActionPerformed(e);
            }
          });
          removeLoginButton.setIcon(new ImageIcon(GlobalSettingsDialog.class
              .getResource("/com/famfamfam/silk/delete.png")));
          removeLoginButton.setFont(UIManager.getFont("Button.font"));
          removeLoginButton.setMargin(new Insets(1, 5, 1, 5));
          GridBagConstraints gbc_removeLoginButton = new GridBagConstraints();
          gbc_removeLoginButton.insets = new Insets(0, 0, 0, 5);
          gbc_removeLoginButton.gridx = 2;
          gbc_removeLoginButton.gridy = 0;
          loginActionsPanel.add(removeLoginButton, gbc_removeLoginButton);
        }
        {
          JButton editLoginButton = new JButton(Localizer.localize("Edit"));
          editLoginButton.setIcon(new ImageIcon(GlobalSettingsDialog.class
              .getResource("/com/famfamfam/silk/pencil.png")));
          editLoginButton.setFont(UIManager.getFont("Button.font"));
          editLoginButton.setMargin(new Insets(1, 5, 1, 5));
          editLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              editLoginActionPerformed(e);
            }
          });
          GridBagConstraints gbc_editLoginButton = new GridBagConstraints();
          gbc_editLoginButton.gridx = 3;
          gbc_editLoginButton.gridy = 0;
          loginActionsPanel.add(editLoginButton, gbc_editLoginButton);
        }
      }
    }
    {
      JPanel mDNSPanel = new JPanel();
      mDNSPanel.setBorder(new CompoundBorder(new TitledBorder(Localizer
          .localize("MulticastDNSServiceNameTitle")), new EmptyBorder(5, 5, 5, 5)));
      GridBagConstraints gbc_mDNSPanel = new GridBagConstraints();
      gbc_mDNSPanel.fill = GridBagConstraints.BOTH;
      gbc_mDNSPanel.gridx = 0;
      gbc_mDNSPanel.gridy = 1;
      contentPanel.add(mDNSPanel, gbc_mDNSPanel);
      GridBagLayout gbl_mDNSPanel = new GridBagLayout();
      gbl_mDNSPanel.columnWidths = new int[] { 0, 0 };
      gbl_mDNSPanel.rowHeights = new int[] { 0, 0, 0 };
      gbl_mDNSPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
      gbl_mDNSPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
      mDNSPanel.setLayout(gbl_mDNSPanel);
      {
        JLabel mDNSDescriptionLabel = new JLabel(
            Localizer.localize("MulticastDNSServiceNameDescription"));
        mDNSDescriptionLabel.setFont(UIManager.getFont("Label.font"));
        GridBagConstraints gbc_mDNSDescriptionLabel = new GridBagConstraints();
        gbc_mDNSDescriptionLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_mDNSDescriptionLabel.insets = new Insets(0, 0, 5, 0);
        gbc_mDNSDescriptionLabel.gridx = 0;
        gbc_mDNSDescriptionLabel.gridy = 0;
        mDNSPanel.add(mDNSDescriptionLabel, gbc_mDNSDescriptionLabel);
      }
      {
        JPanel mDNSServiceNamePanel = new JPanel();
        GridBagConstraints gbc_mDNSServiceNamePanel = new GridBagConstraints();
        gbc_mDNSServiceNamePanel.fill = GridBagConstraints.BOTH;
        gbc_mDNSServiceNamePanel.gridx = 0;
        gbc_mDNSServiceNamePanel.gridy = 1;
        mDNSPanel.add(mDNSServiceNamePanel, gbc_mDNSServiceNamePanel);
        GridBagLayout gbl_mDNSServiceNamePanel = new GridBagLayout();
        gbl_mDNSServiceNamePanel.columnWidths = new int[] { 266, 0, 0 };
        gbl_mDNSServiceNamePanel.rowHeights = new int[] { 25, 0 };
        gbl_mDNSServiceNamePanel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
        gbl_mDNSServiceNamePanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        mDNSServiceNamePanel.setLayout(gbl_mDNSServiceNamePanel);
        {
          mDNSNameTextField = new JTextField();
          GridBagConstraints gbc_mDNSNameTextField = new GridBagConstraints();
          gbc_mDNSNameTextField.fill = GridBagConstraints.BOTH;
          gbc_mDNSNameTextField.insets = new Insets(0, 0, 0, 5);
          gbc_mDNSNameTextField.gridx = 0;
          gbc_mDNSNameTextField.gridy = 0;
          mDNSServiceNamePanel.add(mDNSNameTextField, gbc_mDNSNameTextField);
          mDNSNameTextField.setColumns(10);
        }
        {
          JButton mDNSRestoreButton = new JButton(Localizer.localize("RestoreDefaultButtonText"));
          mDNSRestoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              mDNSRestoreActionPerformed(e);
            }
          });
          mDNSRestoreButton.setIcon(new ImageIcon(GlobalSettingsDialog.class
              .getResource("/com/famfamfam/silk/arrow_rotate_anticlockwise.png")));
          mDNSRestoreButton.setFont(UIManager.getFont("Button.font"));
          mDNSRestoreButton.setMargin(new Insets(1, 5, 1, 5));
          GridBagConstraints gbc_mDNSRestoreButton = new GridBagConstraints();
          gbc_mDNSRestoreButton.gridx = 1;
          gbc_mDNSRestoreButton.gridy = 0;
          mDNSServiceNamePanel.add(mDNSRestoreButton, gbc_mDNSRestoreButton);
        }
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setBorder(new EmptyBorder(0, 12, 12, 12));
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton cancelButton = new JButton(Localizer.localize("Cancel"));
        cancelButton.setIcon(new ImageIcon(GlobalSettingsDialog.class
            .getResource("/com/famfamfam/silk/cross.png")));
        cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            cancelActionPerformed(e);
          }
        });
        {
          JButton saveButton = new JButton("Save");
          buttonPane.add(saveButton);
          saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              saveActionPerformed(e);
            }
          });
          saveButton.setIcon(new ImageIcon(GlobalSettingsDialog.class
              .getResource("/com/famfamfam/silk/disk.png")));
          saveButton.setFont(UIManager.getFont("Button.font"));
          saveButton.setMargin(new Insets(1, 5, 1, 5));
        }
        cancelButton.setFont(UIManager.getFont("Button.font"));
        cancelButton.setMargin(new Insets(1, 5, 1, 5));
        buttonPane.add(cancelButton);
      }
    }
  }

  public final void initialize() {
    popuplateLoginList();
    restoreServiceName();
  }

  private void mDNSRestoreActionPerformed(ActionEvent e) {
    mDNSNameTextField.setText(DiscoveryJob.SANE_SERVICE_NAME);
  }

  private void popuplateLoginList() {
    Set<String> resources = logins.keySet();
    for (String resource : resources) {
      loginListModel.addElement(resource);
    }
    loginList.setModel(loginListModel);
  }

  private void removeLoginActionPerformed(ActionEvent e) {
    String resource = loginList.getSelectedValue();
    if (resource == null) {
      return;
    }
    loginListModel.removeElement(resource);
    logins.remove(resource);
    loginList.revalidate();
  }

  private void restoreServiceName() {
    mDNSNameTextField.setText(saneServiceName);
  }

  private void saveActionPerformed(ActionEvent e) {
    dialogResult = JOptionPane.OK_OPTION;
    dispose();
  }

  public final void setLogins(HashMap<String, Login> logins) {
    this.logins = logins;
  }

  public final void setSaneServiceName(String saneServiceName) {
    this.saneServiceName = saneServiceName;
  }
}
