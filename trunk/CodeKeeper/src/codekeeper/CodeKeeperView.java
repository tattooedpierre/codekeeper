/*
 * CodeKeeperView.java
 */
package codekeeper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.MutableTreeNode;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

/**
 * The application's main frame.
 */
public class CodeKeeperView extends FrameView implements TreeSelectionListener {

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private JTree tree;
    private RSyntaxTextArea codeEditor;
    private RTextScrollPane codePane;
    private RichTextEditor richTextEditor;
    private Preferences prefs;

    public CodeKeeperView(SingleFrameApplication app)
    {
        super(app);
        System.setProperty("swing.aatext", "true");
        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++)
        {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName))
                {
                    if (!busyIconTimer.isRunning())
                    {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName))
                {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName))
                {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName))
                {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        Setup();

        tree.addTreeSelectionListener(this);
        MouseListener ml = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e)
            {

                if (e.getClickCount() == 2)
                {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                    if (node == null)
                    {
                        return;
                    }

                    Object nodeInfo = node.getUserObject();
                    if (node.isLeaf() && node.getUserObject().getClass().getName().equals("codekeeper.SnippetObject"))
                    {
                        SnippetObject snippet = (SnippetObject) nodeInfo;
                        TreeDoubleClick(snippet);
                    } else if (node.getAllowsChildren() && !node.isRoot())
                    {
                        String newName = JOptionPane.showInputDialog(getRootPane(), "Enter a folder name", node.toString());
                        if (newName != null && newName.length() > 0)
                        {
                            node.setUserObject(newName);
                            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                            model.nodeChanged(node);
                        }
                    }
                }
            }
        };
        tree.addMouseListener(ml);

        codeEditor.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                UpdateCurrentCodeObject();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                UpdateCurrentCodeObject();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                UpdateCurrentCodeObject();
            }
        });
        richTextEditor.richTextPane.getStyledDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                UpdateCurrentCodeObject();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                UpdateCurrentCodeObject();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                UpdateCurrentCodeObject();
            }
        });
    }

    private void Setup()
    {
        // Get our preferences
        prefs = Preferences.userNodeForPackage(this.getClass());
        mainPanel.setSize(prefs.getInt("WIDTH", 640), prefs.getInt("HEIGHT", 480));
        mainPanel.validate();
        
        // Populate the tree
        PopulateTree();

        // Setup the code editor pane
        codeEditor = new RSyntaxTextArea();
        codePane = new RTextScrollPane(codeEditor);
        richTextEditor = new RichTextEditor();

        // Set the code highlighter as the current editor
        splitPaneMain.setRightComponent(codePane);

        // Set the default fonts for Mac and Windows.. because Windows might
        // only have crappy Courier..
        if (System.getProperty("os.name").contains("Windows"))
        {
            Constants.setFont(codeEditor, new Font("Courier New", Font.PLAIN, 14));
        } else
        {
            Constants.setFont(codeEditor, new Font("Monaco", Font.PLAIN, 14));
        }

        // .. but try and select the users chosen font if we can...
        codeEditor.setFont(new Font(prefs.get("EDITOR_FONT", null), Font.PLAIN, prefs.getInt("EDITOR_FONT_SIZE", 14)));

        // Turn on text anti-aliasing.. because we like that.
        codeEditor.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");

        // This ensures we generate a new database file if it didnt exist.
        Constants.SaveObjectToXml(tree.getModel(), Constants.DatabaseFilename);
    }

    private void PopulateTree()
    {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Code Keeper");
        CreateTreeNodes(top);

        if (new File(Constants.DatabaseFilename).exists())
        {
            tree = new JTree(Constants.ReadXml(Constants.DatabaseFilename));
        } else
        {
            tree = new JTree(top);
            JOptionPane.showMessageDialog(this.mainPanel,
                    "The file " + Constants.DatabaseFilename + " could not be found in " + Constants.Newline
                    + "the application directory. Using default tree.",
                    "Database File Not Found",
                    JOptionPane.WARNING_MESSAGE);

        }

        JScrollPane treeView = new JScrollPane(tree);
        splitPaneMain.setLeftComponent(treeView);
    }

    private void AddNodeToTree(SnippetObject snippet)
    {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

        // This way ensures the model is updated correctly, the UI is notified
        // and we cleanly update the JTree without having to force a repaint.
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(snippet);
        newNode.setAllowsChildren(false); // disallow child nodes since we're a snippet and not a folder..

        MutableTreeNode newLocation;
        if (tree.getSelectionPath() != null)
        {
            newLocation = (MutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        } else
        {
            newLocation = (MutableTreeNode) model.getRoot();
        }

        if (!newLocation.getAllowsChildren())
        {
            newLocation = (MutableTreeNode) newLocation.getParent();
        }

        newNode.setAllowsChildren(false);
        model.insertNodeInto(newNode, newLocation, 0);
        model.nodeChanged(newNode);
        tree.setSelectionPath(new TreePath(newNode.getPath()));

        SaveAllSnippets();
        LoadCodeIntoEditor(snippet.SnippetType, snippet.getData());
    }

    private void CreateTreeNodes(DefaultMutableTreeNode top)
    {
        ArrayList<DefaultMutableTreeNode> category = new ArrayList<DefaultMutableTreeNode>();
        ArrayList<DefaultMutableTreeNode> items = new ArrayList<DefaultMutableTreeNode>();

        for (SnippetCategory c : Constants.DefaultCategoryList)
        {
            category.add(new DefaultMutableTreeNode(c));
        }

        items.add(new DefaultMutableTreeNode(
                new SnippetObject(
                "CSharp Test Item",
                Constants.SnippetTypes.CodeCSharp,
                new Date(),
                "private static void TestClass"
                + "{"
                + "  string t = 'Test'"
                + "}")));
        items.add(new DefaultMutableTreeNode(
                new SnippetObject("Text Test Item", Constants.SnippetTypes.TextMisc, new Date(), "Data data data")));
        items.add(new DefaultMutableTreeNode(
                new SnippetObject("SQL Test Item", Constants.SnippetTypes.SQLMisc, new Date(), "SELECT * FROM test_table WHERE test = '17'")));
        items.add(new DefaultMutableTreeNode(
                new SnippetObject("HTML Test Item", Constants.SnippetTypes.CodeHTML, new Date(), "body head href etc.")));

        for (DefaultMutableTreeNode currentCategory : category)
        {
            Object categoryInfo = currentCategory.getUserObject();
            SnippetCategory sc = (SnippetCategory) categoryInfo;

            for (DefaultMutableTreeNode currentItem : items)
            {
                Object itemInfo = currentItem.getUserObject();
                SnippetObject s = (SnippetObject) itemInfo;

                if (sc.SnippetCategoryType.equals(s.SnippetType))
                {
                    currentCategory.add(currentItem);
                }
            }
            top.add(currentCategory);
        }

    }

    private void LoadCodeIntoEditor(Constants.SnippetTypes snippetType, String code)
    {
        if (snippetType.equals(Constants.SnippetTypes.TextMisc))
        {
            // TODO: Fix this so it actually saves the RTF....
            splitPaneMain.setRightComponent(richTextEditor);
            richTextEditor.richTextPane.setText(code);
        } else
        {
            splitPaneMain.setRightComponent(codePane);
            codeEditor.setText(code);
            codeEditor.requestFocus();
        }

        switch (snippetType)
        {
            case CodeJavaScript:
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                break;

            case CodeCSharp:
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSHARP);
                break;

            case CodeJava:
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                break;

            case CodeDelphi:
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_DELPHI);
                break;

            case SQLMisc:
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
                break;

            case TextMisc:
            {
                //codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                break;
            }

            case CodeCSS:
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
                break;

            case CodeHTML:
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
                break;

            case CodeXML:
                codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
                break;
        }
    }

    private void UpdateCurrentCodeObject()
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null)
        {
            Object nodeInfo = node.getUserObject();
            if (node.isLeaf() && node.getUserObject().getClass().getName().equals("codekeeper.SnippetObject"))
            {
                SnippetObject snippet = (SnippetObject) nodeInfo;

                if (snippet.SnippetType.equals(Constants.SnippetTypes.TextMisc))
                    snippet.Data = richTextEditor.richTextPane.getText();
                else
                    snippet.Data = codeEditor.getText();
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node == null)
        {
            return;
        }

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf() && node.getUserObject().getClass().getName().equals("codekeeper.SnippetObject"))
        {
            SnippetObject snippet = (SnippetObject) nodeInfo;
            codeEditor.setEditable(true);
            LoadCodeIntoEditor(snippet.SnippetType, snippet.Data);
        } else
        {
            codeEditor.setText("");
            codeEditor.setEditable(false);
        }
    }

    private void TreeDoubleClick(SnippetObject snippet)
    {
        SnipDataReturn r = ShowSnippetEditor(snippet);

        if (r.Result == JOptionPane.OK_OPTION)
        {
            SnippetObject newSnippet = snippet;
            newSnippet.SnippetName = r.SnipName;

            for (SnippetCategory s : Constants.DefaultCategoryList)
            {
                if (s.SnippetCategoryName.equals(r.CategoryName))
                {
                    newSnippet.SnippetType = s.SnippetCategoryType;
                    break;
                }
            }
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            currentNode.setUserObject(newSnippet);
            model.nodeChanged(currentNode);

            LoadCodeIntoEditor(snippet.SnippetType, snippet.getData());
        }
    }

    private SnipDataReturn ShowSnippetEditor(SnippetObject snippet)
    {
        SnipDataReturn r = new SnipDataReturn();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel lblCats = new JLabel("Snippet name & category:");
        JComboBox cboCategories = new JComboBox(PopulateCategories());

        for (SnippetCategory s : Constants.DefaultCategoryList)
        {
            if (s.SnippetCategoryType == snippet.SnippetType)
            {
                cboCategories.setSelectedItem(s.SnippetCategoryName);
            }
        }

        JTextField txtBox = new JTextField(snippet.SnippetName);
        txtBox.setHorizontalAlignment((int) JTextField.CENTER_ALIGNMENT);
        txtBox.setSize(150, 20);
        txtBox.validate();
        
        panel.add(lblCats, BorderLayout.PAGE_START);
        panel.add(cboCategories, BorderLayout.CENTER);
        panel.add(txtBox, BorderLayout.SOUTH);

        r.Result = JOptionPane.showConfirmDialog(this.getRootPane(), panel, "Edit Snippet", JOptionPane.OK_CANCEL_OPTION);
        r.SnipName = txtBox.getText();
        r.CategoryName = cboCategories.getSelectedItem().toString();

        return r;
    }

    private Object[] PopulateCategories()
    {
        Collections.sort(Constants.DefaultCategoryList);
        ArrayList<String> categoryList = new ArrayList<String>();

        for (SnippetCategory s : Constants.DefaultCategoryList)
        {
            categoryList.add(s.SnippetCategoryName);
        }

        return categoryList.toArray();
    }

    public DefaultMutableTreeNode FindObjectInTree(Object obj, DefaultMutableTreeNode rootNode)
    {
        DefaultMutableTreeNode result = null;
        Enumeration i = rootNode.depthFirstEnumeration(); // could also do breadth first
        while (result == null && i.hasMoreElements())
        {
            DefaultMutableTreeNode next = (DefaultMutableTreeNode) i.nextElement();
            if (next.toString().equals(obj))
            {
                result = next;
                break;
            }
        }
        return result;
    }

    @Action
    public void DeleteNodeFromTree()
    {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        TreePath[] paths = tree.getSelectionPaths();
        int result = -1;
        if (paths != null)
        {
            for (TreePath path : paths)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

                if (node.getParent() != null && node.isRoot() == false)
                {
                    if (node.getChildCount() > 0)
                    {
                        result = JOptionPane.showConfirmDialog(
                                this.getRootPane(),
                                "This folder contains " + node.getChildCount() + " snippet(s)." + Constants.Newline + "Are you sure you want to delete them all?",
                                "Delete Child Nodes",
                                JOptionPane.YES_NO_OPTION);
                    }

                    if (result != 1)
                    {
                        model.removeNodeFromParent(node);
                    }
                } else
                {
                    JOptionPane.showMessageDialog(
                            this.getRootPane(),
                            "The node you are trying to delete is the root tree node!",
                            "Cannot Delete Root",
                            JOptionPane.WARNING_MESSAGE);
                }
                model.nodeChanged(node);
            }
            SaveAllSnippets();
        }
    }

    @Action
    public void showAboutBox()
    {
        if (aboutBox == null)
        {
            JFrame mainFrame = CodeKeeperApp.getApplication().getMainFrame();
            aboutBox = new CodeKeeperAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        CodeKeeperApp.getApplication().show(aboutBox);
    }

    @Action
    public void CreateNewSnippet()
    {
        SnippetObject s = new SnippetObject();
        SnipDataReturn r = ShowSnippetEditor(s);

        if (r.Result == JOptionPane.OK_OPTION)
        {
            s.SnippetName = r.SnipName;

            for (SnippetCategory sc : Constants.DefaultCategoryList)
            {
                if (sc.SnippetCategoryName.equals(r.CategoryName))
                {
                    s.SnippetType = sc.SnippetCategoryType;
                    break;
                }
            }

            AddNodeToTree(s);
            codeEditor.requestFocus();
        }
    }

    @Action
    public void SaveAllSnippets()
    {
        tree.updateUI();
        String saveMsg = Constants.SaveObjectToXml(tree.getModel(), Constants.DatabaseFilename);
        //JOptionPane.showMessageDialog(this.mainPanel, saveMsg);
    }

    @Action
    public void ChangeEditorFont()
    {
        FontChooser fc = null;

        if (fc == null)
        {
            JFrame mainFrame = CodeKeeperApp.getApplication().getMainFrame();
            fc = new FontChooser(mainFrame, true);
            fc.setLocationRelativeTo(mainFrame);
            CodeKeeperApp.getApplication().show(fc);
        }

        if (fc.getChosenFont() != null)
        {
            codeEditor.setFont(fc.getChosenFont());
            prefs.putInt("EDITOR_FONT_SIZE", fc.getChosenFont().getSize());
            prefs.put("EDITOR_FONT", fc.getChosenFont().getFamily());
        }
    }

    @Action
    public void AddFolderToTree()
    {
        String result = JOptionPane.showInputDialog(this.getRootPane(), "Enter New Folder Name", "New Folder", JOptionPane.QUESTION_MESSAGE);

        if (result != null && result.length() > 0)
        {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode resultNode = FindObjectInTree(result, (DefaultMutableTreeNode) model.getRoot());

            if (resultNode == null)
            {
                // This way ensures the model is updated correctly, the UI is notified
                // and we cleanly update the JTree without having to force a repaint.
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(result);
                newNode.setAllowsChildren(true); // disallow child nodes since we're a snippet and not a folder..

                model.insertNodeInto(newNode, (MutableTreeNode) model.getRoot(), 0);
                SaveAllSnippets();
                tree.setSelectionPath(new TreePath(newNode.getPath()));
            }
        } else
        {
            JOptionPane.showMessageDialog(this.getRootPane(), "That folder name is invalid.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        splitPaneMain = new javax.swing.JSplitPane();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnuDelete = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuSave = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        mnuFont = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                mainPanelComponentResized(evt);
            }
        });
        mainPanel.setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(codekeeper.CodeKeeperApp.class).getContext().getActionMap(CodeKeeperView.class, this);
        jButton1.setAction(actionMap.get("AddFolderToTree")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(codekeeper.CodeKeeperApp.class).getContext().getResourceMap(CodeKeeperView.class);
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setName("jButton1"); // NOI18N
        jToolBar1.add(jButton1);

        jButton3.setAction(actionMap.get("CreateNewSnippet")); // NOI18N
        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setName("jButton3"); // NOI18N
        jToolBar1.add(jButton3);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        jButton2.setAction(actionMap.get("DeleteNodeFromTree")); // NOI18N
        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setName("jButton2"); // NOI18N
        jToolBar1.add(jButton2);

        mainPanel.add(jToolBar1, java.awt.BorderLayout.NORTH);

        splitPaneMain.setDividerLocation(175);
        splitPaneMain.setName("splitPaneMain"); // NOI18N
        mainPanel.add(splitPaneMain, java.awt.BorderLayout.CENTER);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setAction(actionMap.get("DeleteNodeFromTree")); // NOI18N
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem1.setAction(actionMap.get("CreateNewSnippet")); // NOI18N
        jMenuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    jMenuItem1.setText(resourceMap.getString("mnuNewSnippet.text")); // NOI18N
    jMenuItem1.setToolTipText(resourceMap.getString("mnuNewSnippet.toolTipText")); // NOI18N
    jMenuItem1.setName("mnuNewSnippet"); // NOI18N
    fileMenu.add(jMenuItem1);

    mnuDelete.setAction(actionMap.get("DeleteNodeFromTree")); // NOI18N
    mnuDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
mnuDelete.setText(resourceMap.getString("mnuDelete.text")); // NOI18N
mnuDelete.setName("mnuDelete"); // NOI18N
fileMenu.add(mnuDelete);

jSeparator2.setName("jSeparator2"); // NOI18N
fileMenu.add(jSeparator2);

mnuSave.setAction(actionMap.get("SaveAllSnippets")); // NOI18N
mnuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    mnuSave.setText(resourceMap.getString("mnuSave.text")); // NOI18N
    mnuSave.setName("mnuSave"); // NOI18N
    fileMenu.add(mnuSave);

    exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
    exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
exitMenuItem.setName("exitMenuItem"); // NOI18N
fileMenu.add(exitMenuItem);

menuBar.add(fileMenu);

helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
helpMenu.setName("helpMenu"); // NOI18N

mnuFont.setAction(actionMap.get("ChangeEditorFont")); // NOI18N
mnuFont.setText(resourceMap.getString("mnuFont.text")); // NOI18N
mnuFont.setName("mnuFont"); // NOI18N
helpMenu.add(mnuFont);

aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
aboutMenuItem.setName("aboutMenuItem"); // NOI18N
helpMenu.add(aboutMenuItem);

menuBar.add(helpMenu);

statusPanel.setName("statusPanel"); // NOI18N

statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

statusMessageLabel.setName("statusMessageLabel"); // NOI18N

statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

progressBar.setName("progressBar"); // NOI18N

org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
statusPanel.setLayout(statusPanelLayout);
statusPanelLayout.setHorizontalGroup(
    statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
    .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
    .add(statusPanelLayout.createSequentialGroup()
        .addContainerGap()
        .add(statusMessageLabel)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 529, Short.MAX_VALUE)
        .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(statusAnimationLabel)
        .addContainerGap())
    );
    statusPanelLayout.setVerticalGroup(
        statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(statusPanelLayout.createSequentialGroup()
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(statusMessageLabel)
                .add(statusAnimationLabel)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(3, 3, 3))
    );

    setComponent(mainPanel);
    setMenuBar(menuBar);
    setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void mainPanelComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_mainPanelComponentResized
    {//GEN-HEADEREND:event_mainPanelComponentResized
        Dimension size = evt.getComponent().getSize();
        prefs.putInt("HEIGHT", size.height);
        prefs.putInt("WIDTH", size.width);
    }//GEN-LAST:event_mainPanelComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mnuDelete;
    private javax.swing.JMenuItem mnuFont;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSplitPane splitPaneMain;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
}
