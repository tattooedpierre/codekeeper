/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FontChooser.java
 *
 * Created on May 15, 2010, 2:12:35 PM
 */

package codekeeper;

import java.awt.Font;
import org.jdesktop.application.Action;

/**
 *
 * @author tattooedpierre
 */
public class FontChooser extends javax.swing.JDialog {

    public Font ChosenFont;

    public Font getChosenFont()
    {
        return ChosenFont;
    }

    public void setChosenFont(Font ChosenFont)
    {
        this.ChosenFont = ChosenFont;
    }

    /** Creates new form FontChooser */
    public FontChooser(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getRootPane().setDefaultButton(btnFontOk);
    }

    @Action
    public void AcceptChanges()
    {
        this.setChosenFont(this.jFontChooser1.getSelectedFont());
        this.setVisible(false);
    }

    @Action
    public void CancelChanges()
    {
        this.setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFontChooser1 = new say.swing.JFontChooser();
        jPanel1 = new javax.swing.JPanel();
        btnFontOk = new javax.swing.JButton();
        btnFontCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("Form"); // NOI18N
        setResizable(false);

        jFontChooser1.setName("jFontChooser1"); // NOI18N
        getContentPane().add(jFontChooser1, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(codekeeper.CodeKeeperApp.class).getContext().getActionMap(FontChooser.class, this);
        btnFontOk.setAction(actionMap.get("AcceptChanges")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(codekeeper.CodeKeeperApp.class).getContext().getResourceMap(FontChooser.class);
        btnFontOk.setText(resourceMap.getString("btnFontOk.text")); // NOI18N
        btnFontOk.setName("btnFontOk"); // NOI18N
        jPanel1.add(btnFontOk);

        btnFontCancel.setAction(actionMap.get("CancelChanges")); // NOI18N
        btnFontCancel.setText(resourceMap.getString("btnFontCancel.text")); // NOI18N
        btnFontCancel.setName("btnFontCancel"); // NOI18N
        jPanel1.add(btnFontCancel);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FontChooser dialog = new FontChooser(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFontCancel;
    private javax.swing.JButton btnFontOk;
    private say.swing.JFontChooser jFontChooser1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
