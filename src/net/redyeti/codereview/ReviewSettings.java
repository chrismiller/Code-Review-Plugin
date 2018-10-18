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

/**
 * The settings that dictate how the code review email should be constructed.
 */
public class ReviewSettings {

  private String smtpServer;
  private int smtpPort;
  private String smtpUsername;
  private String smtpPassword;
  private boolean useSSL;
  private String fromAddress;
  private String replyToAddress;
  private String toAddresses;
  private String ccAddresses;
  private String bccAddresses;
  private String subject;
  private boolean plainAttached;
  private boolean htmlAttached;
  private boolean zipAttached;

  public String getSmtpServer() {
    return smtpServer;
  }

  public void setSmtpServer(String smtpServer) {
    this.smtpServer = smtpServer;
  }

  public int getSmtpPort() {
    return smtpPort;
  }

  public void setSmtpPort(int smtpPort) {
    this.smtpPort = smtpPort;
  }

  public String getSmtpUsername() {
    return smtpUsername;
  }

  public void setSmtpUsername(String smtpUsername) {
    this.smtpUsername = smtpUsername;
  }

  public String getSmtpPassword() {
    return smtpPassword;
  }

  public void setSmtpPassword(String smtpPassword) {
    this.smtpPassword = smtpPassword;
  }

  public boolean isUseSSL() {
    return useSSL;
  }

  public void setUseSSL(boolean useSSL) {
    this.useSSL = useSSL;
  }

  public String getFromAddress() {
    return fromAddress;
  }

  public void setFromAddress(String fromAddress) {
    this.fromAddress = fromAddress;
  }

  public String getReplyToAddress() {
    return replyToAddress;
  }

  public void setReplyToAddress(String replyToAddress) {
    this.replyToAddress = replyToAddress;
  }

  public String getToAddresses() {
    return toAddresses;
  }

  public void setToAddresses(String toAddresses) {
    this.toAddresses = toAddresses;
  }

  public String getCcAddresses() {
    return ccAddresses;
  }

  public void setCcAddresses(String ccAddresses) {
    this.ccAddresses = ccAddresses;
  }

  public String getBccAddresses() {
    return bccAddresses;
  }

  public void setBccAddresses(String bccAddresses) {
    this.bccAddresses = bccAddresses;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public boolean hasPlainAttached() {
    return plainAttached;
  }

  public void setPlainAttached(boolean plainAttached) {
    this.plainAttached = plainAttached;
  }

  public boolean hasHtmlAttached() {
    return htmlAttached;
  }

  public void setHtmlAttached(boolean htmlAttached) {
    this.htmlAttached = htmlAttached;
  }

  public boolean hasZipAttached() {
    return zipAttached;
  }

  public void setZipAttached(boolean zipAttached) {
    this.zipAttached = zipAttached;
  }
}
