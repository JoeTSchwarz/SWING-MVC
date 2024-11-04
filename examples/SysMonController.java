import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;
import java.text.NumberFormat;
//
import joeapp.odb.*;
// Joe T. Schwarz (C)
/**
Modelling with SWINGLoader Package
*/
public class SysMonController {
  /**
  Predefined Constructor for Controller
  @param map HashMap with String as keys (defined in the model) and Object as values (J Components)
  */
  public SysMonController(HashMap<String, Object> map, String[] argv) {
    // get and start SysMonSWING panel
    SysMonSWING sysmon = (SysMonSWING) map.get("sysmon");
    sysmon.setLayout(500, 500);
    sysmon.setTitle(argv[0]);
    (new Thread(sysmon)).start();
  }
}