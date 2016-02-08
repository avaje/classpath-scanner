/**
 * Copyright 2010-2016 Boxfuse GmbH
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.avaje.classpath.scanner;

import org.avaje.classpath.scanner.internal.EnvironmentDetection;
import org.avaje.classpath.scanner.internal.scanner.ResourceAndClassScanner;
import org.avaje.classpath.scanner.internal.scanner.classpath.ClassPathScanner;
import org.avaje.classpath.scanner.internal.scanner.classpath.android.AndroidScanner;
import org.avaje.classpath.scanner.internal.scanner.filesystem.FileSystemScanner;

import java.util.List;

/**
 * Scanner for Resources and Classes.
 */
public class Scanner {

  private final ResourceAndClassScanner resourceAndClassScanner;

  private final FileSystemScanner fileSystemScanner = new FileSystemScanner();

  public Scanner(ClassLoader classLoader) {
    if (EnvironmentDetection.isAndroid()) {
      resourceAndClassScanner = new AndroidScanner(classLoader);
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
  public List<Resource> scanForResources(Location location, MatchResource predicate) {

    if (location.isFileSystem()) {
      return fileSystemScanner.scanForResources(location, predicate);
    }
    return resourceAndClassScanner.scanForResources(location, predicate);
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
  public List<Resource> scanForResources(String location, MatchResource predicate) {
    return scanForResources(new Location(location), predicate);
  }

  /**
   * Scans the classpath for classes under the specified package matching the given predicate.
   *
   * @param location  The package in the classpath to start scanning. Subpackages are also scanned.
   * @param predicate The predicate used to match scanned classes.
   * @return The classes found matching the predicate
   * @throws Exception when the location could not be scanned.
   */
  public List<Class<?>> scanForClasses(Location location, MatchClass predicate) {
    return resourceAndClassScanner.scanForClasses(location, predicate);
  }

  /**
   * Scans the classpath for classes under the specified package matching the given predicate.
   *
   * @param location  The package in the classpath to start scanning. Subpackages are also scanned.
   * @param predicate The predicate used to match scanned classes.
   * @return The classes found matching the predicate
   * @throws Exception when the location could not be scanned.
   */
  public List<Class<?>> scanForClasses(String location, MatchClass predicate) {
    return scanForClasses(new Location(location), predicate);
  }

}