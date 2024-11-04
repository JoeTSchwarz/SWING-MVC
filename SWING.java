package joeapp.mvc;
//
import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.nio.file.*;
import javax.imageio.*;
import java.awt.event.*;
import javax.swing.tree.*;
// Joe T. Schwarz (C)
/**
@author Joe T. Schwarz (C)
The JAVA MVC SWINGLoader. SWING utilities
*/
public class SWING {
  /**
  toFile for its existence
  @param dir  directory if file is not in the working directory
  @param fName String, file name
  @return String
  @exception Exception thrown by Java (e.g. null String, unknown file, etc.)
  */
  public static String toFile(String dir, String fName) throws Exception {
    fName = fName.replace("/", File.separator);
    if ((new File(fName)).exists()) return fName;
    if (!fName.startsWith(File.separator)) fName = File.separator + fName;
    while (true) {
      if ((new File(dir+fName)).exists()) return dir+fName;
      int n = dir.lastIndexOf(File.separator);
      if (n > 0) dir = dir.substring(0, n);
      else break;
    }
    throw new Exception("Unknown "+fName.substring(File.separator.length()));
  }
  /**
  loadFile
  @param dir String, working directory
  @param fName String, file name or URL
  @param cls  Class<?> referenced to JAR class
  @return byte Array
  @exception Exception thrown by Java (e.g. nullPointer, unknown file, etc.)
  */
  public static byte[] loadFile(String dir, String fName, Class<?> cls) throws Exception {
    if (cls != null) {
      InputStream inp = cls.getResourceAsStream("/"+fName);
      if (inp != null) {
        byte[] rec = new byte[inp.available()];
        int n = inp.read(rec);
        inp.close();
        return rec;
      }
    }
    return Files.readAllBytes((new File(toFile(dir, fName)).toPath()));
  }
  /**
  create an ImageIcon
  @param icon String, file name or URL
  @param dir  directory if file is not in the working directory
  @param cls  Class<?> referenced to JAR class
  @return ImageIcon, null if file is not found or NOT an image file
  @exception Exception thrown by Java (e.g. nullPointer, unknown file, etc.)
  */
  public static ImageIcon getIcon(String icon, String dir, Class<?> cls) throws Exception {
    if (!isImage(icon)) return null;
    if (icon.indexOf("://") > 0) return new ImageIcon(ImageIO.read(new URL(icon)));
    return new ImageIcon(ImageIO.read(new ByteArrayInputStream(loadFile(dir, icon, cls))));
  }
  /**
  isImage - true if the file is an image file
  @param img String, file name
  @return boolean true if it's a .gif or .png or .jpg or .jpeg
  */
  public static boolean isImage(String img) {
    String s = img.toLowerCase();
    return (s.indexOf(".gif") > 0 || s.indexOf(".png") > 0 ||
            s.indexOf(".jpg") > 0 || s.indexOf(".jpeg") > 0 ||
            s.indexOf("://") > 0); // web Image
  }
  /**
  getItem - get the value of the given pattern
  @param pat String, the pattern
  @param s String, the elements for the Model J Component
  @return String the value of pat
  @exception Exception thrown by Java (e.g. nullPointer, etc.)
  */
  public static String getItem(String pat, String s) throws Exception {
    int b = s.indexOf(pat);
    if (b < 0) return null;
    b += pat.length();
    if (s.charAt(b) == '"') {
      int e = s.indexOf("\"", ++b);
      if (e < 0) throw new Exception("Missing ending quote of "+pat+" @line:"+s);
      return s.substring(b, e);
    }
    int e = s.indexOf(" ", b);
    if (e < 0) throw new Exception("Invalid "+pat+" @line:"+s);
    return s.substring(b, e);
  }
  /**
  asObject - get an Object array from the Model JComponent
  @param s String, the elements for the Model J Component
  @param dir String, the working directory
  @param cls  Class<?> referenced to JAR class
  @return Object array
  @exception Exception thrown by Java (e.g. nullPointer, etc.)
  */
  public static Object[] asObject(String s, String dir, Class<?> cls) throws Exception {
    String[] M = getArray("items=", s, dir, cls);
    if (M == null) return null;
    ArrayList<String> al = new ArrayList<>(M.length);
    for (int i = 0; i < M.length; ++i) {
      if (M[i].indexOf(",") > 0) {
        String[] m = M[i].split(",");
        for (int l = 0; l < m.length; ++l) al.add(m[l].trim());
      } else al.add(M[i].trim());
    }
    M = al.toArray(new String[al.size()]);
    Object[] obj = new Object[M.length];
    for (int i = 0; i < M.length; ++i) {
      if (isImage(M[i])) obj[i] = new ICell(M[i], dir, cls);
      else obj[i] = M[i];
    }
    return obj;
  }
  /**
  getArray - get String array of the given pattern from Element s
  @param pat String, the pattern
  @param s String, Model element
  @return String the value of pat
  @exception Exception thrown by Java (e.g. nullPointer, etc.)
  */
  public static String[] getArray(String pat, String s) throws Exception {
    int b = s.indexOf(pat);
    if (b < 0) return null;
    b += pat.length();
    int e = s.indexOf(" ", b);
    if (e < 0) throw new Exception("Invalid "+pat+"... @line:"+s);
    String[] X = s.substring(b, e).split(",");
    for (int i = 0; i < X.length; ++i) {
      X[i] = X[i].replace("\"", "").trim();
    }
    return X;
  }
  /**
  getArray - get String array of the given pattern from Element s
  @param pat String, the pattern
  @param s String, Model element
  @param dir String, working directory
  @param cls  Class<?> referenced to JAR class
  @return String the value of pat
  @exception Exception thrown by Java (e.g. nullPointer, etc.)
  */
  public static String[] getArray(String pat, String s, String dir, Class<?> cls) throws Exception {
    String T[] = getArray(pat, s);
    if (T == null) return null;
    if (T.length == 1) {
      if (T[0].indexOf(".") < 0) return T;
      ArrayList<String> L = purify(new String(loadFile(dir, T[0], cls)));
      T = L.toArray(new String[L.size()]);
    }
    for (int i = 0; i < T.length; ++i) T[i] = T[i].trim();
    return T;
  }
  /**
  purify - clean all comments and unnecessary spaces
  @param S String, the String to be purified
  @return ArrayList of model-lines
  @exception Exception thrown by Java (e.g. nullPointer, etc.)
  */
  public static ArrayList<String> purify(String S) throws Exception {
    StringBuilder sb = new StringBuilder(S.replace("\r",""));
    for (int i = sb.indexOf("<!--"), e; i >= 0; ) { // remove all comments
      e = sb.indexOf("--!>", i);
      e = e < 0? sb.length(): e + 4;
      sb.replace(i, e, "");
      i = sb.indexOf("<!--");
    } 
    // concate line with continued lines and verify for correctness
    ArrayList<String> lst = new ArrayList<>();
    for (int i, j, b, e, l = sb.length(); l > 0; l = sb.length()) {
      b = sb.indexOf("<");
      if (b < 0) break;
      e = sb.indexOf("/>", b);
      if (e > 0 && e > b) { // exclude < (b+1) and /> (at e)
        lst.add(sb.substring(b+1, e).replace("\n", " ").replace(" ,", ",").
                   replace(", ", ",").replace("  ", " ").trim());
      } else {
        i = sb.indexOf(">", b);
        j = sb.indexOf("</",i);
        e = sb.indexOf(">", j);
        if (i < 0 || j < 0 || i > j || e < 0 || e == i)
          throw new Exception("Invalid Model up line:"+sb.substring(b));
        S = sb.substring(b, e+1).replace("\n", " ").replace("</", " </").
               replace(">", "> ").replace("< ", "<").replace(" =", "=").
               replace("= ", "=").replace("  ", " ").replace(" ,", ",").
               replace(", ", ",").trim();
        if (S.indexOf("</"+S.substring(1, S.indexOf(">")+1)) < 0 || S.indexOf("name=") < 0 || 
            S.indexOf("size=") < 0 || S.indexOf("location=") < 0) 
            throw new Exception("Missing mandatory keywords @line:"+S);
        lst.add(S);
      }
      sb.replace(b, e+1, "");
    }
    return lst;
  }
  /**
  isKeyword
  @param s String of MVC SWING
  @return true it it is
  */
  public static boolean isKeyword(String s) throws Exception {
    int i = s.indexOf("<"); 
    int e = s.indexOf(">");
    if (i < 0 || e < 0 || e < i)
      throw new Exception("Missing keyword: <keyword> @line:"+s);
    String com = s.substring(i, e+1);
    for (i = 0; i < keys.length; ++i)
       if (com.equalsIgnoreCase(keys[i])) return true;
    return false;
  }
  /**
  getValues - get twin values (of size=w,h or location=x,y)
  @param pat String, the pattern
  @param s String, the elements for the Model J Component
  @return int array containing w/h or x,y or NULL if pattern is not found
  @exception Exception thrown by Java (e.g. nullPointer, etc.)
  */
  public static int[] getValues(String pat, String s) throws Exception {
    int ib  = s.indexOf(pat);
    if (ib < 0) return null;
    ib += pat.length();
    int[] d = new int[2];
    int ie = s.indexOf(",", ib);
    if (ie < 0) throw new Exception("Invalid "+pat+"... @line:"+s);
    d[0] = Integer.parseInt(s.substring(ib, ie).trim());
    ib = s.indexOf(" ", ++ie);
    if (ib < 0) ib = s.indexOf("<", ie);
    if (ib < 0) throw new Exception("Invalid "+pat+"... @line:"+s);
    d[1] = Integer.parseInt(s.substring(ie, ib).trim());
    return d;
  }
  /**
  getBounds - get the values w/h and x/y
  @param s String, the elements for the Model J Component
  @return int array containing w,h,x,y
  @exception Exception thrown by Java (e.g. nullPointer, etc.)
  */
  public static void getBounds(Component com, String s) throws Exception {
    // size=w,h
    int ib  = s.indexOf("size=");
    if (ib < 0)throw new Exception("Missing size= @line:"+s);
    ib += 5;
    int ie = s.indexOf(",", ib);
    if (ie < 0) throw new Exception("Invalid size=... @line:"+s);
    int w = Integer.parseInt(s.substring(ib, ie).trim()); // width
    ib = s.indexOf(" ", ++ie);
    if (ib < 0) ib = s.indexOf("<", ie);
    if (ib < 0) throw new Exception("Invalid size=...... @line:"+s);
    int h = Integer.parseInt(s.substring(ie, ib).trim()); // height
    // location=x,y
    ib = s.indexOf("location=");
    if (ib < 0) throw new Exception("Missing location=...... @line:"+s);
    ib += 9; ie = s.indexOf(",", ib);
    if (ie < 0) throw new Exception("Invalid location=... @line:"+s);
    int x = Integer.parseInt(s.substring(ib, ie).trim()); // X coordinate
    ib = s.indexOf(" ", ++ie);
    if (ib < 0) ib = s.indexOf("<", ie);
    if (ib < 0) throw new Exception("Invalid location=...... @line:"+s);
    int y = Integer.parseInt(s.substring(ie, ib).trim()); // Y coordinate
    com.setBounds(x, y, w, h);
  }
  /**
  @param s String, the elements for the Model J Component
  @return Font
  @exception Exception thrown by Java (e.g. nullPointer, etc.)
  */
  public static Font getFont(String s) throws Exception {
    String font = getItem("font=", s);
    if (font == null) return null;
    String type = getItem("fontType=", s);
    if (type == null) type = "PLAIN";
    String fsize = getItem("fontSize=", s);
    int size = fsize == null? 12:Integer.parseInt(fsize.trim());
    if ("bold".equalsIgnoreCase(type)) return new Font(font, Font.BOLD, size);
    if ("italic".equalsIgnoreCase(type)) return new Font(font, Font.ITALIC, size);
    return new Font(font, Font.PLAIN, size);
  }
  /**
  getColor - get JAVA color the given string color
  @param color String, the color (e.g. blue, red, etc.)
  @return java.awt.Color
  */
  public static Color getColor(String color) {
    switch(color.toLowerCase()) {
    case "red": return Color.red;
    case "blue": return Color.blue;
    case "cyan": return Color.cyan;
    case "green": return Color.green;
    case "black": return Color.black;
    case "gray": return Color.gray;
    case "darkgray": return Color.darkGray;
    case "lightgray": return Color.lightGray;
    case "magenta": return Color.magenta;
    case "white": return Color.white;
    case "pink": return Color.pink;
    case "yellow": return Color.yellow;
    default: return Color.lightGray;
    }
  }
  /**
  mnemenic - get JAVA Key mnemonic
  @param mne String, the mnemonic (e.g. VK_0, VK_A, etc.)
  @return KeyEvent Mnemonic
  @exception Exception thrown by JAVA
  */
  public static int mnemonic(String mne) throws Exception {
    switch(mne.toUpperCase()) {
    case "VK_0": return KeyEvent.VK_0;
    case "VK_1": return KeyEvent.VK_1;
    case "VK_2": return KeyEvent.VK_2;
    case "VK_3": return KeyEvent.VK_3;
    case "VK_4": return KeyEvent.VK_4;
    case "VK_5": return KeyEvent.VK_5;
    case "VK_6": return KeyEvent.VK_6;
    case "VK_7": return KeyEvent.VK_7;
    case "VK_8": return KeyEvent.VK_8;
    case "VK_9": return KeyEvent.VK_9;
    case "VK_A": return KeyEvent.VK_A;
    case "VK_ACCEPT": return KeyEvent.VK_ACCEPT;
    case "VK_ADD": return KeyEvent.VK_ADD;
    case "VK_AGAIN": return KeyEvent.VK_AGAIN;
    case "VK_ALL_CANDIDATES": return KeyEvent.VK_ALL_CANDIDATES;
    case "VK_ALPHANUMERIC": return KeyEvent.VK_ALPHANUMERIC;
    case "VK_ALT": return KeyEvent.VK_ALT;
    case "VK_ALT_GRAPH": return KeyEvent.VK_ALT_GRAPH;
    case "VK_AMPERSAND": return KeyEvent.VK_AMPERSAND;
    case "VK_ASTERISK": return KeyEvent.VK_ASTERISK;
    case "VK_AT": return KeyEvent.VK_AT;
    case "VK_B": return KeyEvent.VK_B;
    case "VK_BACK_QUOTE": return KeyEvent.VK_BACK_QUOTE;
    case "VK_C": return KeyEvent.VK_C;
    case "VK_CANCEL": return KeyEvent.VK_CANCEL;
    case "VK_CIRCUMFLEX": return KeyEvent.VK_CIRCUMFLEX;
    case "VK_CLEAR": return KeyEvent.VK_CLEAR;
    case "VK_CLOSE_BRACKET": return KeyEvent.VK_CLOSE_BRACKET;
    case "VK_COLON": return KeyEvent.VK_COLON;
    case "VK_COMMA": return KeyEvent.VK_COMMA;
    case "VK_CONTROL": return KeyEvent.VK_CONTROL;
    case "VK_COPY": return KeyEvent.VK_COPY; 
    case "VK_CUT": return KeyEvent.VK_CUT;
    case "VK_D": return KeyEvent.VK_D;
    case "VK_E": return KeyEvent.VK_E;
    case "VK_END": return KeyEvent.VK_END;
    case "VK_ENTER": return KeyEvent.VK_ENTER;
    case "VK_EQUALS": return KeyEvent.VK_EQUALS;
    case "VK_ESCAPE": return KeyEvent.VK_ESCAPE;
    case "VK_EURO_SIGN": return KeyEvent.VK_EURO_SIGN;
    case "VK_EXCLAMATION_MARK": return KeyEvent.VK_EXCLAMATION_MARK;
    case "VK_F": return KeyEvent.VK_F;
    case "VK_F1": return KeyEvent.VK_F1;
    case "VK_F10": return KeyEvent.VK_F10;
    case "VK_F11": return KeyEvent.VK_F11;
    case "VK_F12": return KeyEvent.VK_F12;
    case "VK_F13": return KeyEvent.VK_F13;
    case "VK_F14": return KeyEvent.VK_F14;
    case "VK_F15": return KeyEvent.VK_F15;
    case "VK_F16": return KeyEvent.VK_F16;
    case "VK_F17": return KeyEvent.VK_F17;
    case "VK_F18": return KeyEvent.VK_F18;
    case "VK_F19": return KeyEvent.VK_F19;
    case "VK_F2": return KeyEvent.VK_F2;
    case "VK_F20": return KeyEvent.VK_F20;
    case "VK_F21": return KeyEvent.VK_F21;
    case "VK_F22": return KeyEvent.VK_F22;
    case "VK_F23": return KeyEvent.VK_F23;
    case "VK_F24": return KeyEvent.VK_F24;
    case "VK_F3": return KeyEvent.VK_F3;
    case "VK_F4": return KeyEvent.VK_F4;
    case "VK_F5": return KeyEvent.VK_F5;
    case "VK_F6": return KeyEvent.VK_F6;
    case "VK_F7": return KeyEvent.VK_F7;
    case "VK_F8": return KeyEvent.VK_F8;
    case "VK_F9": return KeyEvent.VK_F9;
    case "VK_FINAL": return KeyEvent.VK_FINAL;
    case "VK_FIND": return KeyEvent.VK_FIND;
    case "VK_FULL_WIDTH": return KeyEvent.VK_FULL_WIDTH;
    case "VK_G": return KeyEvent.VK_G;
    case "VK_GREATER": return KeyEvent.VK_GREATER;
    case "VK_H": return KeyEvent.VK_H;
    case "VK_HALF_WIDTH": return KeyEvent.VK_HALF_WIDTH;
    case "VK_HELP": return KeyEvent.VK_HELP;
    case "VK_HOME": return KeyEvent.VK_HOME;
    case "VK_I": return KeyEvent.VK_I;
    case "VK_INPUT_METHOD_ON_OFF": return KeyEvent.VK_INPUT_METHOD_ON_OFF;
    case "VK_INSERT": return KeyEvent.VK_INSERT;
    case "VK_INVERTED_EXCLAMATION_MARK": return KeyEvent.VK_INVERTED_EXCLAMATION_MARK;
    case "VK_J": return KeyEvent.VK_J;
    case "VK_K": return KeyEvent.VK_K;
    case "VK_KP_DOWN": return KeyEvent.VK_KP_DOWN;
    case "VK_KP_LEFT": return KeyEvent.VK_KP_LEFT;
    case "VK_KP_RIGHT": return KeyEvent.VK_KP_RIGHT;
    case "VK_KP_UP": return KeyEvent.VK_KP_UP;
    case "VK_L": return KeyEvent.VK_L;
    case "VK_LEFT": return KeyEvent.VK_LEFT;
    case "VK_LEFT_PARENTHESIS": return KeyEvent.VK_LEFT_PARENTHESIS;
    case "VK_LESS": return KeyEvent.VK_LESS;
    case "VK_M": return KeyEvent.VK_M;
    case "VK_META": return KeyEvent.VK_META;
    case "VK_MINUS": return KeyEvent.VK_MINUS;
    case "VK_MODECHANGE": return KeyEvent.VK_MODECHANGE;
    case "VK_MULTIPLY": return KeyEvent.VK_MULTIPLY;
    case "VK_N": return KeyEvent.VK_N;
    case "VK_NONCONVERT": return KeyEvent.VK_NONCONVERT;
    case "VK_NUM_LOCK": return KeyEvent.VK_NUM_LOCK;
    case "VK_NUMBER_SIGN": return KeyEvent.VK_NUMBER_SIGN;
    case "VK_NUMPAD0": return KeyEvent.VK_NUM_LOCK;
    case "VK_NUMPAD1": return KeyEvent.VK_NUMPAD1;
    case "VK_NUMPAD2": return KeyEvent.VK_NUMPAD2;
    case "VK_NUMPAD3": return KeyEvent.VK_NUMPAD3;
    case "VK_NUMPAD4": return KeyEvent.VK_NUMPAD4;
    case "VK_NUMPAD5": return KeyEvent.VK_NUMPAD5;
    case "VK_NUMPAD6": return KeyEvent.VK_NUMPAD6;
    case "VK_NUMPAD7": return KeyEvent.VK_NUMPAD7;
    case "VK_NUMPAD8": return KeyEvent.VK_NUMPAD8;
    case "VK_NUMPAD9": return KeyEvent.VK_NUMPAD9;
    case "VK_O": return KeyEvent.VK_O;
    case "VK_OPEN_BRACKET": return KeyEvent.VK_OPEN_BRACKET;
    case "VK_P": return KeyEvent.VK_P;
    case "VK_PAGE_DOWN": return KeyEvent.VK_PAGE_DOWN;
    case "VK_PAGE_UP": return KeyEvent.VK_PAGE_UP;
    case "VK_PASTE": return KeyEvent.VK_PASTE;
    case "VK_PAUSE": return KeyEvent.VK_PAUSE;
    case "VK_PERIOD": return KeyEvent.VK_PERIOD;
    case "VK_PLUS": return KeyEvent.VK_PLUS;
    case "VK_PREVIOUS_CANDIDATE": return KeyEvent.VK_PREVIOUS_CANDIDATE;
    case "VK_PRINTSCREEN": return KeyEvent.VK_PRINTSCREEN;
    case "VK_PROPS": return KeyEvent.VK_PROPS;
    case "VK_Q": return KeyEvent.VK_Q;
    case "VK_QUOTE": return KeyEvent.VK_QUOTE;
    case "VK_QUOTEDBL": return KeyEvent.VK_QUOTEDBL;
    case "VK_R": return KeyEvent.VK_R;
    case "VK_RIGHT": return KeyEvent.VK_RIGHT;
    case "VK_RIGHT_PARENTHESIS": return KeyEvent.VK_RIGHT_PARENTHESIS;
    case "VK_ROMAN_CHARACTERS": return KeyEvent.VK_ROMAN_CHARACTERS;
    case "VK_S": return KeyEvent.VK_S;
    case "VK_SCROLL_LOCK": return KeyEvent.VK_SCROLL_LOCK;
    case "VK_SEMICOLON": return KeyEvent.VK_SEMICOLON;
    case "VK_SEPARATER": return KeyEvent.VK_SEPARATER;
    case "VK_SEPARATOR": return KeyEvent.VK_SEPARATOR;
    case "VK_SHIFT": return KeyEvent.VK_SHIFT;
    case "VK_SLASH": return KeyEvent.VK_SLASH;
    case "VK_SPACE": return KeyEvent.VK_SPACE;
    case "VK_STOP": return KeyEvent.VK_STOP;
    case "VK_SUBTRACT": return KeyEvent.VK_SUBTRACT;
    case "VK_T": return KeyEvent.VK_T;
    case "VK_TAB": return KeyEvent.VK_TAB;
    case "VK_U": return KeyEvent.VK_U;
    case "VK_UNDEFINED": return KeyEvent.VK_UNDEFINED;
    case "VK_UNDERSCORE": return KeyEvent.VK_UNDERSCORE;
    case "VK_UNDO": return KeyEvent.VK_UNDO; 
    case "VK_UP": return KeyEvent.VK_UP;
    case "VK_V": return KeyEvent.VK_V;
    case "VK_W": return KeyEvent.VK_W;
    case "VK_WINDOWS": return KeyEvent.VK_WINDOWS;
    case "VK_X": return KeyEvent.VK_X;
    case "VK_Y": return KeyEvent.VK_Y;
    case "VK_Z": return KeyEvent.VK_Z;
    }
    throw new Exception("Unsupported Mnemonic:"+mne);
  }
  //
  private static String[] keys = { "<frame>", "<panel>", "<dialog>", "<tabbedpane>",
                                   "<button>", "<radiobutton>", "<togglebutton>", "<label>",
                                   "<textfield>", "<formattedtextfield>", "<passwordfield>",
                                   "<textarea>", "<textpane>", "<editorpane>", "<list>",
                                   "<combobox>", "<checkbox>", "progressbar>", "<table>",
                                   "<tree>", "<fxpanel>", "<menubar>", "<popupmenu>",
                                   "<toolbar>" };
}