package com.swingsane.gui.controller;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.swingsane.gui.list.ScannerListItem;

public interface IComponents {

  JButton getAddScannerButton();

  JCheckBox getAutoCropCheckBox();

  JTextField getBatchNameTextField();

  JCheckBox getBatchScanCheckBox();

  JSpinner getBlackThresholdSpinner();

  JButton getCancelDetectScannersButton();

  JButton getCancelScanButton();

  JComboBox<String> getColorComboBox();

  JButton getCustomSettingsButton();

  JCheckBox getDefaultThresholdCheckBox();

  JButton getDetectScannersButton();

  JCheckBox getDuplexScanningCheckBox();

  JButton getEditScannerButton();

  JButton getGlobalSettingsButton();

  JTextPane getMessagesTextPane();

  JComboBox<String> getPageSizeComboBox();

  JSpinner getPagesToScanSpinner();

  JButton getRemoveScannerButton();

  JComboBox<Integer> getResolutionComboBox();

  Component getRootComponent();

  JButton getSaveSettingsButton();

  JButton getScanButton();

  JList<ScannerListItem> getScannerList();

  JProgressBar getScanProgressBar();

  JComboBox<String> getSourceComboBox();

  JCheckBox getUseCustomSettingsCheckBox();

}
