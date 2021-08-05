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
package io.avaje.classpath.scanner.internal.scanner.filesystem;

import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.internal.FileCopyUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * A resource on the filesystem.
 */
class FileSystemResource implements Resource {

  /**
   * The location of the resource on the filesystem.
   */
  private final File file;

  public FileSystemResource(String location) {
    this.file = new File(location);
  }

  @Override
  public String toString() {
    return file.toString();
  }

  @Override
  public String getLocation() {
    return file.getPath().replace('\\', '/');
  }

  @Override
  public String getFilename() {
    return file.getName();
  }

  @Override
  public InputStream inputStream() {
    try {
      return new FileInputStream(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public String loadAsString(Charset charset) {
    return FileCopyUtils.copyToString(inputStream(), charset);
  }

  @Override
  public List<String> loadAsLines(Charset charset) {
    return FileCopyUtils.readLines(inputStream(), charset);
  }
}
