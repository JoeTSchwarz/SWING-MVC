import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
//
import joeapp.mvc.*;
// Joe Nartca (C)
public class FrameController {
  public FrameController(HashMap<String, Object> map) {
    // get JButton, JTextField and JTextArea
    JButton but = (JButton) map.get("But1");
    JTextField jtf = (JTextField) map.get("TxtF1");    
    JTextArea jta  = (JTextArea) map.get("TxtA1");
    but.addActionListener(e -> {
      but.setBackground(on? Color.yellow:Color.green);
      jta.append("\nButton was clicked. Color changed to:"+(on?"YELLOW":"GREEN"));
      on = !on;
    });
    // read JTextField and write into JTextArea
    jtf.addActionListener(e -> {
      jta.append("\nName is:"+jtf.getText());
    });
  }
  private boolean on = false;
}

