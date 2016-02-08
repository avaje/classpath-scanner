package org.avaje.classpath.scanner;

/**
 * Filter predicate to determine which scanned resources should be returned.
 */
public interface MatchResource {

  /**
   * Return true if this resource should be included in the scan result.
   */
  boolean isMatch(String resourceName);
}
