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

import java.awt.Color;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;

@State(
    name = "CodeReview.CodeReviewPlugin",
    storages = {@Storage(value = "code.review.xml")}
)
public class CodeReviewConfig implements PersistentStateComponent<CodeReviewConfig> {
  public String smtpServer;
  public int smtpPort = Mailer.DEFAULT_PORT;
  public String smtpUsername;
  public String encodedSmtpPassword;
  public boolean useSSL;
  public String fromAddress;
  public String replyToAddress;
  public String toAddresses;
  public String ccAddresses;
  public String bccAddresses;
  public String subject;
  public String subjectPrefix;
  public int linesOfContext;
  public boolean ignoreTrailingWhitespace;
  public boolean sendAsHtml;
  public boolean attachZipFile;
  public String insertedLineColor;
  public String deletedLineColor;
  public String omittedLineColor;

  CodeReviewConfig() {
    insertedLineColor = getHtmlColorString(Color.BLUE);
    deletedLineColor = getHtmlColorString(Color.RED);
    omittedLineColor = getHtmlColorString(Color.GRAY);
  }

  public CodeReviewConfig getState() {
    return this;
  }

  public void loadState(CodeReviewConfig state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void setSmtpServer(final String smtpServer) {
    this.smtpServer = smtpServer;
  }

  public void setSmtpPort(int smtpPort) {
    this.smtpPort = smtpPort;
  }

  public void setSmtpUsername(String smtpUsername) {
    this.smtpUsername = smtpUsername;
  }

  public void setEncodedSmtpPassword(String encodedPassword) {
    this.encodedSmtpPassword = encodedPassword;
  }

  @Transient
  public Color getInsertedLineColor() {
    return getColorFromHtmlString(insertedLineColor);
  }

  @Transient
  public void setInsertedLineColor(Color color) {
    insertedLineColor = getHtmlColorString(color);
  }

  @Transient
  public Color getDeletedLineColor() {
    return getColorFromHtmlString(deletedLineColor);
  }

  @Transient
  public void setDeletedLineColor(Color color) {
    deletedLineColor = getHtmlColorString(color);
  }

  @Transient
  public Color getOmittedLineColor() {
    return getColorFromHtmlString(omittedLineColor);
  }

  @Transient
  public void setOmittedLineColor(Color color) {
    omittedLineColor = getHtmlColorString(color);
  }

  @Transient
  public String getInsertedLineColorAsHtml() {
    return insertedLineColor;
  }

  @Transient
  public String getDeletedLineColorAsHtml() {
    return deletedLineColor;
  }

  @Transient
  public String getOmittedLineColorAsHtml() {
    return omittedLineColor;
  }

  private static Color getColorFromHtmlString(String color) {
    return new Color(
        Integer.valueOf(color.substring(0, 2), 16),
        Integer.valueOf(color.substring(2, 4), 16),
        Integer.valueOf(color.substring(4, 6), 16)
    );
  }

  private static String getHtmlColorString(Color color) {
    return Integer.toHexString(color.getRGB() & 0xffffff | 0x1000000).substring(1);
  }
}
