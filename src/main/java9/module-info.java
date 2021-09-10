module io.avaje.classpath.scanner {

  requires transitive org.slf4j;
  requires transitive io.avaje.classpath.scanner.api;
  requires static org.eclipse.osgi;
  requires static android;
  requires static jboss.vfs;

  exports io.avaje.classpath.scanner.core;

  provides io.avaje.classpath.scanner.ClassPathScannerFactory with io.avaje.classpath.scanner.core.ScannerFactory;
}
