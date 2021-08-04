package io.avaje.classpath.scanner;

import java.util.List;

/**
 * Scans the class path for resources or classes.
 */
public interface Scanner {

  static Scanner of(ClassLoader classLoader) {
    return new DScanner(classLoader);
  }

  /**
   * Scan for file resources using the starting location and filter.
   *
   * @param location The path location from which the scan will start.
   * @param filter   The filter used to match resources.
   * @return The list of resources found that match our filter.
   */
  List<Resource> scanForResources(String location, ResourceFilter filter);

}
