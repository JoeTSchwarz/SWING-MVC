package joeapp.mvc;
//
import javax.swing.*;
import java.awt.Component;
// Joe T. Schwarz (C)
/**
JComboBox Renderer used with Images as icons
*/
public class ComboRenderer extends DefaultListCellRenderer {
  /**
  Constructor
  */
  public ComboRenderer() { }
  /**
  set ICell to this Cell
  Overwrite the getListCellRendererComponent of DefaultListCellRenderer
  @param list Jlist
  @param value Object (the cell)
  @param index int, the Cell index
  @param isSelected boolean true if it was selected
  @param cellHasFocus boolean true if it has
  @return Component
  */
  public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                boolean isSelected, boolean cellHasFocus) {
    ICell cell = (ICell) value;
    setText(cell.getText());
    ImageIcon icon = cell.getIcon();
    if (icon != null) setIcon(icon);
    return this;
  }
}