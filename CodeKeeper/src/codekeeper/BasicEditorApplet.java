/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package codekeeper;

import java.awt.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

/**
 *
 * @author tattooedpierre
 */
/*      BasicEditorApplet.java
*       @author Charles Bell
*       @version Oct 23, 2002
*/


public class BasicEditorApplet extends JApplet{
    private JTextPane editor;

    public void init(){

        try{
            JPanel buttonPanel = new JPanel();

            editor = new JTextPane();
            editor.setContentType("text/html");

            JButton cutButton = new JButton();
            Action cutAction = new DefaultEditorKit.CutAction();
            cutAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getDocumentBase(), "cut.gif")));
            cutButton.setAction(cutAction);
            cutButton.setText("");

            JButton copyButton = new JButton();
            Action copyAction = new DefaultEditorKit.CopyAction();
            copyAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getCodeBase(), "copy.gif")));
            copyButton.setAction(copyAction);
            copyButton.setText("");

            JButton pasteButton = new JButton();
            Action pasteAction = new DefaultEditorKit.PasteAction();
            pasteAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getCodeBase(), "paste.gif")));
            pasteButton.setAction(pasteAction);
            pasteButton.setText("");

            JButton boldButton = new JButton();
            Action boldAction = new StyledEditorKit.BoldAction();
            boldAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getCodeBase(), "bold.gif")));
            boldButton.setAction(boldAction);
            boldButton.setText("");

            JButton italicButton = new JButton();
            Action italicAction = new StyledEditorKit.ItalicAction();
            italicAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getCodeBase(), "italic.gif")));
            italicButton.setAction(italicAction);
            italicButton.setText("");

            JButton underLineButton = new JButton();
            Action underlineAction = new StyledEditorKit.UnderlineAction();
            underlineAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getCodeBase(), "underline.gif")));
            underLineButton.setAction(underlineAction);
            underLineButton.setText("");

            JButton alignLeftButton = new JButton();
            Action alignLeftAction = new StyledEditorKit.AlignmentAction("Left Align",StyleConstants.ALIGN_LEFT);
            alignLeftAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getCodeBase(), "left.gif")));
            alignLeftButton.setAction(alignLeftAction);
            alignLeftButton.setText("");

            JButton centerButton = new JButton();
            Action centerAction = new StyledEditorKit.AlignmentAction("Center",StyleConstants.ALIGN_CENTER);
            centerAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getCodeBase(), "center.gif")));
            centerButton.setAction(centerAction);
            centerButton.setText("");

            JButton alignRightButton = new JButton();
            Action alignRightAction = new StyledEditorKit.AlignmentAction ("Right Align",StyleConstants.ALIGN_RIGHT);
            alignRightAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getCodeBase(), "right.gif")));
            alignRightButton.setAction(alignRightAction);
            alignRightButton.setText("");
            JButton bulletButton = new JButton();
            String INSERT_UL_HTML = "<ul><li></li></ul>";
            HTMLEditorKit.InsertHTMLTextAction bulletAction = new HTMLEditorKit.InsertHTMLTextAction("Bullets",  INSERT_UL_HTML,
				 HTML.Tag.UL, HTML.Tag.LI,
				 HTML.Tag.BODY, HTML.Tag.UL);
            bulletAction.putValue(Action.SMALL_ICON, new ImageIcon(new URL(getCodeBase(), "list.gif")));
            bulletButton.setAction(bulletAction);
            bulletButton.setText("");

            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(cutButton);
            buttonPanel.add(copyButton);
            buttonPanel.add(pasteButton);
            buttonPanel.add(boldButton);
            buttonPanel.add(italicButton);
            buttonPanel.add(underLineButton);
            buttonPanel.add(alignLeftButton);
            buttonPanel.add(centerButton);
            buttonPanel.add(alignRightButton);
            buttonPanel.add(bulletButton);

            JScrollPane scrollPane = new JScrollPane(editor);
            scrollPane.setPreferredSize(new Dimension(getSize().width*7/8,getSize().height*5/8));

            getContentPane().add(buttonPanel, BorderLayout.NORTH);
            getContentPane().add(scrollPane, BorderLayout.CENTER);
            setVisible(true);
        }catch(MalformedURLException murle){
            System.err.println("MalformedURLException: "+ murle.getMessage());
        }

    }

}
