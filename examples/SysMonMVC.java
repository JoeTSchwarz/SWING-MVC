import javax.swing.*;
import java.awt.event.*;
//
import joeapp.mvc.SWINGLoader;
// Joe T. Schwarz (C)
public class SysMonMVC {
  public SysMonMVC( ) throws Exception {
    SWINGLoader ml = new SWINGLoader("model/sysMon.txt",
                                     "SysMonController",
                                     new String[] { "System Activities Monitoring" }
                                    );
    JFrame jframe = new JFrame("SWING SystemMonitor (C)");
    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jframe.add((JPanel) ml.load());
    jframe.setSize(700,555);
    jframe.setVisible(true);
  }
  public static void main(String... a) throws Exception {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    new SysMonMVC();
  }
}

