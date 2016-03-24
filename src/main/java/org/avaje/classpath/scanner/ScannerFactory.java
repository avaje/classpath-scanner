package org.avaje.classpath.scanner;

/**
 * Service implementation of ClassPathScannerFactory.
 */
public class ScannerFactory implements ClassPathScannerFactory {

  @Override
  public ClassPathScanner createScanner(ClassLoader classLoader) {
    return new Scanner(classLoader);
  }
}
