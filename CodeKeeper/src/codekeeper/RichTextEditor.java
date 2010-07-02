/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RichTextEditor.java
 *
 * Created on May 19, 2010, 9:40:05 PM
 */
package codekeeper;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.jdesktop.application.Action;

/**
 *
 * @author tattooedpierre
 */
public class RichTextEditor extends javax.swing.JPanel {

    public StyledDocument doc;
    public SimpleAttributeSet simpleAttribs;
    public Style currentStyle;
    public JTextPane richTextPane;

    /** Creates new form RichTextEditor */
    public RichTextEditor()
    {
        initComponents();
        doc = jTextPaneMain.getStyledDocument();
        simpleAttribs = new SimpleAttributeSet();
        richTextPane = this.jTextPaneMain;
    }

    @Action
    public void SetBold()
    {
        if (btnBold.isSelected())
        {
            simpleAttribs.removeAttribute(StyleConstants.CharacterConstants.Bold);
            btnBold.setSelected(Boolean.FALSE);
        } else
        {
            simpleAttribs.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
            btnBold.setSelected(Boolean.TRUE);
        }

        jTextPaneMain.setCharacterAttributes(simpleAttribs, true);
    }

    @Action
    public void SetItalics()
    {
        if (btnItalic.isSelected())
        {
            simpleAttribs.removeAttribute(StyleConstants.CharacterConstants.Italic);
            btnItalic.setSelected(Boolean.FALSE);
        } else
        {
            simpleAttribs.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
            btnItalic.setSelected(Boolean.TRUE);
        }

        jTextPaneMain.setCharacterAttributes(simpleAttribs, true);
    }

    @Action
    public void SetUnderline()
    {
        if (btnUnderline.isSelected())
        {
            simpleAttribs.removeAttribute(StyleConstants.CharacterConstants.Underline);
            btnUnderline.setSelected(Boolean.FALSE);
        } else
        {
            simpleAttribs.addAttribute(StyleConstants.CharacterConstants.Underline, Boolean.TRUE);
            btnUnderline.setSelected(Boolean.TRUE);
        }

        jTextPaneMain.setCharacterAttributes(simpleAttribs, true);
    }

    public void SetText(String text) throws BadLocationException
    {
        richTextPane.setText(text);
    }

    public void ClearText() throws BadLocationException
    {
        richTextPane.setText("");
    }

        /** This method is called from within the constructor to
         * initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrpBulletStyle = new javax.swing.ButtonGroup();
        jToolBarMain = new javax.swing.JToolBar();
        btnBold = new javax.swing.JButton();
        btnItalic = new javax.swing.JButton();
        btnUnderline = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPaneText = new javax.swing.JScrollPane();
        jTextPaneMain = new javax.swing.JTextPane();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        jToolBarMain.setRollover(true);
        jToolBarMain.setName("jToolBarMain"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(codekeeper.CodeKeeperApp.class).getContext().getActionMap(RichTextEditor.class, this);
        btnBold.setAction(actionMap.get("SetBold")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(codekeeper.CodeKeeperApp.class).getContext().getResourceMap(RichTextEditor.class);
        btnBold.setText(resourceMap.getString("btnBold.text")); // NOI18N
        btnBold.setFocusable(false);
        btnBold.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBold.setName("btnBold"); // NOI18N
        btnBold.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMain.add(btnBold);

        btnItalic.setAction(actionMap.get("SetItalics")); // NOI18N
        btnItalic.setText(resourceMap.getString("btnItalic.text")); // NOI18N
        btnItalic.setFocusable(false);
        btnItalic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnItalic.setName("btnItalic"); // NOI18N
        btnItalic.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMain.add(btnItalic);

        btnUnderline.setAction(actionMap.get("SetUnderline")); // NOI18N
        btnUnderline.setText(resourceMap.getString("btnUnderline.text")); // NOI18N
        btnUnderline.setFocusable(false);
        btnUnderline.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUnderline.setName("btnUnderline"); // NOI18N
        btnUnderline.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMain.add(btnUnderline);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBarMain.add(jSeparator1);

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        btnGrpBulletStyle.add(jButton4);
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMain.add(jButton4);

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        btnGrpBulletStyle.add(jButton5);
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMain.add(jButton5);

        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        btnGrpBulletStyle.add(jButton6);
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMain.add(jButton6);

        add(jToolBarMain, java.awt.BorderLayout.PAGE_START);

        jScrollPaneText.setName("jScrollPaneText"); // NOI18N

        jTextPaneMain.setName("jTextPaneMain"); // NOI18N
        jScrollPaneText.setViewportView(jTextPaneMain);

        add(jScrollPaneText, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBold;
    private javax.swing.ButtonGroup btnGrpBulletStyle;
    private javax.swing.JButton btnItalic;
    private javax.swing.JButton btnUnderline;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JScrollPane jScrollPaneText;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTextPane jTextPaneMain;
    private javax.swing.JToolBar jToolBarMain;
    // End of variables declaration//GEN-END:variables
    }
