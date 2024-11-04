import javax.swing.*;
import java.awt.event.*;
//
import joeapp.mvc.SWINGLoader;
// Joe Nartca (C)
public class EmbeddedMVC {
  public EmbeddedMVC(String fName) throws Exception {
    SWINGLoader ml = new SWINGLoader(fName, "PanelController");
    JFrame jf = (JFrame) ml.load();
    //PanelController ec = new PanelController(ml.getComponentMap());
    jf.setVisible(true);
  }
  public static void main(String... a) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
      new EmbeddedMVC(a.length == 0?"c:/joeapp/mvc/example/model/embedded.txt":a[0]);
    } catch (Exception ex) {ex.printStackTrace(); }
  }
}

