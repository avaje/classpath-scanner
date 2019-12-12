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
package org.avaje.classpath.scanner.core;

import io.avaje.classpath.scanner.ClassFilter;
import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.ResourceFilter;
import org.avaje.classpath.scanner.internal.EnvironmentDetection;
import org.avaje.classpath.scanner.internal.ResourceAndClassScanner;
import org.avaje.classpath.scanner.internal.scanner.classpath.ClassPathScanner;
import org.avaje.classpath.scanner.internal.scanner.classpath.android.AndroidScanner;
import org.avaje.classpath.scanner.internal.scanner.filesystem.FileSystemScanner;

import java.util.List;

/**
 * Scanner for Resources and Classes.
 */
public class Scanner implements io.avaje.classpath.scanner.ClassPathScanner {

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
  public List<Resource> scanForResources(Location location, ResourceFilter predicate) {

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
  @Override
  public List<Resource> scanForResources(String location, ResourceFilter predicate) {
    return scanForResources(new Location(location), predicate);
  }

  /**
   * Scans the classpath for classes under the specified package matching the given predicate.
   *
   * @param location  The package in the classpath to start scanning. Subpackages are also scanned.
   * @param predicate The predicate used to match scanned classes.
   * @return The classes found matching the predicate
   */
  public List<Class<?>> scanForClasses(Location location, ClassFilter predicate) {
    return resourceAndClassScanner.scanForClasses(location, predicate);
  }

  /**
   * Scans the classpath for classes under the specified package matching the given predicate.
   *
   * @param location  The package in the classpath to start scanning. Subpackages are also scanned.
   * @param predicate The predicate used to match scanned classes.
   * @return The classes found matching the predicate
   */
  @Override
  public List<Class<?>> scanForClasses(String location, ClassFilter predicate) {
    return scanForClasses(new Location(location), predicate);
  }

}
