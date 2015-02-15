package com.swingsane.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.swingsane.gui.Main;
import com.swingsane.i18n.Localizer;
import com.swingsane.util.Misc;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

  /**
   * Log4J logger.
   */
  private static final Logger LOG = Logger.getLogger(AboutDialog.class);

  private static final int BOUNDS_WIDTH = 600;
  private static final int BOUNDS_HEIGHT = 500;

  private final Dimension bounds = new Dimension(BOUNDS_WIDTH, BOUNDS_HEIGHT);

  private final JPanel contentPanel = new JPanel();
  private Properties swingSaneProperties;
  private JTextArea noticeFileTextArea;

  public AboutDialog(JFrame frame) {
    loadSwingSaneProperties();
    initComponents();
    loadNoticeFile();
    pack();
    setLocationRelativeTo(frame);
  }

  private void closeActionPerformed(ActionEvent e) {
    dispose();
  }

  private String getApplicationName() {
    return swingSaneProperties.getProperty("swingsane.name");
  }

  private String getApplicationURL() {
    return swingSaneProperties.getProperty("swingsane.url");
  }

  private String getBuildDate() {
    return swingSaneProperties.getProperty("swingsane.builddate");
  }

  private String getDescription() {
    return swingSaneProperties.getProperty("swingsane.description");
  }

  private String getVersion() {
    return swingSaneProperties.getProperty("swingsane.version");
  }

  private void initComponents() {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setMinimumSize(bounds);
    setPreferredSize(bounds);
    setSize(bounds);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(20, 12, 20, 12));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] { 10, 0 };
    gbl_contentPanel.rowHeights = new int[] { 10, 0, 0 };
    gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
    contentPanel.setLayout(gbl_contentPanel);
    {
      JPanel applicationPropertiesPanel = new JPanel();
      GridBagConstraints gbc_applicationPropertiesPanel = new GridBagConstraints();
      gbc_applicationPropertiesPanel.insets = new Insets(0, 0, 5, 0);
      gbc_applicationPropertiesPanel.anchor = GridBagConstraints.NORTH;
      gbc_applicationPropertiesPanel.gridx = 0;
      gbc_applicationPropertiesPanel.gridy = 0;
      contentPanel.add(applicationPropertiesPanel, gbc_applicationPropertiesPanel);
      GridBagLayout gbl_applicationPropertiesPanel = new GridBagLayout();
      gbl_applicationPropertiesPanel.columnWidths = new int[] { 70, 0 };
      gbl_applicationPropertiesPanel.rowHeights = new int[] { 15, 0, 0, 0, 0 };
      gbl_applicationPropertiesPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
      gbl_applicationPropertiesPanel.rowWeights = new double[] { 1.0, 1.0, 0.0, 0.0,
          Double.MIN_VALUE };
      applicationPropertiesPanel.setLayout(gbl_applicationPropertiesPanel);
      {
        JLabel applicationNameLabel = new JLabel(getApplicationName());
        applicationNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        applicationNameLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        GridBagConstraints gbc_applicationNameLabel = new GridBagConstraints();
        gbc_applicationNameLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_applicationNameLabel.insets = new Insets(0, 0, 15, 0);
        gbc_applicationNameLabel.anchor = GridBagConstraints.NORTH;
        gbc_applicationNameLabel.gridx = 0;
        gbc_applicationNameLabel.gridy = 0;
        applicationPropertiesPanel.add(applicationNameLabel, gbc_applicationNameLabel);
      }
      {
        JLabel descriptionLabel = new JLabel("<HTML>" + getDescription() + "</HTML>");
        descriptionLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        descriptionLabel.setMinimumSize(new Dimension(400, 40));
        descriptionLabel.setMaximumSize(new Dimension(400, 50));
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setFont(UIManager.getFont("Label.font"));
        GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
        gbc_descriptionLabel.insets = new Insets(0, 0, 15, 0);
        gbc_descriptionLabel.gridx = 0;
        gbc_descriptionLabel.gridy = 1;
        applicationPropertiesPanel.add(descriptionLabel, gbc_descriptionLabel);
      }
      {
        JLabel versionLabel = new JLabel(String.format(Localizer.localize("VersionLabelText"),
            getVersion(), getBuildDate()));
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        versionLabel.setFont(UIManager.getFont("Label.font"));
        GridBagConstraints gbc_versionLabel = new GridBagConstraints();
        gbc_versionLabel.insets = new Insets(0, 0, 15, 0);
        gbc_versionLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_versionLabel.gridx = 0;
        gbc_versionLabel.gridy = 2;
        applicationPropertiesPanel.add(versionLabel, gbc_versionLabel);
      }
      {
        final JLabel urlLabel = new JLabel(getApplicationURL());
        urlLabel.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            openApplicationURL();
          }

          @Override
          public void mouseEntered(MouseEvent e) {
            urlLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          }

          @Override
          public void mouseExited(MouseEvent e) {
            urlLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
        });
        urlLabel.setHorizontalAlignment(SwingConstants.CENTER);
        urlLabel.setForeground(Color.blue);
        urlLabel.setBorder(null);
        urlLabel.setFont(UIManager.getFont("Label.font"));
        GridBagConstraints gbc_urlLabel = new GridBagConstraints();
        gbc_urlLabel.insets = new Insets(0, 0, 15, 0);
        gbc_urlLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_urlLabel.gridx = 0;
        gbc_urlLabel.gridy = 3;
        applicationPropertiesPanel.add(urlLabel, gbc_urlLabel);
      }
    }
    {
      JScrollPane noticeFileScrollPane = new JScrollPane();
      GridBagConstraints gbc_noticeFileScrollPane = new GridBagConstraints();
      gbc_noticeFileScrollPane.fill = GridBagConstraints.BOTH;
      gbc_noticeFileScrollPane.gridx = 0;
      gbc_noticeFileScrollPane.gridy = 1;
      contentPanel.add(noticeFileScrollPane, gbc_noticeFileScrollPane);
      {
        noticeFileTextArea = new JTextArea();
        noticeFileTextArea.setEditable(false);
        noticeFileScrollPane.setViewportView(noticeFileTextArea);
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setBorder(new EmptyBorder(0, 12, 12, 12));
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton closeButton = new JButton(Localizer.localize("Close"));
        closeButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            closeActionPerformed(e);
          }
        });
        closeButton.setFont(UIManager.getFont("Button.font"));
        closeButton.setMargin(new Insets(1, 5, 1, 5));
        closeButton.setIcon(new ImageIcon(AboutDialog.class
            .getResource("/com/famfamfam/silk/cross.png")));
        buttonPane.add(closeButton);
      }
    }
  }

  private void loadNoticeFile() {
    InputStream in = getClass().getResourceAsStream("/com/swingsane/NOTICE");
    try {
      noticeFileTextArea.read(new InputStreamReader(in), null);
      in.close();
    } catch (IOException ex) {
      LOG.error(ex, ex);
    }
  }

  private void loadSwingSaneProperties() {
    swingSaneProperties = new Properties();
    try {
      swingSaneProperties.load(Main.class
          .getResourceAsStream("/com/swingsane/swingsane.properties"));
      swingSaneProperties.getProperty("swingsane.name");
    } catch (IOException ioex) {
      LOG.error(ioex, ioex);
    }
  }

  private void openApplicationURL() {
    Misc.openURL(getApplicationURL());
  }
}
