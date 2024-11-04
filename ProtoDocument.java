package joeapp.mvc;
//
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
// Joe T. Schwarz (C)
public class ProtoDocument extends DefaultStyledDocument {
  /**
  Constructor
  is the word starting at p with the length le a word?
  @param kColor Color for the Keyword
  @param mColor Color for the Reserved word
  @param oColor Color for the rest
  @return DefaultStyledDocument
  */
  public ProtoDocument(Color kColor, Color mColor, Color oColor) {
    super();
    StyleContext content = StyleContext.getDefaultStyleContext();
    AttributeSet as = content.getEmptySet();
    keyWord = content.addAttribute(as, StyleConstants.Foreground, kColor);
    mandatory = content.addAttribute(as, StyleConstants.Foreground, mColor);
    optional = content.addAttribute(as, StyleConstants.Foreground, oColor);
    plain = content.addAttribute(as, StyleConstants.Foreground, Color.black);
  }
  public void insertString (int offs, String S, AttributeSet att) throws BadLocationException {
    super.insertString(offs, S, att);
    int le = getLength(); // get the raw length
    String text = getText(0, le);
    int end = offs+S.length();
    int beg = offs;
    char cb, ce;
    while (beg > 0) {
      cb = text.charAt(beg);
      if (cb < '@' || cb > 'Z' && cb < 'a' || cb > 'z') break; else --beg;
    }
    if (end > le) end = le;
    else {
      while (end < le) {
        ce = text.charAt(end);
        if (ce < '@' || ce > 'Z' && ce < 'a' || ce > 'z') break; else ++end;
      }
      if (end < le) ++end;
    }
    // look for Mandatory, Keyword and Optionaö
    LOOP: for (int q, p = beg; beg < end; p = beg++) {
      cb = text.charAt(beg);
      if (cb >= '@' && cb <= 'Z' || cb >= 'a' && cb <= 'z') {
        for (int i = 0; i < keys.length; ++i) {
          le = keys[i].length();
          q  = beg + le;
          if (q < end) {
            cb = text.charAt(p);
            ce = text.charAt(q);
            if (text.substring(beg, q).equals(keys[i]) &&
               (cb < '@' || cb > 'Z' && cb < 'a' || cb > 'z') &&
               (ce < '@' || ce > 'Z' && ce < 'a' || ce > 'z')) {
              if (i < KEY) // mandatory word
                setCharacterAttributes(beg, le, mandatory, false);
              else if (i < OPT) // keyword
                setCharacterAttributes(beg, le, keyWord, false);
              else // optional word
                setCharacterAttributes(beg, le, optional, false);
              beg = q;
              continue LOOP;
            }
          }
        }
        for (le = beg; beg < end; ++beg) {
          cb = text.charAt(beg);
          if (cb < '@' || cb > 'Z' && cb < 'a' || cb > 'z') break;
        }
        setCharacterAttributes(le, beg-le, plain, false);
      }
    }
  }
  public void remove(int offs, int len) throws BadLocationException {
    super.remove(offs, len);
    String text = getText(0, getLength());
    int le = text.length();
    if (offs >= le) return;
    int beg = offs;
    int end = offs;
    while (beg > 0) {
      char c = text.charAt(beg);
      if (c < '@' || c > 'Z' && c < 'a' || c > 'z') {
        ++beg; // back to the last letter
        break;
      } else --beg;
    }
    while (end < le) {
      char c = text.charAt(end);
      if (c < '@' || c > 'Z' && c < 'a' || c > 'z') break; else ++end;
    }
    if (end < le) ++end;
    String pat = text.substring(beg, end);
    // look for Mandatory, Keyword and Optionaö
    for (int i = 0; i < keys.length; ++i) if (pat.equals(keys[i])) {
      if (i < KEY) // Mandatory
        setCharacterAttributes(beg, pat.length(), mandatory, false);
      else if (i < OPT) // keyword
        setCharacterAttributes(beg, pat.length(), keyWord, false);
      else // optional
        setCharacterAttributes(beg, pat.length(), optional, false);
      return;
    }
    setCharacterAttributes(beg, pat.length(), plain, false);
  }
  //
  int KEY = 5;  // starting of Keywords
  int OPT = 30; // starting of Optionals
  private AttributeSet keyWord, optional, mandatory, plain;
  private String[] keys =
                  {
                    "location", "name", "size", "tabs", "tabtext",
  
                    "frame", "panel", "label", "textfield", "button",
                    "combobox", "textarea", "checkbox", "radiobutton", "togglebutton", 
                    "passwordfield","textpane", "formattedtextfield", "dialog", "tabbedpane",
                    "editorpane", "list", "progressbar", "table", "tree",
                    "slider", "toolbar", "fxpanel", "menubar", "popupmenu",
                        
                    "text", "items", "load", "color", "textColor",
                    "icon", "file", "close", "resize", "title",
                    "bgimage", "column", "row", "font", "fontSize",
                    "fontType", "place", "policy", "paintTicks", "paintLabels",
                    "selicon", "content", "opaque", "minmax", "orientation"                    
                  };
}