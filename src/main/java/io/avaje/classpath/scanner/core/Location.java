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
package io.avaje.classpath.scanner.core;


/**
 * A starting location to scan from.
 */
public final class Location implements Comparable<Location> {

  /**
   * The prefix for classpath locations.
   */
  private static final String CLASSPATH_PREFIX = "classpath:";

  /**
   * The prefix for filesystem locations.
   */
  private static final String FILESYSTEM_PREFIX = "filesystem:";

  /**
   * The prefix part of the location. Can be either classpath: or filesystem:.
   */
  private final String prefix;

  /**
   * The path part of the location.
   */
  private String path;

  /**
   * Creates a new location.
   *
   * @param descriptor The location descriptor.
   */
  public Location(String descriptor) {
    String normalizedDescriptor = descriptor.trim().replace("\\", "/");

    if (normalizedDescriptor.contains(":")) {
      prefix = normalizedDescriptor.substring(0, normalizedDescriptor.indexOf(":") + 1);
      path = normalizedDescriptor.substring(normalizedDescriptor.indexOf(":") + 1);
    } else {
      prefix = CLASSPATH_PREFIX;
      path = normalizedDescriptor;
    }

    if (isClassPath()) {
      path = path.replace(".", "/");
      if (path.startsWith("/")) {
        path = path.substring(1);
      }
    } else {
      if (!isFileSystem()) {
        throw new IllegalStateException("Unknown prefix, should be either filesystem: or classpath: " + normalizedDescriptor);
      }
    }
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
  }

  /**
   * Return true if this denotes a classpath location.
   */
  public boolean isClassPath() {
    return CLASSPATH_PREFIX.equals(prefix);
  }

  /**
   * Return true if this denotes a filesystem location.
   */
  public boolean isFileSystem() {
    return FILESYSTEM_PREFIX.equals(prefix);
  }

  /**
   * Return the path part of the location.
   */
  public String path() {
    return path;
  }

  /**
   * Return the prefix denoting classpath of filesystem.
   */
  public String prefix() {
    return prefix;
  }

  /**
   * Return the complete location descriptor.
   */
  public String descriptor() {
    return prefix + path;
  }

  public int compareTo(Location o) {
    return descriptor().compareTo(o.descriptor());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Location location = (Location) o;
    return descriptor().equals(location.descriptor());
  }

  @Override
  public int hashCode() {
    return descriptor().hashCode();
  }

  @Override
  public String toString() {
    return descriptor();
  }
}
