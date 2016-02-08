package org.avaje.classpath.scanner;

/**
 * Filter predicate to determine which scanned classes should be included.
 */
public interface MatchClass {

  /**
   * Return true if this class should be included in the scan result.
   */
  boolean isMatch(Class<?> cls);
}
