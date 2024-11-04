package joeapp.mvc;
//
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;
// Joe T. Schwarz (C)
/**
CopyAndPaste for J Components such as JTextField, JTextArea, etc.
*/
public class CopyCutPaste implements MouseListener {
  /**
   Constructor A pop-up menu "Copy/Cut/Paste" with the click of the right mouse-pad
   @param jcomp any Jcomponent. Example JTextArea, JtextField, JTextPane, etc.
  */
  public CopyCutPaste(JTextComponent jcomp) {
    this.jcomp = jcomp;
  }
  /**
  self-explained
  @param e MouseEvent
  */
  public void mousePressed(MouseEvent e){
    if (e.isPopupTrigger()) popup(e);
  }
  /**
  self-explained
  @param e MouseEvent
  */
  public void mouseReleased(MouseEvent e){
    if (e.isPopupTrigger())popup(e);
  }
 
  private void popup(MouseEvent e){
    JPopupMenu menu = new JPopupMenu();
    JMenuItem copy = new JMenuItem(jcomp.getActionMap().get(DefaultEditorKit.copyAction));
    copy.setAccelerator(KeyStroke.getKeyStroke((int)'C', InputEvent.CTRL_DOWN_MASK));
    copy.setText("Copy");
    menu.add(copy);
 
    JMenuItem cut = new JMenuItem(jcomp.getActionMap().get(DefaultEditorKit.cutAction));
    cut.setAccelerator(KeyStroke.getKeyStroke((int)'X', InputEvent.CTRL_DOWN_MASK));
    cut.setText("Cut");
    menu.add(cut);
 
    JMenuItem paste = new JMenuItem(jcomp.getActionMap().get(DefaultEditorKit.pasteAction));
    paste.setAccelerator(KeyStroke.getKeyStroke((int)'V', InputEvent.CTRL_DOWN_MASK));
    paste.setText("Paste");
    menu.add(paste);
    menu.show(e.getComponent(), e.getX(), e.getY());
  }
  // Implementation of MouseListener
  public void mouseClicked(MouseEvent me) {
    Highlighter hl = jcomp.getHighlighter();
    Highlighter.Highlight[] hls = hl.getHighlights();
    try { // remove the highlight at this click
      int caretPos = jcomp.getCaretPosition();
      int beg = Utilities.getWordStart(jcomp, caretPos);
      int end = Utilities.getWordEnd(jcomp, caretPos);
      String word = null;
      try {
        word = jcomp.getDocument().getText(beg, end - beg);
      } catch (Exception ex) { }
      if (word == null) return;
      for (int i = 0, b, e; i < hls.length; ++i) {
        b = hls[i].getStartOffset();
        e = hls[i].getEndOffset();
        if (beg <= b && e >= end ) {
          hl.removeHighlight(hls[i]);
          return;
        }
      }
    } catch (Exception ex) { }
  }  
  public void mouseEntered(MouseEvent me) { }  
  public void mouseExited(MouseEvent me) { }  
  private JTextComponent jcomp;
}
