import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
//
import joeapp.mvc.*;
// Joe Nartca (C)
public class TabbedController {
  public TabbedController(HashMap<String, Object> map) {
    JPanel jp = (JPanel) map.get("Pane3");
    JTree tree = (JTree) map.get("jTree");
    JPopupMenu pop = (JPopupMenu) map.get("Popup");
    pop.addMouseListener(new MouseListener() {
      public void mouseEntered(MouseEvent e) {
        pop.show(jp , e.getX(), e.getY());  
      }
      public void mouseClicked(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
      public void mousePressed(MouseEvent e) { }
      public void mouseReleased(MouseEvent e) { }
    });
    // Tab_1
    JTree jtree = (JTree) map.get("jTree");
    JLabel lab = (JLabel) map.get("Lab1");
    jtree.getSelectionModel().addTreeSelectionListener(e -> {
      DefaultMutableTreeNode dn = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
      Object obj = dn.getUserObject();
      if (obj instanceof ICell) lab.setText("From JTree:"+((ICell)obj).getText());
      else lab.setText("From JTree:"+(String)obj);
    });
    // Tab_2
    JButton but2 = (JButton) map.get("But2");
    JTextField jtf2 = (JTextField) map.get("TxtF2");    
    JTextArea jta2  = (JTextArea) map.get("TxtA2");
    but2.addActionListener(e -> {
      but2.setBackground(on2? Color.yellow:Color.green);
      jta2.append("\nButtonTab_2 was clicked. Color changed to:"+(on2?"YELLOW":"GREEN"));
      on2 = !on2;
    });
    // read JTextField and write into JTextArea
    jtf2.addActionListener(e -> {
      jta2.append("\nName is:"+jtf2.getText());
    });
    JComboBox cbx = (JComboBox) map.get("cBox");
    cbx.addActionListener(e -> {
      jta2.append("\nYou've selected:"+(String)cbx.getSelectedItem());
    });
  }
  private boolean on2 = false;
}

