import javax.swing.*;
import java.awt.event.*;
//
import joeapp.mvc.SWINGLoader;
// Joe Nartca (C)
public class DirectMVC {
  public DirectMVC(String fName) throws Exception {
    SWINGLoader ml = new SWINGLoader(fName);
    JFrame jf = (JFrame) ml.load();
    DirectController ec = new DirectController(ml.getComponentMap());
    jf.setVisible(true);
  }
  public static void main(String... a) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
      new DirectMVC(a.length == 0?"c:/joeapp/mvc/example/model/direct.txt":a[0]);
    } catch (Exception ex) {ex.printStackTrace(); }
  }
}

