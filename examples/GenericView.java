import javax.swing.*;
import joeapp.mvc.SWINGLoader;
// Joe T. Schwarz (C)
public class GenericView {
  public GenericView(String[] p) throws Exception {
    SWINGLoader ml = new SWINGLoader(p[0], p[1]);
    ((JFrame) ml.load()).setVisible(true);
  }
  private static String[] parms;
  public static void main(String... a) throws Exception {
    if (a.length == 2) parms = a;
    else {
      parms = new String[2];
      parms[0] = "c:/joeapp/mvc/example/model/generic.txt";
      parms[1] = "GenericController";
    }
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    new GenericView(parms);
  }
}

