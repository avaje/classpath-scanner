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
package org.avaje.classpath.scanner.internal.scanner.filesystem;

import org.avaje.classpath.scanner.core.ClassPathScanException;
import org.avaje.classpath.scanner.core.Location;
import org.avaje.classpath.scanner.ResourceFilter;
import org.avaje.classpath.scanner.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * FileSystem scanner.
 */
public class FileSystemScanner {

  private static final Logger LOG = LoggerFactory.getLogger(FileSystemScanner.class);

  /**
   * Scans the FileSystem for resources under the specified location, starting with the specified prefix and ending with
   * the specified suffix.
   *
   * @param location  The location in the filesystem to start searching. Subdirectories are also searched.
   * @param predicate The predicate used to match resources.
   * @return The resources that were found.
   */
  public List<Resource> scanForResources(Location location, ResourceFilter predicate) {

    String path = location.getPath();

    File dir = new File(path);
    if (!dir.isDirectory() || !dir.canRead()) {
      LOG.warn("Unable to resolve location filesystem:" + path);
      return Collections.emptyList();
    }

    List<Resource> resources = new ArrayList<Resource>();

    try {
      Set<String> resourceNames = findResourceNames(path, predicate);
      for (String resourceName : resourceNames) {
        resources.add(new FileSystemResource(resourceName));
        LOG.debug("Found filesystem resource: " + resourceName);
      }
      return resources;

    } catch (IOException e) {
      throw new ClassPathScanException(e);
    }
  }

  /**
   * Finds the resources names present at this location and below on the classpath starting with this prefix and
   * ending with this suffix.
   */
  private Set<String> findResourceNames(String path, ResourceFilter predicate) throws IOException {
    Set<String> resourceNames = findResourceNamesFromFileSystem(path, new File(path));
    return filterResourceNames(resourceNames, predicate);
  }

  /**
   * Finds all the resource names contained in this file system folder.
   *
   * @param scanRootLocation The root location of the scan on disk.
   * @param folder           The folder to look for resources under on disk.
   * @return The resource names;
   * @throws IOException when the folder could not be read.
   */
  @SuppressWarnings("ConstantConditions")
  private Set<String> findResourceNamesFromFileSystem(String scanRootLocation, File folder) throws IOException {

    LOG.debug("scanning in path: {} ({})", folder.getPath(), scanRootLocation);

    Set<String> resourceNames = new TreeSet<String>();

    File[] files = folder.listFiles();
    for (File file : files) {
      if (file.canRead()) {
        if (file.isDirectory()) {
          resourceNames.addAll(findResourceNamesFromFileSystem(scanRootLocation, file));
        } else {
          resourceNames.add(file.getPath());
        }
      }
    }

    return resourceNames;
  }

  /**
   * Filters this list of resource names to only include the ones whose filename matches this prefix and this suffix.
   */
  private Set<String> filterResourceNames(Set<String> resourceNames, ResourceFilter predicate) {
    Set<String> filteredResourceNames = new TreeSet<String>();
    for (String resourceName : resourceNames) {
      if (predicate.isMatch(resourceName)) {
        filteredResourceNames.add(resourceName);
      }
    }
    return filteredResourceNames;
  }
}
