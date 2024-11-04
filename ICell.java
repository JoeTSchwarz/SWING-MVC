package joeapp.mvc;
//
import java.net.URL;
import java.io.File;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
// Joe T. Schwarz (C)
/**
@author Joe T. Schwarz (C)
ICell for SWINGLoader
*/
public class ICell {
  private JLabel lab;
  private ImageIcon icon;
  private String text, ifile;
  /**
  Contructor
  @param txtIcon String contains the text and Icon file
  @param root String, the directory in case of relative path for Icon file or null by absolute path
  @param cls  Class<?> referenced to JAR class
  */
  public ICell(String txtIcon, String root, Class<?> cls) {
    int p = txtIcon.indexOf("@");
    if (p < 0) {
      ifile = txtIcon;
      text  = txtIcon;
    } else {
      ifile = txtIcon.substring(p+1);
      if (p > 0) text = txtIcon.substring(0, p);
      else text = ifile;
    }
    try {
      icon = SWING.getIcon(ifile, root, cls);
      if (icon != null) {
        if (text != null) lab = new JLabel(text, icon, SwingConstants.LEFT);
        else lab = new JLabel(icon);
        return;
      }
    } catch (Exception ex) { }
    lab = new JLabel(text, (Icon)UIManager.get("Tree.leafIcon"), SwingConstants.LEFT);
  }
  /**
  setIcon
  @param icon ImageIcon
  */
  public void setIcon(ImageIcon icon) {
    this.icon = icon;
    lab.setIcon(icon);
  }
  /**
  setTex
  @param text String
  */
  public void setText(String text) {
    this.text = text;
    lab.setText(text);
    if (icon != null) lab.setHorizontalAlignment(SwingConstants.LEFT);
  }
  /**
  getCell
  @return JLabel containing icon and text (left alignment)
  */
  public JLabel getCell() {
    return lab;
  }
  /**
  getIcon
  @return ImageIcon
  */
  public ImageIcon getIcon() {
    return icon;
  }
  /**
  getFile
  @return the file name
  */
  public String getFile() {
    return ifile;
  }
  /**
  getText
  @return the ICell text
  */
  public String getText() {
    return text;
  }
}
