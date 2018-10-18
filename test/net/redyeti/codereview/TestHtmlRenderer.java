/*
 * Copyright 2018 Chris Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.redyeti.codereview;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

import org.xml.sax.SAXException;

public class TestHtmlRenderer extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextArea textArea;
  private JEditorPane htmlPanel;

  public TestHtmlRenderer() {
    setContentPane(contentPane);
    setModal(true);

    textArea.setText("<html><head><style type='text/css'>\n" +
        "body { font-family: ProFontWindows,Monospaced }\n" +
        ".insert { color:#0000ff }\n" +
        ".delete { color:#ff0000 }\n" +
        "</style></head><body class='delete'>Testing<div class='insert'>Testing Insert</div></body></html>");

    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {onOK();}
    });

    buttonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {onCancel();}
    });

// call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

// call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  private void onOK() {
    try {
      updateHtml();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  private void onCancel() {
    dispose();
  }

  private void updateHtml() throws IOException, SAXException {
    HTMLEditorKit editorKit = new HTMLEditorKit();
    htmlPanel.setEditorKit(editorKit);
    javax.swing.text.Document doc = editorKit.createDefaultDocument();
    htmlPanel.setDocument(doc);
    htmlPanel.setText(textArea.getText());
    htmlPanel.setCaretPosition(0);
  }

  public static void main(String[] args) {
    TestHtmlRenderer dialog = new TestHtmlRenderer();
    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
  }
}
