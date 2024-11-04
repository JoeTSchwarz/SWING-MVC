import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
//
import joeapp.mvc.SWINGLoader;
// Joe T. Schwarz (C)
public class DirectController {
  public DirectController(HashMap<String, Object> map) {
    // get JButton, JTextField and JTextArea
    JButton start  = (JButton) map.get("Start");
    JTextArea jta  = (JTextArea) map.get("TxtA");
    start.addActionListener(e -> {
      if (dialog) return;
      start.setEnabled(false);
      start.setBackground(on? Color.yellow:Color.green);
      jta.append("\nButton Start was clicked. Color changed to:"+(on?"YELLOW":"GREEN"));
      on = !on;
      try {
        SWINGLoader ml = new SWINGLoader("c:/joeApp/mvc/example/model/jdialog.txt");
        JDialog jd = (JDialog) ml.load();
        HashMap<String, Object> mp = ml.getComponentMap();
        JButton but = (JButton) mp.get("But");
        but.addActionListener(e1 -> {
          jta.append("\nDialogButton was clicked. Dialog closed");
          start.setEnabled(true);
          dialog = false;
          jd.dispose();
        });
        JTextField jtf = (JTextField) mp.get("TxtF");  
        jtf.addActionListener(e2 -> {
          jta.append("\nFrom Dialog\nName is:"+jtf.getText());
        });
        JComboBox cbx = (JComboBox) mp.get("cBox");
        cbx.addActionListener(e3 -> {
            jta.append("\nFrom Dialog:\nYou've selected:"+(String)cbx.getSelectedItem());
        });
        jd.setLocationRelativeTo((JFrame)map.get("MyFrame"));
        jta.append("\nMVC for JDialog started.");
        jd.setVisible(true); 
      } catch (Exception ex) {
        ex.printStackTrace();
        jta.append("\nCan't start SWINGLoader. Reason:"+ex.toString());
      }
    });   
  }
  private boolean on = false, dialog = false;
}

