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

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;

import net.redyeti.util.EmailSettings;

/**
 * Exercises the Mailer class
 */
public class TestMailer {
  public void testMailer() throws MessagingException, IOException {
    Mailer mailer = new Mailer();

    EmailSettings settings = new EmailSettings();
    settings.setSmtpServer("dv1");
    settings.setFromAddress("test@example.com");
    settings.setToAddresses("test@example.com");
    settings.setSubject("Test email");

    // Plain text only
    settings.setPlainContent("Testing plain only");
    mailer.sendMail(settings);

    // HTML only
    settings.setPlainContent(null);
    settings.setHtmlContent("<h1>Testing HTML only</h1>");
    mailer.sendMail(settings);

    // Multipart alternative plain + HTML
    settings.setPlainContent("Testing plain + HTML");
    settings.setHtmlContent("<h1>Testing plain + HTML</h1>");
    mailer.sendMail(settings);

    // Multipart alternative with attachment
    settings.setPlainContent("Testing plain + HTML + attachment");
    settings.setHtmlContent("<h1>Testing plain + HTML + attachment</h1>");
    InputStream is = null;
    try {
      FileInputStream fis = new FileInputStream("C:\\tostring-plugin-src.jar");
      is = new BufferedInputStream(fis, 16384);
      DataSource ds = new ByteArrayDataSource(is, "application/octet-stream");
      settings.addAttachment(new Attachment("tostring.zip", ds));
      mailer.sendMail(settings);
    } finally {
      if (is != null)
        is.close();
    }

    // HTML with attachment
    settings.setPlainContent(null);
    settings.setHtmlContent("<h1>Testing HTML + attachment</h1>");
    mailer.sendMail(settings);

    // Plain text with attachment
    settings.setHtmlContent(null);
    settings.setPlainContent("Testing plain + attachment");
    mailer.sendMail(settings);
  }

  public static void main(String[] args) throws Exception {
    TestMailer mailer = new TestMailer();
    mailer.testMailer();
  }
}
