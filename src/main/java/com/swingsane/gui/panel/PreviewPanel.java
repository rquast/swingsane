package com.swingsane.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.imgscalr.Scalr.Rotation;

import com.google.common.io.Files;
import com.swingsane.business.image.transform.CropTransform;
import com.swingsane.business.image.transform.IImageTransform;
import com.swingsane.business.image.transform.RotateTransform;
import com.swingsane.business.notification.DialogNotificationImpl;
import com.swingsane.business.notification.INotification;
import com.swingsane.business.scanning.ScanEvent;
import com.swingsane.gui.component.ImageLabel;
import com.swingsane.gui.dialog.ImageTransformDialog;
import com.swingsane.i18n.Localizer;
import com.swingsane.preferences.IPreferredDefaults;
import com.swingsane.preferences.ISwingSanePreferences;
import com.swingsane.util.FilenameExtensionFilter;
import com.swingsane.util.Misc;
import com.swingsane.util.RandomGUID;

@SuppressWarnings("serial")
public class PreviewPanel extends JPanel implements MouseMotionListener, MouseListener,
MouseWheelListener {

  private class TempFileListItem {

    private File tempFile;
    private String batchPrefix;
    private int pageNumber;
    private int pagesToScan;
    private String guid = (new RandomGUID()).toString();

    public TempFileListItem(ScanEvent scanEvent) throws IOException {
      tempFile = createTempImageFile(scanEvent.getBufferedImage());
      pagesToScan = scanEvent.getPagesToScan();
      pageNumber = scanEvent.getPageNumber();
      batchPrefix = scanEvent.getBatchPrefix();
    }

    private File createTempImageFile(BufferedImage bufferedImage) throws IOException {
      File tempDir = preferences.getTempDirectory();
      File temporaryFile = File.createTempFile(guid, "", tempDir);
      temporaryFile.deleteOnExit();
      ImageIO.write(bufferedImage, "PNG", temporaryFile);
      return temporaryFile;
    }

    @Override
    public String toString() {
      return parseBatchPrefix(batchPrefix, pageNumber + "", pagesToScan);
    }

  }

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(PreviewPanel.class);

  private BufferedImage sourceImage;

  private BufferedImage convertedImage;
  private Float realZoom = 1.0f;

  private Float lastZoom = 1.0f;
  private JSplitPane dialogPane;
  private JPanel contentPanel;
  private JScrollPane imagePreviewScrollPane;
  private ImageLabel imagePreviewLabel;
  private JPanel masterPanel;
  private JPanel zoomPanel;
  private JComboBox<String> zoomBox;
  private JLabel zoomInLabel;

  private JLabel zoomOutLabel;

  private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

  private final Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
  private final Point pp = new Point();
  private JPanel tempFileListPanel;
  private JList<TempFileListItem> tempFileList;

  private DefaultListModel<TempFileListItem> tempFileListModel = new DefaultListModel<TempFileListItem>();
  private JPanel scannedImagesActionsPanel;
  private JButton saveScanButton;
  private JPanel imagePreviewPanel;
  private JScrollPane tempFileListScrollPane;
  private JComboBox<String> exportTypeComboBox;
  private JPanel scannedImagesButtonPanel;
  private JButton removeScanButton;

  private JButton transformButton;
  private ISwingSanePreferences preferences;
  private JLabel rotateLeftLabel;
  private JLabel rotateRightLabel;
  private JPanel imageTransformPanel;

  private JLabel cropLabel;

  private IPreferredDefaults preferredDefaults;

  public PreviewPanel() {
    initComponents();
  }

  public final void addImage(ScanEvent scanEvent) throws IOException {
    TempFileListItem tempFileListItem = new TempFileListItem(scanEvent);
    tempFileListModel.addElement(tempFileListItem);
  }

  private void cropActionPerformed() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        int[] indicies = tempFileList.getSelectedIndices();
        if (indicies.length <= 0) {
          String message = Localizer.localize("RequireImagesToCropSelectionMessage");
          JOptionPane.showMessageDialog(getRootPane().getTopLevelAncestor(), message);
          return;
        }
        CropTransform cropTransform = new CropTransform();
        cropTransform.setSourceImageFile(tempFileList.getSelectedValue().tempFile);
        cropTransform.setOutputImageFile(tempFileList.getSelectedValue().tempFile);
        try {
          cropTransform.configure(preferredDefaults);
          cropTransform.transform();
          tempFileListSelectionChanged(null);
        } catch (Exception ex) {
          LOG.error(ex, ex);
          showCropErrorMessage(ex);
        }
      }
    });
  }

  private File getSaveDirectory() {
    JFileChooser fd = new JFileChooser();
    fd.setCurrentDirectory(new File("."));
    fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fd.setDialogTitle(Localizer.localize("SaveImagesToDirectoryTittle"));
    fd.setAcceptAllFileFilterUsed(false);
    if (fd.showOpenDialog(getRootPane().getTopLevelAncestor()) == JFileChooser.APPROVE_OPTION) {
      File directory = fd.getCurrentDirectory();
      File selectedFile = fd.getSelectedFile();
      if ((selectedFile != null) && selectedFile.isDirectory()) {
        return selectedFile;
      } else if (directory != null) {
        return directory;
      }
    }
    return null;
  }

  private File getSaveFile(String filename, String extension) {
    FileDialog fd = new FileDialog((Frame) getRootPane().getTopLevelAncestor(),
        Localizer.localize("SaveImagesToFileTittle"), FileDialog.SAVE);
    fd.setDirectory(".");
    FilenameExtensionFilter filter = new FilenameExtensionFilter();
    filter.addExtension(extension);
    fd.setFilenameFilter(filter);
    fd.setFile(filename + "." + extension);
    fd.setModal(true);
    fd.setVisible(true);
    return new File(fd.getDirectory() + File.separator + fd.getFile());
  }

  private Point getViewportMidpoint(JViewport viewport) {
    Point viewPosition = viewport.getViewPosition();
    Rectangle viewportRectangle = new Rectangle(viewPosition, viewport.getSize());
    Point viewportMidpoint = new Point((int) viewportRectangle.getCenterX(),
        (int) viewportRectangle.getCenterY());
    return viewportMidpoint;
  }

  private void initComponents() {

    setBorder(new EmptyBorder(5, 5, 5, 5));
    setOpaque(false);

    contentPanel = new JPanel();
    contentPanel.setOpaque(false);
    imagePreviewScrollPane = new JScrollPane();
    imagePreviewScrollPane.setOpaque(false);
    imagePreviewScrollPane.setWheelScrollingEnabled(false);

    imagePreviewLabel = new ImageLabel();
    imagePreviewLabel.setFont(UIManager.getFont("Label.font"));

    setLayout(new BorderLayout());

    contentPanel.setBorder(null);
    contentPanel.setLayout(new GridBagLayout());
    ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] { 0, 0 };
    ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] { 0, 0, 0, 0 };
    ((GridBagLayout) contentPanel.getLayout()).columnWeights = new double[] { 1.0, 1.0E-4 };
    ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] { 1.0, 0.0, 0.0, 1.0E-4 };
    imagePreviewScrollPane.setViewportBorder(null);
    imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
    imagePreviewLabel.setViewer(this);
    imagePreviewScrollPane.setViewportView(imagePreviewLabel);
    imagePreviewPanel = new JPanel();
    imagePreviewPanel.setOpaque(false);

    imagePreviewPanel.setBorder(new CompoundBorder(new TitledBorder(Localizer
        .localize("ImagePreviewBorderTitle")), new EmptyBorder(5, 5, 5, 5)));
    GridBagConstraints gbc_imagePreviewPanel = new GridBagConstraints();
    gbc_imagePreviewPanel.insets = new Insets(0, 0, 5, 0);
    gbc_imagePreviewPanel.fill = GridBagConstraints.BOTH;
    gbc_imagePreviewPanel.gridx = 0;
    gbc_imagePreviewPanel.gridy = 0;
    contentPanel.add(imagePreviewPanel, gbc_imagePreviewPanel);
    GridBagLayout gbl_imagePreviewPanel = new GridBagLayout();
    gbl_imagePreviewPanel.columnWidths = new int[] { 0 };
    gbl_imagePreviewPanel.rowHeights = new int[] { 0 };
    gbl_imagePreviewPanel.columnWeights = new double[] { Double.MIN_VALUE };
    gbl_imagePreviewPanel.rowWeights = new double[] { Double.MIN_VALUE };
    imagePreviewPanel.setLayout(gbl_imagePreviewPanel);
    imagePreviewPanel.add(imagePreviewScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

    tempFileListPanel = new JPanel();
    GridBagConstraints gbc_tempFileListPanel = new GridBagConstraints();
    gbc_tempFileListPanel.fill = GridBagConstraints.VERTICAL;
    gbc_tempFileListPanel.gridx = 1;
    gbc_tempFileListPanel.gridy = 0;

    tempFileListPanel.setBorder(new CompoundBorder(new TitledBorder(Localizer
        .localize("ScannedImagesBorderTitle")), new EmptyBorder(5, 5, 5, 5)));
    tempFileListPanel.setOpaque(false);
    GridBagLayout gbl_tempFileListPanel = new GridBagLayout();
    gbl_tempFileListPanel.columnWidths = new int[] { 0, 0 };
    gbl_tempFileListPanel.rowHeights = new int[] { 0, 100, 0, 0 };
    gbl_tempFileListPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_tempFileListPanel.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
    tempFileListPanel.setLayout(gbl_tempFileListPanel);

    scannedImagesButtonPanel = new JPanel();
    GridBagConstraints gbc_scannedImagesButtonPanel = new GridBagConstraints();
    gbc_scannedImagesButtonPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_scannedImagesButtonPanel.insets = new Insets(0, 0, 5, 0);
    gbc_scannedImagesButtonPanel.gridx = 0;
    gbc_scannedImagesButtonPanel.gridy = 0;
    tempFileListPanel.add(scannedImagesButtonPanel, gbc_scannedImagesButtonPanel);
    scannedImagesButtonPanel.setOpaque(false);
    GridBagLayout gbl_scannedImagesButtonPanel = new GridBagLayout();
    gbl_scannedImagesButtonPanel.columnWidths = new int[] { 164, 0, 109, 0 };
    gbl_scannedImagesButtonPanel.rowHeights = new int[] { 26, 0 };
    gbl_scannedImagesButtonPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
    gbl_scannedImagesButtonPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    scannedImagesButtonPanel.setLayout(gbl_scannedImagesButtonPanel);

    removeScanButton = new JButton(Localizer.localize("Remove"));
    removeScanButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        removeActionPerformed(e);
      }
    });

    transformButton = new JButton(Localizer.localize("TransformButtonText"));
    transformButton.setEnabled(true);
    transformButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        transformActionPerformed(e);
      }
    });
    transformButton.setIcon(new ImageIcon(PreviewPanel.class
        .getResource("/com/famfamfam/silk/wand.png")));
    transformButton.setFont(UIManager.getFont("Button.font"));
    transformButton.setMargin(new Insets(1, 5, 1, 5));
    GridBagConstraints gbc_transformButton = new GridBagConstraints();
    gbc_transformButton.anchor = GridBagConstraints.WEST;
    gbc_transformButton.insets = new Insets(0, 0, 0, 5);
    gbc_transformButton.gridx = 0;
    gbc_transformButton.gridy = 0;
    scannedImagesButtonPanel.add(transformButton, gbc_transformButton);
    removeScanButton.setIcon(new ImageIcon(PreviewPanel.class
        .getResource("/com/famfamfam/silk/delete.png")));
    removeScanButton.setFont(UIManager.getFont("Button.font"));
    removeScanButton.setMargin(new Insets(1, 5, 1, 5));
    GridBagConstraints gbc_removeScanButton = new GridBagConstraints();
    gbc_removeScanButton.anchor = GridBagConstraints.NORTHEAST;
    gbc_removeScanButton.gridx = 2;
    gbc_removeScanButton.gridy = 0;
    scannedImagesButtonPanel.add(removeScanButton, gbc_removeScanButton);

    tempFileListScrollPane = new JScrollPane();
    tempFileListScrollPane.setMinimumSize(new Dimension(150, 22));
    GridBagConstraints gbc_tempFileListScrollPane = new GridBagConstraints();
    gbc_tempFileListScrollPane.insets = new Insets(0, 0, 5, 0);
    gbc_tempFileListScrollPane.fill = GridBagConstraints.BOTH;
    gbc_tempFileListScrollPane.gridx = 0;
    gbc_tempFileListScrollPane.gridy = 1;
    tempFileListPanel.add(tempFileListScrollPane, gbc_tempFileListScrollPane);

    tempFileList = new JList<TempFileListItem>();
    tempFileList.setFont(UIManager.getFont("List.font"));
    tempFileList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        tempFileListSelectionChanged(e);
      }
    });
    tempFileListScrollPane.setViewportView(tempFileList);

    dialogPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contentPanel, tempFileListPanel);
    masterPanel = new JPanel();
    masterPanel.setBorder(new EmptyBorder(0, 12, 0, 12));
    zoomPanel = new JPanel();
    zoomBox = new JComboBox<String>();
    zoomInLabel = new JLabel();
    zoomOutLabel = new JLabel();
    masterPanel.setOpaque(false);
    masterPanel.setLayout(new GridBagLayout());
    ((GridBagLayout) masterPanel.getLayout()).columnWidths = new int[] { 0, 0, 0, 0 };
    ((GridBagLayout) masterPanel.getLayout()).rowHeights = new int[] { 0, 0 };
    ((GridBagLayout) masterPanel.getLayout()).columnWeights = new double[] { 1.0, 0.0, 1.0, 1.0E-4 };
    ((GridBagLayout) masterPanel.getLayout()).rowWeights = new double[] { 0.0, 1.0E-4 };
    zoomPanel.setOpaque(false);
    zoomPanel.setLayout(new GridBagLayout());
    ((GridBagLayout) zoomPanel.getLayout()).columnWidths = new int[] { 0, 0, 0, 0, 0, 10, 0 };
    ((GridBagLayout) zoomPanel.getLayout()).rowHeights = new int[] { 0, 0 };
    ((GridBagLayout) zoomPanel.getLayout()).columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0,
        1.0, 1.0E-4 };
    ((GridBagLayout) zoomPanel.getLayout()).rowWeights = new double[] { 0.0, 1.0E-4 };
    zoomBox.setModel(new DefaultComboBoxModel<String>(new String[] { "5%", "10%", "25%", "50%",
        "100%", "200%", "500%", "1000%" }));
    zoomBox.setSelectedIndex(4);
    zoomBox.setFont(UIManager.getFont("ComboBox.font"));
    zoomBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        zoomBoxItemStateChanged(e);
      }
    });
    zoomPanel.add(zoomBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
    zoomInLabel.setIcon(new ImageIcon(PreviewPanel.class
        .getResource("/com/famfamfam/silk/zoom_in.png")));
    zoomInLabel.setFont(UIManager.getFont("Label.font"));
    zoomInLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        zoomInLabelMouseClicked(e);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        zoomInLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        zoomInLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    });
    zoomPanel.add(zoomInLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
    zoomOutLabel.setIcon(new ImageIcon(PreviewPanel.class
        .getResource("/com/famfamfam/silk/zoom_out.png")));
    zoomOutLabel.setFont(UIManager.getFont("Label.font"));
    zoomOutLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        zoomOutLabelMouseClicked(e);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        zoomOutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        zoomOutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    });
    zoomPanel.add(zoomOutLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
    masterPanel.add(zoomPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
    contentPanel.add(masterPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

    imageTransformPanel = new JPanel();
    imageTransformPanel.setOpaque(false);
    GridBagConstraints gbc_imageTransformPanel = new GridBagConstraints();
    gbc_imageTransformPanel.fill = GridBagConstraints.BOTH;
    gbc_imageTransformPanel.gridx = 0;
    gbc_imageTransformPanel.gridy = 2;
    contentPanel.add(imageTransformPanel, gbc_imageTransformPanel);
    GridBagLayout gbl_imageTransformPanel = new GridBagLayout();
    gbl_imageTransformPanel.columnWidths = new int[] { 0, 0, 100, 0, 0 };
    gbl_imageTransformPanel.rowHeights = new int[] { 16, 0 };
    gbl_imageTransformPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_imageTransformPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    imageTransformPanel.setLayout(gbl_imageTransformPanel);

    cropLabel = new JLabel(Localizer.localize("CropImageLabelText"));
    cropLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.isConsumed()) {
          return;
        }
        cropActionPerformed();
        e.consume();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        cropLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        cropLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    });
    cropLabel.setIcon(new ImageIcon(PreviewPanel.class
        .getResource("/com/famfamfam/silk/shape_handles.png")));
    cropLabel.setFont(UIManager.getFont("Label.font"));
    GridBagConstraints gbc_cropLabel = new GridBagConstraints();
    gbc_cropLabel.insets = new Insets(5, 5, 5, 5);
    gbc_cropLabel.gridx = 0;
    gbc_cropLabel.gridy = 0;
    imageTransformPanel.add(cropLabel, gbc_cropLabel);

    rotateLeftLabel = new JLabel(Localizer.localize("RotateLeftLabelText"));
    GridBagConstraints gbc_rotateLeftLabel = new GridBagConstraints();
    gbc_rotateLeftLabel.insets = new Insets(5, 5, 5, 5);
    gbc_rotateLeftLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_rotateLeftLabel.gridx = 2;
    gbc_rotateLeftLabel.gridy = 0;
    imageTransformPanel.add(rotateLeftLabel, gbc_rotateLeftLabel);
    rotateLeftLabel.setFont(UIManager.getFont("Label.font"));
    rotateLeftLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.isConsumed()) {
          return;
        }
        rotateLeftActionPerformed();
        e.consume();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        rotateLeftLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        rotateLeftLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    });
    rotateLeftLabel.setIcon(new ImageIcon(PreviewPanel.class
        .getResource("/com/famfamfam/silk/arrow_rotate_anticlockwise.png")));

    rotateRightLabel = new JLabel(Localizer.localize("RotateRightLabelText"));
    GridBagConstraints gbc_rotateRightLabel = new GridBagConstraints();
    gbc_rotateRightLabel.insets = new Insets(5, 5, 5, 5);
    gbc_rotateRightLabel.gridx = 3;
    gbc_rotateRightLabel.gridy = 0;
    imageTransformPanel.add(rotateRightLabel, gbc_rotateRightLabel);
    rotateRightLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.isConsumed()) {
          return;
        }
        rotateRightActionPerformed();
        e.consume();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        rotateRightLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        rotateRightLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    });
    rotateRightLabel.setFont(UIManager.getFont("Label.font"));
    rotateRightLabel.setIcon(new ImageIcon(PreviewPanel.class
        .getResource("/com/famfamfam/silk/arrow_rotate_clockwise.png")));
    dialogPane.setResizeWeight(0.9);
    add(dialogPane, BorderLayout.CENTER);

    scannedImagesActionsPanel = new JPanel();
    scannedImagesActionsPanel.setOpaque(false);
    GridBagConstraints gbc_scannedImagesActionsPanel = new GridBagConstraints();
    gbc_scannedImagesActionsPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_scannedImagesActionsPanel.gridx = 0;
    gbc_scannedImagesActionsPanel.gridy = 2;
    tempFileListPanel.add(scannedImagesActionsPanel, gbc_scannedImagesActionsPanel);
    GridBagLayout gbl_scannedImagesActionsPanel = new GridBagLayout();
    gbl_scannedImagesActionsPanel.columnWidths = new int[] { 0, 0 };
    gbl_scannedImagesActionsPanel.rowHeights = new int[] { 0, 0, 0, 0 };
    gbl_scannedImagesActionsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_scannedImagesActionsPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
    scannedImagesActionsPanel.setLayout(gbl_scannedImagesActionsPanel);

    JPanel exportTypePanel = new JPanel();
    exportTypePanel.setOpaque(false);
    GridBagConstraints gbc_exportTypePanel = new GridBagConstraints();
    gbc_exportTypePanel.insets = new Insets(0, 0, 5, 0);
    gbc_exportTypePanel.fill = GridBagConstraints.BOTH;
    gbc_exportTypePanel.gridx = 0;
    gbc_exportTypePanel.gridy = 1;
    scannedImagesActionsPanel.add(exportTypePanel, gbc_exportTypePanel);
    GridBagLayout gbl_exportTypePanel = new GridBagLayout();
    gbl_exportTypePanel.columnWidths = new int[] { 155, 0, 0 };
    gbl_exportTypePanel.rowHeights = new int[] { 24, 0 };
    gbl_exportTypePanel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    gbl_exportTypePanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    exportTypePanel.setLayout(gbl_exportTypePanel);

    exportTypeComboBox = new JComboBox<String>();
    exportTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
    exportTypeComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {
        Localizer.localize("JPGSinglePageText"),
        Localizer.localize("PNGSinglePageText"),
        Localizer.localize("PDFSinglePageText"),
        Localizer.localize("PDFMultiPageText") }));
    exportTypeComboBox.setSelectedIndex(0);
    GridBagConstraints gbc_exportTypeComboBox = new GridBagConstraints();
    gbc_exportTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_exportTypeComboBox.insets = new Insets(0, 0, 0, 5);
    gbc_exportTypeComboBox.gridx = 0;
    gbc_exportTypeComboBox.gridy = 0;
    exportTypePanel.add(exportTypeComboBox, gbc_exportTypeComboBox);

    saveScanButton = new JButton(Localizer.localize("SaveButtonText"));
    GridBagConstraints gbc_saveScanButton = new GridBagConstraints();
    gbc_saveScanButton.gridx = 1;
    gbc_saveScanButton.gridy = 0;
    exportTypePanel.add(saveScanButton, gbc_saveScanButton);
    saveScanButton.setFont(UIManager.getFont("Button.font"));
    saveScanButton.setMargin(new Insets(1, 5, 1, 5));
    saveScanButton.setIcon(new ImageIcon(PreviewPanel.class
        .getResource("/com/famfamfam/silk/disk.png")));
    saveScanButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveScanActionPerformed(e);
      }
    });
  }

  public final void initialize() {
    tempFileList.setModel(tempFileListModel);
    imagePreviewScrollPane.addMouseMotionListener(this);
    imagePreviewScrollPane.addMouseListener(this);
    imagePreviewScrollPane.addMouseWheelListener(this);
    loadDebugImages();
  }

  private void loadDebugImages() {
    File userHomeDir = new File(System.getProperty("user.home"));
    if (userHomeDir.exists() && userHomeDir.canRead() && userHomeDir.isDirectory()) {
      File[] files = userHomeDir.listFiles();
      for (File file : files) {
        if (file.exists() && file.isFile() && file.canRead()) {
          if (file.getName().toLowerCase().startsWith("swingsane_debug_image")) {
            try {
              ScanEvent scanEvent = new ScanEvent(file);
              scanEvent.setBatchPrefix(file.getName());
              scanEvent.setPageNumber(1);
              scanEvent.setPagesToScan(1);
              scanEvent.setAcquiredImage(ImageIO.read(file));
              addImage(scanEvent);
            } catch (Exception ex) {
              LOG.debug(ex, ex);
            }
          }
        }
      }
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public final void mouseDragged(MouseEvent e) {
    JViewport vport = ((JScrollPane) e.getSource()).getViewport();
    Point cp = e.getPoint();
    Point vp = vport.getViewPosition();
    vp.translate(pp.x - cp.x, pp.y - cp.y);
    imagePreviewLabel.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
    pp.setLocation(cp);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }

  @Override
  public final void mousePressed(MouseEvent e) {
    imagePreviewLabel.setCursor(moveCursor);
    pp.setLocation(e.getPoint());
  }

  @Override
  public final void mouseReleased(MouseEvent e) {
    imagePreviewLabel.setCursor(defCursor);
    imagePreviewLabel.repaint();
  }

  @Override
  public final void mouseWheelMoved(MouseWheelEvent e) {

    Point clickPoint = e.getPoint();

    JViewport viewport = ((JScrollPane) e.getSource()).getViewport();
    Point viewPosition = viewport.getViewPosition();

    // 1. Get viewport rectangle.
    Rectangle viewportRectangle = new Rectangle(viewPosition, viewport.getSize());

    // 2. Get the midpoint of the viewport.
    Point viewportMidpoint = getViewportMidpoint(viewport);

    // 3. calculate the distance from the click point to the midpoint
    int deltaX = (viewPosition.x + clickPoint.x) - viewportMidpoint.x;
    int deltaY = (viewPosition.y + clickPoint.y) - viewportMidpoint.y;

    // 4. translate the viewport rectangle
    viewportRectangle.translate(deltaX, deltaY);

    // 5. scroll the viewport to the new viewport rectangle position
    imagePreviewLabel.scrollRectToVisible(viewportRectangle);

    int notches = e.getWheelRotation();
    if (notches < 0) {
      zoomIn();
    } else {
      zoomOut();
    }

  }

  private String parseBatchPrefix(String batchPrefix, String pageNumber, int pagesToScan) {
    String str = batchPrefix;
    str = str.replace(Localizer.localize("PageNumberToken"), pageNumber);
    str = str.replace(Localizer.localize("PageCountToken"), pagesToScan + "");
    return str;
  }

  private File parseOutputFileName(File file, String pageNumber, int pagesToScan) {
    return parseOutputFileName(file.getAbsolutePath(), pageNumber, pagesToScan);
  }

  private File parseOutputFileName(String prefix, String pageNumber, int pagesToScan) {
    File file = new File(parseBatchPrefix(prefix, pageNumber, pagesToScan));
    return file;
  }

  private void removeActionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        int[] indicies = tempFileList.getSelectedIndices();
        if (indicies.length <= 0) {
          String message = Localizer.localize("RequireImagesToRemoveSelectionMessage");
          JOptionPane.showMessageDialog(getRootPane().getTopLevelAncestor(), message);
          return;
        }
        ArrayList<TempFileListItem> itemsToRemove = new ArrayList<TempFileListItem>();
        for (int index : indicies) {
          itemsToRemove.add(tempFileListModel.get(index));
        }
        for (TempFileListItem tempFileListItem : itemsToRemove) {
          tempFileListItem.tempFile.delete();
          tempFileListModel.removeElement(tempFileListItem);
        }
        tempFileList.revalidate();
        tempFileListSelectionChanged(null);
      }
    });
  }

  public final void renderImagePreview(Graphics2D g) {

    if (sourceImage != null) {

      if (convertedImage == null) {

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        if (sourceImage.getColorModel() != gc.getColorModel()) {
          convertedImage = gc.createCompatibleImage(sourceImage.getWidth(),
              sourceImage.getHeight(), Transparency.OPAQUE);
          Graphics2D g2d = convertedImage.createGraphics();
          g2d.drawImage(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
          convertedImage.flush();
          g2d.dispose();
        } else {
          convertedImage = sourceImage;
        }

      }

      g.setColor(Color.darkGray);
      g.fill(new Rectangle2D.Double(0, 0, imagePreviewLabel.getWidth(), imagePreviewLabel
          .getHeight()));

      g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
          RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

      double x = (imagePreviewLabel.getWidth() / 2) - ((sourceImage.getWidth() * realZoom) / 2);
      double y = (imagePreviewLabel.getHeight() / 2) - ((sourceImage.getHeight() * realZoom) / 2);
      AffineTransform at = AffineTransform.getTranslateInstance(x, y);
      at.scale(realZoom, realZoom);
      g.drawRenderedImage(convertedImage, at);

    }

  }

  private void resizeImageLabel() {

    JViewport viewport = imagePreviewScrollPane.getViewport();
    Dimension viewportSize = viewport.getSize();
    Point viewportMidpoint = getViewportMidpoint(viewport);

    viewportMidpoint.x /= lastZoom;
    viewportMidpoint.y /= lastZoom;
    viewportMidpoint.x *= realZoom;
    viewportMidpoint.y *= realZoom;
    viewportMidpoint.x -= (viewportSize.width / 2);
    viewportMidpoint.y -= (viewportSize.height / 2);

    // create a new scaled rectangle around the midpoint
    Rectangle scrollToRectangle = new Rectangle(viewportMidpoint, viewportSize);

    Dimension dim = null;
    if (sourceImage != null) {
      dim = new Dimension((int) (sourceImage.getWidth() * realZoom),
          (int) (sourceImage.getHeight() * realZoom));
    } else {
      dim = new Dimension((int) (10 * realZoom), (int) (10 * realZoom));
    }
    if (imagePreviewLabel != null) {
      imagePreviewLabel.setMaximumSize(dim);
      imagePreviewLabel.setMinimumSize(dim);
      imagePreviewLabel.setPreferredSize(dim);
    }
    imagePreviewLabel.revalidate();
    imagePreviewLabel.scrollRectToVisible(scrollToRectangle);

  }

  private void rotateLeftActionPerformed() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        rotateSelection(Rotation.CW_270);
      }
    });
  }

  private void rotateRightActionPerformed() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        rotateSelection(Rotation.CW_90);
      }
    });
  }

  private void rotateSelection(final Rotation rotation) {

    int[] indicies = tempFileList.getSelectedIndices();
    if (indicies.length <= 0) {
      String message = Localizer.localize("RequireImagesToRotateSelectionMessage");
      JOptionPane.showMessageDialog(getRootPane().getTopLevelAncestor(), message);
      return;
    }

    final INotification notification = new DialogNotificationImpl(getRootPane()
        .getTopLevelAncestor());

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {
        notification.message(Localizer.localize("RotatingImageMessageText"));

        int[] indicies = tempFileList.getSelectedIndices();
        if (indicies.length <= 0) {
          throw new Exception(Localizer.localize("RequireImagesToRotateSelectionMessage"));
        }

        for (int index : indicies) {
          TempFileListItem tempFileListItem = tempFileList.getModel().getElementAt(index);
          RotateTransform rotateTransform = new RotateTransform();
          rotateTransform.setRotation(rotation);
          rotateTransform.setSourceImageFile(tempFileListItem.tempFile);
          rotateTransform.setOutputImageFile(tempFileListItem.tempFile);
          rotateTransform.transform();
        }

        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          ((JDialog) notification).setVisible(false);
          tempFileListSelectionChanged(null);
        } catch (Exception ex) {
          LOG.error(ex, ex);
        } finally {
          ((JDialog) notification).dispose();
        }
      }
    };

    worker.execute();

    ((JDialog) notification).setModal(true);
    ((JDialog) notification).setVisible(true);

  }

  private void saveIndividualPDFFile(final ArrayList<TempFileListItem> selectedItems,
      final File outputFile, final String batchPrefix) {

    final INotification notification = new DialogNotificationImpl(getRootPane()
        .getTopLevelAncestor());

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      private PDDocument document = null;

      @Override
      protected Void doInBackground() throws Exception {
        for (TempFileListItem tempFileListItem : selectedItems) {
          File destinationFile = parseOutputFileName(outputFile.getCanonicalFile() + File.separator
              + batchPrefix + ".pdf", tempFileListItem.pageNumber + "",
              tempFileListItem.pagesToScan);
          notification.message(String.format(Localizer.localize("ReadingImageFileMessage"),
              tempFileListItem.tempFile.getName()));
          BufferedImage bufferedImage = ImageIO.read(tempFileListItem.tempFile);
          document = new PDDocument();
          PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(),
              bufferedImage.getHeight()));
          notification.message(Localizer.localize("AddingPageMessage"));
          document.addPage(page);
          PDXObjectImage ximage = null;
          ximage = new PDPixelMap(document, bufferedImage);
          PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
          contentStream.drawXObject(ximage, 0, 0, bufferedImage.getWidth(),
                  bufferedImage.getHeight());
          contentStream.close();
          notification.message(String.format(Localizer.localize("SavingFileMessage"),
                  destinationFile.getCanonicalPath()));
          document.save(destinationFile);
        }
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          showSaveSuccessMessage();
          ((JDialog) notification).setVisible(false);
        } catch (Exception ex) {
          LOG.error(ex, ex);
          showSaveErrorMessage(ex);
        } finally {
          if (document != null) {
            try {
              document.close();
            } catch (IOException e) {
            }
          }
          ((JDialog) notification).dispose();
        }
      }
    };

    worker.execute();

    ((JDialog) notification).setModal(true);
    ((JDialog) notification).setVisible(true);

  }

  private void saveMultiPagePDFFile(final ArrayList<TempFileListItem> selectedItems,
      final File destinationFile) {

    final INotification notification = new DialogNotificationImpl(getRootPane()
        .getTopLevelAncestor());

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      private PDDocument document = null;

      @Override
      protected Void doInBackground() throws Exception {
        document = new PDDocument();
        for (TempFileListItem tempFileListItem : selectedItems) {
          notification.message(String.format(Localizer.localize("ReadingImageFileMessage"),
              tempFileListItem.tempFile.getName()));
          BufferedImage bufferedImage = ImageIO.read(tempFileListItem.tempFile);
          PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(),
              bufferedImage.getHeight()));
          notification.message(Localizer.localize("AddingPageMessage"));
          document.addPage(page);
          PDXObjectImage ximage = null;
          ximage = new PDPixelMap(document, bufferedImage);
          PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
          contentStream.drawXObject(ximage, 0, 0, bufferedImage.getWidth(),
              bufferedImage.getHeight());
          contentStream.close();
        }
        notification.message(String.format(Localizer.localize("SavingFileMessage"),
            destinationFile.getCanonicalPath()));
        document.save(destinationFile);
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          showSaveSuccessMessage();
          ((JDialog) notification).setVisible(false);
        } catch (Exception ex) {
          LOG.error(ex, ex);
          showSaveErrorMessage(ex);
        } finally {
          if (document != null) {
            try {
              document.close();
            } catch (IOException e) {
            }
          }
          ((JDialog) notification).dispose();
        }
      }
    };

    worker.execute();

    ((JDialog) notification).setModal(true);
    ((JDialog) notification).setVisible(true);

  }

  private void savePNGFile(final ArrayList<TempFileListItem> selectedItems, final File outputFile,
                           final String batchPrefix) {

    final INotification notification = new DialogNotificationImpl(getRootPane()
            .getTopLevelAncestor());

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        for (TempFileListItem tempFileListItem : selectedItems) {
          notification.message(String.format(Localizer.localize("CopyingFileMessage"),
                  tempFileListItem.tempFile.getName()));
          final File destinationFile = parseOutputFileName(outputFile.getCanonicalFile()
                          + File.separator + batchPrefix + ".png", tempFileListItem.pageNumber + "",
                  tempFileListItem.pagesToScan);
          Files.copy(tempFileListItem.tempFile, destinationFile);
        }
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          showSaveSuccessMessage();
          ((JDialog) notification).setVisible(false);
        } catch (Exception ex) {
          LOG.error(ex, ex);
          showSaveErrorMessage(ex);
        } finally {
          ((JDialog) notification).dispose();
        }
      }
    };
    worker.execute();

    ((JDialog) notification).setModal(true);
    ((JDialog) notification).setVisible(true);

  }

  private void saveJPGFile(final ArrayList<TempFileListItem> selectedItems, final File outputFile,
                           final String batchPrefix) {

    final INotification notification = new DialogNotificationImpl(getRootPane()
            .getTopLevelAncestor());

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        for (TempFileListItem tempFileListItem : selectedItems) {
          notification.message(String.format(Localizer.localize("CopyingFileMessage"),
                  tempFileListItem.tempFile.getName()));
          final File destinationFile = parseOutputFileName(outputFile.getCanonicalFile()
                          + File.separator + batchPrefix + ".jpg", tempFileListItem.pageNumber + "",
                  tempFileListItem.pagesToScan);

          //convert to JPG
          BufferedImage img = ImageIO.read(tempFileListItem.tempFile);
          BufferedImage jpg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
          jpg.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
          ImageIO.write(jpg, "jpg", destinationFile);
        }
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          showSaveSuccessMessage();
          ((JDialog) notification).setVisible(false);
        } catch (Exception ex) {
          LOG.error(ex, ex);
          showSaveErrorMessage(ex);
        } finally {
          ((JDialog) notification).dispose();
        }
      }
    };
    worker.execute();

    ((JDialog) notification).setModal(true);
    ((JDialog) notification).setVisible(true);

  }

  private void saveScanActionPerformed(ActionEvent e) {

    ArrayList<TempFileListItem> selectedItems = new ArrayList<TempFileListItem>();
    int[] indicies = tempFileList.getSelectedIndices();
    if (indicies.length <= 0) {
      String message = Localizer.localize("RequireImagesToSaveSelectionMessage");
      JOptionPane.showMessageDialog(getRootPane().getTopLevelAncestor(), message);
      return;
    }

    for (int index : indicies) {
      selectedItems.add(tempFileListModel.get(index));
    }

    File outputFile = null;
    int selectedIndex = exportTypeComboBox.getSelectedIndex();

    TempFileListItem firstItem = selectedItems.get(0);
    if (firstItem == null) {
      return;
    }

    switch (selectedIndex) {
      case 0:
        outputFile = getSaveDirectory();
        if (outputFile == null) {
          return;
        }
        saveJPGFile(selectedItems, outputFile, firstItem.batchPrefix);
        break;
    case 1:
      outputFile = getSaveDirectory();
      if (outputFile == null) {
        return;
      }
      savePNGFile(selectedItems, outputFile, firstItem.batchPrefix);
      break;
    case 2:
      outputFile = getSaveDirectory();
      if (outputFile == null) {
        return;
      }
      saveIndividualPDFFile(selectedItems, outputFile, firstItem.batchPrefix);
      break;
    case 3:
    default:
      outputFile = getSaveFile(firstItem.batchPrefix, "pdf");
      if (outputFile == null) {
        return;
      }
      saveMultiPagePDFFile(
          selectedItems,
          parseOutputFileName(outputFile, Localizer.localize("ALLPagesText"), firstItem.pagesToScan));
      break;
    }

  }

  public final void setPreferences(ISwingSanePreferences preferences) {
    this.preferences = preferences;
  }

  public void setPreferredDefaults(IPreferredDefaults preferredDefaultsImpl) {
    preferredDefaults = preferredDefaultsImpl;
  }

  private void setSourceImage(BufferedImage sourceImage) {
    if (sourceImage != null) {
      sourceImage.flush();
    }
    this.sourceImage = sourceImage;
    convertedImage = null;
    resizeImageLabel();
    repaint();
  }

  private void showCropErrorMessage(Exception e) {
    Misc.showErrorMsg(getRootPane().getTopLevelAncestor(),
        String.format(Localizer.localize("FailureCroppingMessage"), e.getLocalizedMessage()));
  }

  private void showSaveErrorMessage(Exception e) {
    Misc.showErrorMsg(getRootPane().getTopLevelAncestor(),
        String.format(Localizer.localize("FailureSavingMessage"), e.getLocalizedMessage()));
  }

  private void showSaveSuccessMessage() {
    Misc.showSuccessMsg(getRootPane().getTopLevelAncestor(),
        Localizer.localize("SaveSuccessMessage"));
  }

  private void showTransformErrorMessage(Exception e) {
    Misc.showErrorMsg(getRootPane().getTopLevelAncestor(),
        String.format(Localizer.localize("FailureTransformingMessage"), e.getLocalizedMessage()));
  }

  private void tempFileListSelectionChanged(ListSelectionEvent e) {
    if ((e != null) && !(e.getValueIsAdjusting())) {
      return;
    }
    final TempFileListItem tempFileListItem = tempFileList.getSelectedValue();
    if (tempFileListItem == null) {
      setSourceImage(null);
      return;
    }
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          BufferedImage bufferedImage = ImageIO.read(tempFileListItem.tempFile);
          setSourceImage(bufferedImage);
        } catch (IOException ex) {
          LOG.error(ex, ex);
        }
      }
    });
  }

  private void transformActionPerformed(ActionEvent e) {

    final int[] indicies = tempFileList.getSelectedIndices();
    if (indicies.length <= 0) {
      String message = Localizer.localize("RequireImagesToTransformSelectionMessage");
      JOptionPane.showMessageDialog(getRootPane().getTopLevelAncestor(), message);
      return;
    }

    ImageTransformDialog imageTransformDialog = new ImageTransformDialog(getRootPane()
        .getTopLevelAncestor());
    imageTransformDialog.setModal(true);
    imageTransformDialog.setVisible(true);
    if (imageTransformDialog.getDialogResult() == JOptionPane.OK_OPTION) {

      final ArrayList<IImageTransform> transforms = imageTransformDialog.getTransforms();
      for (int index : indicies) {
        TempFileListItem tempFileListItem = tempFileList.getModel().getElementAt(index);

        for (final IImageTransform transform : transforms) {
          transform.setSourceImageFile(tempFileListItem.tempFile);
          transform.setOutputImageFile(tempFileListItem.tempFile);
          try {
            transform.configure(preferredDefaults);
          } catch (Exception ex) {
            LOG.error(ex, ex);
            showTransformErrorMessage(ex);
            return;
          }
        }
      }

      final INotification notification = new DialogNotificationImpl(getRootPane()
          .getTopLevelAncestor());

      SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

        @Override
        protected Void doInBackground() throws Exception {
          notification.message(Localizer.localize("TransformImageMessageText"));
          for (int index : indicies) {
            TempFileListItem tempFileListItem = tempFileList.getModel().getElementAt(index);
            for (final IImageTransform transform : transforms) {
              transform.setSourceImageFile(tempFileListItem.tempFile);
              transform.setOutputImageFile(tempFileListItem.tempFile);
              transform.transform();
            }
          }
          return null;
        }

        @Override
        protected void done() {
          try {
            get();
            ((JDialog) notification).setVisible(false);
            tempFileListSelectionChanged(null);
          } catch (Exception ex) {
            LOG.error(ex, ex);
          } finally {
            ((JDialog) notification).dispose();
          }
        }
      };

      worker.execute();

      ((JDialog) notification).setModal(true);
      ((JDialog) notification).setVisible(true);

    }

  }

  private void zoomBoxItemStateChanged(ItemEvent e) {
    String unparsedString = (String) zoomBox.getSelectedObjects()[0];
    String zoomString = "";
    for (int i = 0; i < unparsedString.length(); i++) {
      if ((unparsedString.charAt(i) >= 48) && (unparsedString.charAt(i) <= 57)) {
        zoomString += unparsedString.charAt(i);
      }
    }
    lastZoom = realZoom;
    realZoom = Float.parseFloat(zoomString) / 100.0f;
    resizeImageLabel();
    repaint();
  }

  private void zoomIn() {
    int selectedIndex = zoomBox.getSelectedIndex();

    if (selectedIndex < (zoomBox.getItemCount() - 1)) {
      zoomBox.setSelectedIndex(selectedIndex + 1);
    }
  }

  private void zoomInLabelMouseClicked(MouseEvent e) {
    zoomIn();
  }

  private void zoomOut() {
    int selectedIndex = zoomBox.getSelectedIndex();

    if (selectedIndex > 0) {
      zoomBox.setSelectedIndex(selectedIndex - 1);
    }
  }

  private void zoomOutLabelMouseClicked(MouseEvent e) {
    zoomOut();
  }

}
