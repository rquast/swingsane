package com.swingsane.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.swingsane.business.image.transform.CropTransform;
import com.swingsane.business.image.transform.IImageTransform;
import com.swingsane.gui.panel.ITransformSettingsPanel;
import com.swingsane.i18n.Localizer;

/**
 * @author Roland Quast (roland@formreturn.com)
 *
 */
@SuppressWarnings("serial")
public class CropImageDialog extends JDialog implements ITransformSettingsPanel {

  private final JPanel contentPanel = new JPanel();

  private int dialogResult = JOptionPane.CANCEL_OPTION;

  private static final int BOUNDS_WIDTH = 500;
  private static final int BOUNDS_HEIGHT = 500;

  private final Dimension minBounds = new Dimension(BOUNDS_WIDTH, BOUNDS_HEIGHT);

  private CropTransform transform;

  private ITransformSettingsPanel transformSettingsPanel;

  private boolean hit = false;

  private double zoom = -1.0d;

  private Rectangle maximumWindowSize;

  private BufferedImage sourceImage;

  private JPanel cropPanel;

  private JLabel imageLabel = new JLabel("", SwingConstants.LEFT);

  private BufferedImage bufferedImage;

  private ChangeListener transformListener;

  private BufferedImage bufferedImageCopy;

  public CropImageDialog(Component parent) {
    initComponents();
    pack();
  }

  private void addSettingsPanel() {
    transformSettingsPanel = transform.getTransformSettingsPanel();
    contentPanel.add((JPanel) transformSettingsPanel, BorderLayout.SOUTH);
  }

  private void cropSelectionRectangle() {

    double width = sourceImage.getWidth();
    double height = sourceImage.getHeight();
    bufferedImage = new BufferedImage((int) (width * zoom), (int) (height * zoom),
        BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(zoom, zoom);
    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    bufferedImage = scaleOp.filter(sourceImage, bufferedImage);

    bufferedImageCopy = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
        bufferedImage.getType());

    imageLabel.setIcon(new ImageIcon(bufferedImageCopy));

    repaint(bufferedImage, bufferedImageCopy);
    imageLabel.repaint();

    imageLabel.addMouseListener(new MouseAdapter() {

      @Override
      public void mousePressed(MouseEvent me) {
        Point point = me.getPoint();
        translate(point);
        if ((transform.getBounds() != null) && transform.getBounds().contains(point)) {
          setHit(true);
        } else {
          setHit(false);
        }
      }

    });

    imageLabel.addMouseMotionListener(new MouseMotionAdapter() {

      private Point start = new Point();

      @Override
      public void mouseDragged(MouseEvent me) {
        Point end = me.getPoint();
        translate(end);
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        if (hit) {
          transform.getBounds().x += dx;
          transform.getBounds().y += dy;
          start = end;
        } else {
          transform.setBounds(new Rectangle(start, new Dimension(dx, dy)));
        }
        repaint(bufferedImage, bufferedImageCopy);
        imageLabel.repaint();
        restoreSettings();
        ((JPanel) transformSettingsPanel).revalidate();
        ((JPanel) transformSettingsPanel).repaint();
      }

      @Override
      public void mouseMoved(MouseEvent me) {
        start = me.getPoint();
        translate(start);
        repaint(bufferedImage, bufferedImageCopy);
        imageLabel.repaint();
      }
    });

  }

  public final int getDialogResult() {
    return dialogResult;
  }

  @Override
  public final IImageTransform getTransform() {
    return transform;
  }

  private Rectangle getTranslatedBounds(Rectangle bounds) {
    Rectangle rect = new Rectangle();
    rect.x = (int) (bounds.getX() * zoom);
    rect.y = (int) (bounds.getY() * zoom);
    rect.width = (int) (bounds.getWidth() * zoom);
    rect.height = (int) (bounds.getHeight() * zoom);
    return rect;
  }

  private void initComponents() {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(Localizer.localize("CropImageDialogTitle"));
    setMinimumSize(minBounds);
    setMaximumWindowBounds();
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(12, 12, 0, 12));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setBorder(new EmptyBorder(12, 12, 12, 12));
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton cropButton = new JButton(Localizer.localize("CropButtonText"));
        cropButton.setIcon(new ImageIcon(CropImageDialog.class
            .getResource("/com/famfamfam/silk/shape_handles.png")));
        cropButton.setFont(UIManager.getFont("Button.font"));
        cropButton.setMargin(new Insets(1, 5, 1, 5));
        cropButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            dialogResult = JOptionPane.OK_OPTION;
            dispose();
          }
        });
        buttonPane.add(cropButton);
        getRootPane().setDefaultButton(cropButton);
      }
      {
        JButton cancelButton = new JButton(Localizer.localize("Cancel"));
        cancelButton.setIcon(new ImageIcon(CropImageDialog.class
            .getResource("/com/famfamfam/silk/cross.png")));
        cancelButton.setFont(UIManager.getFont("Button.font"));
        cancelButton.setMargin(new Insets(1, 5, 1, 5));
        cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        });
        buttonPane.add(cancelButton);
      }
    }

    cropPanel = new JPanel(new BorderLayout());
    cropPanel.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        updateImage();
      }

      @Override
      public void componentShown(ComponentEvent e) {
        updateImage();
      }
    });
    contentPanel.add(cropPanel, BorderLayout.CENTER);
    cropPanel.add(imageLabel, BorderLayout.CENTER);

  }

  public final void loadImage() throws IOException {
    setImage(ImageIO.read(transform.getSourceImageFile()));
  }

  private void repaint(BufferedImage orig, BufferedImage copy) {
    Graphics2D g = copy.createGraphics();
    g.drawImage(orig, 0, 0, null);
    if (transform.getBounds() != null) {
      Rectangle translatedBounds = getTranslatedBounds(transform.getBounds());
      g.setColor(Color.BLUE);
      g.draw(translatedBounds);
      g.setColor(new Color(200, 200, 255, 150));
      g.fill(translatedBounds);
    }
    g.dispose();
  }

  @Override
  public final void restoreSettings() {
    if ((transform == null) || (transformSettingsPanel == null)) {
      return;
    }
    transformSettingsPanel.restoreSettings();
  }

  private void setHit(boolean hit) {
    this.hit = hit;
  }

  private void setImage(final BufferedImage image) {
    sourceImage = image;
  }

  private void setMaximumWindowBounds() {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    maximumWindowSize = graphicsEnvironment.getMaximumWindowBounds();
    maximumWindowSize.height -= 50;
    this.setBounds(maximumWindowSize);
    Dimension bounds = maximumWindowSize.getSize();
    setPreferredSize(bounds);
    setSize(bounds);
  }

  @Override
  public final void setTransform(IImageTransform transform) {
    this.transform = (CropTransform) transform;
    transformListener = new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            repaint(bufferedImage, bufferedImageCopy);
            imageLabel.revalidate();
            imageLabel.repaint();
          }
        });
      }
    };
    this.transform.addChangeListener(transformListener);
    addSettingsPanel();
  }

  private void translate(Point p) {
    p.x = (int) (p.getX() / zoom);
    p.y = (int) (p.getY() / zoom);
  }

  private void updateImage() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        updateZoom(sourceImage.getWidth(), sourceImage.getHeight());
        cropSelectionRectangle();
        cropPanel.revalidate();
        cropPanel.repaint();
      }
    });
  }

  private void updateZoom(int imageWidth, int imageHeight) {
    Dimension viewSize = imageLabel.getSize();
    if ((viewSize == null) || (viewSize.width == 0) || (viewSize.height == 0)) {
      return;
    }
    double aspectRatio = (double) imageWidth / (double) imageHeight;
    if (aspectRatio < 1) {
      zoom = (double) viewSize.width / (double) imageWidth;
    } else {
      zoom = (double) viewSize.height / (double) imageHeight;
    }
  }

}
