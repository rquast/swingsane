package com.swingsane.gui.component;

// http://www.devx.com/getHelpOn/10MinuteSolution/20425
// courtesy of Daniel F. Savarese

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

public class JTableButtonMouseListener implements MouseListener {

  private JTable jTable;

  public JTableButtonMouseListener(JTable table) {
    jTable = table;
  }

  private void forwardEventToButton(MouseEvent e) {

    TableColumnModel columnModel = jTable.getColumnModel();
    int column = columnModel.getColumnIndexAtX(e.getX());
    int row = e.getY() / jTable.getRowHeight();
    Object value;
    JButton button;
    MouseEvent buttonEvent;

    if ((row >= jTable.getRowCount()) || (row < 0) || (column >= jTable.getColumnCount())
        || (column < 0)) {
      return;
    }

    value = jTable.getValueAt(row, column);

    if (!(value instanceof JButton)) {
      return;
    }

    button = (JButton) value;

    buttonEvent = SwingUtilities.convertMouseEvent(jTable, e, button);
    button.dispatchEvent(buttonEvent);
    jTable.repaint();

  }

  @Override
  public final void mouseClicked(MouseEvent e) {
    forwardEventToButton(e);
  }

  @Override
  public final void mouseEntered(MouseEvent e) {
    forwardEventToButton(e);
  }

  @Override
  public final void mouseExited(MouseEvent e) {
    forwardEventToButton(e);
  }

  @Override
  public final void mousePressed(MouseEvent e) {
    forwardEventToButton(e);
  }

  @Override
  public final void mouseReleased(MouseEvent e) {
    forwardEventToButton(e);
  }

}
