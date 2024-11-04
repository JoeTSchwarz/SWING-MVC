import javax.swing.*;
import java.awt.event.*;
//
import joeapp.mvc.SWINGLoader;
// Joe T. Schwarz (C)
public class PanelMVC {
  public PanelMVC(String fName) throws Exception {
    SWINGLoader ml = new SWINGLoader(fName);
    JPanel jp = (JPanel) ml.load();
    PanelController pc = new PanelController(ml.getComponentMap());
    JFrame jf = new JFrame("PanelMVC");
    jf.setSize(400, 500);
    jf.setLocation(20, 20);
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jf.add(jp);
    jf.setVisible(true);
  }
  public static void main(String... a) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
      //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      new PanelMVC(a.length == 0?"c:/joeapp/mvc/example/model/panel.txt":a[0]);
    } catch (Exception ex) {ex.printStackTrace(); }
  }
}

