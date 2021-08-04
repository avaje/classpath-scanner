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

import io.avaje.classpath.scanner.core.Location;
import io.avaje.classpath.scanner.ClassFilter;
import io.avaje.classpath.scanner.ResourceFilter;
import io.avaje.classpath.scanner.Resource;

import java.util.List;

/**
 * Scanner for both resources and classes.
 */
public interface ResourceAndClassScanner {
  /**
   * Scans the classpath for resources under the specified location, starting with the specified prefix and ending with
   * the specified suffix.
   *
   * @param location  The location in the classpath to start searching. Subdirectories are also searched.
   * @param predicate The predicate used to match the resource names.
   * @return The resources that were found.
   */
  List<Resource> scanForResources(Location location, ResourceFilter predicate);

}
