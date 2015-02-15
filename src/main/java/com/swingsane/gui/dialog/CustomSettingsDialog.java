package com.swingsane.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import com.swingsane.business.notification.DialogNotificationImpl;
import com.swingsane.business.notification.INotification;
import com.swingsane.business.scanning.IScanService;
import com.swingsane.business.scanning.ScanJob;
import com.swingsane.gui.component.JTableButtonMouseListener;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.PreferencesUtils;
import com.swingsane.preferences.model.BooleanOption;
import com.swingsane.preferences.model.ButtonOption;
import com.swingsane.preferences.model.Constraints;
import com.swingsane.preferences.model.FixedOption;
import com.swingsane.preferences.model.IntegerOption;
import com.swingsane.preferences.model.OptionsOrderValuePair;
import com.swingsane.preferences.model.Scanner;
import com.swingsane.preferences.model.StringOption;
import com.swingsane.util.FilenameExtensionFilter;
import com.swingsane.util.Misc;
import com.thoughtworks.xstream.XStream;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class CustomSettingsDialog extends JDialog {

  private class CustomTableCellEditor extends DefaultCellEditor {

    private OptionsOrderValuePair valuePair;

    public CustomTableCellEditor(JCheckBox checkBox, final OptionsOrderValuePair vp) {
      super(checkBox);
      checkBox.removeActionListener(delegate);
      valuePair = vp;
      delegate.setValue(checkBox);
      delegate = new EditorDelegate() {
        @Override
        public void setValue(Object val) {
          if (val instanceof JCheckBox) {
            if (value == null) {
              value = val;
            }
          }
        }

        @Override
        public boolean stopCellEditing() {
          updateOption(valuePair, (Component) value);
          return true;
        }
      };
      checkBox.addActionListener(delegate);
      setClickCountToStart(1);
    }

    public CustomTableCellEditor(final JComboBox<?> comboBox, final OptionsOrderValuePair vp) {
      super(comboBox);
      comboBox.removeActionListener(delegate);
      valuePair = vp;
      delegate.setValue(comboBox);
      delegate = new EditorDelegate() {
        @Override
        public void setValue(Object val) {
          if (val instanceof JComboBox) {
            if (value == null) {
              value = val;
            }
          }
        }

        @Override
        public boolean stopCellEditing() {
          updateOption(valuePair, (Component) value);
          return true;
        }
      };
      comboBox.addActionListener(delegate);
      setClickCountToStart(1);
    }

    public CustomTableCellEditor(JTextField textField, final OptionsOrderValuePair vp) {
      super(textField);
      textField.removeActionListener(delegate);
      valuePair = vp;
      delegate.setValue(textField);
      delegate = new EditorDelegate() {
        @Override
        public void setValue(Object val) {
          if (val instanceof JTextField) {
            if (value == null) {
              value = val;
            }
          }
        }

        @Override
        public boolean stopCellEditing() {
          updateOption(valuePair, (Component) value);
          return true;
        }
      };
      textField.addActionListener(delegate);
      setClickCountToStart(1);
    }

    @Override
    public Object getCellEditorValue() {
      if (editorComponent instanceof JCheckBox) {
        return editorComponent;
      } else if (editorComponent instanceof JTextField) {
        return editorComponent;
      } else if (editorComponent instanceof JCheckBox) {
        return editorComponent;
      } else if (editorComponent instanceof JSpinner) {
        return editorComponent;
      } else if (editorComponent instanceof JButton) {
        return editorComponent;
      } else {
        return editorComponent;
      }
    }

  }

  public class CustomTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof Component) {
        return (Component) value;
      } else {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      }
    }

  }

  private class JTableCustomComponent extends JTable {

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
      Object value = getValueAt(row, column);
      if (value instanceof JCheckBox) {
        return new CustomTableCellEditor((JCheckBox) value, scanner.getOptionOrdering().get(row));
      } else if (value instanceof JComboBox) {
        return new CustomTableCellEditor((JComboBox<?>) value, scanner.getOptionOrdering().get(row));
      } else if (value instanceof JTextField) {
        return new CustomTableCellEditor((JTextField) value, scanner.getOptionOrdering().get(row));
      } else if (value instanceof JSpinner) {
        return new SpinnerEditor((JSpinner) value, scanner.getOptionOrdering().get(row));
      } else {
        return getDefaultEditor(value.getClass());
      }
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
      Object obj = getValueAt(row, column);
      if (obj == null) {
        return getDefaultRenderer(null);
      }
      Class<?> cellClass = obj.getClass();
      return getDefaultRenderer(cellClass);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      Class<?> cellClass = getValueAt(row, column).getClass();
      return (cellClass == JCheckBox.class) || (cellClass == JTextField.class)
          || (cellClass == JSpinner.class) || (cellClass == JComboBox.class);
    }

  }

  private class SpinnerEditor extends DefaultCellEditor {

    private OptionsOrderValuePair valuePair;

    private JSpinner spinner;

    public SpinnerEditor(final JSpinner spinner, final OptionsOrderValuePair vp) {
      super(new JTextField());
      this.spinner = spinner;
      valuePair = vp;
      spinner.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          updateOption(valuePair, spinner);
        }
      });
      setClickCountToStart(1);
    }

    @Override
    public Object getCellEditorValue() {
      return spinner;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
        int row, int column) {
      if (value instanceof Integer) {
        spinner.setValue(value);
      } else if (value instanceof Double) {
        spinner.setValue(value);
      }
      return spinner;
    }

  }

  private static DefaultTableModel getTableModel() {
    return new DefaultTableModel(new String[] { Localizer.localize("OptionNameHeaderText"),
        Localizer.localize("OptionActiveStatusHeaderText"),
        Localizer.localize("OptionModifyHeaderText") }, 0) {

      private boolean[] columnEditable = new boolean[] { false, false, true };

      @Override
      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnEditable[columnIndex];
      }
    };
  }

  private static final int BOUNDS_WIDTH = 700;

  private static final int BOUNDS_HEIGHT = 500;

  private final Dimension bounds = new Dimension(BOUNDS_WIDTH, BOUNDS_HEIGHT);

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(CustomSettingsDialog.class);

  private static final int ROW_HEIGHT = 30;

  private final JPanel contentPanel = new JPanel();
  private int dialogResult = JOptionPane.CANCEL_OPTION;

  private JTableCustomComponent optionNameTable;

  private DefaultTableModel optionNameTableModel = getTableModel();

  private Scanner scanner;

  private ArrayList<OptionsOrderValuePair> optionOrdering;

  private IScanService scanService;

  private XStream xstream;

  public CustomSettingsDialog(Component parent) {
    initComponents();
    pack();
    setLocationRelativeTo(parent);
  }

  private void cancelActionPerformed(ActionEvent e) {
    dispose();
  }

  private void cellButtonClicked(String key) {
    ScanJob scanJob = new ScanJob(scanService, scanner);
    scanJob.setButtonValue(key);
  }

  private void checkOptionsActionPerformed(ActionEvent e) {

    final INotification notificaiton = new DialogNotificationImpl(getRootPane()
        .getTopLevelAncestor());

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {
        notificaiton.message(Localizer.localize("CheckingOptionsText"));
        ScanJob scanJob = new ScanJob(scanService, scanner);
        try {
          scanJob.checkOptions();
        } catch (Exception ex) {
          showCheckOptionsErrorMessage(ex);
        }
        updateTableModel();
        optionNameTable.revalidate();
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          ((JDialog) notificaiton).setVisible(false);
        } catch (Exception ex) {
          LOG.error(ex, ex);
        } finally {
          ((JDialog) notificaiton).dispose();
        }
      }
    };

    worker.execute();

    ((JDialog) notificaiton).setModal(true);
    ((JDialog) notificaiton).setVisible(true);

  }

  private void exportActionPerformed(ActionEvent e) {

    final File file = getSaveFile("scanner", "xml");

    if (file == null) {
      return;
    }

    final INotification notificaiton = new DialogNotificationImpl(getRootPane()
        .getTopLevelAncestor());

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {
        notificaiton.message(Localizer.localize("SavingScannerPreferencesMessageText"));
        PreferencesUtils.exportScannerXML(xstream, scanner, file);
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          showSaveSuccessMessage();
          ((JDialog) notificaiton).setVisible(false);
        } catch (Exception ex) {
          LOG.error(ex, ex);
          showSaveErrorMessage(ex);
        } finally {
          ((JDialog) notificaiton).dispose();
        }
      }
    };

    worker.execute();

    ((JDialog) notificaiton).setModal(true);
    ((JDialog) notificaiton).setVisible(true);

  }

  public final int getDialogResult() {
    return dialogResult;
  }

  private File getLoadFile(String filename, String extension) {
    FileDialog fd = new FileDialog((JDialog) getRootPane().getTopLevelAncestor(),
        Localizer.localize("LoadScannerOptionsFromFileTitle"), FileDialog.LOAD);
    fd.setDirectory(".");
    FilenameExtensionFilter filter = new FilenameExtensionFilter();
    filter.addExtension("xml");
    fd.setFilenameFilter(filter);
    fd.setModal(true);
    fd.setVisible(true);
    if (fd.getFile() == null) {
      return null;
    }
    return new File(fd.getDirectory() + File.separator + fd.getFile());
  }

  private Component getOptionComponent(OptionsOrderValuePair vp) {

    // TODO: finish adding the constraints code.

    switch (vp.getSaneOptionType()) {

    case STRING:

      StringOption stringOption = scanner.getStringOptions().get(vp.getKey());

      Constraints constraints = stringOption.getConstraints();
      if (constraints != null) {
        List<String> values = constraints.getStringList();
        if (values != null) {

          DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>();
          for (String value : values) {
            dcbm.addElement(value);
          }
          JComboBox<String> comboBox = new JComboBox<String>();
          comboBox.setModel(dcbm);
          comboBox.setSelectedItem(stringOption.getValue());
          comboBox.setFont(UIManager.getFont("ComboBox.font"));
          comboBox.setToolTipText(stringOption.getDescription());
          comboBox.setEditable(false);
          return comboBox;
        }
      }

      JTextField tf = new JTextField();
      tf.setText(stringOption.getValue());
      return tf;

    case INTEGER:
      JSpinner integerSpinner = new JSpinner();
      IntegerOption integerOptions = scanner.getIntegerOptions().get(vp.getKey());
      int minInt = (integerOptions.getConstraints() != null)
          && (integerOptions.getConstraints().getMinimumInteger() != null) ? integerOptions
          .getConstraints().getMinimumInteger() : 1;
      int maxInt = (integerOptions.getConstraints() != null)
          && (integerOptions.getConstraints().getMaximumInteger() != null) ? integerOptions
          .getConstraints().getMaximumInteger() : Integer.MAX_VALUE;
      int stepInt = (integerOptions.getConstraints() != null)
          && (integerOptions.getConstraints().getQuantumInteger() != null) ? integerOptions
          .getConstraints().getQuantumInteger() : 1;
      integerSpinner.setModel(new SpinnerNumberModel(new Integer(integerOptions.getValue()),
          new Integer(minInt), new Integer(maxInt), new Integer(stepInt)));
      integerSpinner.setToolTipText(integerOptions.getDescription());
      integerSpinner.setFont(UIManager.getFont("Spinner.font"));
      return integerSpinner;

    case BOOLEAN:
      JCheckBox checkBox = new JCheckBox();
      BooleanOption booleanOptions = scanner.getBooleanOptions().get(vp.getKey());
      checkBox.setSelected(booleanOptions.getValue());
      checkBox.setToolTipText(booleanOptions.getDescription());
      checkBox.setFont(UIManager.getFont("CheckBox.font"));
      return checkBox;

    case FIXED:
      JSpinner fixedSpinner = new JSpinner();
      FixedOption fixedOptions = scanner.getFixedOptions().get(vp.getKey());
      double minDouble = (fixedOptions.getConstraints() != null)
          && (fixedOptions.getConstraints().getMinimumFixed() != null) ? fixedOptions
          .getConstraints().getMinimumFixed() : 1;
      double maxDouble = (fixedOptions.getConstraints() != null)
          && (fixedOptions.getConstraints().getMaximumFixed() != null) ? fixedOptions
          .getConstraints().getMaximumFixed() : Double.MAX_VALUE;
      double stepDouble = (fixedOptions.getConstraints() != null)
          && (fixedOptions.getConstraints().getQuantumFixed() != null) ? fixedOptions
          .getConstraints().getQuantumFixed() : 1;
      fixedSpinner.setModel(new SpinnerNumberModel(new Double(fixedOptions.getValue()), new Double(
          minDouble), new Double(maxDouble), new Double(stepDouble)));
      fixedSpinner.setToolTipText(fixedOptions.getDescription());
      fixedSpinner.setFont(UIManager.getFont("Spinner.font"));
      return fixedSpinner;

    case BUTTON:
      JButton button = new JButton();
      button.setText(Localizer.localize("RunButtonText"));
      String key = vp.getKey();
      button.setActionCommand(key);
      button.setFont(UIManager.getFont("Button.font"));
      button.setToolTipText(getToolTipText(key));
      button.addMouseListener(new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.isConsumed()) {
            return;
          }
          cellButtonClicked(((JButton) (e.getSource())).getActionCommand());
          e.consume();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
      });
      return button;

    case GROUP:
      return null;
    default:
      break;

    }

    return null;

  }

  private File getSaveFile(String filename, String extension) {
    FileDialog fd = new FileDialog((JDialog) getRootPane().getTopLevelAncestor(),
        Localizer.localize("SaveScannerOptionsToFileTitle"), FileDialog.SAVE);
    fd.setDirectory(".");
    FilenameExtensionFilter filter = new FilenameExtensionFilter();
    filter.addExtension("xml");
    fd.setFilenameFilter(filter);
    fd.setFile(filename + "." + extension);
    fd.setModal(true);
    fd.setVisible(true);
    if (fd.getFile() == null) {
      return null;
    }
    return new File(fd.getDirectory() + File.separator + fd.getFile());
  }

  public final Scanner getScanner() {
    return scanner;
  }

  public final IScanService getScanService() {
    return scanService;
  }

  private String getToolTipText(String key) {
    ButtonOption buttonOption = scanner.getButtonOptions().get(key);
    return buttonOption.getDescription();
  }

  public final XStream getXstream() {
    return xstream;
  }

  private void importActionPerformed(ActionEvent e) {

    final File file = getLoadFile("scanner", "xml");

    if (file == null) {
      return;
    }

    final INotification notificaiton = new DialogNotificationImpl(getRootPane()
        .getTopLevelAncestor());

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {
        notificaiton.message(Localizer.localize("LoadingScannerPreferencesMessageText"));
        String remoteAddress = scanner.getRemoteAddress();
        int remotePort = scanner.getRemotePortNumber();
        String description = scanner.getDescription();
        scanner = PreferencesUtils.importScannerXML(xstream, file);
        scanner.setRemoteAddress(remoteAddress);
        scanner.setRemotePortNumber(remotePort);
        scanner.setDescription(description);
        updateTableModel();
        optionNameTable.revalidate();
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          showLoadSuccessMessage();
          ((JDialog) notificaiton).setVisible(false);
        } catch (Exception ex) {
          LOG.error(ex, ex);
          showLoadErrorMessage(ex);
        } finally {
          ((JDialog) notificaiton).dispose();
        }
      }
    };

    worker.execute();

    ((JDialog) notificaiton).setModal(true);
    ((JDialog) notificaiton).setVisible(true);

  }

  private void initComponents() {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(Localizer.localize("CustomSettingsDialogTitle"));
    setBounds(0, 0, bounds.width, bounds.height);
    setPreferredSize(bounds);
    setSize(bounds);
    setMinimumSize(bounds);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] { 80, 0 };
    gbl_contentPanel.rowHeights = new int[] { 0, 0, 0 };
    gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    contentPanel.setLayout(gbl_contentPanel);
    {
      JScrollPane optionNameScrollPane = new JScrollPane();
      GridBagConstraints gbc_optionNameScrollPane = new GridBagConstraints();
      gbc_optionNameScrollPane.insets = new Insets(0, 0, 5, 0);
      gbc_optionNameScrollPane.fill = GridBagConstraints.BOTH;
      gbc_optionNameScrollPane.gridx = 0;
      gbc_optionNameScrollPane.gridy = 0;
      contentPanel.add(optionNameScrollPane, gbc_optionNameScrollPane);
      {
        optionNameTable = new JTableCustomComponent();
        optionNameTable.setFont(UIManager.getFont("Table.font"));
        optionNameTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        optionNameTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
        optionNameScrollPane.setViewportView(optionNameTable);
      }
    }
    {
      JPanel listActionButtonPanel = new JPanel();
      GridBagConstraints gbc_listActionButtonPanel = new GridBagConstraints();
      gbc_listActionButtonPanel.fill = GridBagConstraints.BOTH;
      gbc_listActionButtonPanel.gridx = 0;
      gbc_listActionButtonPanel.gridy = 1;
      contentPanel.add(listActionButtonPanel, gbc_listActionButtonPanel);
      GridBagLayout gbl_listActionButtonPanel = new GridBagLayout();
      gbl_listActionButtonPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
      gbl_listActionButtonPanel.rowHeights = new int[] { 25, 0 };
      gbl_listActionButtonPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 1.0,
          Double.MIN_VALUE };
      gbl_listActionButtonPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
      listActionButtonPanel.setLayout(gbl_listActionButtonPanel);
      {
        JButton checkOptionsButton = new JButton(Localizer.localize("CheckOptionsButtonText"));
        checkOptionsButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            checkOptionsActionPerformed(e);
          }
        });
        checkOptionsButton.setFont(UIManager.getFont("Button.font"));
        checkOptionsButton.setMargin(new Insets(1, 5, 1, 5));
        checkOptionsButton.setIcon(new ImageIcon(CustomSettingsDialog.class
            .getResource("/com/famfamfam/silk/brick.png")));
        GridBagConstraints gbc_checkOptionsButton = new GridBagConstraints();
        gbc_checkOptionsButton.insets = new Insets(0, 0, 0, 5);
        gbc_checkOptionsButton.gridx = 2;
        gbc_checkOptionsButton.gridy = 0;
        listActionButtonPanel.add(checkOptionsButton, gbc_checkOptionsButton);
      }
      {
        JButton exportButton = new JButton(Localizer.localize("ExportSettingsButtonText"));
        GridBagConstraints gbc_exportButton = new GridBagConstraints();
        gbc_exportButton.insets = new Insets(0, 0, 0, 5);
        gbc_exportButton.gridx = 3;
        gbc_exportButton.gridy = 0;
        listActionButtonPanel.add(exportButton, gbc_exportButton);
        exportButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            exportActionPerformed(e);
          }
        });
        exportButton.setIcon(new ImageIcon(CustomSettingsDialog.class
            .getResource("/com/famfamfam/silk/table_save.png")));
        exportButton.setFont(UIManager.getFont("Button.font"));
        exportButton.setMargin(new Insets(1, 5, 1, 5));
      }
      {
        JButton importSettingsButton = new JButton(Localizer.localize("ImportSettingsButtonText"));
        GridBagConstraints gbc_importSettingsButton = new GridBagConstraints();
        gbc_importSettingsButton.insets = new Insets(0, 0, 0, 5);
        gbc_importSettingsButton.gridx = 4;
        gbc_importSettingsButton.gridy = 0;
        listActionButtonPanel.add(importSettingsButton, gbc_importSettingsButton);
        importSettingsButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            importActionPerformed(e);
          }
        });
        importSettingsButton.setIcon(new ImageIcon(CustomSettingsDialog.class
            .getResource("/com/famfamfam/silk/table_go.png")));
        importSettingsButton.setFont(UIManager.getFont("Button.font"));
        importSettingsButton.setMargin(new Insets(1, 5, 1, 5));
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setBorder(new EmptyBorder(0, 12, 12, 12));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      GridBagLayout gbl_buttonPane = new GridBagLayout();
      gbl_buttonPane.columnWidths = new int[] { 355, 54, 81, 0 };
      gbl_buttonPane.rowHeights = new int[] { 25, 0 };
      gbl_buttonPane.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
      gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
      buttonPane.setLayout(gbl_buttonPane);
      {
        JButton okButton = new JButton(Localizer.localize("OK"));
        okButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            okActionPerformed(e);
          }
        });
        okButton.setIcon(new ImageIcon(CustomSettingsDialog.class
            .getResource("/com/famfamfam/silk/tick.png")));
        okButton.setFont(UIManager.getFont("Button.font"));
        okButton.setMargin(new Insets(1, 5, 1, 5));
        GridBagConstraints gbc_okButton = new GridBagConstraints();
        gbc_okButton.anchor = GridBagConstraints.NORTHWEST;
        gbc_okButton.insets = new Insets(0, 0, 0, 5);
        gbc_okButton.gridx = 1;
        gbc_okButton.gridy = 0;
        buttonPane.add(okButton, gbc_okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton(Localizer.localize("Cancel"));
        cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            cancelActionPerformed(e);
          }
        });
        cancelButton.setIcon(new ImageIcon(CustomSettingsDialog.class
            .getResource("/com/famfamfam/silk/cross.png")));
        cancelButton.setFont(UIManager.getFont("Button.font"));
        cancelButton.setMargin(new Insets(1, 5, 1, 5));
        GridBagConstraints gbc_cancelButton = new GridBagConstraints();
        gbc_cancelButton.anchor = GridBagConstraints.NORTHWEST;
        gbc_cancelButton.gridx = 2;
        gbc_cancelButton.gridy = 0;
        buttonPane.add(cancelButton, gbc_cancelButton);
      }
    }
  }

  public final void initialize() {
    updateTableModel();
  }

  private void okActionPerformed(ActionEvent e) {
    dialogResult = JOptionPane.OK_OPTION;
    dispose();
  }

  public final void setScanner(Scanner scanner) {
    this.scanner = PreferencesUtils.copy(scanner);
  }

  public final void setScanService(IScanService scanService) {
    this.scanService = scanService;
  }

  public final void setXstream(XStream xstream) {
    this.xstream = xstream;
  }

  private void showCheckOptionsErrorMessage(Exception e) {
    Misc.showErrorMsg(getRootPane().getTopLevelAncestor(), String.format(
        Localizer.localize("CheckOptionsFailureMessageText"), e.getLocalizedMessage()));
  }

  private void showLoadErrorMessage(Exception e) {
    Misc.showErrorMsg(getRootPane().getTopLevelAncestor(),
        String.format(Localizer.localize("LoadFailureMessageText"), e.getLocalizedMessage()));
  }

  private void showLoadSuccessMessage() {
    Misc.showSuccessMsg(getRootPane().getTopLevelAncestor(),
        Localizer.localize("LoadSuccessMessageText"));
  }

  private void showSaveErrorMessage(Exception e) {
    Misc.showErrorMsg(getRootPane().getTopLevelAncestor(),
        String.format(Localizer.localize("FailureSavingMessage"), e.getLocalizedMessage()));
  }

  private void showSaveSuccessMessage() {
    Misc.showSuccessMsg(getRootPane().getTopLevelAncestor(),
        Localizer.localize("SaveSuccessMessage"));
  }

  private void updateOption(OptionsOrderValuePair vp, Component component) {

    switch (vp.getSaneOptionType()) {

    case STRING:
      StringOption stringOption = scanner.getStringOptions().get(vp.getKey());
      if (component instanceof JTextField) {
        stringOption.setValue(((JTextField) component).getText());
      } else if (component instanceof JComboBox) {
        stringOption.setValue((String) ((JComboBox<?>) component).getSelectedItem());
      }
      break;

    case INTEGER:
      IntegerOption integerOptions = scanner.getIntegerOptions().get(vp.getKey());
      integerOptions.setValue((Integer) ((JSpinner) component).getValue());
      break;

    case BOOLEAN:
      BooleanOption booleanOptions = scanner.getBooleanOptions().get(vp.getKey());
      booleanOptions.setValue(((JCheckBox) component).isSelected());
      break;

    case FIXED:
      FixedOption fixedOptions = scanner.getFixedOptions().get(vp.getKey());
      fixedOptions.setValue((Double) ((JSpinner) component).getValue());
      break;

    case BUTTON:
      break;

    case GROUP:
      break;

    default:
      break;

    }

  }

  private void updateTableModel() {
    optionOrdering = scanner.getOptionOrdering();
    int rowCount = optionNameTableModel.getRowCount();
    if (rowCount > 0) {
      for (int i = rowCount - 1; i >= 0; i--) {
        optionNameTableModel.removeRow(i);
      }
    }
    for (OptionsOrderValuePair vp : optionOrdering) {
      optionNameTableModel.addRow(new Object[] {
          vp.getKey(),
          vp.isActive() ? Localizer.localize("OptionActiveStatusText") : Localizer
              .localize("OptionInactiveStatusText"), getOptionComponent(vp) });
    }
    optionNameTable.setDefaultRenderer(JCheckBox.class, new CustomTableCellRenderer());
    optionNameTable.setDefaultRenderer(JTextField.class, new CustomTableCellRenderer());
    optionNameTable.setDefaultRenderer(JSpinner.class, new CustomTableCellRenderer());
    optionNameTable.setDefaultRenderer(JComboBox.class, new CustomTableCellRenderer());
    optionNameTable.setDefaultRenderer(JButton.class, new CustomTableCellRenderer());
    optionNameTable.setModel(optionNameTableModel);
    optionNameTable.addMouseListener(new JTableButtonMouseListener(optionNameTable));
    optionNameTable.setCellSelectionEnabled(false);
    optionNameTable.setRowHeight(ROW_HEIGHT);
    optionNameTable.getTableHeader().setReorderingAllowed(false);
    optionNameTable.revalidate();
  }

}
