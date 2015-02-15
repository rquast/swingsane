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
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.swingsane.business.image.transform.BinarizeTransform;
import com.swingsane.business.image.transform.CropTransform;
import com.swingsane.business.image.transform.DeskewTransform;
import com.swingsane.business.image.transform.IImageTransform;
import com.swingsane.business.image.transform.ImageTransformType;
import com.swingsane.business.image.transform.RotateTransform;
import com.swingsane.gui.panel.ITransformSettingsPanel;
import com.swingsane.i18n.Localizer;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class ImageTransformDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();

  private int dialogResult = JOptionPane.CANCEL_OPTION;

  private static final int BOUNDS_WIDTH = 600;
  private static final int BOUNDS_HEIGHT = 500;

  private final Dimension bounds = new Dimension(BOUNDS_WIDTH, BOUNDS_HEIGHT);

  private JList<IImageTransform> transformTypeList;

  private DefaultListModel<IImageTransform> transformTypeListModel = new DefaultListModel<IImageTransform>();
  private JComboBox<ImageTransformType> transformTypeComboBox;
  private JPanel transformSettingsContainerPanel;

  public ImageTransformDialog(Component parent) {
    initComponents();
    pack();
    setLocationRelativeTo(parent);
  }

  private void addTransformActionPerformed(ActionEvent e) {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        ImageTransformType transform = (ImageTransformType) transformTypeComboBox.getSelectedItem();

        switch (transform) {

        case DESKEW:
          DeskewTransform deskewTransform = new DeskewTransform();
          transformTypeListModel.addElement(deskewTransform);
          break;

        case ROTATE:
          RotateTransform rotateTransform = new RotateTransform();
          transformTypeListModel.addElement(rotateTransform);
          break;

        case BINARIZE:
          BinarizeTransform binarizeTransform = new BinarizeTransform();
          transformTypeListModel.addElement(binarizeTransform);
          break;

        case CROP:
          CropTransform cropTransform = new CropTransform();
          transformTypeListModel.addElement(cropTransform);
          break;

        default:
          break;

        }

        transformTypeList.revalidate();
        transformTypeList.repaint();
      }
    });

  }

  public final int getDialogResult() {
    return dialogResult;
  }

  public final ArrayList<IImageTransform> getTransforms() {
    ArrayList<IImageTransform> transforms = new ArrayList<IImageTransform>();
    for (int i = 0; i < transformTypeListModel.getSize(); i++) {
      transforms.add(transformTypeListModel.getElementAt(i));
    }
    return transforms;
  }

  private ITransformSettingsPanel getTransformSettingsPanel() {
    if (transformTypeList.getSelectedIndex() < 0) {
      return null;
    }
    return transformTypeList.getSelectedValue().getTransformSettingsPanel();
  }

  private void initComponents() {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(Localizer.localize("ImageTransformDialogTitle"));
    setResizable(false);
    setBounds(0, 0, bounds.width, bounds.height);
    setSize(bounds);
    setPreferredSize(bounds);
    setMinimumSize(bounds);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] { 0, 0 };
    gbl_contentPanel.rowHeights = new int[] { 100, 0, 0 };
    gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
    contentPanel.setLayout(gbl_contentPanel);
    {
      JPanel transformTypePanel = new JPanel();
      transformTypePanel.setBorder(new CompoundBorder(new TitledBorder(Localizer
          .localize("TransformationsBorderTitle")), new EmptyBorder(5, 5, 5, 5)));
      GridBagConstraints gbc_transformTypePanel = new GridBagConstraints();
      gbc_transformTypePanel.insets = new Insets(0, 0, 5, 0);
      gbc_transformTypePanel.fill = GridBagConstraints.BOTH;
      gbc_transformTypePanel.gridx = 0;
      gbc_transformTypePanel.gridy = 0;
      contentPanel.add(transformTypePanel, gbc_transformTypePanel);
      GridBagLayout gbl_transformTypePanel = new GridBagLayout();
      gbl_transformTypePanel.columnWidths = new int[] { 0, 0 };
      gbl_transformTypePanel.rowHeights = new int[] { 0, 0, 0 };
      gbl_transformTypePanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
      gbl_transformTypePanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
      transformTypePanel.setLayout(gbl_transformTypePanel);
      {
        JScrollPane transformTypeScrollPane = new JScrollPane();
        GridBagConstraints gbc_transformTypeScrollPane = new GridBagConstraints();
        gbc_transformTypeScrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_transformTypeScrollPane.fill = GridBagConstraints.BOTH;
        gbc_transformTypeScrollPane.gridx = 0;
        gbc_transformTypeScrollPane.gridy = 0;
        transformTypePanel.add(transformTypeScrollPane, gbc_transformTypeScrollPane);
        {
          transformTypeList = new JList<IImageTransform>();
          transformTypeList.setFont(UIManager.getFont("List.font"));
          transformTypeList.setModel(transformTypeListModel);
          transformTypeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
              transformListSelectionChanged(e);
            }
          });
          transformTypeScrollPane.setViewportView(transformTypeList);
        }
      }
      {
        JPanel transformActionsPanel = new JPanel();
        GridBagConstraints gbc_transformActionsPanel = new GridBagConstraints();
        gbc_transformActionsPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_transformActionsPanel.gridx = 0;
        gbc_transformActionsPanel.gridy = 1;
        transformTypePanel.add(transformActionsPanel, gbc_transformActionsPanel);
        GridBagLayout gbl_transformActionsPanel = new GridBagLayout();
        gbl_transformActionsPanel.columnWidths = new int[] { 0, 32, 0, 0 };
        gbl_transformActionsPanel.rowHeights = new int[] { 24, 0 };
        gbl_transformActionsPanel.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_transformActionsPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        transformActionsPanel.setLayout(gbl_transformActionsPanel);
        {
          JButton removeTransformButton = new JButton(
              Localizer.localize("RemoveTransformButtonText"));
          GridBagConstraints gbc_removeTransformButton = new GridBagConstraints();
          gbc_removeTransformButton.insets = new Insets(0, 0, 0, 5);
          gbc_removeTransformButton.anchor = GridBagConstraints.WEST;
          gbc_removeTransformButton.gridx = 0;
          gbc_removeTransformButton.gridy = 0;
          transformActionsPanel.add(removeTransformButton, gbc_removeTransformButton);
          removeTransformButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              removeTransformActionPerformed(e);
            }
          });
          removeTransformButton.setFont(UIManager.getFont("Button.font"));
          removeTransformButton.setMargin(new Insets(1, 5, 1, 5));
          removeTransformButton.setIcon(new ImageIcon(ImageTransformDialog.class
              .getResource("/com/famfamfam/silk/delete.png")));
        }
        {
          transformTypeComboBox = new JComboBox<ImageTransformType>();
          transformTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
          transformTypeComboBox.setModel(new DefaultComboBoxModel<ImageTransformType>(
              new ImageTransformType[] { ImageTransformType.DESKEW, ImageTransformType.BINARIZE,
                  ImageTransformType.ROTATE, ImageTransformType.CROP }));
          GridBagConstraints gbc_transformTypeComboBox = new GridBagConstraints();
          gbc_transformTypeComboBox.insets = new Insets(0, 0, 0, 5);
          gbc_transformTypeComboBox.anchor = GridBagConstraints.WEST;
          gbc_transformTypeComboBox.gridx = 1;
          gbc_transformTypeComboBox.gridy = 0;
          transformActionsPanel.add(transformTypeComboBox, gbc_transformTypeComboBox);
        }
        {
          JButton addTransformButton = new JButton(Localizer.localize("AddTransformButtonText"));
          GridBagConstraints gbc_addTransformButton = new GridBagConstraints();
          gbc_addTransformButton.gridx = 2;
          gbc_addTransformButton.gridy = 0;
          transformActionsPanel.add(addTransformButton, gbc_addTransformButton);
          addTransformButton.setFont(UIManager.getFont("Button.font"));
          addTransformButton.setMargin(new Insets(1, 5, 1, 5));
          addTransformButton.setIcon(new ImageIcon(ImageTransformDialog.class
              .getResource("/com/famfamfam/silk/add.png")));
          {
            transformSettingsContainerPanel = new JPanel();
            GridBagConstraints gbc_transformSettingsContainerPanel = new GridBagConstraints();
            gbc_transformSettingsContainerPanel.fill = GridBagConstraints.BOTH;
            gbc_transformSettingsContainerPanel.gridx = 0;
            gbc_transformSettingsContainerPanel.gridy = 1;
            contentPanel.add(transformSettingsContainerPanel, gbc_transformSettingsContainerPanel);
            GridBagLayout gbl_transformSettingsContainerPanel = new GridBagLayout();
            gbl_transformSettingsContainerPanel.columnWidths = new int[] { 0, 0 };
            gbl_transformSettingsContainerPanel.rowHeights = new int[] { 0, 0 };
            gbl_transformSettingsContainerPanel.columnWeights = new double[] { 1.0,
                Double.MIN_VALUE };
            gbl_transformSettingsContainerPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
            transformSettingsContainerPanel.setLayout(gbl_transformSettingsContainerPanel);
          }
          addTransformButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addTransformActionPerformed(e);
            }
          });
        }
      }
    }
    {
      updateTransformSettingsPanel(getTransformSettingsPanel());
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setBorder(new EmptyBorder(0, 12, 12, 12));
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton transformButton = new JButton(Localizer.localize("DoTransformButtonText"));
        transformButton.setIcon(new ImageIcon(ImageTransformDialog.class
            .getResource("/com/famfamfam/silk/accept.png")));
        transformButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            dialogResult = JOptionPane.OK_OPTION;
            dispose();
          }
        });
        transformButton.setFont(UIManager.getFont("Button.font"));
        transformButton.setMargin(new Insets(1, 5, 1, 5));
        buttonPane.add(transformButton);
        getRootPane().setDefaultButton(transformButton);
      }
      {
        JButton cancelButton = new JButton(Localizer.localize("Cancel"));
        cancelButton.setIcon(new ImageIcon(ImageTransformDialog.class
            .getResource("/com/famfamfam/silk/cross.png")));
        cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        });
        cancelButton.setFont(UIManager.getFont("Button.font"));
        cancelButton.setMargin(new Insets(1, 5, 1, 5));
        buttonPane.add(cancelButton);
      }
    }
  }

  private void removeTransformActionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (transformTypeList.getSelectedIndex() < 0) {
          return;
        }
        int[] indicies = transformTypeList.getSelectedIndices();

        ArrayList<IImageTransform> removeList = new ArrayList<IImageTransform>();

        for (int index : indicies) {
          removeList.add(transformTypeListModel.get(index));
        }

        for (IImageTransform transform : removeList) {
          transformTypeListModel.removeElement(transform);
        }

        transformTypeList.clearSelection();
        transformTypeList.revalidate();
        transformTypeList.repaint();
        updateTransformSettingsPanel(getTransformSettingsPanel());
      }
    });
  }

  private void transformListSelectionChanged(ListSelectionEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        updateTransformSettingsPanel(getTransformSettingsPanel());
      }
    });
  }

  private void updateTransformSettingsPanel(ITransformSettingsPanel transformSettingsPanel) {
    transformSettingsContainerPanel.removeAll();
    if (transformSettingsPanel != null) {
      GridBagConstraints gbc_transformSettingsPanel = new GridBagConstraints();
      gbc_transformSettingsPanel.fill = GridBagConstraints.BOTH;
      gbc_transformSettingsPanel.gridx = 0;
      gbc_transformSettingsPanel.gridy = 0;
      transformSettingsContainerPanel.add((JPanel) transformSettingsPanel,
          gbc_transformSettingsPanel);
    }
    transformSettingsContainerPanel.revalidate();
    transformSettingsContainerPanel.repaint();
  }

}
