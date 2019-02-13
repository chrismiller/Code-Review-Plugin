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

import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Encrypts/Decrypts strings using a caller-supplied passphrase.
 */
public class Encrypter {
  private char[] key;
  private PBEParameterSpec paramSpec = new PBEParameterSpec(
      new byte[]{
          (byte) 0xc4, (byte) 0x12, (byte) 0x5e, (byte) 0x0c,
          (byte) 0x6e, (byte) 0xff, (byte) 0xce, (byte) 0x2a
      },
      23
  );

  /**
   * Constructs an encrypter that uses the given characters as an encryption key.
   */
  public Encrypter(char[] key) {
    this.key = key;
  }

  /**
   * Returns an encrypted, base64 encoded version of the supplied string.
   */
  public String encrypt(String data) {
    try {
      PBEKeySpec keySpec = new PBEKeySpec(key);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
      SecretKey key = factory.generateSecret(keySpec);

      Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
      cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

      byte[] inputBytes = data.getBytes("UTF8");
      byte[] outputBytes = cipher.doFinal(inputBytes);

      Base64.Encoder encoder = Base64.getMimeEncoder();
      return encoder.encodeToString(outputBytes);
    }
    catch (Exception e) {
      throw new EncrypterException("Failed to encrypt string.", e);
    }
  }

  /**
   * Returns the unencrypted contents of the supplied encrypted, base64 encoded string.
   */
  public String decrypt(String encryptedData) {
    try {
      Base64.Decoder decoder = Base64.getMimeDecoder();
      byte[] inputBytes = decoder.decode(encryptedData);

      PBEKeySpec keySpec = new PBEKeySpec(key);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
      SecretKey key = factory.generateSecret(keySpec);

      Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
      cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

      byte[] outputBytes = cipher.doFinal(inputBytes);
      return new String(outputBytes, "UTF8");
    }
    catch (Exception e) {
      throw new EncrypterException("Failed to decrypt string.", e);
    }
  }

  public static class EncrypterException extends RuntimeException {
    public EncrypterException(String message) {
      super(message);
    }

    public EncrypterException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
