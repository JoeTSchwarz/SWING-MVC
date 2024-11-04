import javax.swing.*;
import java.awt.event.*;
//
import joeapp.mvc.SWINGLoader;
// Joe T. Schwarz (C)
public class FrameMVC {
  public FrameMVC(String fName) throws Exception {
    SWINGLoader ml = new SWINGLoader(fName);
    JFrame jf = (JFrame) ml.load();
    FrameController tc = new FrameController(ml.getComponentMap());
    jf.setVisible(true);
  }
  public static void main(String... a) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
      new FrameMVC(a.length == 0?"c:/joeapp/mvc/example/model/frame.txt":a[0]);
    } catch (Exception ex) {ex.printStackTrace(); }
  }
}

