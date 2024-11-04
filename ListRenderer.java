package joeapp.mvc;
//
import javax.swing.*;
import java.awt.Component;
// Joe T. Schwarz (C)
/**
JList Cell Renderer
*/
public class ListRenderer extends DefaultListCellRenderer {
  /** constructor
  */
  public ListRenderer() { }
  /**
  Overwrite the getListCellRendererComponent of DefaultListCellRenderer
  @param list Jlist
  @param value Object (the cell)
  @param index int, the Cell index
  @param isSelected boolean true if it was selected
  @param cellHasFocus boolean true if it has
  @return Component ICell
  */
  public Component getListCellRendererComponent(JList<?> list, Object value,
                                                int index, boolean isSelected,
                                                boolean cellHasFocus) {
    return ((ICell) value).getCell();
  }
}

