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
package io.avaje.classpath.scanner.internal.scanner.classpath;

import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.internal.FileCopyUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * A resource on the classpath.
 */
class ClassPathResource implements Resource {

  /**
   * The location of the resource on the classpath.
   */
  private final String location;

  /**
   * The ClassLoader to use.
   */
  private final ClassLoader classLoader;

  ClassPathResource(String location, ClassLoader classLoader) {
    this.location = location;
    this.classLoader = classLoader;
  }

  @Override
  public String toString() {
    return location;
  }

  @Override
  public String location() {
    return location;
  }

  @Override
  public String name() {
    return location.substring(location.lastIndexOf("/") + 1);
  }

  @Override
  public InputStream inputStream() {
    return classLoader.getResourceAsStream(location);
  }

  @Override
  public List<String> loadAsLines(Charset charset) {
    return FileCopyUtils.readLines(inputStream(), charset);
  }

  @Override
  public String loadAsString(Charset charset) {
    return FileCopyUtils.copyToString(inputStream(), charset);
  }

}
