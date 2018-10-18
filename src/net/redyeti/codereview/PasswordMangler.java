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
 * Encodes and decodes passwords
 */
public class PasswordMangler {
  private static final Encrypter ENCRYPTER = new Encrypter("*&^@;as!=-#/!21".toCharArray());

  static String encode(String password) {
    if (password == null)
      return null;
    return ENCRYPTER.encrypt(password);
  }

  static String decode(String encodedPassword) {
    if (encodedPassword == null)
      return null;
    return ENCRYPTER.decrypt(encodedPassword);
  }
}
