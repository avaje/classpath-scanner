/*
  Copyright 2010-2016 Boxfuse GmbH
  <p/>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p/>
  http://www.apache.org/licenses/LICENSE-2.0
  <p/>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package io.avaje.classpath.scanner.internal;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for copying files and their contents. Inspired by Spring's own.
 */
public class FileCopyUtils {
  /**
   * Prevent instantiation.
   */
  private FileCopyUtils() {
    // Do nothing
  }

  /**
   * Copy the contents of the given Reader into a String.
   * Closes the reader when done.
   *
   * @param in the reader to copy from
   * @return the String that has been copied to
   * @throws IOException in case of I/O errors
   */
  public static String copyToString(Reader in) throws IOException {
    StringWriter out = new StringWriter();
    copy(in, out);
    String str = out.toString();

    //Strip UTF-8 BOM if necessary
    if (str.startsWith("\ufeff")) {
      return str.substring(1);
    }

    return str;
  }

  /**
   * Copy the contents of the given InputStream into a new byte array.
   * Closes the stream when done.
   *
   * @param in the stream to copy from
   * @return the new byte array that has been copied to
   * @throws IOException in case of I/O errors
   */
  public static byte[] copyToByteArray(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
    copy(in, out);
    return out.toByteArray();
  }

  /**
   * Copy the contents of the given Reader to the given Writer.
   * Closes both when done.
   *
   * @param in  the Reader to copy from
   * @param out the Writer to copy to
   * @throws IOException in case of I/O errors
   */
  private static void copy(Reader in, Writer out) throws IOException {
    try {
      char[] buffer = new char[4096];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
      out.flush();
    } finally {
      try {
        in.close();
      } catch (IOException ex) {
        //Ignore
      }
      try {
        out.close();
      } catch (IOException ex) {
        //Ignore
      }
    }
  }

  /**
   * Copy the contents of the given InputStream to the given OutputStream.
   * Closes both streams when done.
   *
   * @param in  the stream to copy from
   * @param out the stream to copy to
   * @throws IOException in case of I/O errors
   */
  private static void copy(InputStream in, OutputStream out) throws IOException {
    try {
      int byteCount = 0;
      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
        byteCount += bytesRead;
      }
      out.flush();
    } finally {
      try {
        in.close();
      } catch (IOException ex) {
        //Ignore
      }
      try {
        out.close();
      } catch (IOException ex) {
        //Ignore
      }
    }
  }

  public static List<String> readLines(InputStream inputStream, Charset charset) {
    if (inputStream == null) {
      return Collections.emptyList();
    }
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
      List<String> result = new ArrayList<>();

      String line;
      while ((line = reader.readLine()) != null) {
        result.add(line);
      }
      return result;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
