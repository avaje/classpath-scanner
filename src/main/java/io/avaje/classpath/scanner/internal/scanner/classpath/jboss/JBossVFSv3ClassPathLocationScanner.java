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
package io.avaje.classpath.scanner.internal.scanner.classpath.jboss;

import io.avaje.classpath.scanner.internal.UrlUtils;
import io.avaje.classpath.scanner.internal.scanner.classpath.ClassPathLocationScanner;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * ClassPathLocationScanner for JBoss VFS v3.
 */
public class JBossVFSv3ClassPathLocationScanner implements ClassPathLocationScanner {

  public Set<String> findResourceNames(String location, URL locationUrl) throws IOException {
    String filePath = UrlUtils.toFilePath(locationUrl);
    String classPathRootOnDisk = filePath.substring(0, filePath.length() - location.length());
    if (!classPathRootOnDisk.endsWith("/")) {
      classPathRootOnDisk = classPathRootOnDisk + "/";
    }
    Set<String> resourceNames = new TreeSet<>();
    List<VirtualFile> files = VFS.getChild(filePath).getChildrenRecursively(VirtualFile::isFile);
    for (VirtualFile file : files) {
      resourceNames.add(file.getPathName().substring(classPathRootOnDisk.length()));
    }
    return resourceNames;
  }

}
