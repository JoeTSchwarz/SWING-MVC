import javax.swing.*;
import java.awt.event.*;
//
import joeapp.mvc.SWINGLoader;
// Joe T. Schwarz (C)
public class TabbedMVC {
  public TabbedMVC(String fName) throws Exception {
    //SWINGLoader ml = new SWINGLoader(fName);
    SWINGLoader ml = new SWINGLoader(fName, "TabbedController");
    JFrame jf = (JFrame) ml.load();
    jf.setVisible(true);
  }
  public static void main(String... a) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
      new TabbedMVC(a.length == 0?"c:/joeapp/mvc/example/model/tabbed.txt":a[0]);
    } catch (Exception ex) {ex.printStackTrace(); }
  }
}
