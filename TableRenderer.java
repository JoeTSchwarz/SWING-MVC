package joeapp.mvc;
//
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
// Joe T. Schwarz (C)
/**
@author Joe T. Schwarz (C)
JTable Renderer
*/
public class TableRenderer extends DefaultTableCellRenderer {
  /**
  constructor
  */
  public TableRenderer() { }
  /**
  Overwrite the getTableCellRendererComponent of DefaultTableCellRenderer
  @param table JTable
  @param value Object (the cell)
  @param isSelected boolean true if it was selected
  @param hasFocus boolean true if it has
  @param row int, the row
  @param column int, the column
  @return Component ICell object
  */
  public Component getTableCellRendererComponent(JTable table, Object value,
                                      boolean isSelected, boolean hasFocus,
                                      int row, int column) {
    return ((ICell) value).getCell();
  }
}
