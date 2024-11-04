package joeapp.mvc;
//
import java.io.*;
import java.awt.*;
import java.nio.*;
import java.net.*;
import java.util.*;
import java.net.URL;
import javax.swing.*;
import java.nio.file.Files;
import java.lang.reflect.*;
import javax.imageio.ImageIO;
import javafx.embed.swing.JFXPanel;
import java.awt.image.BufferedImage;
import javax.swing.tree.DefaultMutableTreeNode;
// Joe T. Schwarz (C)
/**
@author Joe T. Schwarz (C)
The JAVA MVC SWINGLoader. 4 Constructors:
<br>The first and the third/fourth are used to load and to instantiate the J components.
<br>The second works like the first/the second AND instantiate the Controller.
<br>In case of First/Second the load() method is used to work with the Constroller as
    following:
<br>SWINGLoader sl = new SWINGLoader(myModel.txt);
<br>JFrame jf = (JFrame) sl.load(); // get the supporting pane
<br>MyController mc = new MyController(sl.getComponentMap());
<br>...
Note: Model file should be given with absolute path. The path is considered as the common
      path for all subsequent
<br>files. If only filename is given, current working directory is taken as the common path.
    URL for Icon
<br>is allowed.
<br>Customized SWING component must have an empty Contructor beside the other Constructors
*/
@SuppressWarnings("unchecked")
public class SWINGLoader {
  /**
  Constructor. Contruct a View Model without Controller
  @param model String, the fileName of the model in plain text, or the content of the
               model file
  @exception Exception thrown by Java
  */
  public SWINGLoader(String model) throws Exception {
    loadFile(model);
  }
  /**
  Constructor. Contruct a View Model with Caller or Controller class
  @param model String, the fileName of the model in plain text, or the content of the
               model file
  @param cls Class<?> of caller
  @exception Exception thrown by Java
  */
  public SWINGLoader(String model, Class<?> cls) throws Exception {
    this.cls = cls;
    loadFile(model);
  }
  /**
  Constructor.  Contruct a View Model without Controller
  @param buf byte array of the model
  @param dir String, the dependency directory
  @exception Exception thrown by Java
  */
  public SWINGLoader(byte[] buf, String dir) throws Exception {
    oMap = new HashMap<>();
    list = SWING.purify(new String(buf));
    this.dir = dir.replace("/", File.separator);
    if (dir.endsWith(File.separator)) 
      dir = dir.substring(0, dir.length()-File.separator.length());
  }
  /**
  Constructor with controller that will be instantiated by SWINGLoader
  @param model String, the fileName of the model in plain text
  @param controller String, the class name of the implemented Controller
  @exception Exception thrown by Java
  */
  public SWINGLoader(String model, String controller) throws Exception {
    this.controller = controller;
    loadFile(model);
  }
  /**
  Constructor with controller and parameters that will be instantiated
  by SWINGLoader
  @param model String, the fileName of the model in plain text
  @param controller String, the class name of the implemented Controller
  @param parms String array for the Controller parameters
  @exception Exception thrown by Java
  */
  public SWINGLoader(String model, String controller, String[] parms) throws Exception {
    this.controller = controller;
    this.parms = parms;
    loadFile(model);
  }
  //
  private void loadFile(String model) throws Exception {
    oMap = new HashMap<>();
    File fi = new File(model);
    if (fi.exists()) {
      dir = fi.getAbsolutePath();
      dir = dir.substring(0, dir.lastIndexOf(File.separator));
    } else dir = System.getProperty("user.dir");
    // get Controller Class
    if (controller != null) cls = Class.forName(controller); 
    byte[] buf = SWING.loadFile(dir, model, cls);
    list = SWING.purify(buf != null? new String(buf):model);
  }
  /**
  nameList
  @return ArrayList containing the names (or keys) of J components in the Model
  */
  public ArrayList<String> nameList() {
    return new ArrayList<String>(oMap.keySet());
  }
  /**
  getComponentMap -the map contains all J components given in the Model
  @return HashMap of String (as key) and Object (as value)
  */
  public HashMap<String, Object> getComponentMap() {
    return oMap;
  }
  /**
  load the model and instantiate the given J components
  @return Object of the supporting pane (JFrame, JPanel or JDialog)
  @exception Exception thrown by Java
  */
  public Object load( ) throws Exception {
    Object frame = null;
    String s = list.get(0);
    StringBuilder sb = new StringBuilder(s);
    //
    if (s.indexOf("<frame>") >= 0) {
      frame = jframe(new JFrame(), s, 1);
    } else if (s.indexOf("<panel>") >= 0) {
      frame = jpanel(new JPanel(), s, 1);
    } else if (s.indexOf("<dialog>") >= 0) {
      frame = jdialog(new JDialog(), s, 1);
    } else try { // customized JFrame, JPanel or JDialog
      Object obj = Class.forName(s.substring(s.indexOf("<")+1, s.indexOf(">"))).
                         getConstructors()[0].newInstance( ); // default
      if (obj instanceof JFrame) frame = jframe((JFrame)obj, s, 1);
      else if (obj instanceof JPanel) frame = jpanel((JPanel)obj, s, 1);
      else if (obj instanceof JDialog) frame = jdialog((JDialog)obj, s, 1);
      else throw new Exception("Unknown:"+s);
    } catch (Exception ex) {
      throw new Exception("Model must start with <frame> or <panel> or <dialog>:\n"+
                          "Found:"+s);
    }
    if (controller != null) try {
      if (parms == null) { // instantiate Controller
        Constructor<?> cons = cls.getConstructor(HashMap.class);
        cons.newInstance(oMap);
      } else {
        Constructor<?> cons = cls.getConstructor(HashMap.class, String[].class);
        cons.newInstance(oMap, parms);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new Exception("Unable to instantiate "+controller);
    }
    return frame;
  }
  //
  private Object load(Object frame, int idx) throws Exception {
    for (int mx = list.size(); idx < mx; ++idx) {
      Object obj = getObject(list.get(idx), idx, true);
      if (obj != null && !(obj instanceof JDialog)) {
        if (frame instanceof JFrame) ((JFrame)frame).add((JComponent)obj);
        else if (frame instanceof JPanel) ((JPanel)frame).add((JComponent)obj);
        else ((JDialog)frame).add((JComponent)obj);
      }
    }
    return frame;
  }
  //
  private void put(String s, Object obj) throws Exception {
    String name = SWING.getItem("name=", s);
    if (oMap.containsKey(name)) throw new Exception("Duplicate Name:"+name+" @"+s);
    oMap.put(name, obj);
    name = SWING.getItem("run=", s); // is it a Runnable customized?
    if ("start".equalsIgnoreCase(name)) (new Thread((Runnable)obj)).start();
  }
  //<fxpanel>name=anyName size=w,h location=x,y </fxpanel>
  private Object jfxpanel(JFXPanel jfx, String s) throws Exception {
    put(s, jfx);
    SWING.getBounds(jfx, s);
    return jfx;
  }
  //<toolbar>name=anyName text=anyText orientation size=w,h location=x,y </toolbar>
  private Object toolbar(JToolBar tb, String s) throws Exception {
    String item = SWING.getItem("orientation=", s);
    int d = SwingConstants.HORIZONTAL; // default
    if ("vertical".equalsIgnoreCase(item)) d = SwingConstants.VERTICAL;
    tb.setOrientation(d);
    item = SWING.getItem("text=", s);
    if (item != null) tb.setName(item);
    put(s, tb);
    SWING.getBounds(tb, s);
    return tb;
  }
  //<editorpane>name=anyName content=plain/html text=file.txt size=w,h location=x,y </editorpane>
  //<textpane>name=anyName content=plain/html text=file.txt size=w,h location=x,y </textpane>
  //optional: color=anyColor and textColor=anyColor for back and foreground
  private Object textpane(JEditorPane tp, String s) throws Exception {
    put(s, tp);
    String item = SWING.getItem("content=", s);
    if (item != null) {
      if (!item.equals("plain") && !item.equals("html"))
        throw new Exception("Invalid content="+item+" q@line:"+s);
      tp.setContentType("text/"+item);
    }
    item = SWING.getItem("color=", s);
    if (item != null) tp.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) tp.setForeground(SWING.getColor(item));
    
    item = SWING.getItem("text=", s);
    if (item != null) {
      if (item.indexOf("://") > 0) tp.setPage(item);
      else tp.setText(new String(SWING.loadFile(dir, item, cls)));
    }
    JScrollPane jsp = new JScrollPane(tp);
    SWING.getBounds(jsp, s);
    jsp.setAutoscrolls(true);
    return jsp;
  }
  //<popupmenu>name=anyName text=anyText size=w,h location=x,y file=items.txt</popupmenu>
  private Object popup(JPopupMenu pu, String s) throws Exception {
    int[] si = SWING.getValues("size=", s);
    if (si == null) throw new Exception("Expected size=... @line:"+s);
    int[] lo = SWING.getValues("location=", s);
    // populate the JPopupMenu with JMenu and JMenuItem/JCheckBoxMenuItem
    String X = SWING.getItem("text=", s);
    if (X != null)  pu.setLabel(X);
    put(s, pu);
    String[] M = SWING.getArray("file=", s, dir, cls);
    if (M == null) throw new Exception("Expected file=... @line:"+s);
    for (int i = 0; i < M.length; ++i) {
      String[] m = M[i].split(",");
      for (int l = 0; l < m.length; l += 2) {
        X = m[l].substring(m[l].indexOf(":")+1);
        ImageIcon img = SWING.getIcon(m[l+1], dir, cls);
        if (m[l].startsWith("item:")) {
          JMenuItem jmi = img == null? new JMenuItem(X):new JMenuItem(X, img);
          if (oMap.containsKey(X)) throw new Exception("Duplicate Name:"+X+" @"+s);
          oMap.put(X, jmi);
          pu.add(jmi);
        } else if (m[l].startsWith("checkbox:")) {
          JCheckBoxMenuItem jcb = new JCheckBoxMenuItem(X);
          if (img != null) jcb.setIcon(img);
          if (oMap.containsKey(X)) throw new Exception("Duplicate Name:"+X+" @"+s);
          oMap.put(X, jcb);
          pu.add(jcb);
        } else throw new Exception("Invalid "+m[l]+" @line:"+M[i]);
      }
    }
    if (lo == null) pu.setSize(si[0], si[1]);
    else pu.setBounds(lo[0], lo[1], si[0], si[1]);
    return pu;
  }
  //<menubar>name=anyName size=w,h location=x,y menu=menu.txt</menubar>
  private Object menubar(JMenuBar mb, String s) throws Exception {
    // populate the JMenuBar with JMenu and JMenuItem/JCheckBoxMenuItem
    put(s, mb);
    String[] M = SWING.getArray("file=", s, dir, cls);
    if (M == null) throw new Exception("Expected file=... @line:"+s);
    for (int i = 0; i < M.length; ++i) {
      String[] m = M[i].split(",");
      String mName = m[0].substring(m[0].indexOf(":")+1);
      JMenu jm = new JMenu(mName); 
      if (oMap.containsKey(mName)) throw new Exception("Duplicate Name:"+mName+" @"+s);
      oMap.put(mName, jm); // set Menu Name
      jm.setMnemonic(SWING.mnemonic(m[1].trim()));
      for (int l = 2; l < m.length; l += 2) {
        ImageIcon img = SWING.getIcon(m[l+1], dir, cls);
        int mne = img == null? 0:SWING.mnemonic(m[l+1].trim());
        String X = m[l].trim().substring(m[l].indexOf(":")+1);
        if (m[l].trim().startsWith("item:")) {
          JMenuItem jmi = img == null? new JMenuItem(X, mne):new JMenuItem(X, img);
          if (oMap.containsKey(X)) throw new Exception("Duplicate Name:"+X+" @"+s);
          oMap.put(X, jmi);
          jm.add(jmi);
        } else if (m[l].trim().startsWith("checkbox:")) {
          JCheckBoxMenuItem jcb = new JCheckBoxMenuItem(X);
          if (img == null) jcb.setMnemonic(mne);
          else jcb.setIcon(img);
          if (oMap.containsKey(X)) throw new Exception("Duplicate Name:"+X+" @"+s);
          oMap.put(X, jcb);
          jm.add(jcb);
        } else throw new Exception("Invalid "+m[l]+" @line:"+M[i]);
      }
      mb.add(jm);
    }
    SWING.getBounds(mb, s);
    return mb;
  }
  //<table>name=anyName size=w,h location=x,y roco=n,m table=table.txt</table>
  //optional: color=anyColor and textColor=anyColor for back and foreground
  private Object table(JTable tb, String s) throws Exception {
    if (tb == null) {
      int[] mm = SWING.getValues("roco=", s);
      if (s.indexOf("table=") < 0) {
        if (tb == null) {
          if (mm == null) tb = new JTable();
          else tb = new JTable(mm[0], mm[1]);
        }
      } else {
        boolean rend = false;
        String[] T = SWING.getArray("table=", s,  dir, cls);
        Object[] cols = (Object[])T[0].split(",");
        Object[][] rows = new Object[T.length-1][cols.length];
        for (int i = 1; i < T.length; ++i) if (SWING.isImage(T[i])) {
          rend = true;
          break;
        }
        for (int i = 0, j = 1; i < rows.length; ++i, ++j) {
          String[] X = T[j].split(",");
          rows[i] = new Object[X.length];
          for (int a = 0; a < X.length; ++a) {
            if (rend) rows[i][a] = new ICell(X[a], dir, cls);
            else rows[i][a] = (Object)X[a];
          }
        }
        if (tb == null) tb = new JTable(rows, cols);
        if (rend) tb.setDefaultRenderer(Object.class, new TableRenderer());
      }
    }
    put(s, tb);
    
    String item = SWING.getItem("color=", s);
    if (item != null) tb.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) tb.setForeground(SWING.getColor(item));
    
    JScrollPane jsp = new JScrollPane(tb);
    SWING.getBounds(jsp, s);
    jsp.setAutoscrolls(true);
    return jsp;
  }
  //<slider>name=anyName size=w,h location=x,y minmax=a,b 
  //             orient=vertical/horizontal, paintTicks=true/false,
  //             paintLabels=true/false</slider>
  private Object slider(JSlider js, String s) throws Exception {
    int[] mm = SWING.getValues("minmax=", s);
    if (mm != null) {
      js.setMinimum(mm[0]);
      js.setMaximum(mm[1]);
    }
    String item = SWING.getItem("orient=", s);
    if (item != null) {
      if (item.charAt(0) == 'h') js.setOrientation(SwingConstants.HORIZONTAL);
      else js.setOrientation(SwingConstants.VERTICAL);
    }
    item = SWING.getItem("paintTick=", s);
    if (item != null && "true".equalsIgnoreCase(item)) js.setPaintTicks(true);
    item = SWING.getItem("paintLabel=", s);
    if (item != null && "true".equalsIgnoreCase(item)) js.setPaintLabels(true);
    put(s, js);
    SWING.getBounds(js, s);
    return js;
  }
  //<progressbar>name=anyName size=w,h location=x,y minmax=a,b 
  //             orient=vertical/horizontal</progressbar>
  private Object progress(JProgressBar pb, String s) throws Exception {
    int[] mm = SWING.getValues("minmax=", s);
    if (mm != null) {
      pb.setMinimum(mm[0]);
      pb.setMaximum(mm[1]);
    }
    String item = SWING.getItem("orient=", s);
    if (item != null) {
      if (item.charAt(0) == 'h') pb.setOrientation(SwingConstants.HORIZONTAL);
      else pb.setOrientation(SwingConstants.VERTICAL);
    }
    put(s, pb);
    SWING.getBounds(pb, s);
    pb.setStringPainted(true);
    return pb;
  }
  //<checkbox>name="name" text="text" icon=file selicon=file disicon=file
  //           selected=true size=w,h location=x,y</checkbox>
  // optional: color=anyColor and textColor=anyColor for back and foreground
  private Object checkbox(JCheckBox cb, String s) throws Exception {
    put(s, cb);
    
    String item = SWING.getItem("selected=", s);
    if (item != null && item.equals("true")) cb.setSelected(true);
    
    item = SWING.getItem("color=", s);
    if (item != null) cb.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) cb.setForeground(SWING.getColor(item));

    item = SWING.getItem("text=", s);
    if (item != null)cb.setText(item);
    
    item = SWING.getItem("icon=", s);
    if (item != null) cb.setIcon(SWING.getIcon(item, dir, cls));

    item = SWING.getItem("selicon=", s);
    if (item != null) cb.setIcon(SWING.getIcon(item, dir, cls));
    
    item = SWING.getItem("disicon=", s);
    if (item != null) cb.setIcon(SWING.getIcon(item, dir, cls));
    
    SWING.getBounds(cb, s);
    return cb;
  }
  //<tree>name=anyName nodes=nodes.txt size=w.h location=x,y </tree>
  // optional: color=anyColor and textColor=anyColor for back and foreground
  private Object tree(JTree jt, String s) throws Exception {
    if (jt == null) {
      int ib = s.indexOf("nodes=");
      if (ib < 0) {
        if (jt == null) jt = new JTree();
      } else {
        String[] nodes = SWING.getArray("nodes=", s, dir, cls);
        ib = nodes[0].indexOf(":");
        if (ib < 0) {
          if (jt == null) jt = new JTree(nodes);
        } else { // create JTree from the list      
          String R = nodes[0].substring(0, ib);
          if (R.length() == 0) throw new Exception("Invalid JTree. Root is missing @line:"+s);
          DefaultMutableTreeNode root = new DefaultMutableTreeNode(R);
          //
          boolean rend = false;
          for (int i = 0; i < nodes.length; ++i) {
            nodes[i] = nodes[i].replace(": ", ":").replace(" :", ":");
            if (!rend && SWING.isImage(nodes[i])) rend = true;
          }
          String[] N = nodes[0].replace(R+":", "").replace("+:", "").replace("-:", "").split(",");
          if (nodes.length > 1) {
            for (int i = 1, j = 0; i < nodes.length; ++i) if (nodes[i].length() > 0) {
              DefaultMutableTreeNode node = new DefaultMutableTreeNode(N[j]);
              String[] L = nodes[i].replace(N[j]+":", "").replace(":", ",").split(",");
              if (L[0].charAt(0) == '+') for (int a = 0; a < L.length; ++a) getChildren(node, nodes, L, i+1, rend);
              else if (L[0].charAt(0) != '-') throw new Exception("Missing Level Indicator +/- at:"+N[j]);
              else for (int a = 1; a < L.length; ++a) {
                if (rend) node.add(new DefaultMutableTreeNode(new ICell(L[a], dir, cls)));
                else node.add(new DefaultMutableTreeNode(L[a]));
              }              
              root.add(node); 
              ++j;
            }
          } else for (int i = 1; i < N.length; ++i) {
            if (rend) root.add(new DefaultMutableTreeNode(new ICell(N[i], dir, cls)));
            else root.add(new DefaultMutableTreeNode(N[i]));
          }
          if (jt == null) jt = new JTree(root);
          if (rend) jt.setCellRenderer(new TreeRenderer());
        }
      }
    }
    put(s, jt);
    
    String item = SWING.getItem("color=", s);
    if (item != null) jt.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) jt.setForeground(SWING.getColor(item));
    
    JScrollPane jsp = new JScrollPane(jt);
    SWING.getBounds(jsp, s);
    jsp.setAutoscrolls(true);
    return jsp;
  }
  // recursive for JTree
  private void getChildren(DefaultMutableTreeNode node, String[] N,
                                String[] L, int i, boolean rend) throws Exception {
    for (int a = 1; a < L.length; ++a) {
      DefaultMutableTreeNode child = new DefaultMutableTreeNode(L[a]);
      for (int j = i; j < N.length; ++j) if (N[j].startsWith(L[a])) {
        String[] C = N[j].replace(L[a]+":", "").replace(":", ",").split(",");
        if (C[0].charAt(0) == '+') getChildren(child, N, C, j+1, rend);
        else if (C[0].charAt(0) != '-') throw new Exception("Missing Level Indicator +/- at:"+N[j]);
        else for (int b = 1; b < C.length; ++b) {
          if (rend) child.add(new DefaultMutableTreeNode(new ICell(C[b], dir, cls)));
          else child.add(new DefaultMutableTreeNode(C[b]));
        }
        node.add(child);
        N[j] = "";
        ++i;
      }
    }
  }
  //<combobox>name="name" items="string1",..,"string2" size=w.h location=x,y</combobox>
  //<combobox>name="name" items=list.txt size=w.h location=x,y</combobox>
  //Optional: font="Arial" fontType=PLAIN fontSize=40
  //optional: color=anyColor and textColor=anyColor for back and foreground
  private Object combo(JComboBox<Object> cb, String s) throws Exception {
    Object[] obj = SWING.asObject(s, dir, cls);
    if (obj != null) {
      for (Object o:obj) cb.addItem(o);
      if (obj[0] instanceof ICell) cb.setRenderer(new ComboRenderer());
    }
    put(s, cb);
    
    String item = SWING.getItem("color=", s);
    if (item != null) cb.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) cb.setForeground(SWING.getColor(item));
    
    Font font = SWING.getFont(s);
    if (font != null) cb.setFont(font);
    
    SWING.getBounds(cb, s);
    return cb;
  }
  //<list>name="name" items="string1",..,"string2" size=w.h location=x,y</list>
  //<list>name="name" items=file.txt size=w.h location=x,y</list>
  // optional: color=anyColor and textColor=anyColor for back and foreground
  private Object jlist(JList<Object> jl, String s) throws Exception {
    Object[] obj = SWING.asObject(s, dir, cls);
    if (obj != null) {
      jl.setListData(obj);
      if (obj[0] instanceof ICell)
        jl.setCellRenderer(new ListRenderer());
    }
    put(s, jl);    
    String item = SWING.getItem("color=", s);
    if (item != null) jl.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) jl.setForeground(SWING.getColor(item));
    JScrollPane jsp = new JScrollPane(jl);
    SWING.getBounds(jsp, s);
    jsp.setAutoscrolls(true);
    return jsp;
  } 
  //<textarea>name="name" text="xyz" size=w,h location=x,x row=n 
  //          column=n  scroll=false edit=false</textarea>
  // optional: color=anyColor and textColor=anyColor for back and foreground
  private Object textarea(JTextArea ta, String s) throws Exception {
    put(s, ta);
    
    String item = SWING.getItem("row=", s);
    if (item != null) ta.setRows(Integer.parseInt(item));
   
    item = SWING.getItem("edit=", s);
    if (item != null && item.equals("false")) ta.setEditable(false);
   
    item = SWING.getItem("column=", s);
    if (item != null) ta.setColumns(Integer.parseInt(item));
    
    item = SWING.getItem("color=", s);
    if (item != null) ta.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) ta.setForeground(SWING.getColor(item));
    
    item = SWING.getItem("text=", s);
    if (item != null) ta.setText(item);
    
    Font font = SWING.getFont(s);
    if (font != null) ta.setFont(font);
    
    JScrollPane jsp = new JScrollPane(ta);
    SWING.getBounds(jsp, s);
    jsp.setAutoscrolls(true);
    return jsp;
  }
  //<textfield>name="name" text="xyz" size=w,h location=x,x column=n</textfield>
  //<passwordfield>name=name text="xyz" size=w,h location=x,x column=n <passwordfield>
  //<formattedtextfield>name=name text="xyz" size=w,h location=x,y column=n</formattedtextfield>
  // optional: color=anyColor and textColor=anyColor for back and foreground
  private Object textfield(JTextField tf, String s) throws Exception {
    put(s, tf);
    
    String item = SWING.getItem("column=", s);
    if (item != null)tf.setColumns(Integer.parseInt(item));
    
    item = SWING.getItem("color=", s);
    if (item != null) tf.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) tf.setForeground(SWING.getColor(item));
    
    item = SWING.getItem("text=", s);
    if (item != null)tf.setText(item);
    
    Font font = SWING.getFont(s);
    if (font != null) tf.setFont(font);
    
    SWING.getBounds(tf, s);
    return tf;
  } 
  //<label>name="name" text="xyz" size=w,h location=x,x icon=fName opaque=true</label>
  // optional: color=anyColor and textColor=anyColor for back and foreground
  private Object label(JLabel lab, String s) throws Exception {
    put(s, lab);
    
    String item = SWING.getItem("opaque=", s);
    if (item != null && item.equals("true")) lab.setOpaque(true);
    
    item = SWING.getItem("color=", s);
    if (item != null) lab.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) lab.setForeground(SWING.getColor(item));
    
    item = SWING.getItem("text=", s);
    if (item != null) lab.setText(item);

    item = SWING.getItem("icon=", s);
    if (item != null) lab.setIcon(SWING.getIcon(item, dir, cls));
    
    Font font = SWING.getFont(s);
    if (font != null) lab.setFont(font);
    
    SWING.getBounds(lab, s);
    return lab;
  }
  //<button>name="name" text="xyz" size=w,h location=x,x icon=fName</button>
  //<radiobutton>name=anyName text="xyz" size=w,h location=x,y icon=fName</radiobutton>
  //<togglebutton>name="name" text="xyz" size=w,h location=x,x icon=fName</togglebutton>
  //Optional: font="Arial" fontType=PLAIN fontSize=40
  //optional: color=anyColor and textColor=anyColor for back and foreground
  private Object button(AbstractButton but, String s) throws Exception {
    put(s, but);
        
    String item = SWING.getItem("color=", s);
    if (item != null) but.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) but.setForeground(SWING.getColor(item));
    
    Font font = SWING.getFont(s);
    if (font != null) but.setFont(font);
    
    item = SWING.getItem("text=", s);
    if (item != null) but.setText(item);
    
    item = SWING.getItem("icon=", s);
    if (item != null) but.setIcon(SWING.getIcon(item, dir, cls));
    SWING.getBounds(but, s);
    return but;
  }
  //<tabbedpane>name=name tabs=panel1.txt,panel2.txt,...  size=w,h location=x,y
  //            place=top/bottom/right/left policy=scroll/wrap</tabbedpane>
  // optional: color=anyColor and textColor=anyColor for back and foreground
  //           place, policy
  private Object tabpane(JTabbedPane tab, String s, int idx) throws Exception {
    String item = SWING.getItem("place=", s);
    if (item != null) {
      if ("top".equalsIgnoreCase(item)) tab.setTabPlacement(JTabbedPane.TOP);
      else if ("left".equalsIgnoreCase(item)) tab.setTabPlacement(JTabbedPane.LEFT);
      else if ("right".equalsIgnoreCase(item)) tab.setTabPlacement(JTabbedPane.RIGHT);
      else if ("bottom".equalsIgnoreCase(item)) tab.setTabPlacement(JTabbedPane.BOTTOM);
    }
    item = SWING.getItem("policy=", s);
    if (item != null) {
      if ("wrap".equalsIgnoreCase(item)) tab.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
      else if ("scroll".equalsIgnoreCase(item)) tab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
    put(s, tab);
    
    item = SWING.getItem("color=", s);
    if (item != null) tab.setBackground(SWING.getColor(item));
        
    item = SWING.getItem("textColor=", s);
    if (item != null) tab.setForeground(SWING.getColor(item));
    
    SWING.getBounds(tab, s);
    String[] tabs = SWING.getArray("tabs=", s);
    String[] text = SWING.getArray("tabtext=", s);
    if (tabs != null) {
      if (tabs.length != text.length) throw new Exception("Inbalanced Tabs and TabTexts @line:"+s);
      for (int i = 0; i < tabs.length; ++i) {
        String cont = new String(SWING.loadFile(dir, tabs[i], cls));
        if (cont.indexOf("<panel>") >= 0) {
          SWINGLoader ml = new SWINGLoader(cont.getBytes(), dir);
          JPanel jp = (JPanel) ml.load();
          ArrayList<String> keys = ml.nameList();
          HashMap<String, Object> map = ml.getComponentMap();
          for (String key : keys) {
            if (oMap.containsKey(key))
              throw new Exception("Duplicate Name:"+key+" @"+s);
            oMap.put(key, map.get(key));
          }
          tab.addTab(text[i], jp);
        } else if (SWING.isKeyword(s)) {
          tab.addTab(text[i], (JComponent)getObject(cont, idx, true));
        } else {
          tab.addTab(text[i], (JComponent)checkObj(cont, idx, true));
        }
      }
    }
    return tab;
  }
  //<dialog>name=name title="anyTitle" owner=ownerName load=panel.txt size=w,h location=x,y</dialog>
  // owner: jframe name
  private Object jdialog(JDialog jd, String s, int idx) throws Exception {
    put(s, jd);
    jd.setLayout(null);
    
    int[] si = SWING.getValues("size=", s);
    int[] lo = SWING.getValues("location=", s);
    
    jd.setSize(si[0], si[1]);
    if (lo != null) jd.setLocation(lo[0], lo[1]);
    
    String name = SWING.getItem("close=", s);
    if (name == null || name.equals("true"))
         jd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    else jd.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    
    name = SWING.getItem("title=", s);
    if (name != null) jd.setTitle(name);

    name = SWING.getItem("bgimage=", s);
    if (name != null) jd.setContentPane(new JLabel(SWING.getIcon(name, dir, cls)));
    
    name = SWING.getItem("load=", s);
    if (name == null) return load(jd, idx);
    // load panel for dialog
    name = SWING.toFile(dir, name);
    SWINGLoader ml = new SWINGLoader(name, cls);
    jd.add((JPanel) ml.load());
    ArrayList<String> keys = ml.nameList();
    HashMap<String, Object> map = ml.getComponentMap();
    for (String key : keys) {
      if (oMap.containsKey(key)) throw new Exception("Duplicate Name:"+key+" @"+s);
      oMap.put(key, map.get(key));
    }
    return jd;
  }
  //<panel>load:modelFile size=w,h location=x,y</panel>
  private Object loadPanel(String s) throws Exception {
    String file = SWING.getItem("file=", s);
    if (file == null) throw new Exception("Invalid here:\""+s+"\"");
    file = SWING.toFile(dir, file);
    SWINGLoader ml = new SWINGLoader(file, cls);
    JPanel jp = (JPanel) ml.load();
    ArrayList<String> keys = ml.nameList();
    HashMap<String, Object> map = ml.getComponentMap();
    for (String key : keys) {
      if (oMap.containsKey(key)) throw new Exception("Duplicate Name:"+key+" @"+s);
      oMap.put(key, map.get(key));
    }
    put(s, jp);    
    SWING.getBounds(jp, s);
    return jp;
  }
  //<panel>name=name size=w,h location=x,y</panel>
  //optinal: alignX, alignY
  private Object jpanel(JPanel jp, String s, int idx) throws Exception {
    put(s, jp);
    jp.setLayout(null);    
    SWING.getBounds(jp, s);
    return load(jp, idx);    
  }
  //<frame> name=anyName title="anyTittle" width=nnn height=nnnn
  //        bgimage=imgName location=a,b resize=false close=false</frame>
  private Object jframe(JFrame jf, String s, int idx) throws Exception {
    put(s, jf);
    
    String item = SWING.getItem("title=", s);
    if (item != null) jf.setTitle(item);

    item = SWING.getItem("resize=", s);
    if (item != null && "false".equals(item)) jf.setResizable(false);

    item = SWING.getItem("close=", s);
    if (item != null && "true".equals(item)) jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    item = SWING.getItem("bgimage=", s);
    if (item != null) jf.setContentPane(new JLabel(SWING.getIcon(item, dir, cls)));

    SWING.getBounds(jf, s);
    jf.setLayout(null);
    return load(jf, idx);    
  }
  private Object getObject(String s, int idx, boolean checked) throws Exception {
    switch (s.substring(0, s.indexOf(">")+1)) {
    case "<panel>":
      return loadPanel(s);
    case "<dialog>":
      return jdialog(new JDialog(), s, idx);
    case "<list>":
      return jlist(new JList<Object>(), s);
    case "<fxpanel>":
      return jfxpanel(new JFXPanel(), s);
    case "<button>":
      return button(new JButton(), s);
    case "<radiobutton>":
      return button(new JRadioButton(), s);
    case "<togglebutton>":
      return button(new JToggleButton(), s);
    case "<tabbedpane>":
      return tabpane(new JTabbedPane(), s, idx);
    case "<label>": 
      return label(new JLabel(), s);
    case "<menubar>":
      return menubar(new JMenuBar(), s);
    case "<table>": 
      return table(null, s);
    case "<tree>": 
      return tree(null, s);
    case "<textfield>":
      return textfield(new JTextField(), s);
    case "<passwordfield>":
      return textfield(new JPasswordField(), s);
    case "<formattedtextfield>":
      return textfield(new JFormattedTextField(), s);
    case "<textarea>": 
      return textarea(new JTextArea(), s);
    case "<textpane>":
      return textpane(new JTextPane(), s);
    case "<editorpane>":
      return textpane(new JEditorPane(), s);
    case "<combobox>": 
      return combo(new JComboBox<Object>(), s);
    case "<checkbox>":
      return checkbox(new JCheckBox(), s);
    case "<slide>":
      return slider(new JSlider(), s);
    case "<progressbar>":
      return progress(new JProgressBar(), s);
    case "<popupmenu>":
      return popup(new JPopupMenu(), s);
    case "<toolbar>":
       return toolbar(new JToolBar(), s);
    default:
      if (checked) return checkObj(s, idx, false);
    }
    throw new Exception("Invalid line:"+s);
  }
  // Customized SWING objects
  private Object checkObj(String s, int idx, boolean loaded) throws Exception {
    String com = s.substring(0, s.indexOf(">")+1);
    String clsName = com.substring(1, com.length()-1);
    String name = com.substring(1, com.length()-1).trim();
    try { // load the given class name and instantiate it...
      Object obj = Class.forName(clsName, true, this.getClass().
                   getClassLoader()).
                   getDeclaredConstructor().
                   newInstance();
      if (obj instanceof JPanel) {
        ((JPanel)obj).setLayout(null);       
        SWING.getBounds(((JPanel)obj), s);
        put(s, obj);
        return obj;
      } 
      if (obj instanceof JDialog) return jdialog((JDialog)obj, s, idx);
      else if (obj instanceof JList) return jlist((JList)obj, s);
      else if (obj instanceof JFXPanel) return jfxpanel((JFXPanel)obj, s);
      else if (obj instanceof AbstractButton) return button((AbstractButton)obj, s);
      else if (obj instanceof JTabbedPane) return tabpane((JTabbedPane)obj, s, idx);
      else if (obj instanceof JLabel) return label((JLabel)obj, s);
      else if (obj instanceof JMenuBar) return menubar((JMenuBar)obj, s);
      else if (obj instanceof JTable) return table((JTable)obj, s);
      else if (obj instanceof JTree) return tree((JTree)obj, s);
      else if (obj instanceof JTextField) return textfield((JTextField)obj, s);
      else if (obj instanceof JTextArea) return textarea((JTextArea)obj, s);
      else if (obj instanceof JTextPane) return textpane((JTextPane)obj, s);
      else if (obj instanceof JComboBox) return combo((JComboBox)obj, s);
      else if (obj instanceof JCheckBox) return checkbox((JCheckBox)obj, s);
      else if (obj instanceof JSlider) return slider((JSlider)obj, s);
      else if (obj instanceof JProgressBar) return progress((JProgressBar)obj, s);
      else if (obj instanceof JPopupMenu) return popup((JPopupMenu)obj, s);
      else if (obj instanceof JToolBar) return toolbar((JToolBar)obj, s);
    } catch (ClassNotFoundException ce) {
      if (loaded) return getObject(s, idx, false);
    } catch (Exception ex) { }
    throw new Exception("Unknown Object:"+name+" @line:"+s);
  }
  //
  private Class<?> cls;
  private String[] parms;
  private ArrayList<String> list;
  private String dir, controller;
  private HashMap<String, Object> oMap;
}
