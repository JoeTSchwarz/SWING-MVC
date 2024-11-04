package joeapp.mvc;
//
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Component;
// Joe T. Schwarz (C)
/**
@author Joe T. Schwarz (C)
Tree Renderer for the TreeCellRenderer
*/
public class TreeRenderer implements TreeCellRenderer {
  public TreeRenderer() { }
  /**
  Overwrite the getTreeCellRendererComponent of TreeCellRenderer
  @param tree JTree
  @param value Object (the cell)
  @param selected boolean true if it was selected
  @param expanded boolean true if it has
  @param leaf boolean, true if leaf
  @param row int, the row
  @param hasFocus boolean true if it has
  @return Component object or a JLabel containing the value
  */
  public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                boolean selected, boolean expanded,
                                                boolean leaf, int row, boolean hasFocus) {
    Object obj = ((DefaultMutableTreeNode) value).getUserObject();
    if (obj instanceof ICell) return ((ICell)obj).getCell();
    return new JLabel("" + value);
  }
}

