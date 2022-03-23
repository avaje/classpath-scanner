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

import io.avaje.classpath.scanner.internal.ScanLog;
import io.avaje.classpath.scanner.internal.UrlUtils;

import java.io.File;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

/**
 * ClassPathLocationScanner for the file system.
 */
public class FileSystemClassPathLocationScanner implements ClassPathLocationScanner {
  private static final System.Logger log = ScanLog.log;

  public Set<String> findResourceNames(String location, URL locationUrl) {
    String filePath = UrlUtils.toFilePath(locationUrl);
    File folder = new File(filePath);
    if (!folder.isDirectory()) {
      return new TreeSet<>();
    }
    String classPathRootOnDisk = filePath.substring(0, filePath.length() - location.length());
    if (!classPathRootOnDisk.endsWith(File.separator)) {
      classPathRootOnDisk = classPathRootOnDisk + File.separator;
    }
    log.log(Level.TRACE, "scan starting at root in filesystem: {0}", classPathRootOnDisk);
    return findResourceNamesFromFileSystem(classPathRootOnDisk, location, folder);
  }

  /**
   * Finds all the resource names contained in this file system folder.
   *
   * @param classPathRootOnDisk The location of the classpath root on disk, with a trailing slash.
   * @param scanRootLocation    The root location of the scan on the classpath, without leading or trailing slashes.
   * @param folder              The folder to look for resources under on disk.
   * @return The resource names;
   */
  Set<String> findResourceNamesFromFileSystem(String classPathRootOnDisk, String scanRootLocation, File folder) {
    log.log(Level.TRACE, "scan resources in path: {0} ({1})", folder.getPath(), scanRootLocation);
    Set<String> resourceNames = new TreeSet<>();

    File[] files = folder.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.canRead()) {
          String resourcePath = toResourceNameOnClasspath(classPathRootOnDisk, file);
          if (file.isDirectory()) {
            if (!ignorePath(resourcePath)) {
              resourceNames.addAll(findResourceNamesFromFileSystem(classPathRootOnDisk, scanRootLocation, file));
            }
          } else {
            resourceNames.add(resourcePath);
          }
        }
      }
    }
    return resourceNames;
  }

  private boolean ignorePath(String resourcePath) {
    return resourcePath.startsWith("io/avaje/classpath") || resourcePath.startsWith("io/ebean");
  }

  /**
   * Converts this file into a resource name on the classpath.
   *
   * @param classPathRootOnDisk The location of the classpath root on disk, with a trailing slash.
   * @param file                The file.
   * @return The resource name on the classpath.
   */
  private String toResourceNameOnClasspath(String classPathRootOnDisk, File file) {
    String fileName = file.getAbsolutePath().replace("\\", "/");
    //Cut off the part on disk leading to the root of the classpath
    //This leaves a resource name starting with the scanRootLocation,
    //   with no leading slash, containing subDirs and the fileName.
    return fileName.substring(classPathRootOnDisk.length());
  }
}
