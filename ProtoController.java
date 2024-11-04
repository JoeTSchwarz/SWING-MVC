package joeapp.mvc;
//
import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.nio.file.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.JFileChooser;
import javax.swing.text.Utilities;
//
import javax.swing.text.*;
//
import joeapp.mvc.*;
// Joe T. Schwarz (C)
/**
Modelling with SWINGLoader Package
*/
public class ProtoController implements DocumentListener, ActionListener, MenuKeyListener {
  /**
  Predefined Constructor for Controller
  @param map HashMap with String as keys (defined in the model) and Object as values (J Components)
  */
  public ProtoController(HashMap<String, Object> map) {
    content = "";
    root = new ArrayList<>();
    loaded = new ArrayList<>();
    undoList = new ArrayList<>();
    redoList = new ArrayList<>();
    changed  = new ArrayList<>();
    frame = (JFrame) map.get("frame");
    area  = (JTextPane) map.get("area");
    tabbed = (JTabbedPane) map.get("tabbed");
    cyan   = new DefaultHighlighter.DefaultHighlightPainter(Color.cyan);
    if (area == null)  area = new JTextPane();
    area.setDocument(new ProtoDocument(Color.red, Color.blue, Color.magenta));
    setListeners(area); // Listeners
    //
    tabbed.addChangeListener(e -> {
      int idx = tabbed.getSelectedIndex();
      if (root.size() > idx) {
        dir = root.get(idx);
        redo = redoList.get(idx);
        undo = undoList.get(idx);
        fName = dir + tabbed.getTitleAt(idx);
      }
      area  = (JTextPane) (((((JScrollPane) tabbed.
                                            getComponentAt(idx)).
                                            getViewport()))).
                                            getView();
      content = area.getText();
    });
    HashMap<String, String> keys = new HashMap<>();
    try { // load templates
      String els[] = new String(SWING.loadFile(System.getProperty("user.dir"),
                                               "resources/templates.txt",
                                               ProtoController.class)
                                              ).
                     replace("\r", "").
                     split("\n");
      for (String s : els) { 
        keys.put(s.substring(1,s.indexOf(">")), s);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(frame, "Missing templates.txt file", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(0);
    }
    //
    JMenuItem iNew = (JMenuItem) map.get("New");
    iNew.addActionListener(this);
    JMenuItem iLoad = (JMenuItem) map.get("Load");
    iLoad.addMenuKeyListener(this);
    iLoad.addActionListener(this);
    JMenuItem iSave = (JMenuItem) map.get("Save");
    iSave.addActionListener(this);
    JMenuItem iFind = (JMenuItem) map.get("Find");
    iFind.addActionListener(this);
    JMenuItem iReplace = (JMenuItem) map.get("Replace");
    iReplace.addActionListener(this);
    //
    ((JButton) map.get("new")).addActionListener(e -> newFile());
    ((JButton) map.get("redo")).addActionListener(e -> {
      pop(redo, undo);
    });
    ((JButton) map.get("undo")).addActionListener(e -> {
      pop(undo, redo);
    });
    ((JButton) map.get("load")).addActionListener(e -> {
      load();
    });
    ((JButton) map.get("save")).addActionListener(e -> {
      stow(tabbed.getSelectedIndex());
    });
    ((JButton) map.get("search")).addActionListener(e -> {
      search();
    });
    ((JButton) map.get("replace")).addActionListener(e -> {
      replace();
    });
    ((JButton) map.get("check")).addActionListener(e -> {
      try {
        (new SWINGLoader(getText().getBytes(), dir)).load();
        JOptionPane.showConfirmDialog(frame, "Syntax is OK", "Acknowledge",
                                      JOptionPane.DEFAULT_OPTION);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(frame, "SyntaxProblem:"+ex.toString(), 
                                      "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    ((JButton) map.get("show")).addActionListener(e -> {
      show();
    });
    ((JButton) map.get("clear")).addActionListener(e -> {
      int idx = tabbed.getSelectedIndex();
      area = (JTextPane)((JScrollPane) tabbed.getComponentAt(idx)).getViewport().getView();
      if (idx == 0 && tabbed.getTabCount() < 2) {
        root.clear();
        loaded.clear();
        changed.clear();
        redoList.clear();
        undoList.clear();
        area.setText("");
        tabbed.setTitleAt(0, "New_Model");
        dir = System.getProperty("user.dir")+File.separator;
      } else {
        tabbed.remove(idx);
        if (loaded.size() < idx) {
          loaded.remove(idx);
          changed.remove(idx);
          redoList.remove(idx);
          undoList.remove(idx);
          dir = root.remove(idx);
        }
        idx = (idx > 0? idx:tabbed.getTabCount())-1;
        tabbed.setSelectedIndex(idx);
      }
    });
    @SuppressWarnings("unchecked")
    JComboBox<Object> combo = (JComboBox) map.get("cBox");
    combo.addActionListener(e -> {
      int p = tabbed.getSelectedIndex();
      area = (JTextPane)((JScrollPane) tabbed.getComponentAt(p)).getViewport().getView();
      area.requestFocusInWindow();
      p = area.getCaretPosition();
      String key = keys.get((String) combo.getSelectedItem())+System.lineSeparator();
      try {
        ((StyledDocument) area.getDocument()).insertString(p, key, null);
      } catch (Exception ex) { }
      area.setCaretPosition(p+key.length());
    });
  }
  private void newFile() {
    int idx = tabbed.getSelectedIndex();
    String T = tabbed.getTitleAt(idx);
    if ("New_Model".equals(T)) return;
    area = createArea(""); // append new Tab
    tabbed.addTab("New_Model", new JScrollPane(area));
    tabbed.setSelectedIndex(tabbed.getTabCount()-1);
    createStack();
  }
  private void show() {
    if (getText().length() == 0) {
      JOptionPane.showMessageDialog(frame, "Empty Model. NO show!", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    for (int i = 0, mx = changed.size(); i < mx; ++i) if (changed.get(i)) {
      if (JOptionPane.showConfirmDialog(frame,
          "To have the desired effect EDITED area must be saved. Saved?",
          "Question", JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) stow(i);
    }
    try { // SWINGLoader
      Object obj = (new SWINGLoader(content.getBytes(), dir)).load();
      if (obj instanceof JFrame) {
        ((JFrame)obj).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ((JFrame)obj).setVisible(true);
      } else if (obj instanceof JDialog) {
        ((JDialog)obj).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ((JDialog)obj).setVisible(true);
      } else if (obj instanceof JPanel) {
        JFrame jf = new JFrame("Show a JPanel");
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.setLocation(((JPanel)obj).getLocation());
        Dimension D = ((JPanel)obj).getSize();
        String W = JOptionPane.showInputDialog(frame, "JFrame Width for thisJPanel?",
                                                      ""+(int)D.getWidth());
        if (W == null || W.length() == 0) W = ""+(int)D.getWidth();
        String H = JOptionPane.showInputDialog(frame, "JFrame Height for thisJPanel?",
                                                      ""+(int)D.getHeight());
        if (H == null || H.length() == 0) H = ""+(int)D.getHeight();
        jf.setSize(Integer.parseInt(W), Integer.parseInt(H));
        jf.add((JPanel)obj);
        jf.setVisible(true);
      } else JOptionPane.showMessageDialog(frame, "JFrame or JDialog or JPanel is expected.", 
                                           "Error", JOptionPane.ERROR_MESSAGE);

    } catch (Exception ex) {
      //ex.printStackTrace();
      JOptionPane.showMessageDialog(frame, "Unable to show "+fName+"\nReason:"+ex.toString(), 
                                    "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  private void load() {
    JFileChooser jfc = new JFileChooser(dir != null? dir :
                                        System.getProperty("user.dir")+File.separator);
    if (jfc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
    fName = jfc.getSelectedFile().getAbsolutePath();
    anew = true;    
    if (fName.length() > 0) try {
      getDir(fName);
      File file = new File(fName);
      if (loaded.contains(fName)) {
        int idx = loaded.indexOf(fName);
        tabbed.setSelectedIndex(idx);
        content = new String(Files.readAllBytes(file.toPath()));
        area  = (JTextPane) (((((JScrollPane) tabbed.
                                              getComponentAt(idx)).
                                              getViewport()))).
                                              getView();
        area.setText(content);
        area.setCaretPosition(0);
        redo = undoList.get(idx);
        undo = undoList.get(idx);
        return;
      }
      int idx = tabbed.getSelectedIndex();
      String tit = tabbed.getTitleAt(idx);
      int e = fName.lastIndexOf(File.separator);
      content = new String(Files.readAllBytes(file.toPath()));
      if (loaded.size() > 0 && !tit.equals("New_Model")) {
        area = createArea(content);
        tabbed.addTab(fName.substring(e+1), new JScrollPane(area));
        idx = tabbed.getTabCount()-1;
      } else {
        if (tit.equals("New_Model")) {
          area  = (JTextPane) (((((JScrollPane) tabbed.
                                                getComponentAt(idx)).
                                                getViewport()))).
                                                getView();
        }
        tabbed.setTitleAt(idx, fName.substring(e+1));
        area.setText(content);
      }
      root.add(dir);
      loaded.add(fName);
      area.setCaretPosition(0);
      createStack();
      tabbed.setSelectedIndex(idx);
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(frame, "Unknown "+fName, 
                                    "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  private void replace() {
    String cont = getText();
    if (cont.length() == 0) return;
    String pat = JOptionPane.showInputDialog(frame, "Search for word");
    if (pat == null || pat.length() == 0) return;
    int pl = pat.length();
    String rep = JOptionPane.showInputDialog(frame, "Replaced by word");
    if (rep == null || rep.length() == 0) return;
    int p = cont.indexOf(pat);
    if (p < 0) {
      JOptionPane.showMessageDialog(frame, pat+" not found.", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    boolean mod = false;
    int rl = rep.length();
    String old = new String(cont);
    boolean full = JOptionPane.showConfirmDialog(frame,"Full word only?", "Question",
                         JOptionPane.YES_NO_OPTION,
                         JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    StringBuilder sb = new StringBuilder(cont);
    while (p >= 0){
      if (!full || isWord(p, pl, sb.toString())) {
        sb.replace(p, p+pl, rep);
        mod = true;
      }
      p = sb.indexOf(pat, p+rl);
    }
    if (mod) {
      ext = true;
      int idx = tabbed.getSelectedIndex();
      content = sb.toString();
      changed.set(idx, true);
      area.setText(content);
      undo.push(old);
      try {
        p = content.indexOf(rep);
        area.getHighlighter().removeAllHighlights();
        Highlighter hl = area.getHighlighter();
        while (p >= 0) {
          if (!full || isWord(p, rl, content)) hl.addHighlight(p, p+rl, cyan);
          p = content.indexOf(rep, p+rl);
        }
      } catch (Exception ex) { }
      ext = false;
    }
  }
  private void search() {
    String cont = getText();
    if (cont == null || cont.length() == 0) return;
    String pat = JOptionPane.showInputDialog(frame, "Search for word");
    if (pat == null || pat.length() == 0) return;
    int pl = pat.length();
    int p = cont.indexOf(pat);
    if (p < 0) {
      JOptionPane.showMessageDialog(frame, pat+" not found.", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    boolean full = JOptionPane.showConfirmDialog(frame,"Full word only?", "Question",
                         JOptionPane.YES_NO_OPTION,
                         JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    try {
      Highlighter hl = area.getHighlighter();
      area.getHighlighter().removeAllHighlights();
      while(p >= 0){
        if (!full || isWord(p, pl, cont)) hl.addHighlight(p, p+pl, cyan);
        p = cont.indexOf(pat, p+pl);    
      }
    } catch (Exception ex) { }
  }
  private void stow(int idx) {
    area  = (JTextPane) (((((JScrollPane) tabbed.
                                          getComponentAt(idx)).
                                          getViewport()))).
                                          getView();
    String cont = getText();
    String fN = loaded.get(idx);
    fN = JOptionPane.showInputDialog(frame, "Save to file:", fN);
    if (fN == null || fN.length() == 0) return;
    if ((new File(fN)).exists()) {
      if (JOptionPane.showConfirmDialog(frame,"Overwite?", "Question",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) return;
    }
    if (dir == null || !fN.equalsIgnoreCase(fName)) {
      getDir(fN);
      int e = fN.lastIndexOf("/");
      if (e < 0) e = fN.lastIndexOf(File.separator);
      tabbed.setTitleAt(idx, fN.substring(e+1));
      root.set(idx, dir);
      fName = fN;
    }
    changed.set(idx, false);     
    try {
      FileOutputStream fout = new FileOutputStream(fN, false);
      fout.write(cont.getBytes());
      fout.flush();
      fout.close();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(frame, "Unable to save to "+fN, 
                                    "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  private void pop(Stack<String> OUT, Stack<String> IN) {
    if (OUT == null || OUT.empty()) return;
    ext = true;
    IN.push(content);
    content = OUT.pop();
    area.setText(content);
    changed.set(tabbed.getSelectedIndex(), true);
    ext = false;
  }
  private void getDir(String fName) {
    fName = fName.replace("/", File.separator);
    File f = new File(fName);
    if (!f.exists()) dir = System.getProperty("user.dir");
    else {
      dir = f.getAbsolutePath();
      dir = dir.substring(0, dir.lastIndexOf(File.separator));
    }
  }
  private JTextPane createArea(String cont) {
    JTextPane jtp = new JTextPane(new ProtoDocument(Color.red, Color.blue, Color.magenta));
    jtp.setText(cont);
    setListeners(jtp);
    return jtp;
  }
  private void setListeners(JTextPane jtp) {
    jtp.setFont(new Font("Dialog", Font.PLAIN, 13));
    jtp.getDocument().addDocumentListener(this);
    jtp.addMouseListener(new CopyCutPaste(jtp));
  }
  private String getText( ) {
    try {
      return area.getDocument().getText(0, area.getDocument().getLength());
    } catch (Exception ex) { }
    return null;
  }
  // implementation of DocumentListener
  public void removeUpdate(DocumentEvent e) {
    setStack();
  }
  public void insertUpdate(DocumentEvent e) {
    setStack();
  }
  public void changedUpdate(DocumentEvent e) {
    setStack();
    anew = false;
  }
  private void setStack() {
    if (ext || anew || undo == null) return;
    int idx = tabbed.getSelectedIndex();
    if (changed.size() > 0) changed.set(idx, true);
    undo.push(content);
    content = getText();
    redo.push(content);
  }
  private void createStack() {
    undo = new Stack<>();
    redo = new Stack<>();
    undoList.add(undo);
    redoList.add(redo);
    changed.add(false);
  }
  // Implemetation of ActionListener
  public void actionPerformed(ActionEvent a) {
    String cmd = a.getActionCommand();
    if (cmd.equals("New")) newFile();
    else if (cmd.equals("Load")) load();
    else if (cmd.equals("Find")) search();
    else if (cmd.equals("Replace")) replace();
    else if (cmd.equals("Save")) stow(tabbed.getSelectedIndex());
  }
  // Implementation of MenuKeyListener
  public void menuKeyPressed(MenuKeyEvent e) { }
  public void menuKeyReleased(MenuKeyEvent e) { }
  public void menuKeyTyped(MenuKeyEvent e) {
    char key = (char)(e.getKeyChar() | 0x20);
    if (key == 'n') newFile();
    else if (key == 'l') load();
    else if (key == 'f') search();
    else if (key == 'r') replace();
    else if (key == 's') stow(tabbed.getSelectedIndex());
  }
  //
  private boolean isWord(int p, int le, String cont) {
    if (p > 0) {
      char b = cont.charAt(p-1);
      if ((b >= '0' && b <= '9' ||
           b >= '@' && b <= 'z' ||
           b >= 'A' && b <= 'Z')) return false;
    }
    if ((p+le) == cont.length()) return true;
    char b = cont.charAt(p+le);
    return !(b >= '0' && b <= '9' ||
             b >= '@' && b <= 'z' ||
             b >= 'A' && b <= 'Z');
  }
  //
  private JFrame frame;
  private JTextPane area;
  private JTabbedPane tabbed;
  private Stack<String> undo, redo;
  private String fName, dir, content;
  private ArrayList<Boolean> changed;
  private ArrayList<String> root, loaded;
  private Highlighter.HighlightPainter cyan;
  private boolean ext = false, anew = false;
  private ArrayList<Stack<String>> undoList, redoList;
}

