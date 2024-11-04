import javax.swing.*;
import java.awt.event.*;
//
import joeapp.mvc.SWINGLoader;
// Joe Nartca (C)
public class DialogMVC {
  public DialogMVC(String fName) throws Exception {
    SWINGLoader ml = new SWINGLoader(fName);
    JFrame jf = (JFrame) ml.load();
    DialogController ec = new DialogController(ml.getComponentMap());
    jf.setVisible(true);
  }
  public static void main(String... a) throws Exception {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    new DialogMVC(a.length == 0?"c:/joeapp/mvc/example/model/dialog.txt":a[0]);
  }
}

