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

/**
 * Text handling helper methods for dealing with HTML.
 */
public class HtmlUtils {
  public static String escape(Object obj) {
    return escape(obj.toString());
  }

  public static String escape(String str) {
    StringBuilder result = new StringBuilder((int) (str.length() * 1.05 + 20));

    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
        case '<':
          result.append("&lt;");
          break;
        case '>':
          result.append("&gt;");
          break;
        case '\"':
          result.append("&quot;");
          break;
        case '\'':
          result.append("&#039;");
          break;
        case '\\':
          result.append("&#092;");
          break;
        case '&':
          result.append("&amp;");
          break;
        case '\n':
          result.append("<br/>");
          break;
        case '\r':
          break;
        default:
          result.append(c);
      }
    }
    return result.toString();
  }

  public static String pad(int number, int size) {
    StringBuilder buf = new StringBuilder(size);
    buf.setLength(size);
    for (int i = size; --i >= 0;) {
      int digit = number % 10;
      number /= 10;
      if (number != 0 || digit != 0)
        buf.setCharAt(i, (char) ('0' + digit));
      else
        buf.setCharAt(i, ' ');
    }
    return buf.toString();
  }
}
