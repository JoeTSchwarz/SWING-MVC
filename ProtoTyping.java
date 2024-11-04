package joeapp.mvc;
//
import javax.swing.*;
//
import joeapp.mvc.*;
// Joe T. Schwarz (C)
/**
The ProtoTyping View
*/
public class ProtoTyping {
  /** constructor
  @exception Exception Java Exceptions thrown by Java
  */
  public ProtoTyping( ) throws Exception {
    SWINGLoader ml = new SWINGLoader("resources/prototype.txt", "joeapp.mvc.ProtoController");
    ((JFrame) ml.load()).setVisible(true);
  }
  private static String[] parms;
  /**
  The default starting main
  @param a Parameter String array
  @exception Exception thrown by JAVA
  */
  public static void main(String... a) throws Exception {
    
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    new ProtoTyping();
  }
}

