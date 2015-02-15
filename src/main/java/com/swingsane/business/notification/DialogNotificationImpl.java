package com.swingsane.business.notification;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class DialogNotificationImpl extends JDialog implements INotification {

  private final JPanel contentPanel = new JPanel();
  private JLabel messageLabel;
  private JProgressBar progressBar;

  private boolean interrupted = false;
  private Exception exception;

  public DialogNotificationImpl(Component parent) {
    init();
    pack();
    setLocationRelativeTo(parent);
  }

  @Override
  public void addAbortListener() {
  }

  @Override
  public final Exception getException() {
    return exception;
  }

  private void init() {
    setPreferredSize(new Dimension(400, 100));
    setFocusTraversalKeysEnabled(false);
    setFocusCycleRoot(false);
    setFocusableWindowState(false);
    setFocusable(false);
    setName("notificationDialog");
    setSize(new Dimension(400, 100));
    setUndecorated(true);
    setMinimumSize(new Dimension(400, 100));
    setAlwaysOnTop(true);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setModalityType(ModalityType.DOCUMENT_MODAL);
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(
        5, 5, 5, 5)));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] { 0, 0 };
    gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
    gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
    contentPanel.setLayout(gbl_contentPanel);

    messageLabel = new JLabel("MESSAGE");
    messageLabel.setFont(new Font("Dialog", Font.PLAIN, 9));
    GridBagConstraints gbc_messageLabel = new GridBagConstraints();
    gbc_messageLabel.insets = new Insets(0, 0, 5, 0);
    gbc_messageLabel.gridx = 0;
    gbc_messageLabel.gridy = 1;
    contentPanel.add(messageLabel, gbc_messageLabel);

    progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    GridBagConstraints gbc_progressBar = new GridBagConstraints();
    gbc_progressBar.insets = new Insets(0, 0, 5, 0);
    gbc_progressBar.gridx = 0;
    gbc_progressBar.gridy = 2;
    contentPanel.add(progressBar, gbc_progressBar);
  }

  @Override
  public final boolean isInterrupted() {
    return interrupted;
  }

  @Override
  public final void message(final String message) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        messageLabel.setText(message);
      }
    });
  }

  @Override
  public final void setException(Exception exception) {
    this.exception = exception;
  }

  @Override
  public final void setInterrupted(boolean interrupted) {
    this.interrupted = interrupted;
  }

}
