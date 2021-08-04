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
package io.avaje.classpath.scanner.core.scanner.filesystem;

import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.core.FileCopyUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * A resource on the filesystem.
 */
class FileSystemResource implements Resource, Comparable<FileSystemResource> {
  /**
   * The location of the resource on the filesystem.
   */
  private final File location;

  /**
   * Creates a new ClassPathResource.
   *
   * @param location The location of the resource on the filesystem.
   */
  public FileSystemResource(String location) {
    this.location = new File(location);
  }

  @Override
  public String toString() {
    return location.toString();
  }

  /**
   * Return The filename of this resource, without the path.
   */
  @Override
  public String fileName() {
    return location.getName();
  }

  /**
   * Return the location of the resource on the classpath.
   */
  @Override
  public String location() {
    return location.getPath().replace('\\','/');
  }

  @Override
  public List<String> loadAsLines(Charset charset) {
    try {
      InputStream inputStream = new FileInputStream(location);
      return FileCopyUtils.readLines(inputStream, charset);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Loads this resource as a string.
   *
   * @param charset The encoding to use.
   * @return The string contents of the resource.
   */
  @Override
  public String loadAsString(Charset charset) {
    try {
      InputStream inputStream = new FileInputStream(location);
      Reader reader = new InputStreamReader(inputStream, charset);
      return FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      throw new UncheckedIOException("Unable to load filesystem resource: " + location.getPath() + " (charset: " + charset + ")", e);
    }
  }

  @Override
  public int compareTo(FileSystemResource o) {
    return location.compareTo(o.location);
  }
}
