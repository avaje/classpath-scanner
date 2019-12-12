package org.avaje.classpath.scanner.core;

import io.avaje.classpath.scanner.ClassPathScanner;
import io.avaje.classpath.scanner.ClassPathScannerFactory;

/**
 * Service implementation of ClassPathScannerFactory.
 */
public class ScannerFactory implements ClassPathScannerFactory {

  @Override
  public ClassPathScanner createScanner(ClassLoader classLoader) {
    return new Scanner(classLoader);
  }
}
