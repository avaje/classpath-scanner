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
package io.avaje.classpath.scanner;

import io.avaje.classpath.scanner.core.Location;
import io.avaje.classpath.scanner.core.EnvironmentDetection;
import io.avaje.classpath.scanner.core.ResourceAndClassScanner;
import io.avaje.classpath.scanner.core.scanner.classpath.ClassPathScanner;
import io.avaje.classpath.scanner.core.scanner.classpath.android.AndroidScanner;
import io.avaje.classpath.scanner.core.scanner.filesystem.FileSystemScanner;

import java.util.List;
import java.util.function.Predicate;

/**
 * Scanner for Resources and Classes.
 */
class DScanner implements Scanner {

  private final ResourceAndClassScanner resourceAndClassScanner;

  private final FileSystemScanner fileSystemScanner = new FileSystemScanner();

  DScanner(ClassLoader classLoader) {
    if (EnvironmentDetection.isAndroid()) {
      resourceAndClassScanner = new AndroidScanner();
    } else {
      resourceAndClassScanner = new ClassPathScanner(classLoader);
    }
  }

  /**
   * Scans this location for resources matching the given predicate.
   * <p>
   * The location can have a prefix of <code>filesystem:</code> or <code>classpath:</code> to determine
   * how to scan. If no prefix is used then classpath scan is the default.
   * </p>
   *
   * @param location  The location to start searching. Subdirectories are also searched.
   * @param predicate The predicate used to match resource names.
   * @return The resources that were found.
   */
  @Override
  public List<Resource> scanForResources(String location, Predicate<String> predicate) {
    Location loc = new Location(location);
    if (loc.isFileSystem()) {
      return fileSystemScanner.scanForResources(loc, predicate);
    }
    return resourceAndClassScanner.scanForResources(loc, predicate);
  }

}
