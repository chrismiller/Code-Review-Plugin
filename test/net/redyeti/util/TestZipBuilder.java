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

package net.redyeti.util;

import java.io.*;

/**
 * Gives the ZipBuilder class a bit of a workout.
 */
public class TestZipBuilder {
  public static void main(String[] args) throws IOException {
    OutputStream os = new FileOutputStream("C:\\test.zip");
    ZipBuilder builder = new ZipBuilder(os);

    String test = "this is a test text file";
    InputStream is = new ByteArrayInputStream(test.getBytes("UTF-8"));
    builder.addFile("this\\is\\a\\test.txt", is);

    test = "test file number 2!";
    is = new ByteArrayInputStream(test.getBytes("UTF-8"));
    builder.addFile("this\\is\\the\\2nd\\test2.txt", is);

    test = "final test";
    is = new ByteArrayInputStream(test.getBytes("UTF-8"));
    builder.addFile("final\\test3.txt", is);

    builder.setComment("This is a comment for the zip file");
    builder.close();
  }
}