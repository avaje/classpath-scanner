package org.avaje.classpath.scanner.core;

import org.avaje.classpath.scanner.ClassPathScanner;
import org.avaje.classpath.scanner.ClassPathScannerFactory;

/**
 * Service implementation of ClassPathScannerFactory.
 */
public class ScannerFactory implements ClassPathScannerFactory {

  @Override
  public ClassPathScanner createScanner(ClassLoader classLoader) {
    return new Scanner(classLoader);
  }
}
