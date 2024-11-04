import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
// Joe Nartca (C)
public class DialogController {
  public DialogController(HashMap<String, Object> map) {
    // get JButton, JTextField and JTextArea
    JButton start  = (JButton) map.get("Start");
    JButton but    = (JButton) map.get("But");
    JTextField jtf = (JTextField) map.get("TxtF");    
    JTextArea jta  = (JTextArea) map.get("TxtA");
    start.addActionListener(e -> {
      JDialog jd = (JDialog) map.get("MyDialog");
      // because location is not given -> setLocationRelativeTo.
      jd.setLocationRelativeTo((JFrame)map.get("MyFrame"));
      jta.append("\nMVC for JDialog started.");
      jd.setVisible(true); 
    });
    
    but.addActionListener(e -> {
      but.setBackground(on? Color.yellow:Color.green);
      jta.append("\nButtonTab_1 was clicked. Color changed to:"+(on?"YELLOW":"GREEN"));
      on = !on;
    });
    // read JTextField and write into JTextArea
    jtf.addActionListener(e -> {
      jta.append("\nName is:"+jtf.getText());
    });
    JComboBox cbx = (JComboBox) map.get("cBox");
    cbx.addActionListener(e -> {
      jta.append("\nYou've selected:"+(String)cbx.getSelectedItem());
    });
  }
  private boolean on = false;
}

