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

/**
 * $Source:$
 * $Id:$
 */
package net.redyeti.codereview;

import java.util.*;

import javax.swing.*;

import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.*;

/**
 * Sends the code review email.
 */
public class CodeReviewCommitSession implements CommitSession {

  private final Project project;
  private PreviewEmailForm form;
  private String plainText;
  private String htmlText;

  public CodeReviewCommitSession(Project project) {
    this.project = project;
  }

  @Nullable
  public JComponent getAdditionalConfigurationUI() {
    return null;
  }

  /**
   * Calculates the differences and presents the code review dialog to the user.
   *
   * @param changes       the collection of changes the user had selected in the commit dialog.
   * @param commitMessage the commit message entered by the user.
   */
  @Nullable
  public JComponent getAdditionalConfigurationUI(Collection<Change> changes, String commitMessage) {
    List<Change> sortedChanges = sortChanges(changes);

    ReviewBuilder reviewBuilder = new ReviewBuilder(project, sortedChanges, commitMessage);
    try {
      reviewBuilder.build();
    } catch (BuildException e) {
      showMessage("Unable to generate the email content.\n\nReason: " + e.getMessage(),
                  "Code Review Error",
                  Messages.getErrorIcon());
      return null;
    }
    plainText = reviewBuilder.getPlainContent();
    htmlText = reviewBuilder.getHtmlContent();
    form = new PreviewEmailForm(commitMessage, plainText, htmlText);
    return form.getPreviewComponent();
  }

  public boolean canExecute(Collection<Change> changes, String commitMessage) {
    return true;
  }

  /**
   * Sends the code review email.
   *
   * @param changes       the collection of changes the user had selected in the commit dialog.
   * @param commitMessage the commit message entered by the user.
   */
  public void execute(Collection<Change> changes, String commitMessage) {
    form.saveSettings();
    CodeReviewSender sender = new CodeReviewSender(project, changes, form.getChosenSettings(), plainText, htmlText);
    ApplicationManager.getApplication().invokeAndWait(sender, ModalityState.defaultModalityState());
  }

  private static List<Change> sortChanges(Collection<Change> changes) {
    List<Change> result = new ArrayList<Change>(changes);
    Collections.sort(result, new Comparator<Change>() {
      public int compare(Change c1, Change c2) {
        ContentRevision r1 = c1.getBeforeRevision() == null ? c1.getAfterRevision() : c1.getBeforeRevision();
        ContentRevision r2 = c2.getBeforeRevision() == null ? c2.getAfterRevision() : c2.getBeforeRevision();
        if (r1 == null)
          return r2 == null ? 0 : -1;
        if (r2 == null)
          return 1;
        return r1.getFile().getPath().compareTo(r2.getFile().getPath());
      }
    });
    return result;
  }

  public void executionCanceled() {
  }

  public String getHelpId() {
    return null;
  }

  private static void showMessage(final String message, final String title, final Icon icon) {
    Runnable messageRunner = new Runnable() {
      public void run() {
        Messages.showMessageDialog(message, title, icon);
      }
    };
    ApplicationManager.getApplication().invokeLater(messageRunner, ModalityState.defaultModalityState());
  }
}
