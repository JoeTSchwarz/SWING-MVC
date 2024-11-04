import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import joeapp.mvc.*;
// Joe Nartca (C)
public class GenericController {
  public GenericController(HashMap<String, Object> map) {
    // get JButton, JTextField and JTextArea
    JButton but = (JButton) map.get("But1");
    JTextField jtf = (JTextField) map.get("TxtF1");    
    JTextArea jta  = (JTextArea) map.get("TxtA1");
    but.addActionListener(e -> {
      but.setBackground(on? Color.yellow:Color.green);
      jta.append("\nButton was clicked. Color changed to:"+(on?"YELLOW":"GREEN")+
                 "\nProcessBar starts....");
      on = !on;
      // start pb Thread
      (new Fill((JProgressBar) map.get("pBar"), jta)).start();
    });
    // read JTextField and write into JTextArea
    jtf.addActionListener(e -> {
      jta.append("\nName is:"+jtf.getText());
    });
    JComboBox<String> cbx = (JComboBox) map.get("cBox");
    cbx.addActionListener(e -> {
      Object obj = cbx.getSelectedItem();
      if (obj instanceof ICell) jta.append("\nYou've selected:"+((ICell)obj).getText());
      else jta.append("\nYou've selected:"+(String)obj);
    });
    c1 = true;
    c2 = c3 = false;
    JCheckBox cb1 = (JCheckBox) map.get("kBox1");
    cb1.addItemListener(e -> {
       if (c1) jta.append("\nYour choice: Ripe.");
       c1 = !c1;
       cb1.setForeground(c1?Color.red:Color.blue);
       cb1.setText(c1?"Ripe":"gone");
    });
    JCheckBox cb2 = (JCheckBox) map.get("kBox2");
    cb2.addItemListener(e -> {
       c2 = !c2;
       if (c2) jta.append("\nYour choice: Green.");
       cb2.setForeground(c2?Color.blue:Color.green);
       cb2.setText(c2?"gone":"Green");
    });
    JCheckBox cb3 = (JCheckBox) map.get("kBox3");
    cb3.addItemListener(e -> {
       c3 = !c3;
       if (c3) jta.append("\nYour choice: NeverMind.");
       cb3.setForeground(c3?Color.blue:Color.yellow);
       cb3.setText(c3?"gone":"Yellow");
    });
    JRadioButton rad = (JRadioButton) map.get("Radio1");
    rad.addItemListener(e -> {
       br = !br;
       jta.append("\nYou've "+(!br?"Radio popped":"pushed"));
       rad.setForeground(br?Color.blue:Color.red);
       rad.setText(br?"pop":"push");
    });
    JToggleButton tog = (JToggleButton) map.get("toggle1");
    tog.addItemListener(e -> {
       jta.append("\nYou've Toggled");
       tog.setBackground(tog.isSelected()?Color.red:Color.blue);
    });
    JList<String> jlst = (JList) map.get("jlist");
    jlst.addListSelectionListener(e -> {
      Object obj = jlst.getSelectedValue();
      if (obj instanceof ICell) jta.append("\nSelected from JList::"+((ICell)obj).getText());
      else jta.append("\nSelected from JList::"+(String)obj);
    });
    JTree jtree = (JTree) map.get("jTree");
    jtree.getSelectionModel().addTreeSelectionListener(e -> {
      jta.append("\nSelected from JTee:"+e.getPath().toString());
    });
    JTable jtable = (JTable) map.get("jTable");
    jtable.getSelectionModel().addListSelectionListener(e -> {
      int row = jtable.getSelectedRow();
      int col = jtable.getSelectedColumn();
      Object o = jtable.getValueAt(row, col);
      if (o instanceof ICell)
        jta.append("\nSelected from JTable:"+((ICell)o).getText());
      else jta.append("\nSelected from JTable:"+(String)o);
    });
  }
  private class Fill extends Thread {
    public Fill(JProgressBar pb, JTextArea jta) {
      this.jta = jta;
      this.pb = pb;
    }
    private JProgressBar pb;
    private JTextArea jta;
    public void run() {
      try { 
        for (int i = 0; i <= 100; i += 10) { 
          pb.setValue(i); 
          pb.setIndeterminate(false);
          // delay the thread 
          Thread.sleep(400); 
        } 
        jta.append("\nProgressBar is full");
      } catch (Exception ex) {ex.printStackTrace();}
    }
  }
  private boolean on = false, c1, c2, c3, br = false;
}

