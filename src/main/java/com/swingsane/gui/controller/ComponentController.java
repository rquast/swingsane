package com.swingsane.gui.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import au.com.southsky.jfreesane.RateLimitingScanListeners;
import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.ScanListener;
import au.com.southsky.jfreesane.ScanListenerAdapter;

import com.swingsane.business.discovery.DiscoveryEvent;
import com.swingsane.business.discovery.DiscoveryJob;
import com.swingsane.business.discovery.DiscoveryListener;
import com.swingsane.business.notification.INotification;
import com.swingsane.business.options.KnownSaneOptions;
import com.swingsane.business.scanning.IScanService;
import com.swingsane.business.scanning.ScanEvent;
import com.swingsane.business.scanning.ScanEventListener;
import com.swingsane.business.scanning.ScanJob;
import com.swingsane.gui.dialog.CustomSettingsDialog;
import com.swingsane.gui.dialog.GlobalSettingsDialog;
import com.swingsane.gui.dialog.ScannerSettingsDialog;
import com.swingsane.gui.list.ScannerListItem;
import com.swingsane.gui.list.ScannerListItem.ScannerStatus;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.IPreferredDefaults;
import com.swingsane.preferences.ISwingSanePreferences;
import com.swingsane.preferences.PreferencesUtils;
import com.swingsane.preferences.model.ApplicationPreferences;
import com.swingsane.preferences.model.Login;
import com.swingsane.preferences.model.Scanner;
import com.swingsane.util.Misc;
import com.thoughtworks.xstream.XStream;

public class ComponentController implements INotification {

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(ComponentController.class);

  private IComponents components;
  private IScanEventHandler scanEventHandler;
  private IScanService scanService;
  private IPreferredDefaults preferredDefaults;
  private ISwingSanePreferences preferences;

  private DiscoveryJob discoveryJob;
  private DiscoveryListener discoveryListener;

  private static final int PROGRESS_BAR_UPDATE_INTERVAL = 100;

  private DefaultListModel<ScannerListItem> scannerListModel = new DefaultListModel<ScannerListItem>();

  private XStream xstream;

  public ComponentController() {
  }

  @Override
  public void addAbortListener() {
  }

  private void addMessage(String message) {
    StyledDocument doc = components.getMessagesTextPane().getStyledDocument();
    SimpleAttributeSet keyWord = new SimpleAttributeSet();
    StyleConstants.setForeground(keyWord, Color.GREEN);
    StyleConstants.setBackground(keyWord, Color.BLUE);
    StyleConstants.setBold(keyWord, true);
    try {
      if (message.toLowerCase().startsWith("found ")) {
        doc.insertString(0, message + "\n", keyWord);
      } else {
        doc.insertString(0, message + "\n", null);
      }
    } catch (BadLocationException e) {
      LOG.error(e, e);
    }
  }

  private void addScannerButtonActionPerformed(ActionEvent e) {
    ScannerSettingsDialog scannerSettingsDialog = new ScannerSettingsDialog(
        components.getRootComponent());
    scannerSettingsDialog.setTitle(Localizer.localize("AddScannerDialogTitle"));
    scannerSettingsDialog.setModal(true);
    scannerSettingsDialog.setVisible(true);
    if (scannerSettingsDialog.getDialogResult() == JOptionPane.OK_OPTION) {
      try {
        detectScanners(scannerSettingsDialog.getRemoteAddress(),
            scannerSettingsDialog.getPortNumber(), scannerSettingsDialog.getDescription());
      } catch (UnknownHostException ex) {
        LOG.error(ex, ex);
      } catch (IOException ex) {
        LOG.error(ex, ex);
      } catch (SaneException ex) {
        LOG.error(ex, ex);
      }
    }
  }

  private void adjustSettingsComponents(Scanner scanner) {

    boolean isUsingCustomOptions = scanner.isUsingCustomOptions();

    components.getSaveSettingsButton().setEnabled(true);
    components.getUseCustomSettingsCheckBox().setEnabled(true);
    components.getUseCustomSettingsCheckBox().setSelected(isUsingCustomOptions);
    components.getCustomSettingsButton().setEnabled(isUsingCustomOptions);

    if (!isUsingCustomOptions) {
      updateResolutionModel(KnownSaneOptions.getResolutionModel(scanner),
          KnownSaneOptions.getResolution(scanner));
      updatePageSizeModel(KnownSaneOptions.getPageSizeModel(scanner),
          KnownSaneOptions.getPageSize(scanner));
      updateColorModeModel(KnownSaneOptions.getColorModeComponent(scanner),
          KnownSaneOptions.getColorMode(scanner));
      updateDuplexCheckBox(KnownSaneOptions.isDuplexScanningAvailable(scanner),
          KnownSaneOptions.isDuplexScanningEnabled(scanner));
      updateAutoCropCheckBox(KnownSaneOptions.isAutoCropAvailable(scanner),
          KnownSaneOptions.isAutoCropSelected(scanner));
      updateBlackThresholdModel(KnownSaneOptions.getBlackThresholdModel(scanner));
      updateUsingBlackThresholdCheckBox(KnownSaneOptions.isUsingDefaultBlackThreshold(scanner));
      updateSourceModel(KnownSaneOptions.getSourceModel(scanner),
          KnownSaneOptions.getSource(scanner));
    }

    updateBatchPrefix(scanner.getBatchPrefix());
    updatePagesToScan(new Integer(scanner.getPagesToScan()));
    updateBatchScanCheckBox(KnownSaneOptions.isBatchScanAvailable(scanner),
        KnownSaneOptions.isBatchScanSelected(scanner));

  }

  private void autoCropCheckBoxStateChanged(ActionEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    KnownSaneOptions.setAutoCrop(scanner, components.getAutoCropCheckBox().isSelected());
  }

  private void batchNameCaretUpdateActionPerformed(CaretEvent e) {
    ScannerListItem currentScannerListItem = components.getScannerList().getSelectedValue();
    if (currentScannerListItem == null) {
      return;
    }
    Scanner scanner = currentScannerListItem.getScanner();
    if (scanner == null) {
      return;
    }
    scanner.setBatchPrefix(components.getBatchNameTextField().getText());
  }

  private void blackThresholdSpinnerStateChanged(ChangeEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    KnownSaneOptions.setBlackThreshold(scanner, (Integer) components.getBlackThresholdSpinner()
        .getValue());
  }

  private void cancelDetection(ActionEvent e) {
    if ((discoveryJob != null) && discoveryJob.isActive()) {
      discoveryJob.cancel();
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          components.getDetectScannersButton().setEnabled(true);
          components.getCancelDetectScannersButton().setEnabled(false);
          components.getDetectScannersButton().requestFocus();
        }
      });
    }
  }

  private void cancelScan(ActionEvent e) {
    final ScannerListItem scannerListItem = components.getScannerList().getSelectedValue();
    if (scannerListItem == null) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          showMessageDialog(Localizer.localize("SelectAScannerMessage"));
        }
      });
      return;
    }
    if (scannerListItem.isActive()) {
      scannerListItem.cancel();
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          updateScannerListItem(scannerListItem, ScannerStatus.IDLE);
        }
      });
    }
  }

  private void colorComboBoxActionPerformed(ItemEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    KnownSaneOptions
        .setColorMode(scanner, (String) components.getColorComboBox().getSelectedItem());
  }

  private void customSettingsActionPerformed(ActionEvent e) {
    if (components.getScannerList().getSelectedIndex() < 0) {
      return;
    }
    ScannerListItem scannerListItem = components.getScannerList().getSelectedValue();
    Scanner scanner = scannerListItem.getScanner();
    CustomSettingsDialog customSettingsDialog = new CustomSettingsDialog(
        components.getRootComponent());
    customSettingsDialog.setXstream(xstream);
    customSettingsDialog.setScanner(scanner);
    customSettingsDialog.setScanService(scanService);
    customSettingsDialog.initialize();
    customSettingsDialog.setModal(true);
    customSettingsDialog.setVisible(true);
    if (customSettingsDialog.getDialogResult() == JOptionPane.OK_OPTION) {
      PreferencesUtils.update(scanner, customSettingsDialog.getScanner());
    }
  }

  private void defaultThresholdCheckBoxStateChanged(ActionEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    boolean isUsingDefaultBlackThreshold = components.getDefaultThresholdCheckBox().isSelected();
    KnownSaneOptions.setUsingDefaultBlackThreshold(scanner, isUsingDefaultBlackThreshold);
    components.getBlackThresholdSpinner().setEnabled(!isUsingDefaultBlackThreshold);
  }

  private void detectScanners() {
    discoveryJob = new DiscoveryJob(scanService);
    discoveryJob.setNotificaiton(this);
    discoveryListener = new DiscoveryListener() {
      @Override
      public void discoveryEventOccurred(final DiscoveryEvent devt) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            displayDevicesList(devt.getDiscoveredScanners());
            discoveryJob.removeDiscoveryListener(discoveryListener);
            components.getDetectScannersButton().setEnabled(true);
            components.getCancelDetectScannersButton().setEnabled(false);
            components.getDetectScannersButton().requestFocus();
            discoveryListener = null;
            discoveryJob = null;
          }
        });
      }
    };
    discoveryJob.addDiscoveryListener(discoveryListener);
    discoveryJob.discover();
  }

  private void detectScanners(String hostAddress, int portNumber, String description)
      throws IOException, SaneException {
    discoveryJob = new DiscoveryJob(scanService);
    discoveryJob.setNotificaiton(this);
    discoveryListener = new DiscoveryListener() {
      @Override
      public void discoveryEventOccurred(final DiscoveryEvent devt) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            displayDevicesList(devt.getDiscoveredScanners());
            discoveryJob.removeDiscoveryListener(discoveryListener);
            components.getDetectScannersButton().setEnabled(true);
            components.getCancelDetectScannersButton().setEnabled(false);
            components.getDetectScannersButton().requestFocus();
            discoveryListener = null;
            discoveryJob = null;
          }
        });
      }
    };
    discoveryJob.addDiscoveryListener(discoveryListener);
    discoveryJob.discover(InetAddress.getByName(hostAddress), portNumber, description);
  }

  private void detectScannersActionPerformed(ActionEvent e) {
    if ((discoveryJob == null) || !(discoveryJob.isActive())) {
      detectScanners();
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          components.getDetectScannersButton().setEnabled(false);
          components.getCancelDetectScannersButton().setEnabled(true);
          components.getCancelDetectScannersButton().requestFocus();
        }
      });
    }
  }

  private void disableScanButtons() {
    components.getScanProgressBar().setEnabled(false);
    components.getScanButton().setEnabled(false);
    components.getCancelScanButton().setEnabled(false);
  }

  private void disableSettingsComponents() {
    components.getSaveSettingsButton().setEnabled(false);
    components.getUseCustomSettingsCheckBox().setSelected(false);
    components.getUseCustomSettingsCheckBox().setEnabled(false);
    components.getCustomSettingsButton().setEnabled(false);
    updateResolutionModel(null, null);
    updatePageSizeModel(null, null);
    updateColorModeModel(null, null);
    updateDuplexCheckBox(false, false);
    updateAutoCropCheckBox(false, false);
    updateBatchScanCheckBox(false, false);
    updateBlackThresholdModel(null);
    updateUsingBlackThresholdCheckBox(false);
    updateSourceModel(null, null);
    updateBatchPrefix(null);
    updatePagesToScan(null);
  }

  /**
   * @param discoveredScanners
   *          an {@link ArrayList} of {@link Scanner}
   */
  private void displayDevicesList(final ArrayList<Scanner> discoveredScanners) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        updateScannerList(discoveredScanners);
      }
    });
  }

  private void duplexScanningCheckBoxStateChanged(ActionEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    KnownSaneOptions.setDuplex(scanner, components.getDuplexScanningCheckBox().isSelected());
  }

  private void editScannerActionPerformed(ActionEvent e) {
    if (components.getScannerList().getSelectedIndex() < 0) {
      return;
    }
    ScannerListItem scannerListItem = components.getScannerList().getSelectedValue();
    Scanner scanner = scannerListItem.getScanner();

    ScannerSettingsDialog scannerSettingsDialog = new ScannerSettingsDialog(
        components.getRootComponent());
    scannerSettingsDialog.setTitle(Localizer.localize("EditScannerDialogTitle"));
    scannerSettingsDialog.setRemoteAddress(scanner.getRemoteAddress());
    scannerSettingsDialog.setRemotePortNumber(new Integer(scanner.getRemotePortNumber()));
    scannerSettingsDialog.setDescription(scanner.getDescription());
    scannerSettingsDialog.setModal(true);
    scannerSettingsDialog.setVisible(true);
    if (scannerSettingsDialog.getDialogResult() == JOptionPane.OK_OPTION) {
      scanner.setDescription(scannerSettingsDialog.getDescription());
      scanner.setRemoteAddress(scannerSettingsDialog.getRemoteAddress());
      scanner.setRemotePortNumber(scannerSettingsDialog.getPortNumber());
      try {
        preferences.save();
      } catch (IOException ex) {
        LOG.error(ex, ex);
        Misc.showSuccessMsg(
            components.getRootComponent(),
            String.format(Localizer.localize("ScannerSettingsNotSavedMessage"),
                ex.getLocalizedMessage()));
      }
      components.getScannerList().revalidate();
      components.getScannerList().repaint();
    }
  }

  private void enableScanButtons() {
    components.getScanButton().setEnabled(true);
  }

  public final IComponents getComponents() {
    return components;
  }

  @Override
  public final Exception getException() {
    return null;
  }

  public final ISwingSanePreferences getPreferences() {
    return preferences;
  }

  public final IPreferredDefaults getPreferredDefaults() {
    return preferredDefaults;
  }

  public final IScanEventHandler getScanEventHandler() {
    return scanEventHandler;
  }

  public final IScanService getScanService() {
    return scanService;
  }

  private ScannerListItem getSelectedScannerListItem() {
    final ScannerListItem scannerListItem = components.getScannerList().getSelectedValue();
    if (scannerListItem == null) {
      return null;
    }
    return scannerListItem;
  }

  private String getTimeStamp() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    String timeString = dateFormat.format(new Date().getTime());
    return timeString;
  }

  public final XStream getXstream() {
    return xstream;
  }

  private void initComponentListeners() {

    JComboBox<String> sourceComboBox = components.getSourceComboBox();
    sourceComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        sourceComboBoxActionPerformed(e);
      }
    });

    JComboBox<Integer> resolutionComboBox = components.getResolutionComboBox();
    resolutionComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        resolutionComboBoxActionPerformed(e);
      }
    });

    JComboBox<String> colorComboBox = components.getColorComboBox();
    colorComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        colorComboBoxActionPerformed(e);
      }
    });

    JComboBox<String> pageSizeComboBox = components.getPageSizeComboBox();
    pageSizeComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        pageSizeComboBoxActionPerformed(e);
      }
    });

    JSpinner pagesToScanSpinner = components.getPagesToScanSpinner();
    pagesToScanSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent evt) {
        pagesToScanSpinnerStateChanged(evt);
      }
    });

    JSpinner blackThresholdSpinner = components.getBlackThresholdSpinner();
    blackThresholdSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent evt) {
        blackThresholdSpinnerStateChanged(evt);
      }
    });

    JCheckBox duplexScanningCheckBox = components.getDuplexScanningCheckBox();
    duplexScanningCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        duplexScanningCheckBoxStateChanged(e);
      }
    });

    JCheckBox autoCropCheckBox = components.getAutoCropCheckBox();
    autoCropCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        autoCropCheckBoxStateChanged(e);
      }
    });

    JCheckBox useADFCheckBox = components.getBatchScanCheckBox();
    useADFCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        useADFCheckBoxStateChanged(e);
      }
    });

    JCheckBox defaultThresholdCheckBox = components.getDefaultThresholdCheckBox();
    defaultThresholdCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        defaultThresholdCheckBoxStateChanged(e);
      }
    });

    JTextField batchNameTextField = components.getBatchNameTextField();
    batchNameTextField.addCaretListener(new CaretListener() {
      @Override
      public void caretUpdate(CaretEvent e) {
        batchNameCaretUpdateActionPerformed(e);
      }
    });

    JCheckBox useCustomSettingsCheckBox = components.getUseCustomSettingsCheckBox();
    useCustomSettingsCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        useCustomSettingsActionPerformed(e);
      }
    });

    JButton customSettingsButton = components.getCustomSettingsButton();
    customSettingsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        customSettingsActionPerformed(e);
      }
    });

    JButton addScannerButton = components.getAddScannerButton();
    addScannerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addScannerButtonActionPerformed(e);
      }
    });

    JButton globalSettingsButton = components.getGlobalSettingsButton();
    globalSettingsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showGlobalSettingsDialog();
      }
    });

    JButton saveSettingsButton = components.getSaveSettingsButton();
    saveSettingsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveSettingsButtonActionPerformed(e);
      }
    });

    JButton editScannerButton = components.getEditScannerButton();
    editScannerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editScannerActionPerformed(e);
      }
    });

    JButton removeScannerButton = components.getRemoveScannerButton();
    removeScannerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        removeScannerButtonActionPerformed(e);
      }
    });

    JButton scanButton = components.getScanButton();
    scanButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        scanActionPerformed(e);
      }
    });
    scanButton.setEnabled(false);

    JButton cancelScanButton = components.getCancelScanButton();
    cancelScanButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelScan(e);
      }
    });
    cancelScanButton.setEnabled(false);

    JList<ScannerListItem> scannerList = components.getScannerList();
    scannerList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        scannerListSelectionChanged(e);
      }
    });

    JButton detectScannersButton = components.getDetectScannersButton();
    detectScannersButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        detectScannersActionPerformed(e);
      }
    });
    detectScannersButton.setEnabled(true);

    JButton detectCancelButton = components.getCancelDetectScannersButton();
    detectCancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelDetection(e);
      }
    });
    detectCancelButton.setEnabled(false);

  }

  public final boolean isAddScannerConfirmed(Scanner scanner) {
    return Misc.showConfirmDialog(
        components.getRootComponent(),
        Localizer.localize("ConfirmAddScannerTitle"),
        String.format(Localizer.localize("ConfirmAddScannerMessage"), scanner.getName(),
            scanner.getRemoteAddress() + ":" + scanner.getRemotePortNumber()),
        Localizer.localize("OK"), Localizer.localize("Cancel"));
  }

  public final boolean isBusy() {
    if (scannerListModel == null) {
      return false;
    }
    for (int i = 0; i < scannerListModel.getSize(); i++) {
      ScannerListItem scannerListItem = scannerListModel.getElementAt(i);
      ScanEventListener[] scanListeners = scannerListItem.getListeners(ScanEventListener.class);
      if ((scanListeners != null) && (scanListeners.length > 0)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final boolean isInterrupted() {
    return false;
  }

  private boolean isSameScanner(Scanner s1, Scanner s2) {
    if (s1.equals(s2)) {
      return true;
    }
    if (!(s1.getName().equals(s2.getName()))) {
      return false;
    }
    if (!(s1.getRemoteAddress().equals(s2.getRemoteAddress()))) {
      return false;
    }
    if (s1.getRemotePortNumber() != s2.getRemotePortNumber()) {
      return false;
    }
    return true;
  }

  @Override
  public final void message(final String message) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        addMessage(message);
      }
    });
  }

  private void pageSizeComboBoxActionPerformed(ItemEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    KnownSaneOptions.setScanArea(scanner, (String) components.getPageSizeComboBox()
        .getSelectedItem());
  }

  private void pagesToScanSpinnerStateChanged(ChangeEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    scanner.setPagesToScan((Integer) components.getPagesToScanSpinner().getValue());
  }

  private String parseBatchPrefix(String batchPrefix) {
    return batchPrefix.replace(Localizer.localize("TimeStampToken"), getTimeStamp());
  }

  private void removeScannerButtonActionPerformed(ActionEvent e) {

    ScannerListItem selectedScannerListItem = components.getScannerList().getSelectedValue();
    if (selectedScannerListItem == null) {
      return;
    }

    ApplicationPreferences applicationPreferences = preferences.getApplicationPreferences();
    ArrayList<Scanner> scannerList = applicationPreferences.getScannerList();
    ArrayList<Scanner> removalList = new ArrayList<Scanner>();

    if ((scannerList != null) && (scannerList.size() > 0)) {
      for (Scanner scanner : scannerList) {
        if (isSameScanner(scanner, selectedScannerListItem.getScanner())) {
          removalList.add(scanner);
        }
      }
    }

    for (Scanner scanner : removalList) {
      scannerList.remove(scanner);
    }

    scannerListModel.removeElement(selectedScannerListItem);

    disableSettingsComponents();

    try {
      preferences.save();
    } catch (IOException ex) {
      LOG.error(ex, ex);
    }

  }

  private void resolutionComboBoxActionPerformed(ItemEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    KnownSaneOptions.setResolution(scanner, (Integer) components.getResolutionComboBox()
        .getSelectedItem());
  }

  private void restorePreferences() {
    ApplicationPreferences applicationPreferences = preferences.getApplicationPreferences();
    ArrayList<Scanner> scannerList = applicationPreferences.getScannerList();
    if ((scannerList != null) && (scannerList.size() > 0)) {
      for (Scanner scanner : scannerList) {
        restorePreferencesFromScanner(scanner);
      }
    }
  }

  private void restorePreferencesFromScanner(Scanner scanner) {
    final ScannerListItem scannerListItem = new ScannerListItem(scanner);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        scannerListModel.addElement(scannerListItem);
        components.getScannerList().revalidate();
        components.getScannerList().repaint();
      }
    });
  }

  private void savePreferences(boolean showSaveNotification) {
    savePreferences(showSaveNotification, null);
  }

  private void savePreferences(boolean showSaveNotification, ScannerListItem currentScannerListItem) {

    ApplicationPreferences applicationPreferences = preferences.getApplicationPreferences();

    ArrayList<Scanner> scannerList = applicationPreferences.getScannerList();

    if (currentScannerListItem == null) {
      Enumeration<ScannerListItem> elements = scannerListModel.elements();
      scannerList.clear();
      while (elements.hasMoreElements()) {
        ScannerListItem currentItem = elements.nextElement();
        scannerList.add(currentItem.getScanner());
      }
    } else {
      boolean updated = false;
      for (int i = 0; i < scannerList.size(); i++) {
        if (scannerList.get(i).getGuid().equals(currentScannerListItem.getScanner().getGuid())) {
          scannerList.set(i, currentScannerListItem.getScanner());
          updated = true;
        }
      }
      if (!updated) {
        scannerList.add(currentScannerListItem.getScanner());
      }
    }

    try {
      preferences.save();
      if (showSaveNotification) {
        Misc.showSuccessMsg(components.getRootComponent(),
            Localizer.localize("ScannerSettingsSavedSuccessfullyMessage"));
      }
    } catch (IOException ex) {
      LOG.error(ex, ex);
      Misc.showSuccessMsg(
          components.getRootComponent(),
          String.format(Localizer.localize("ScannerSettingsNotSavedMessage"),
              ex.getLocalizedMessage()));
    }

  }

  private void saveSettingsButtonActionPerformed(ActionEvent e) {
    ScannerListItem currentScannerListItem = components.getScannerList().getSelectedValue();
    savePreferences(true, currentScannerListItem);
  }

  private void scanActionPerformed(ActionEvent e) {
    final ScannerListItem scannerListItem = getSelectedScannerListItem();
    if (scannerListItem == null) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          showMessageDialog(Localizer.localize("SelectAScannerMessage"));
        }
      });
      return;
    }
    if (!(scannerListItem.isActive())) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          updateScannerListItem(scannerListItem, ScannerStatus.BUSY);
        }
      });
      ScanEventListener scanEventListener = new ScanEventListener() {
        @Override
        public void eventOccurred(final ScanEvent sevt) {
          if (sevt.getBufferedImage() == null) {
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                updateScannerListItem(scannerListItem, ScannerStatus.IDLE);
              }
            });
            scannerListItem.removeListeners();

            return;
          }
          scanEventHandler.scanPerformed(sevt);

        }
      };
      int pagesToScan = Integer.MAX_VALUE;
      if (components.getPagesToScanSpinner().isEnabled()) {
        pagesToScan = (Integer) components.getPagesToScanSpinner().getValue();
        if (components.getDuplexScanningCheckBox().isSelected()) {
          pagesToScan *= 2;
        }
      }
      ScanListenerAdapter progressBarUpdater = new ScanListenerAdapter() {
        @Override
        public void recordRead(SaneDevice device, final int totalBytesRead, final int imageSize) {
          final double fraction = (1.0d * totalBytesRead) / imageSize;
          SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              if (components.getScannerList().getSelectedValue() == scannerListItem) {
                if (components.getScanProgressBar().isIndeterminate()) {
                  components.getScanProgressBar().setIndeterminate(false);
                }
                components.getScanProgressBar().setEnabled(true);
                components.getScanProgressBar().setValue((int) (fraction * 100));
              }
            }
          });
        }

        @Override
        public void scanningFinished(SaneDevice device) {
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              if (components.getScannerList().getSelectedValue() == scannerListItem) {
                components.getScanProgressBar().setValue(0);
              }
            }
          });
        }
      };

      ScanListener rateLimitedScanListener = RateLimitingScanListeners.noMoreFrequentlyThan(
          progressBarUpdater, PROGRESS_BAR_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);

      ScanJob scanJob = new ScanJob(scanService, scannerListItem.getScanner());
      scannerListItem.setScanEventListener(scanEventListener);
      scannerListItem.setScanJob(scanJob);
      scanJob.setPagesToScan(pagesToScan);
      scanJob.setUseADF(components.getBatchScanCheckBox().isSelected());
      scanJob.setBatchPrefix(parseBatchPrefix(scannerListItem.getScanner().getBatchPrefix()));
      scanJob.setNotificaiton(this);
      scanJob.addScanListener(scanEventListener);
      scanJob.acquire(rateLimitedScanListener);

    }
  }

  private void scannerListSelectionChanged(ListSelectionEvent e) {
    if (!(e.getValueIsAdjusting())) {
      return;
    }
    @SuppressWarnings("unchecked")
    JList<ScannerListItem> scannerList = (JList<ScannerListItem>) e.getSource();
    if (scannerList.getSelectedIndex() < 0) {
      disableSettingsComponents();
      disableScanButtons();
      return;
    }
    disableSettingsComponents();
    ScannerListItem scannerListItem = scannerList.getSelectedValue();
    Scanner scanner = scannerListItem.getScanner();
    adjustSettingsComponents(scanner);
    enableScanButtons();
    updateComponents(scannerListItem);
  }

  private void selectPreferredDefaults(Scanner scanner) {
    preferredDefaults.update(scanner);
  }

  public final void setComponents(IComponents components) {
    this.components = components;
    this.components.getScannerList().setModel(scannerListModel);
    initComponentListeners();
    restorePreferences();
  }

  @Override
  public void setException(Exception exception) {
  }

  @Override
  public void setInterrupted(boolean interrupted) {
  }

  public final void setPreferences(ISwingSanePreferences preferences) {
    this.preferences = preferences;
  }

  public final void setPreferredDefaults(IPreferredDefaults preferredDefaults) {
    this.preferredDefaults = preferredDefaults;
  }

  public final void setScanEventHandler(IScanEventHandler scanEventHandler) {
    this.scanEventHandler = scanEventHandler;
  }

  private void setScannerBusy(boolean scannerBusy) {
    if (scannerBusy) {
      components.getScanProgressBar().setIndeterminate(true);
      components.getScanProgressBar().setEnabled(true);
      components.getScanButton().setEnabled(false);
      components.getCancelScanButton().setEnabled(true);
      components.getCancelScanButton().requestFocus();
    } else {
      components.getScanProgressBar().setIndeterminate(false);
      components.getScanProgressBar().setValue(0);
      components.getScanProgressBar().setEnabled(false);
      components.getScanButton().setEnabled(true);
      components.getCancelScanButton().setEnabled(false);
      components.getScanButton().requestFocus();
    }
  }

  public final void setScanService(IScanService scanServiceImpl) {
    scanService = scanServiceImpl;
  }

  public final void setXstream(XStream xstream) {
    this.xstream = xstream;
  }

  private void showGlobalSettingsDialog() {
    HashMap<String, Login> saneLogins = PreferencesUtils.copy(preferences
        .getApplicationPreferences().getSaneLogins());
    String saneServiceName = preferences.getApplicationPreferences().getSaneServiceIdentity()
        .getServiceName();
    GlobalSettingsDialog globalSettingsDialog = new GlobalSettingsDialog(
        components.getRootComponent());
    globalSettingsDialog.setLogins(saneLogins);
    globalSettingsDialog.setSaneServiceName(saneServiceName);
    globalSettingsDialog.initialize();
    globalSettingsDialog.setModal(true);
    globalSettingsDialog.setVisible(true);
    if (globalSettingsDialog.getDialogResult() == JOptionPane.OK_OPTION) {
      try {
        preferences.getApplicationPreferences().getSaneServiceIdentity()
            .setServiceName(globalSettingsDialog.getServiceName().trim());
        PreferencesUtils.update(preferences.getApplicationPreferences().getSaneLogins(),
            globalSettingsDialog.getSaneLogins());
        preferences.save();
      } catch (IOException e) {
        LOG.error(e, e);
      }
    }
  }

  private void showMessageDialog(final String message) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JOptionPane.showMessageDialog(components.getRootComponent(), message);
      }
    });
  }

  private void sourceComboBoxActionPerformed(ItemEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    KnownSaneOptions.setSource(scanner, (String) components.getSourceComboBox().getSelectedItem());
  }

  private void updateAutoCropCheckBox(boolean enabled, boolean selected) {
    JCheckBox autoCropCheckBox = components.getAutoCropCheckBox();
    autoCropCheckBox.setEnabled(enabled);
    autoCropCheckBox.setSelected(selected);
  }

  private void updateBatchPrefix(final String batchPrefix) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JTextField batchNameTextField = components.getBatchNameTextField();
        if (batchPrefix == null) {
          batchNameTextField.setEnabled(false);
        } else {
          batchNameTextField.setEnabled(true);
        }
        batchNameTextField.setText(batchPrefix);
      }
    });
  }

  private void updateBatchScanCheckBox(boolean enabled, boolean selected) {
    JCheckBox batchScanCheckBox = components.getBatchScanCheckBox();
    batchScanCheckBox.setEnabled(enabled);
    batchScanCheckBox.setSelected(selected);
    if (enabled && selected) {
      components.getPagesToScanSpinner().setEnabled(false);
    } else {
      components.getPagesToScanSpinner().setEnabled(true);
    }
  }

  private void updateBlackThresholdModel(SpinnerModel blackThresholdSpinnerModel) {
    JSpinner blackThresholdSpinner = components.getBlackThresholdSpinner();
    blackThresholdSpinner.setModel(blackThresholdSpinnerModel != null ? blackThresholdSpinnerModel
        : new SpinnerNumberModel(1, 1, null, 1));
    blackThresholdSpinner.setEnabled(blackThresholdSpinnerModel != null ? !(components
        .getDefaultThresholdCheckBox().isSelected()) : false);
  }

  private void updateColorModeModel(ComboBoxModel<String> colorModeModel, String colorMode) {
    JComboBox<String> colorComboBox = components.getColorComboBox();
    colorComboBox.setModel(colorModeModel != null ? colorModeModel
        : new DefaultComboBoxModel<String>());
    colorComboBox.setEnabled(colorModeModel != null ? true : false);
    if (colorModeModel != null) {
      colorComboBox.setSelectedItem(colorMode);
    }
  }

  private void updateComponents(final ScannerListItem scannerListItem) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (components.getScannerList().getSelectedValue() == scannerListItem) {
          if (scannerListItem.getScannerStatus() == ScannerStatus.BUSY) {
            setScannerBusy(true);
          } else if (scannerListItem.getScannerStatus() == ScannerStatus.IDLE) {
            setScannerBusy(false);
          }
        }
        components.getScannerList().revalidate();
        components.getScannerList().repaint();
      }
    });
  }

  private void updateDuplexCheckBox(boolean enabled, boolean selected) {
    JCheckBox duplexCheckBox = components.getDuplexScanningCheckBox();
    duplexCheckBox.setEnabled(enabled);
    duplexCheckBox.setSelected(selected);
  }

  private void updatePageSizeModel(ComboBoxModel<String> pageSizeModel, String pageSize) {
    JComboBox<String> pageSizeComboBox = components.getPageSizeComboBox();
    pageSizeComboBox.setModel(pageSizeModel != null ? pageSizeModel
        : new DefaultComboBoxModel<String>());
    pageSizeComboBox.setEnabled(pageSizeModel != null ? true : false);
    if (pageSizeModel != null) {
      pageSizeComboBox.setSelectedItem(pageSize);
    }
  }

  private void updatePagesToScan(Integer pagesToScan) {
    JSpinner pagesToScanSpinner = components.getPagesToScanSpinner();
    if (pagesToScan == null) {
      pagesToScanSpinner.setEnabled(false);
    } else {
      pagesToScanSpinner.setEnabled(!(components.getBatchScanCheckBox().isSelected()));
      pagesToScanSpinner.setValue(pagesToScan);
    }
  }

  private void updateResolutionModel(ComboBoxModel<Integer> resolutionModel, Integer resolution) {
    JComboBox<Integer> resolutionComboBox = components.getResolutionComboBox();
    resolutionComboBox.setModel(resolutionModel != null ? resolutionModel
        : new DefaultComboBoxModel<Integer>());
    resolutionComboBox.setEnabled(resolutionModel != null ? true : false);
    if (resolutionModel != null) {
      resolutionComboBox.setSelectedItem(resolution);
    }
  }

  private void updateScannerList(ArrayList<Scanner> discoveredScanners) {
    if (discoveredScanners == null) {
      return;
    }
    for (Scanner scanner : discoveredScanners) {
      if (isAddScannerConfirmed(scanner)) {
        selectPreferredDefaults(scanner);
        scannerListModel.addElement(new ScannerListItem(scanner));
      }
    }
    components.getScannerList().setModel(scannerListModel);
    savePreferences(false);
  }

  private void updateScannerListItem(ScannerListItem scannerListItem, ScannerStatus scannerStatus) {
    scannerListItem.setScannerStatus(scannerStatus);
    updateComponents(scannerListItem);
  }

  private void updateSourceModel(ComboBoxModel<String> sourceModel, String source) {
    JComboBox<String> sourceComboBox = components.getSourceComboBox();
    sourceComboBox.setModel(sourceModel != null ? sourceModel : new DefaultComboBoxModel<String>());
    sourceComboBox.setEnabled(sourceModel != null ? true : false);
    if (source != null) {
      sourceComboBox.setSelectedItem(source);
    }
  }

  private void updateUsingBlackThresholdCheckBox(boolean usingDefaultBlackThreshold) {
    JCheckBox useDefaultBlackThreshold = components.getDefaultThresholdCheckBox();
    useDefaultBlackThreshold.setSelected(usingDefaultBlackThreshold);
  }

  private void useADFCheckBoxStateChanged(ActionEvent e) {
    if (getSelectedScannerListItem() == null) {
      return;
    }
    Scanner scanner = getSelectedScannerListItem().getScanner();
    if (scanner == null) {
      return;
    }
    boolean isBatchScanSelected = components.getBatchScanCheckBox().isSelected();
    KnownSaneOptions.setBatchScan(scanner, isBatchScanSelected);
    components.getPagesToScanSpinner().setEnabled(!isBatchScanSelected);
  }

  private void useCustomSettingsActionPerformed(ActionEvent e) {
    if (components.getUseCustomSettingsCheckBox().isSelected()) {
      components.getCustomSettingsButton().setEnabled(true);
      updateResolutionModel(null, null);
      updatePageSizeModel(null, null);
      updateColorModeModel(null, null);
      updateDuplexCheckBox(false, false);
      updateAutoCropCheckBox(false, false);
      updateBlackThresholdModel(null);
      updateUsingBlackThresholdCheckBox(false);
      updateSourceModel(null, null);
      if (components.getScannerList().getSelectedIndex() >= 0) {
        ScannerListItem scannerListItem = components.getScannerList().getSelectedValue();
        Scanner scanner = scannerListItem.getScanner();
        scanner.setUsingCustomOptions(true);
      }
    } else {
      if (components.getScannerList().getSelectedIndex() < 0) {
        disableSettingsComponents();
        return;
      }
      ScannerListItem scannerListItem = components.getScannerList().getSelectedValue();
      Scanner scanner = scannerListItem.getScanner();
      scanner.setUsingCustomOptions(false);
      adjustSettingsComponents(scanner);
    }
  }

}
