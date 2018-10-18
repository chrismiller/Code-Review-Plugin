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

import java.io.*;
import java.util.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.util.diff.Diff;
import com.intellij.util.diff.FilesTooBigForDiffException;

/**
 * Builds up HTML and/or plain text that shows the differences in a list of changes
 */
public class ReviewBuilder {

  private static final String PLAIN_EMAIL_TEMPLATE_VM = "/net/redyeti/codereview/resources/plainEmailTemplate.vm";
  private static final String HTML_EMAIL_TEMPLATE_VM = "/net/redyeti/codereview/resources/htmlEmailTemplateInlineStyles.vm";

  private Project project;
  private Collection<Change> changes;
  private String headerMessage;
  private String htmlContent;
  private String plainContent;

  public ReviewBuilder(Project project, Collection<Change> changes, String headerMessage) {
    this.project = project;
    this.changes = changes;
    this.headerMessage = headerMessage;
  }

  public String getHtmlContent() {
    return htmlContent;
  }

  public String getPlainContent() {
    return plainContent;
  }

  public void build() throws BuildException {
    CodeReviewConfig config = ServiceManager.getService(CodeReviewConfig.class);
    List<ChangedFile> changeList = buildChangeList(changes, config);

    VelocityContext context = new VelocityContext();
    context.put("comment", headerMessage);
    context.put("config", config);
    context.put("changes", changeList);
    context.put("htmlHelper", new HtmlUtils());

//      plainText = renderTemplate(config.getPlainTextTemplate(), context);
    String template;
    try {
      template = FileUtils.readTextResource(PLAIN_EMAIL_TEMPLATE_VM);
    } catch (IOException e) {
      throw new BuildException("An IO error occurred while loading the plain text template " + PLAIN_EMAIL_TEMPLATE_VM + "\n\n" + e.getMessage(), e);
    }
    if (template == null)
      throw new BuildException("Could not find the plain text template " + PLAIN_EMAIL_TEMPLATE_VM + " on the classpath.");

    plainContent = renderTemplate(template, context);

//      htmlText = renderTemplate(config.getHtmlTemplate(), context);
    if (config.sendAsHtml) {
      try {
        template = FileUtils.readTextResource(HTML_EMAIL_TEMPLATE_VM);
      } catch (IOException e) {
        throw new BuildException("An IO error occurred while loading the HTML template " + HTML_EMAIL_TEMPLATE_VM + "\n\n" + e.getMessage(), e);
      }
      if (template == null)
        throw new BuildException("Could not find the HTML template " + HTML_EMAIL_TEMPLATE_VM + " on the classpath.");
      htmlContent = renderTemplate(template, context);
    }
  }

  /**
   * Builds up a user-friendly list of changes that have been made to each file.
   *
   * @param changes the changes selected in the commit dialog, as specified by IDEA's OpenAPI.
   * @param config  the configuration for this plugin.
   * @return
   * @throws BuildException if the content couldn't be retrieved from the VCS.
   */
  private List<ChangedFile> buildChangeList(Collection<Change> changes, CodeReviewConfig config) {
    List<ChangedFile> changeList = new ArrayList<ChangedFile>(changes.size());

    for (Change change : changes) {
      ContentRevision beforeRevision = change.getBeforeRevision();
      ContentRevision afterRevision = change.getAfterRevision();

      if (beforeRevision == null && afterRevision == null)
        continue;

      String beforeName = beforeRevision != null ? beforeRevision.getFile().getPath().replace('\\', '/') : null;
      String afterName = afterRevision != null ? afterRevision.getFile().getPath().replace('\\', '/') : null;
      String beforeRevStr = beforeRevision != null ? beforeRevision.getRevisionNumber().asString() : null;
      String afterRevStr = afterRevision != null ? afterRevision.getRevisionNumber().asString() : null;

      List<ChangedFile.Line> lines = new ArrayList<ChangedFile.Line>(1);
      if (isBinary(beforeRevision) || isBinary(afterRevision)) {
        if (beforeRevStr == null)
          lines.add(new ChangedFile.Line("Binary file has been added", ChangedFile.LineType.UNCHANGED, 1, 1));
        else if (afterRevStr == null)
          lines.add(new ChangedFile.Line("Binary file has been deleted", ChangedFile.LineType.UNCHANGED, 1, 1));
        else
          lines.add(new ChangedFile.Line("Binary file has been changed", ChangedFile.LineType.UNCHANGED, 1, 1));
      } else {
        CharSequence[] beforeLines = getLinesOfText(beforeRevision, config.ignoreTrailingWhitespace);
        CharSequence[] afterLines = getLinesOfText(afterRevision, config.ignoreTrailingWhitespace);

        if (beforeLines != null && afterLines != null)
          try {
            Diff.Change diff = Diff.buildChanges(beforeLines, afterLines);
            lines = buildLines(diff, beforeLines, afterLines, config.linesOfContext);
          } catch (FilesTooBigForDiffException e) {
            lines.add(new ChangedFile.Line("The differences is too big to compare", ChangedFile.LineType.UNCHANGED, 1, 1));
          }
      }
      ChangedFile changedFile = new ChangedFile(beforeName, afterName, beforeRevStr, afterRevStr, lines);
      changeList.add(changedFile);
    }
    return changeList;
  }

  private boolean isBinary(ContentRevision revision) {
    return revision != null && revision.getFile().getFileType().isBinary();
  }

  /**
   * Takes two pieces of content and their differences and builds up a list of
   * lines that is suitable for displaying as a diff.
   *
   * @param diff
   * @param beforeLines    the old content as an array of lines
   * @param afterLines     the new content as an array of lines
   * @param linesOfContext the number of unaltered lines of text to retain around
   *                       each difference so the user can see some context for each difference.
   * @return a list of lines that together make up a complete diff suitable for displaying
   *         to an end user.
   */
  private List<ChangedFile.Line> buildLines(Diff.Change diff, CharSequence[] beforeLines, CharSequence[] afterLines, int linesOfContext) {
    if (linesOfContext == 0)
      linesOfContext = 10000;

    List<ChangedFile.Line> lines = new ArrayList<ChangedFile.Line>(100);
    int previousLine = 0;
    while (diff != null) {
      int i = previousLine;

      // Display the unaltered lines (skipping some if there's too many)
      int linesSkippped = diff.line0 - previousLine - 2 * linesOfContext;
      for (; i < diff.line0; i++) {
        if (i < previousLine + linesOfContext || i >= diff.line0 - linesOfContext || linesSkippped == 1)
          lines.add(new ChangedFile.Line(beforeLines[i], ChangedFile.LineType.UNCHANGED, i + 1, diff.line1 - diff.line0 + i + 1));
        else if (i == previousLine + linesOfContext) {
          lines.add(new ChangedFile.Line("...", ChangedFile.LineType.OMITTED, 0, 0, linesSkippped));
        }
      }
      // Display the deleted and/or inserted lines for this difference
      for (i = 0; i < diff.deleted; i++)
        lines.add(new ChangedFile.Line(beforeLines[diff.line0 + i], ChangedFile.LineType.DELETED, diff.line0 + i + 1, 0));
      for (i = 0; i < diff.inserted; i++)
        lines.add(new ChangedFile.Line(afterLines[diff.line1 + i], ChangedFile.LineType.INSERTED, 0, diff.line1 + i + 1));
      previousLine = diff.line0 + diff.deleted;
      diff = diff.link;
    }
    // Display any remaining lines (plus skip some if there's too many)
    int lastLine = Math.min(beforeLines.length, previousLine + linesOfContext);
    for (int i = previousLine; i < lastLine; i++)
      lines.add(new ChangedFile.Line(beforeLines[i], ChangedFile.LineType.UNCHANGED, i + 1, afterLines.length - beforeLines.length + i + 1));

    int linesSkipped = beforeLines.length - lastLine;
    if (linesSkipped > 0) {
      lines.add(new ChangedFile.Line("...", ChangedFile.LineType.OMITTED, 0, 0, linesSkipped));
    }

    setNextAndPreviousStates(lines);

    return lines;
  }

  /**
   * For each line, sets the nextDifferent/previousDifferent flags. This makes rendering
   * the template easier
   */
  private void setNextAndPreviousStates(List<ChangedFile.Line> lines) {
    for (int i = 0; i < lines.size(); i++) {
      ChangedFile.Line thisLine = lines.get(i);
      thisLine.setPreviousDifferent(i == 0 || lines.get(i - 1).getType() != thisLine.getType());
      thisLine.setNextDifferent(i == lines.size() - 1 || lines.get(i + 1).getType() != thisLine.getType());
    }
  }

  /**
   * Gets the lines of text that makes up this content as an array of strings
   */
  private CharSequence[] getLinesOfText(ContentRevision content, boolean ignoreTrailingWhitespace) {
    if (content == null) {
      return null;
    }

    CharSequence[] result;
    try {
      result = splitLines(content.getContent());
      if (ignoreTrailingWhitespace) {
        for (int i = 0; i < result.length; i++) {
          result[i] = rightTrim(result[i]);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Unable to process content " + content.getFile(), e);
    }
    return result;
  }

  @NotNull
  private static String[] splitLines(@NotNull CharSequence s) {
    return s.length() == 0 ? new String[]{""} : LineTokenizer.tokenize(s, false, false);
  }

  /**
   * Trims any trailing whitespace from a <code>CharSequence</code>.
   *
   * @param line the <code>CharSequence</code> to trim.
   * @return a <code>CharSequence</code> that has no trailing whitespace, but
   *         is otherwise identical to the one supplied.
   */
  private CharSequence rightTrim(CharSequence line) {
    int len = line.length();
    while (0 < len && line.charAt(len - 1) <= ' ') {
      len--;
    }
    line = line.subSequence(0, len);
    return line;
  }

  /**
   * Merge together the supplied template and Velocity context
   */
  private String renderTemplate(String template, VelocityContext context) throws BuildException {
    VelocityEngine engine;
    try {
      engine = VelocityUtils.newVeloictyEngine();
    } catch (Exception e) {
      throw new BuildException("Failed to initialize the Velocity Engine.", e);
    }
    Writer writer = new StringWriter();
    try {
      engine.evaluate(context, writer, "error", template);
    } catch (ParseErrorException e) {
      throw new BuildException("Failed to parse the email template.", e);
    } catch (Exception e) {
      throw new BuildException("Unable to render the email template.", e);
    }
    return writer.toString();
  }
}
