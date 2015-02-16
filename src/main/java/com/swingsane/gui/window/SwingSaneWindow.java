package com.swingsane.gui.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import org.apache.log4j.Logger;

import com.swingsane.business.scanning.IScanService;
import com.swingsane.business.scanning.ScanEvent;
import com.swingsane.gui.controller.ComponentController;
import com.swingsane.gui.controller.IComponents;
import com.swingsane.gui.controller.IScanEventHandler;
import com.swingsane.gui.dialog.AboutDialog;
import com.swingsane.gui.list.ScannerListItem;
import com.swingsane.gui.panel.PreviewPanel;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.IPreferredDefaults;
import com.swingsane.preferences.ISwingSanePreferences;
import com.swingsane.util.Misc;
import com.thoughtworks.xstream.XStream;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
public class SwingSaneWindow implements IComponents, IScanEventHandler {

  private JFrame frame;

  private String applicationName;

  private ComponentController componentController;

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(SwingSaneWindow.class);

  private JButton saveSettingsButton;
  private JList<ScannerListItem> scannerList;
  private JButton detectScannersButton;
  private JButton cancelDetectScannersButton;
  private JButton addScannerButton;
  private JButton removeScannerButton;
  private JButton scanButton;
  private JButton cancelScanButton;
  private JProgressBar scanProgressBar;
  private JButton clearConsoleButton;
  private JTextPane messagesTextPane;
  private JComboBox<String> sourceComboBox;
  private JComboBox<Integer> resolutionComboBox;
  private JComboBox<String> colorComboBox;
  private JComboBox<String> pageSizeComboBox;
  private JCheckBox autoCropCheckBox;
  private JSpinner pagesToScanSpinner;
  private JCheckBox batchScanCheckBox;
  private JSpinner blackThresholdSpinner;
  private JCheckBox useDefaultBlackThreshold;
  private JCheckBox duplexCheckBox;
  private JButton quitButton;

  private JPanel actionsPanel;
  private JPanel settingsPanel;
  private PreviewPanel previewPanel;
  private JTextField batchNameTextField;
  private JLabel batchNameLabel;
  private JPanel batchNamePanel;
  private JButton globalSettingsButton;
  private JButton customSettingsButton;
  private JPanel customSettingsPanel;
  private JCheckBox useCustomSettingsCheckBox;

  private ISwingSanePreferences preferences;
  private IScanService scanService;
  private JButton editScannerButton;
  private JButton aboutButton;

  private XStream xstream;

  private IPreferredDefaults preferredDefaults;

  public SwingSaneWindow() {
  }

  private void aboutActionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        AboutDialog aboutDialog = new AboutDialog(frame);
        aboutDialog.setTitle(Localizer.localize("AboutDialogTitle"));
        aboutDialog.setModal(true);
        aboutDialog.setVisible(true);
      }
    });
  }

  private void clearConsoleButtonActionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        messagesTextPane.setText(null);
      }
    });
  }

  @Override
  public final JButton getAddScannerButton() {
    return addScannerButton;
  }

  @Override
  public final JCheckBox getAutoCropCheckBox() {
    return autoCropCheckBox;
  }

  @Override
  public final JTextField getBatchNameTextField() {
    return batchNameTextField;
  }

  @Override
  public final JCheckBox getBatchScanCheckBox() {
    return batchScanCheckBox;
  }

  @Override
  public final JSpinner getBlackThresholdSpinner() {
    return blackThresholdSpinner;
  }

  @Override
  public final JButton getCancelDetectScannersButton() {
    return cancelDetectScannersButton;
  }

  @Override
  public final JButton getCancelScanButton() {
    return cancelScanButton;
  }

  @Override
  public final JComboBox<String> getColorComboBox() {
    return colorComboBox;
  }

  @Override
  public final JButton getCustomSettingsButton() {
    return customSettingsButton;
  }

  @Override
  public final JCheckBox getDefaultThresholdCheckBox() {
    return useDefaultBlackThreshold;
  }

  @Override
  public final JButton getDetectScannersButton() {
    return detectScannersButton;
  }

  @Override
  public final JCheckBox getDuplexScanningCheckBox() {
    return duplexCheckBox;
  }

  @Override
  public final JButton getEditScannerButton() {
    return editScannerButton;
  }

  @Override
  public final JButton getGlobalSettingsButton() {
    return globalSettingsButton;
  }

  @Override
  public final JTextPane getMessagesTextPane() {
    return messagesTextPane;
  }

  @Override
  public final JComboBox<String> getPageSizeComboBox() {
    return pageSizeComboBox;
  }

  @Override
  public final JSpinner getPagesToScanSpinner() {
    return pagesToScanSpinner;
  }

  @Override
  public final JButton getRemoveScannerButton() {
    return removeScannerButton;
  }

  @Override
  public final JComboBox<Integer> getResolutionComboBox() {
    return resolutionComboBox;
  }

  @Override
  public final Component getRootComponent() {
    return frame;
  }

  @Override
  public final JButton getSaveSettingsButton() {
    return saveSettingsButton;
  }

  @Override
  public final JButton getScanButton() {
    return scanButton;
  }

  @Override
  public final JList<ScannerListItem> getScannerList() {
    return scannerList;
  }

  @Override
  public final JProgressBar getScanProgressBar() {
    return scanProgressBar;
  }

  @Override
  public final JComboBox<String> getSourceComboBox() {
    return sourceComboBox;
  }

  @Override
  public final JCheckBox getUseCustomSettingsCheckBox() {
    return useCustomSettingsCheckBox;
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initComponents() {
    frame = new JFrame();
    frame.setPreferredSize(new Dimension(670, 550));
    frame.setLocationByPlatform(true);
    frame.setName("mainFrame");
    frame.setSize(new Dimension(670, 550));
    frame.setMinimumSize(new Dimension(670, 550));
    frame.setTitle(applicationName);
    frame.setBounds(100, 100, 670, 550);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout(0, 0));

    JPanel contentPane = new JPanel();
    frame.getContentPane().add(contentPane, BorderLayout.CENTER);
    GridBagLayout gbl_contentPane = new GridBagLayout();
    gbl_contentPane.columnWidths = new int[] { 0, 0 };
    gbl_contentPane.rowHeights = new int[] { 0, 0, 0 };
    gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_contentPane.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    contentPane.setLayout(gbl_contentPane);

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setBorder(new EmptyBorder(12, 12, 12, 12));
    tabbedPane.setFont(UIManager.getFont("Panel.font"));
    GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
    gbc_tabbedPane.weighty = 0.0;
    gbc_tabbedPane.weightx = 0.0;
    gbc_tabbedPane.ipady = 0;
    gbc_tabbedPane.ipadx = 0;
    gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
    gbc_tabbedPane.gridwidth = 1;
    gbc_tabbedPane.gridheight = 1;
    gbc_tabbedPane.fill = GridBagConstraints.BOTH;
    gbc_tabbedPane.anchor = GridBagConstraints.CENTER;
    gbc_tabbedPane.gridx = 0;
    gbc_tabbedPane.gridy = 0;
    contentPane.add(tabbedPane, gbc_tabbedPane);

    actionsPanel = new JPanel();
    actionsPanel.setOpaque(false);
    tabbedPane.addTab(Localizer.localize("ActionsTabTitle"), null, actionsPanel, null);
    GridBagLayout gbl_actionsPanel = new GridBagLayout();
    gbl_actionsPanel.columnWidths = new int[] { 680, 0 };
    gbl_actionsPanel.rowHeights = new int[] { 314, 150, 0 };
    gbl_actionsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_actionsPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    actionsPanel.setLayout(gbl_actionsPanel);

    JPanel scannersPanel = new JPanel();
    scannersPanel.setOpaque(false);
    scannersPanel.setBorder(new CompoundBorder(new TitledBorder(Localizer
        .localize("ScannersBorderTitle")), new EmptyBorder(5, 5, 5, 5)));
    GridBagConstraints gbc_scannersPanel = new GridBagConstraints();
    gbc_scannersPanel.fill = GridBagConstraints.BOTH;
    gbc_scannersPanel.insets = new Insets(0, 0, 5, 0);
    gbc_scannersPanel.gridx = 0;
    gbc_scannersPanel.gridy = 0;
    actionsPanel.add(scannersPanel, gbc_scannersPanel);
    GridBagLayout gbl_scannersPanel = new GridBagLayout();
    gbl_scannersPanel.columnWidths = new int[] { 0, 0 };
    gbl_scannersPanel.rowHeights = new int[] { 0, 0, 0, 0 };
    gbl_scannersPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_scannersPanel.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
    scannersPanel.setLayout(gbl_scannersPanel);

    JPanel detectActionPanel = new JPanel();
    detectActionPanel.setOpaque(false);
    GridBagConstraints gbc_detectActionPanel = new GridBagConstraints();
    gbc_detectActionPanel.weighty = 0.0;
    gbc_detectActionPanel.weightx = 0.0;
    gbc_detectActionPanel.ipady = 0;
    gbc_detectActionPanel.ipadx = 0;
    gbc_detectActionPanel.gridwidth = 1;
    gbc_detectActionPanel.gridheight = 1;
    gbc_detectActionPanel.fill = GridBagConstraints.BOTH;
    gbc_detectActionPanel.anchor = GridBagConstraints.CENTER;
    gbc_detectActionPanel.insets = new Insets(0, 0, 5, 0);
    gbc_detectActionPanel.gridx = 0;
    gbc_detectActionPanel.gridy = 0;
    scannersPanel.add(detectActionPanel, gbc_detectActionPanel);
    GridBagLayout gbl_detectActionPanel = new GridBagLayout();
    gbl_detectActionPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
    gbl_detectActionPanel.rowHeights = new int[] { 0, 0 };
    gbl_detectActionPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
        Double.MIN_VALUE };
    gbl_detectActionPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    detectActionPanel.setLayout(gbl_detectActionPanel);

    detectScannersButton = new JButton();
    detectScannersButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/zoom.png")));
    detectScannersButton.setText(Localizer.localize("DetectButtonText"));
    detectScannersButton.setFont(UIManager.getFont("Button.font"));
    detectScannersButton.setMargin(new Insets(1, 5, 1, 5));
    GridBagConstraints gbc_detectScannersButton = new GridBagConstraints();
    gbc_detectScannersButton.weighty = 0.0;
    gbc_detectScannersButton.weightx = 0.0;
    gbc_detectScannersButton.ipady = 0;
    gbc_detectScannersButton.ipadx = 0;
    gbc_detectScannersButton.gridwidth = 1;
    gbc_detectScannersButton.gridheight = 1;
    gbc_detectScannersButton.fill = GridBagConstraints.BOTH;
    gbc_detectScannersButton.anchor = GridBagConstraints.CENTER;
    gbc_detectScannersButton.insets = new Insets(0, 0, 0, 5);
    gbc_detectScannersButton.gridx = 0;
    gbc_detectScannersButton.gridy = 0;
    detectActionPanel.add(detectScannersButton, gbc_detectScannersButton);

    cancelDetectScannersButton = new JButton();
    cancelDetectScannersButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/cancel.png")));
    cancelDetectScannersButton.setText(Localizer.localize("Cancel"));
    cancelDetectScannersButton.setFont(UIManager.getFont("Button.font"));
    cancelDetectScannersButton.setMargin(new Insets(1, 5, 1, 5));
    cancelDetectScannersButton.setEnabled(false);
    GridBagConstraints gbc_cancelDetectScannersButton = new GridBagConstraints();
    gbc_cancelDetectScannersButton.weighty = 0.0;
    gbc_cancelDetectScannersButton.weightx = 0.0;
    gbc_cancelDetectScannersButton.ipady = 0;
    gbc_cancelDetectScannersButton.ipadx = 0;
    gbc_cancelDetectScannersButton.gridwidth = 1;
    gbc_cancelDetectScannersButton.gridheight = 1;
    gbc_cancelDetectScannersButton.fill = GridBagConstraints.BOTH;
    gbc_cancelDetectScannersButton.anchor = GridBagConstraints.CENTER;
    gbc_cancelDetectScannersButton.insets = new Insets(0, 0, 0, 5);
    gbc_cancelDetectScannersButton.gridx = 1;
    gbc_cancelDetectScannersButton.gridy = 0;
    detectActionPanel.add(cancelDetectScannersButton, gbc_cancelDetectScannersButton);

    editScannerButton = new JButton(Localizer.localize("Edit"));
    editScannerButton.setFont(UIManager.getFont("Button.font"));
    editScannerButton.setMargin(new Insets(1, 5, 1, 5));
    editScannerButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/pencil.png")));
    GridBagConstraints gbc_editScannerButton = new GridBagConstraints();
    gbc_editScannerButton.insets = new Insets(0, 0, 0, 5);
    gbc_editScannerButton.gridx = 3;
    gbc_editScannerButton.gridy = 0;
    detectActionPanel.add(editScannerButton, gbc_editScannerButton);

    addScannerButton = new JButton();
    addScannerButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/add.png")));
    addScannerButton.setText(Localizer.localize("Add"));
    addScannerButton.setFont(UIManager.getFont("Button.font"));
    addScannerButton.setMargin(new Insets(1, 5, 1, 5));
    GridBagConstraints gbc_addScannerButton = new GridBagConstraints();
    gbc_addScannerButton.weighty = 0.0;
    gbc_addScannerButton.weightx = 0.0;
    gbc_addScannerButton.ipady = 0;
    gbc_addScannerButton.ipadx = 0;
    gbc_addScannerButton.gridwidth = 1;
    gbc_addScannerButton.gridheight = 1;
    gbc_addScannerButton.fill = GridBagConstraints.BOTH;
    gbc_addScannerButton.anchor = GridBagConstraints.CENTER;
    gbc_addScannerButton.insets = new Insets(0, 0, 0, 5);
    gbc_addScannerButton.gridx = 4;
    gbc_addScannerButton.gridy = 0;
    detectActionPanel.add(addScannerButton, gbc_addScannerButton);

    removeScannerButton = new JButton();
    removeScannerButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/delete.png")));
    removeScannerButton.setText(Localizer.localize("Remove"));
    removeScannerButton.setFont(UIManager.getFont("Button.font"));
    removeScannerButton.setMargin(new Insets(1, 5, 1, 5));
    GridBagConstraints gbc_removeScannerButton = new GridBagConstraints();
    gbc_removeScannerButton.weighty = 0.0;
    gbc_removeScannerButton.weightx = 0.0;
    gbc_removeScannerButton.ipady = 0;
    gbc_removeScannerButton.ipadx = 0;
    gbc_removeScannerButton.insets = new Insets(0, 0, 0, 0);
    gbc_removeScannerButton.gridwidth = 1;
    gbc_removeScannerButton.gridheight = 1;
    gbc_removeScannerButton.fill = GridBagConstraints.BOTH;
    gbc_removeScannerButton.anchor = GridBagConstraints.CENTER;
    gbc_removeScannerButton.gridx = 5;
    gbc_removeScannerButton.gridy = 0;
    detectActionPanel.add(removeScannerButton, gbc_removeScannerButton);

    JScrollPane scannerListScrollPane = new JScrollPane();
    scannerListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scannerListScrollPane.setOpaque(false);
    GridBagConstraints gbc_scannerListScrollPane = new GridBagConstraints();
    gbc_scannerListScrollPane.weighty = 0.0;
    gbc_scannerListScrollPane.weightx = 0.0;
    gbc_scannerListScrollPane.ipady = 0;
    gbc_scannerListScrollPane.ipadx = 0;
    gbc_scannerListScrollPane.gridwidth = 1;
    gbc_scannerListScrollPane.gridheight = 1;
    gbc_scannerListScrollPane.fill = GridBagConstraints.BOTH;
    gbc_scannerListScrollPane.anchor = GridBagConstraints.CENTER;
    gbc_scannerListScrollPane.insets = new Insets(0, 0, 5, 0);
    gbc_scannerListScrollPane.gridx = 0;
    gbc_scannerListScrollPane.gridy = 1;
    scannersPanel.add(scannerListScrollPane, gbc_scannerListScrollPane);

    scannerList = new JList<ScannerListItem>();
    scannerList.setFont(UIManager.getFont("List.font"));
    scannerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    scannerListScrollPane.setViewportView(scannerList);

    JPanel scanActionPanel = new JPanel();
    scanActionPanel.setOpaque(false);
    GridBagConstraints gbc_scanActionPanel = new GridBagConstraints();
    gbc_scanActionPanel.weighty = 0.0;
    gbc_scanActionPanel.weightx = 0.0;
    gbc_scanActionPanel.ipady = 0;
    gbc_scanActionPanel.ipadx = 0;
    gbc_scanActionPanel.insets = new Insets(0, 0, 0, 0);
    gbc_scanActionPanel.gridwidth = 1;
    gbc_scanActionPanel.gridheight = 1;
    gbc_scanActionPanel.fill = GridBagConstraints.BOTH;
    gbc_scanActionPanel.anchor = GridBagConstraints.CENTER;
    gbc_scanActionPanel.gridx = 0;
    gbc_scanActionPanel.gridy = 2;
    scannersPanel.add(scanActionPanel, gbc_scanActionPanel);
    GridBagLayout gbl_scanActionPanel = new GridBagLayout();
    gbl_scanActionPanel.columnWidths = new int[] { 0, 20, 0, 0, 0, 10, 0, 5, 0 };
    gbl_scanActionPanel.rowHeights = new int[] { 0, 0 };
    gbl_scanActionPanel.columnWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        Double.MIN_VALUE };
    gbl_scanActionPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    scanActionPanel.setLayout(gbl_scanActionPanel);

    scanButton = new JButton();
    scanButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/images.png")));
    scanButton.setText(Localizer.localize("ScanButtonText"));
    scanButton.setFont(UIManager.getFont("Button.font"));
    scanButton.setMargin(new Insets(1, 5, 1, 5));
    GridBagConstraints gbc_scanButton = new GridBagConstraints();
    gbc_scanButton.weighty = 0.0;
    gbc_scanButton.weightx = 0.0;
    gbc_scanButton.ipady = 0;
    gbc_scanButton.ipadx = 0;
    gbc_scanButton.gridwidth = 1;
    gbc_scanButton.gridheight = 1;
    gbc_scanButton.fill = GridBagConstraints.HORIZONTAL;
    gbc_scanButton.anchor = GridBagConstraints.CENTER;
    gbc_scanButton.insets = new Insets(0, 0, 0, 5);
    gbc_scanButton.gridx = 3;
    gbc_scanButton.gridy = 0;
    scanActionPanel.add(scanButton, gbc_scanButton);

    cancelScanButton = new JButton();
    cancelScanButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/cancel.png")));
    cancelScanButton.setText(Localizer.localize("Cancel"));
    cancelScanButton.setFont(UIManager.getFont("Button.font"));
    cancelScanButton.setMargin(new Insets(1, 5, 1, 5));
    cancelScanButton.setEnabled(false);
    GridBagConstraints gbc_cancelScanButton = new GridBagConstraints();
    gbc_cancelScanButton.weighty = 0.0;
    gbc_cancelScanButton.weightx = 0.0;
    gbc_cancelScanButton.ipady = 0;
    gbc_cancelScanButton.ipadx = 0;
    gbc_cancelScanButton.gridwidth = 1;
    gbc_cancelScanButton.gridheight = 1;
    gbc_cancelScanButton.fill = GridBagConstraints.HORIZONTAL;
    gbc_cancelScanButton.anchor = GridBagConstraints.CENTER;
    gbc_cancelScanButton.insets = new Insets(0, 0, 0, 5);
    gbc_cancelScanButton.gridx = 4;
    gbc_cancelScanButton.gridy = 0;
    scanActionPanel.add(cancelScanButton, gbc_cancelScanButton);

    scanProgressBar = new JProgressBar();
    scanProgressBar.setFont(UIManager.getFont("ProgressBar.font"));
    GridBagConstraints gbc_scanProgressBar = new GridBagConstraints();
    gbc_scanProgressBar.insets = new Insets(0, 0, 0, 5);
    gbc_scanProgressBar.gridx = 6;
    gbc_scanProgressBar.gridy = 0;
    scanActionPanel.add(scanProgressBar, gbc_scanProgressBar);

    JPanel consolePanel = new JPanel();
    consolePanel.setOpaque(false);
    consolePanel.setBorder(new CompoundBorder(new TitledBorder(Localizer
        .localize("ClientConsoleBorderTitle")), new EmptyBorder(5, 5, 5, 5)));
    GridBagConstraints gbc_consolePanel = new GridBagConstraints();
    gbc_consolePanel.fill = GridBagConstraints.BOTH;
    gbc_consolePanel.gridx = 0;
    gbc_consolePanel.gridy = 1;
    actionsPanel.add(consolePanel, gbc_consolePanel);
    GridBagLayout gbl_consolePanel = new GridBagLayout();
    gbl_consolePanel.columnWidths = new int[] { 0, 0, 0 };
    gbl_consolePanel.rowHeights = new int[] { 0, 0 };
    gbl_consolePanel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    gbl_consolePanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
    consolePanel.setLayout(gbl_consolePanel);

    JScrollPane messagesScrollPane = new JScrollPane();
    messagesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    messagesScrollPane.setOpaque(false);
    GridBagConstraints gbc_messagesScrollPane = new GridBagConstraints();
    gbc_messagesScrollPane.weighty = 0.0;
    gbc_messagesScrollPane.weightx = 0.0;
    gbc_messagesScrollPane.ipady = 0;
    gbc_messagesScrollPane.ipadx = 0;
    gbc_messagesScrollPane.gridwidth = 1;
    gbc_messagesScrollPane.gridheight = 1;
    gbc_messagesScrollPane.fill = GridBagConstraints.BOTH;
    gbc_messagesScrollPane.anchor = GridBagConstraints.CENTER;
    gbc_messagesScrollPane.insets = new Insets(0, 0, 0, 5);
    gbc_messagesScrollPane.gridx = 0;
    gbc_messagesScrollPane.gridy = 0;
    consolePanel.add(messagesScrollPane, gbc_messagesScrollPane);

    messagesTextPane = new JTextPane();
    messagesTextPane.setFont(UIManager.getFont("TextPane.font"));
    messagesTextPane.setEditable(false);
    messagesScrollPane.setViewportView(messagesTextPane);

    JPanel clearConsolePanel = new JPanel();
    clearConsolePanel.setOpaque(false);
    GridBagConstraints gbc_clearConsolePanel = new GridBagConstraints();
    gbc_clearConsolePanel.weighty = 0.0;
    gbc_clearConsolePanel.weightx = 0.0;
    gbc_clearConsolePanel.ipady = 0;
    gbc_clearConsolePanel.ipadx = 0;
    gbc_clearConsolePanel.insets = new Insets(0, 0, 0, 0);
    gbc_clearConsolePanel.gridwidth = 1;
    gbc_clearConsolePanel.gridheight = 1;
    gbc_clearConsolePanel.fill = GridBagConstraints.VERTICAL;
    gbc_clearConsolePanel.anchor = GridBagConstraints.CENTER;
    gbc_clearConsolePanel.gridx = 1;
    gbc_clearConsolePanel.gridy = 0;
    consolePanel.add(clearConsolePanel, gbc_clearConsolePanel);
    GridBagLayout gbl_clearConsolePanel = new GridBagLayout();
    gbl_clearConsolePanel.columnWidths = new int[] { 0, 0 };
    gbl_clearConsolePanel.rowHeights = new int[] { 0, 0, 0 };
    gbl_clearConsolePanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_clearConsolePanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    clearConsolePanel.setLayout(gbl_clearConsolePanel);

    JLabel formReturnScannerIconLabel = new JLabel();
    formReturnScannerIconLabel.setFont(UIManager.getFont("Label.font"));
    formReturnScannerIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
    formReturnScannerIconLabel.setIcon(new ImageIcon(getClass().getResource(
        "/com/swingsane/images/swingsane_64x64.png")));
    GridBagConstraints gbc_formReturnScannerIconLabel = new GridBagConstraints();
    gbc_formReturnScannerIconLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_formReturnScannerIconLabel.weighty = 0.0;
    gbc_formReturnScannerIconLabel.weightx = 0.0;
    gbc_formReturnScannerIconLabel.ipady = 0;
    gbc_formReturnScannerIconLabel.ipadx = 0;
    gbc_formReturnScannerIconLabel.gridwidth = 1;
    gbc_formReturnScannerIconLabel.gridheight = 1;
    gbc_formReturnScannerIconLabel.anchor = GridBagConstraints.CENTER;
    gbc_formReturnScannerIconLabel.insets = new Insets(0, 0, 5, 0);
    gbc_formReturnScannerIconLabel.gridx = 0;
    gbc_formReturnScannerIconLabel.gridy = 0;
    clearConsolePanel.add(formReturnScannerIconLabel, gbc_formReturnScannerIconLabel);

    clearConsoleButton = new JButton();
    clearConsoleButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearConsoleButtonActionPerformed(e);
      }
    });
    clearConsoleButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/bin.png")));
    clearConsoleButton.setText(Localizer.localize("ClearButtonText"));
    clearConsoleButton.setFont(UIManager.getFont("Button.font"));
    clearConsoleButton.setMargin(new Insets(1, 5, 1, 5));
    GridBagConstraints gbc_clearConsoleButton = new GridBagConstraints();
    gbc_clearConsoleButton.fill = GridBagConstraints.HORIZONTAL;
    gbc_clearConsoleButton.weighty = 0.0;
    gbc_clearConsoleButton.weightx = 0.0;
    gbc_clearConsoleButton.ipady = 0;
    gbc_clearConsoleButton.ipadx = 0;
    gbc_clearConsoleButton.insets = new Insets(0, 0, 0, 0);
    gbc_clearConsoleButton.gridwidth = 1;
    gbc_clearConsoleButton.gridheight = 1;
    gbc_clearConsoleButton.anchor = GridBagConstraints.SOUTH;
    gbc_clearConsoleButton.gridx = 0;
    gbc_clearConsoleButton.gridy = 1;
    clearConsolePanel.add(clearConsoleButton, gbc_clearConsoleButton);

    settingsPanel = new JPanel();
    settingsPanel.setOpaque(false);
    settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    tabbedPane.addTab(Localizer.localize("SettingsTabTitle"), null, settingsPanel, null);
    GridBagLayout gbl_settingsPanel = new GridBagLayout();
    gbl_settingsPanel.columnWidths = new int[] { 0, 0, 10, 0, 0, 0 };
    gbl_settingsPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    gbl_settingsPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
    gbl_settingsPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
        Double.MIN_VALUE };
    settingsPanel.setLayout(gbl_settingsPanel);

    batchNameLabel = new JLabel();
    batchNameLabel.setText(Localizer.localize("BatchPrefixLabelText"));
    batchNameLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_batchNameLabel = new GridBagConstraints();
    gbc_batchNameLabel.insets = new Insets(0, 0, 5, 5);
    gbc_batchNameLabel.gridx = 1;
    gbc_batchNameLabel.gridy = 0;
    gbc_batchNameLabel.fill = GridBagConstraints.NONE;
    gbc_batchNameLabel.anchor = GridBagConstraints.EAST;
    settingsPanel.add(batchNameLabel, gbc_batchNameLabel);

    batchNamePanel = new JPanel();
    GridBagConstraints gbc_batchNamePanel = new GridBagConstraints();
    gbc_batchNamePanel.insets = new Insets(0, 0, 5, 5);
    gbc_batchNamePanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_batchNamePanel.gridx = 3;
    gbc_batchNamePanel.gridy = 0;
    settingsPanel.add(batchNamePanel, gbc_batchNamePanel);
    batchNamePanel.setLayout(new GridLayout(1, 0, 0, 0));

    batchNameTextField = new JTextField();
    batchNameTextField.setFont(UIManager.getFont("TextField.font"));
    batchNameTextField.setEnabled(false);
    batchNamePanel.add(batchNameTextField);
    batchNameTextField.setHorizontalAlignment(SwingConstants.LEFT);
    batchNameTextField.setColumns(10);

    JLabel pagesToScanLabel = new JLabel();
    pagesToScanLabel.setText(Localizer.localize("PagesToScanLabelText"));
    pagesToScanLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_pagesToScanLabel = new GridBagConstraints();
    gbc_pagesToScanLabel.weighty = 0.0;
    gbc_pagesToScanLabel.weightx = 0.0;
    gbc_pagesToScanLabel.ipady = 0;
    gbc_pagesToScanLabel.ipadx = 0;
    gbc_pagesToScanLabel.gridwidth = 1;
    gbc_pagesToScanLabel.fill = GridBagConstraints.NONE;
    gbc_pagesToScanLabel.anchor = GridBagConstraints.EAST;
    gbc_pagesToScanLabel.insets = new Insets(0, 0, 5, 5);
    gbc_pagesToScanLabel.gridx = 1;
    gbc_pagesToScanLabel.gridy = 1;
    settingsPanel.add(pagesToScanLabel, gbc_pagesToScanLabel);

    JPanel pagesToScanContainerPanel = new JPanel();
    pagesToScanContainerPanel.setOpaque(false);
    GridBagConstraints gbc_pagesToScanContainerPanel = new GridBagConstraints();
    gbc_pagesToScanContainerPanel.weighty = 0.0;
    gbc_pagesToScanContainerPanel.weightx = 0.0;
    gbc_pagesToScanContainerPanel.ipady = 0;
    gbc_pagesToScanContainerPanel.ipadx = 0;
    gbc_pagesToScanContainerPanel.gridwidth = 1;
    gbc_pagesToScanContainerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_pagesToScanContainerPanel.anchor = GridBagConstraints.CENTER;
    gbc_pagesToScanContainerPanel.insets = new Insets(0, 0, 5, 5);
    gbc_pagesToScanContainerPanel.gridx = 3;
    gbc_pagesToScanContainerPanel.gridy = 1;
    settingsPanel.add(pagesToScanContainerPanel, gbc_pagesToScanContainerPanel);
    GridBagLayout gbl_pagesToScanContainerPanel = new GridBagLayout();
    gbl_pagesToScanContainerPanel.columnWidths = new int[] { 95, 0, 0 };
    gbl_pagesToScanContainerPanel.rowHeights = new int[] { 0, 0 };
    gbl_pagesToScanContainerPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
    gbl_pagesToScanContainerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    pagesToScanContainerPanel.setLayout(gbl_pagesToScanContainerPanel);

    pagesToScanSpinner = new JSpinner();
    pagesToScanSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null,
        new Integer(1)));
    pagesToScanSpinner.setFont(UIManager.getFont("Spinner.font"));
    pagesToScanSpinner.setEnabled(false);
    GridBagConstraints gbc_pagesToScanSpinner = new GridBagConstraints();
    gbc_pagesToScanSpinner.weighty = 0.0;
    gbc_pagesToScanSpinner.weightx = 0.0;
    gbc_pagesToScanSpinner.ipady = 0;
    gbc_pagesToScanSpinner.ipadx = 0;
    gbc_pagesToScanSpinner.gridwidth = 1;
    gbc_pagesToScanSpinner.gridheight = 1;
    gbc_pagesToScanSpinner.fill = GridBagConstraints.BOTH;
    gbc_pagesToScanSpinner.anchor = GridBagConstraints.CENTER;
    gbc_pagesToScanSpinner.insets = new Insets(0, 0, 0, 5);
    gbc_pagesToScanSpinner.gridx = 0;
    gbc_pagesToScanSpinner.gridy = 0;
    pagesToScanContainerPanel.add(pagesToScanSpinner, gbc_pagesToScanSpinner);

    batchScanCheckBox = new JCheckBox();
    batchScanCheckBox.setOpaque(false);
    batchScanCheckBox.setText(Localizer.localize("BatchScanCheckBoxText"));
    batchScanCheckBox.setFont(UIManager.getFont("CheckBox.font"));
    batchScanCheckBox.setEnabled(false);
    GridBagConstraints gbc_batchScanCheckBox = new GridBagConstraints();
    gbc_batchScanCheckBox.weighty = 0.0;
    gbc_batchScanCheckBox.weightx = 0.0;
    gbc_batchScanCheckBox.ipady = 0;
    gbc_batchScanCheckBox.ipadx = 0;
    gbc_batchScanCheckBox.insets = new Insets(0, 0, 0, 0);
    gbc_batchScanCheckBox.gridwidth = 1;
    gbc_batchScanCheckBox.gridheight = 1;
    gbc_batchScanCheckBox.fill = GridBagConstraints.BOTH;
    gbc_batchScanCheckBox.anchor = GridBagConstraints.CENTER;
    gbc_batchScanCheckBox.gridx = 1;
    gbc_batchScanCheckBox.gridy = 0;
    pagesToScanContainerPanel.add(batchScanCheckBox, gbc_batchScanCheckBox);

    JLabel sourceLabel = new JLabel();
    sourceLabel.setText(Localizer.localize("SourceLabelText"));
    sourceLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_sourceLabel = new GridBagConstraints();
    gbc_sourceLabel.weighty = 0.0;
    gbc_sourceLabel.weightx = 0.0;
    gbc_sourceLabel.ipady = 0;
    gbc_sourceLabel.ipadx = 0;
    gbc_sourceLabel.gridwidth = 1;
    gbc_sourceLabel.gridheight = 1;
    gbc_sourceLabel.fill = GridBagConstraints.NONE;
    gbc_sourceLabel.anchor = GridBagConstraints.EAST;
    gbc_sourceLabel.insets = new Insets(0, 0, 5, 5);
    gbc_sourceLabel.gridx = 1;
    gbc_sourceLabel.gridy = 2;
    settingsPanel.add(sourceLabel, gbc_sourceLabel);

    JPanel sourceContainerPanel = new JPanel();
    sourceContainerPanel.setOpaque(false);
    GridBagConstraints gbc_sourceContainerPanel = new GridBagConstraints();
    gbc_sourceContainerPanel.weighty = 0.0;
    gbc_sourceContainerPanel.weightx = 0.0;
    gbc_sourceContainerPanel.ipady = 0;
    gbc_sourceContainerPanel.ipadx = 0;
    gbc_sourceContainerPanel.gridwidth = 1;
    gbc_sourceContainerPanel.gridheight = 1;
    gbc_sourceContainerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_sourceContainerPanel.anchor = GridBagConstraints.CENTER;
    gbc_sourceContainerPanel.insets = new Insets(0, 0, 5, 5);
    gbc_sourceContainerPanel.gridx = 3;
    gbc_sourceContainerPanel.gridy = 2;
    settingsPanel.add(sourceContainerPanel, gbc_sourceContainerPanel);
    GridBagLayout gbl_sourceContainerPanel = new GridBagLayout();
    gbl_sourceContainerPanel.columnWidths = new int[] { 90, 0 };
    gbl_sourceContainerPanel.rowHeights = new int[] { 0, 0 };
    gbl_sourceContainerPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
    gbl_sourceContainerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    sourceContainerPanel.setLayout(gbl_sourceContainerPanel);

    sourceComboBox = new JComboBox<String>();
    sourceComboBox.setFont(UIManager.getFont("ComboBox.font"));
    sourceComboBox.setEnabled(false);
    GridBagConstraints gbc_sourceComboBox = new GridBagConstraints();
    gbc_sourceComboBox.weighty = 0.0;
    gbc_sourceComboBox.weightx = 0.0;
    gbc_sourceComboBox.ipady = 0;
    gbc_sourceComboBox.ipadx = 0;
    gbc_sourceComboBox.insets = new Insets(0, 0, 0, 0);
    gbc_sourceComboBox.gridwidth = 1;
    gbc_sourceComboBox.gridheight = 1;
    gbc_sourceComboBox.fill = GridBagConstraints.BOTH;
    gbc_sourceComboBox.anchor = GridBagConstraints.CENTER;
    gbc_sourceComboBox.gridx = 0;
    gbc_sourceComboBox.gridy = 0;
    sourceContainerPanel.add(sourceComboBox, gbc_sourceComboBox);

    JLabel resolutionLabel = new JLabel();
    resolutionLabel.setText(Localizer.localize("ResolutionLabelText"));
    resolutionLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_resolutionLabel = new GridBagConstraints();
    gbc_resolutionLabel.weighty = 0.0;
    gbc_resolutionLabel.weightx = 0.0;
    gbc_resolutionLabel.ipady = 0;
    gbc_resolutionLabel.ipadx = 0;
    gbc_resolutionLabel.gridwidth = 1;
    gbc_resolutionLabel.gridheight = 1;
    gbc_resolutionLabel.fill = GridBagConstraints.NONE;
    gbc_resolutionLabel.anchor = GridBagConstraints.EAST;
    gbc_resolutionLabel.insets = new Insets(0, 0, 5, 5);
    gbc_resolutionLabel.gridx = 1;
    gbc_resolutionLabel.gridy = 3;
    settingsPanel.add(resolutionLabel, gbc_resolutionLabel);

    JPanel resolutionContainerPanel = new JPanel();
    resolutionContainerPanel.setOpaque(false);
    GridBagConstraints gbc_resolutionContainerPanel = new GridBagConstraints();
    gbc_resolutionContainerPanel.weighty = 0.0;
    gbc_resolutionContainerPanel.weightx = 0.0;
    gbc_resolutionContainerPanel.ipady = 0;
    gbc_resolutionContainerPanel.ipadx = 0;
    gbc_resolutionContainerPanel.gridwidth = 1;
    gbc_resolutionContainerPanel.gridheight = 1;
    gbc_resolutionContainerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_resolutionContainerPanel.anchor = GridBagConstraints.CENTER;
    gbc_resolutionContainerPanel.insets = new Insets(0, 0, 5, 5);
    gbc_resolutionContainerPanel.gridx = 3;
    gbc_resolutionContainerPanel.gridy = 3;
    settingsPanel.add(resolutionContainerPanel, gbc_resolutionContainerPanel);
    GridBagLayout gbl_resolutionContainerPanel = new GridBagLayout();
    gbl_resolutionContainerPanel.columnWidths = new int[] { 90, 0 };
    gbl_resolutionContainerPanel.rowHeights = new int[] { 0, 0 };
    gbl_resolutionContainerPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
    gbl_resolutionContainerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    resolutionContainerPanel.setLayout(gbl_resolutionContainerPanel);

    resolutionComboBox = new JComboBox<Integer>();
    resolutionComboBox.setFont(UIManager.getFont("ComboBox.font"));
    resolutionComboBox.setEnabled(false);
    GridBagConstraints gbc_resolutionComboBox = new GridBagConstraints();
    gbc_resolutionComboBox.weighty = 0.0;
    gbc_resolutionComboBox.weightx = 0.0;
    gbc_resolutionComboBox.ipady = 0;
    gbc_resolutionComboBox.ipadx = 0;
    gbc_resolutionComboBox.insets = new Insets(0, 0, 0, 0);
    gbc_resolutionComboBox.gridwidth = 1;
    gbc_resolutionComboBox.gridheight = 1;
    gbc_resolutionComboBox.fill = GridBagConstraints.BOTH;
    gbc_resolutionComboBox.anchor = GridBagConstraints.CENTER;
    gbc_resolutionComboBox.gridx = 0;
    gbc_resolutionComboBox.gridy = 0;
    resolutionContainerPanel.add(resolutionComboBox, gbc_resolutionComboBox);

    JLabel colorLabel = new JLabel();
    colorLabel.setText(Localizer.localize("ColorLabelText"));
    colorLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_colorLabel = new GridBagConstraints();
    gbc_colorLabel.weighty = 0.0;
    gbc_colorLabel.weightx = 0.0;
    gbc_colorLabel.ipady = 0;
    gbc_colorLabel.ipadx = 0;
    gbc_colorLabel.gridwidth = 1;
    gbc_colorLabel.gridheight = 1;
    gbc_colorLabel.fill = GridBagConstraints.NONE;
    gbc_colorLabel.anchor = GridBagConstraints.EAST;
    gbc_colorLabel.insets = new Insets(0, 0, 5, 5);
    gbc_colorLabel.gridx = 1;
    gbc_colorLabel.gridy = 4;
    settingsPanel.add(colorLabel, gbc_colorLabel);

    JPanel colorContainerPanel = new JPanel();
    colorContainerPanel.setOpaque(false);
    GridBagConstraints gbc_colorContainerPanel = new GridBagConstraints();
    gbc_colorContainerPanel.weighty = 0.0;
    gbc_colorContainerPanel.weightx = 0.0;
    gbc_colorContainerPanel.ipady = 0;
    gbc_colorContainerPanel.ipadx = 0;
    gbc_colorContainerPanel.gridwidth = 1;
    gbc_colorContainerPanel.gridheight = 1;
    gbc_colorContainerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_colorContainerPanel.anchor = GridBagConstraints.CENTER;
    gbc_colorContainerPanel.insets = new Insets(0, 0, 5, 5);
    gbc_colorContainerPanel.gridx = 3;
    gbc_colorContainerPanel.gridy = 4;
    settingsPanel.add(colorContainerPanel, gbc_colorContainerPanel);
    GridBagLayout gbl_colorContainerPanel = new GridBagLayout();
    gbl_colorContainerPanel.columnWidths = new int[] { 90, 0 };
    gbl_colorContainerPanel.rowHeights = new int[] { 0, 0 };
    gbl_colorContainerPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
    gbl_colorContainerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    colorContainerPanel.setLayout(gbl_colorContainerPanel);

    colorComboBox = new JComboBox<String>();
    colorComboBox.setFont(UIManager.getFont("ComboBox.font"));
    colorComboBox.setEnabled(false);
    GridBagConstraints gbc_colorComboBox = new GridBagConstraints();
    gbc_colorComboBox.weighty = 0.0;
    gbc_colorComboBox.weightx = 0.0;
    gbc_colorComboBox.ipady = 0;
    gbc_colorComboBox.ipadx = 0;
    gbc_colorComboBox.insets = new Insets(0, 0, 0, 0);
    gbc_colorComboBox.gridwidth = 1;
    gbc_colorComboBox.gridheight = 1;
    gbc_colorComboBox.fill = GridBagConstraints.BOTH;
    gbc_colorComboBox.anchor = GridBagConstraints.CENTER;
    gbc_colorComboBox.gridx = 0;
    gbc_colorComboBox.gridy = 0;
    colorContainerPanel.add(colorComboBox, gbc_colorComboBox);

    JLabel pageSizeLabel = new JLabel();
    pageSizeLabel.setText(Localizer.localize("PageSizeLabelText"));
    pageSizeLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_pageSizeLabel = new GridBagConstraints();
    gbc_pageSizeLabel.weighty = 0.0;
    gbc_pageSizeLabel.weightx = 0.0;
    gbc_pageSizeLabel.ipady = 0;
    gbc_pageSizeLabel.ipadx = 0;
    gbc_pageSizeLabel.gridwidth = 1;
    gbc_pageSizeLabel.gridheight = 1;
    gbc_pageSizeLabel.fill = GridBagConstraints.NONE;
    gbc_pageSizeLabel.anchor = GridBagConstraints.EAST;
    gbc_pageSizeLabel.insets = new Insets(0, 0, 5, 5);
    gbc_pageSizeLabel.gridx = 1;
    gbc_pageSizeLabel.gridy = 5;
    settingsPanel.add(pageSizeLabel, gbc_pageSizeLabel);

    JPanel pageSizeContainerPanel = new JPanel();
    pageSizeContainerPanel.setOpaque(false);
    GridBagConstraints gbc_pageSizeContainerPanel = new GridBagConstraints();
    gbc_pageSizeContainerPanel.weighty = 0.0;
    gbc_pageSizeContainerPanel.weightx = 0.0;
    gbc_pageSizeContainerPanel.ipady = 0;
    gbc_pageSizeContainerPanel.ipadx = 0;
    gbc_pageSizeContainerPanel.gridwidth = 1;
    gbc_pageSizeContainerPanel.gridheight = 1;
    gbc_pageSizeContainerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_pageSizeContainerPanel.anchor = GridBagConstraints.CENTER;
    gbc_pageSizeContainerPanel.insets = new Insets(0, 0, 5, 5);
    gbc_pageSizeContainerPanel.gridx = 3;
    gbc_pageSizeContainerPanel.gridy = 5;
    settingsPanel.add(pageSizeContainerPanel, gbc_pageSizeContainerPanel);
    GridBagLayout gbl_pageSizeContainerPanel = new GridBagLayout();
    gbl_pageSizeContainerPanel.columnWidths = new int[] { 95, 0, 0 };
    gbl_pageSizeContainerPanel.rowHeights = new int[] { 0, 0 };
    gbl_pageSizeContainerPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
    gbl_pageSizeContainerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    pageSizeContainerPanel.setLayout(gbl_pageSizeContainerPanel);

    pageSizeComboBox = new JComboBox<String>();
    pageSizeComboBox.setFont(UIManager.getFont("ComboBox.font"));
    pageSizeComboBox.setEnabled(false);
    GridBagConstraints gbc_pageSizeComboBox = new GridBagConstraints();
    gbc_pageSizeComboBox.weighty = 0.0;
    gbc_pageSizeComboBox.weightx = 0.0;
    gbc_pageSizeComboBox.ipady = 0;
    gbc_pageSizeComboBox.ipadx = 0;
    gbc_pageSizeComboBox.gridwidth = 1;
    gbc_pageSizeComboBox.gridheight = 1;
    gbc_pageSizeComboBox.fill = GridBagConstraints.BOTH;
    gbc_pageSizeComboBox.anchor = GridBagConstraints.CENTER;
    gbc_pageSizeComboBox.insets = new Insets(0, 0, 0, 5);
    gbc_pageSizeComboBox.gridx = 0;
    gbc_pageSizeComboBox.gridy = 0;
    pageSizeContainerPanel.add(pageSizeComboBox, gbc_pageSizeComboBox);

    autoCropCheckBox = new JCheckBox();
    autoCropCheckBox.setOpaque(false);
    autoCropCheckBox.setText(Localizer.localize("AutoCropEdgesCheckBoxText"));
    autoCropCheckBox.setFont(UIManager.getFont("CheckBox.font"));
    autoCropCheckBox.setEnabled(false);
    GridBagConstraints gbc_autoCropCheckBox = new GridBagConstraints();
    gbc_autoCropCheckBox.weighty = 0.0;
    gbc_autoCropCheckBox.weightx = 0.0;
    gbc_autoCropCheckBox.ipady = 0;
    gbc_autoCropCheckBox.ipadx = 0;
    gbc_autoCropCheckBox.insets = new Insets(0, 0, 0, 0);
    gbc_autoCropCheckBox.gridwidth = 1;
    gbc_autoCropCheckBox.gridheight = 1;
    gbc_autoCropCheckBox.fill = GridBagConstraints.BOTH;
    gbc_autoCropCheckBox.anchor = GridBagConstraints.CENTER;
    gbc_autoCropCheckBox.gridx = 1;
    gbc_autoCropCheckBox.gridy = 0;
    pageSizeContainerPanel.add(autoCropCheckBox, gbc_autoCropCheckBox);

    JLabel blackThresholdLabel = new JLabel();
    blackThresholdLabel.setText(Localizer.localize("BlackThresholdLabelText"));
    blackThresholdLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_blackThresholdLabel = new GridBagConstraints();
    gbc_blackThresholdLabel.weighty = 0.0;
    gbc_blackThresholdLabel.weightx = 0.0;
    gbc_blackThresholdLabel.ipady = 0;
    gbc_blackThresholdLabel.ipadx = 0;
    gbc_blackThresholdLabel.gridwidth = 1;
    gbc_blackThresholdLabel.gridheight = 1;
    gbc_blackThresholdLabel.fill = GridBagConstraints.NONE;
    gbc_blackThresholdLabel.anchor = GridBagConstraints.EAST;
    gbc_blackThresholdLabel.insets = new Insets(0, 0, 5, 5);
    gbc_blackThresholdLabel.gridx = 1;
    gbc_blackThresholdLabel.gridy = 6;
    settingsPanel.add(blackThresholdLabel, gbc_blackThresholdLabel);

    JPanel blackThresholdContainerPanel = new JPanel();
    blackThresholdContainerPanel.setOpaque(false);
    GridBagConstraints gbc_blackThresholdContainerPanel = new GridBagConstraints();
    gbc_blackThresholdContainerPanel.weighty = 0.0;
    gbc_blackThresholdContainerPanel.weightx = 0.0;
    gbc_blackThresholdContainerPanel.ipady = 0;
    gbc_blackThresholdContainerPanel.ipadx = 0;
    gbc_blackThresholdContainerPanel.gridwidth = 1;
    gbc_blackThresholdContainerPanel.gridheight = 1;
    gbc_blackThresholdContainerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_blackThresholdContainerPanel.anchor = GridBagConstraints.CENTER;
    gbc_blackThresholdContainerPanel.insets = new Insets(0, 0, 5, 5);
    gbc_blackThresholdContainerPanel.gridx = 3;
    gbc_blackThresholdContainerPanel.gridy = 6;
    settingsPanel.add(blackThresholdContainerPanel, gbc_blackThresholdContainerPanel);
    GridBagLayout gbl_blackThresholdContainerPanel = new GridBagLayout();
    gbl_blackThresholdContainerPanel.columnWidths = new int[] { 95, 0, 0 };
    gbl_blackThresholdContainerPanel.rowHeights = new int[] { 0, 0 };
    gbl_blackThresholdContainerPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
    gbl_blackThresholdContainerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    blackThresholdContainerPanel.setLayout(gbl_blackThresholdContainerPanel);

    blackThresholdSpinner = new JSpinner();
    blackThresholdSpinner.setModel(new SpinnerNumberModel(0, -128, 128, 1));
    blackThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
    blackThresholdSpinner.setEnabled(false);
    GridBagConstraints gbc_blackThresholdSpinner = new GridBagConstraints();
    gbc_blackThresholdSpinner.weighty = 0.0;
    gbc_blackThresholdSpinner.weightx = 0.0;
    gbc_blackThresholdSpinner.ipady = 0;
    gbc_blackThresholdSpinner.ipadx = 0;
    gbc_blackThresholdSpinner.gridwidth = 1;
    gbc_blackThresholdSpinner.gridheight = 1;
    gbc_blackThresholdSpinner.fill = GridBagConstraints.BOTH;
    gbc_blackThresholdSpinner.anchor = GridBagConstraints.CENTER;
    gbc_blackThresholdSpinner.insets = new Insets(0, 0, 0, 5);
    gbc_blackThresholdSpinner.gridx = 0;
    gbc_blackThresholdSpinner.gridy = 0;
    blackThresholdContainerPanel.add(blackThresholdSpinner, gbc_blackThresholdSpinner);

    useDefaultBlackThreshold = new JCheckBox();
    useDefaultBlackThreshold.setText(Localizer.localize("DefaultBlackThresholdCheckBoxText"));
    useDefaultBlackThreshold.setSelected(true);
    useDefaultBlackThreshold.setOpaque(false);
    useDefaultBlackThreshold.setFont(UIManager.getFont("CheckBox.font"));
    useDefaultBlackThreshold.setEnabled(false);
    GridBagConstraints gbc_useDefaultBlackThreshold = new GridBagConstraints();
    gbc_useDefaultBlackThreshold.weighty = 0.0;
    gbc_useDefaultBlackThreshold.weightx = 0.0;
    gbc_useDefaultBlackThreshold.ipady = 0;
    gbc_useDefaultBlackThreshold.ipadx = 0;
    gbc_useDefaultBlackThreshold.insets = new Insets(0, 0, 0, 0);
    gbc_useDefaultBlackThreshold.gridwidth = 1;
    gbc_useDefaultBlackThreshold.gridheight = 1;
    gbc_useDefaultBlackThreshold.fill = GridBagConstraints.BOTH;
    gbc_useDefaultBlackThreshold.anchor = GridBagConstraints.CENTER;
    gbc_useDefaultBlackThreshold.gridx = 1;
    gbc_useDefaultBlackThreshold.gridy = 0;
    blackThresholdContainerPanel.add(useDefaultBlackThreshold, gbc_useDefaultBlackThreshold);

    JPanel duplexContainerPanel = new JPanel();
    duplexContainerPanel.setOpaque(false);
    GridBagConstraints gbc_duplexContainerPanel = new GridBagConstraints();
    gbc_duplexContainerPanel.weighty = 0.0;
    gbc_duplexContainerPanel.weightx = 0.0;
    gbc_duplexContainerPanel.ipady = 0;
    gbc_duplexContainerPanel.ipadx = 0;
    gbc_duplexContainerPanel.gridwidth = 1;
    gbc_duplexContainerPanel.gridheight = 1;
    gbc_duplexContainerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_duplexContainerPanel.anchor = GridBagConstraints.CENTER;
    gbc_duplexContainerPanel.insets = new Insets(0, 0, 5, 5);
    gbc_duplexContainerPanel.gridx = 3;
    gbc_duplexContainerPanel.gridy = 7;
    settingsPanel.add(duplexContainerPanel, gbc_duplexContainerPanel);
    GridBagLayout gbl_duplexContainerPanel = new GridBagLayout();
    gbl_duplexContainerPanel.columnWidths = new int[] { 0, 0 };
    gbl_duplexContainerPanel.rowHeights = new int[] { 0, 0 };
    gbl_duplexContainerPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
    gbl_duplexContainerPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
    duplexContainerPanel.setLayout(gbl_duplexContainerPanel);

    duplexCheckBox = new JCheckBox();
    duplexCheckBox.setOpaque(false);
    duplexCheckBox.setText(Localizer.localize("DuplexCheckBoxText"));
    duplexCheckBox.setFont(UIManager.getFont("CheckBox.font"));
    duplexCheckBox.setEnabled(false);
    GridBagConstraints gbc_duplexCheckBox = new GridBagConstraints();
    gbc_duplexCheckBox.weighty = 0.0;
    gbc_duplexCheckBox.weightx = 0.0;
    gbc_duplexCheckBox.ipady = 0;
    gbc_duplexCheckBox.ipadx = 0;
    gbc_duplexCheckBox.insets = new Insets(0, 0, 0, 0);
    gbc_duplexCheckBox.gridwidth = 1;
    gbc_duplexCheckBox.gridheight = 1;
    gbc_duplexCheckBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_duplexCheckBox.anchor = GridBagConstraints.CENTER;
    gbc_duplexCheckBox.gridx = 0;
    gbc_duplexCheckBox.gridy = 0;
    duplexContainerPanel.add(duplexCheckBox, gbc_duplexCheckBox);

    customSettingsPanel = new JPanel();
    customSettingsPanel.setOpaque(false);
    GridBagConstraints gbc_customSettingsPanel = new GridBagConstraints();
    gbc_customSettingsPanel.insets = new Insets(0, 0, 5, 5);
    gbc_customSettingsPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_customSettingsPanel.gridx = 3;
    gbc_customSettingsPanel.gridy = 8;
    settingsPanel.add(customSettingsPanel, gbc_customSettingsPanel);
    GridBagLayout gbl_customSettingsPanel = new GridBagLayout();
    gbl_customSettingsPanel.columnWidths = new int[] { 170, 5, 0, 0, 0 };
    gbl_customSettingsPanel.rowHeights = new int[] { 23, 0 };
    gbl_customSettingsPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
    gbl_customSettingsPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    customSettingsPanel.setLayout(gbl_customSettingsPanel);

    useCustomSettingsCheckBox = new JCheckBox(Localizer.localize("UseCustomSettingsCheckBoxText"));
    useCustomSettingsCheckBox.setFont(UIManager.getFont("CheckBox.font"));
    useCustomSettingsCheckBox.setEnabled(false);
    useCustomSettingsCheckBox.setOpaque(false);
    GridBagConstraints gbc_useCustomSettingsCheckBox = new GridBagConstraints();
    gbc_useCustomSettingsCheckBox.insets = new Insets(0, 0, 0, 5);
    gbc_useCustomSettingsCheckBox.anchor = GridBagConstraints.WEST;
    gbc_useCustomSettingsCheckBox.gridx = 0;
    gbc_useCustomSettingsCheckBox.gridy = 0;
    customSettingsPanel.add(useCustomSettingsCheckBox, gbc_useCustomSettingsCheckBox);

    customSettingsButton = new JButton(Localizer.localize("CustomSettingsButtonText"));
    GridBagConstraints gbc_customSettingsButton = new GridBagConstraints();
    gbc_customSettingsButton.insets = new Insets(0, 0, 0, 5);
    gbc_customSettingsButton.gridx = 2;
    gbc_customSettingsButton.gridy = 0;
    customSettingsPanel.add(customSettingsButton, gbc_customSettingsButton);
    customSettingsButton.setEnabled(false);
    customSettingsButton.setFont(UIManager.getFont("Button.font"));
    customSettingsButton.setMargin(new Insets(1, 5, 1, 5));
    customSettingsButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/brick.png")));

    JPanel saveSettingsContainerPanel = new JPanel();
    saveSettingsContainerPanel.setOpaque(false);
    GridBagConstraints gbc_saveSettingsContainerPanel = new GridBagConstraints();
    gbc_saveSettingsContainerPanel.weighty = 0.0;
    gbc_saveSettingsContainerPanel.weightx = 0.0;
    gbc_saveSettingsContainerPanel.ipady = 0;
    gbc_saveSettingsContainerPanel.ipadx = 0;
    gbc_saveSettingsContainerPanel.gridwidth = 1;
    gbc_saveSettingsContainerPanel.gridheight = 1;
    gbc_saveSettingsContainerPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_saveSettingsContainerPanel.anchor = GridBagConstraints.CENTER;
    gbc_saveSettingsContainerPanel.insets = new Insets(0, 0, 0, 5);
    gbc_saveSettingsContainerPanel.gridx = 3;
    gbc_saveSettingsContainerPanel.gridy = 9;
    settingsPanel.add(saveSettingsContainerPanel, gbc_saveSettingsContainerPanel);
    GridBagLayout gbl_saveSettingsContainerPanel = new GridBagLayout();
    gbl_saveSettingsContainerPanel.columnWidths = new int[] { 0, 0, 0, 0 };
    gbl_saveSettingsContainerPanel.rowHeights = new int[] { 0, 0 };
    gbl_saveSettingsContainerPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
    gbl_saveSettingsContainerPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    saveSettingsContainerPanel.setLayout(gbl_saveSettingsContainerPanel);

    saveSettingsButton = new JButton();
    saveSettingsButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/disk.png")));
    saveSettingsButton.setText(Localizer.localize("SaveSettingsButtonText"));
    saveSettingsButton.setFont(UIManager.getFont("Button.font"));
    saveSettingsButton.setMargin(new Insets(1, 5, 1, 5));
    saveSettingsButton.setEnabled(false);
    GridBagConstraints gbc_saveSettingsButton = new GridBagConstraints();
    gbc_saveSettingsButton.weighty = 0.0;
    gbc_saveSettingsButton.weightx = 0.0;
    gbc_saveSettingsButton.ipady = 0;
    gbc_saveSettingsButton.ipadx = 0;
    gbc_saveSettingsButton.insets = new Insets(0, 0, 0, 0);
    gbc_saveSettingsButton.gridwidth = 1;
    gbc_saveSettingsButton.gridheight = 1;
    gbc_saveSettingsButton.fill = GridBagConstraints.BOTH;
    gbc_saveSettingsButton.anchor = GridBagConstraints.CENTER;
    gbc_saveSettingsButton.gridx = 2;
    gbc_saveSettingsButton.gridy = 0;
    saveSettingsContainerPanel.add(saveSettingsButton, gbc_saveSettingsButton);

    previewPanel = new PreviewPanel();
    previewPanel.setPreferences(preferences);
    previewPanel.setPreferredDefaults(preferredDefaults);
    previewPanel.initialize();
    previewPanel.setOpaque(false);
    tabbedPane.addTab(Localizer.localize("PreviewTabTitle"), null, previewPanel, null);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(new EmptyBorder(0, 12, 12, 12));
    GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
    gbc_buttonPanel.fill = GridBagConstraints.BOTH;
    gbc_buttonPanel.gridx = 0;
    gbc_buttonPanel.gridy = 1;
    contentPane.add(buttonPanel, gbc_buttonPanel);
    GridBagLayout gbl_buttonPanel = new GridBagLayout();
    gbl_buttonPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
    gbl_buttonPanel.rowHeights = new int[] { 25, 0 };
    gbl_buttonPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
    gbl_buttonPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
    buttonPanel.setLayout(gbl_buttonPanel);

    quitButton = new JButton(Localizer.localize("Quit"));
    quitButton.setFont(UIManager.getFont("Button.font"));
    quitButton.setMargin(new Insets(1, 5, 1, 5));
    quitButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/door_in.png")));
    quitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        quitButtonActionPerformed(e);
      }
    });

    globalSettingsButton = new JButton(Localizer.localize("GlobalSettingsButtonText"));
    globalSettingsButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/cog.png")));
    globalSettingsButton.setFont(UIManager.getFont("Button.font"));
    globalSettingsButton.setMargin(new Insets(1, 5, 1, 5));
    GridBagConstraints gbc_globalSettingsButton = new GridBagConstraints();
    gbc_globalSettingsButton.insets = new Insets(0, 0, 0, 5);
    gbc_globalSettingsButton.gridx = 0;
    gbc_globalSettingsButton.gridy = 0;
    buttonPanel.add(globalSettingsButton, gbc_globalSettingsButton);

    aboutButton = new JButton("About");
    aboutButton.setIcon(new ImageIcon(SwingSaneWindow.class
        .getResource("/com/famfamfam/silk/information.png")));
    aboutButton.setFont(UIManager.getFont("Button.font"));
    aboutButton.setMargin(new Insets(1, 5, 1, 5));
    aboutButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        aboutActionPerformed(e);
      }
    });
    GridBagConstraints gbc_aboutButton = new GridBagConstraints();
    gbc_aboutButton.insets = new Insets(0, 0, 0, 5);
    gbc_aboutButton.gridx = 1;
    gbc_aboutButton.gridy = 0;
    buttonPanel.add(aboutButton, gbc_aboutButton);
    GridBagConstraints gbc_quitButton = new GridBagConstraints();
    gbc_quitButton.fill = GridBagConstraints.VERTICAL;
    gbc_quitButton.anchor = GridBagConstraints.WEST;
    gbc_quitButton.gridx = 3;
    gbc_quitButton.gridy = 0;
    buttonPanel.add(quitButton, gbc_quitButton);

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        thisWindowClosing(e);
      }
    });

    frame.addWindowFocusListener(new WindowAdapter() {
      @Override
      public void windowGainedFocus(WindowEvent e) {
        thisWindowGainedFocus(e);
      }
    });

    DefaultCaret caret = (DefaultCaret) messagesTextPane.getCaret();
    caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

  }

  private void initController() {
    componentController = new ComponentController();
    componentController.setPreferences(preferences);
    componentController.setScanEventHandler(this);
    componentController.setPreferredDefaults(preferredDefaults);
    componentController.setXstream(xstream);
    componentController.setScanService(scanService);
    componentController.setComponents(this);
  }

  public final void initialize() {
    initComponents();
    initController();
    frame.pack();
  }

  private void quitButtonActionPerformed(ActionEvent e) {
    frame.dispose();
    preferences.cleanUp();
    System.exit(0);
  }

  @Override
  public final void scanPerformed(ScanEvent scanEvent) {
    try {
      previewPanel.addImage(scanEvent);
    } catch (IOException ex) {
      LOG.error(ex, ex);
      Misc.showErrorMsg(frame,
          String.format(Localizer.localize("FailureSavingMessage"), ex.getLocalizedMessage()));
    }
  }

  public final void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public final void setPreferences(ISwingSanePreferences preferences) {
    this.preferences = preferences;
  }

  public final void setPreferredDefaults(IPreferredDefaults preferredDefaults) {
    this.preferredDefaults = preferredDefaults;
  }

  public final void setScanService(IScanService scanServiceImpl) {
    scanService = scanServiceImpl;
  }

  public final void setVisible(final boolean visible) {
    frame.setVisible(visible);
  }

  public final void setXstream(XStream xstream) {
    this.xstream = xstream;
  }

  private void thisWindowClosing(WindowEvent e) {
    frame.dispose();
  }

  private void thisWindowGainedFocus(WindowEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        frame.requestFocus();
      }
    });
  }

}
