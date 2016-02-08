package org.avaje.classpath.scanner;

import org.avaje.classpath.scanner.internal.SomeTestInterface;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScannerTest {

  private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

  @Test
  public void testScanForResources() throws Exception {

    Scanner scanner = new Scanner(classLoader);
    List<Resource> resources = scanner.scanForResources(new Location("scantest"), ResourceMatch.bySuffix(".txt"));

    assertEquals(1, resources.size());
    Resource resource = resources.get(0);
    assertEquals("one.txt", resource.getFilename());
    assertNotNull(resource.getLocationOnDisk());
    assertNotNull(resource.getLocation());
    assertEquals("Hello", resource.loadAsString("UTF-8"));
    assertNotNull(resource.loadAsBytes());

    resources = scanner.scanForResources("scantest", ResourceMatch.bySuffix(".txt"));
    assertEquals(1, resources.size());
    assertEquals("one.txt", resources.get(0).getFilename());
  }

  @Test
  public void testScanForClasses() throws Exception {


    MatchClass predicate = new MatchClass() {
      @Override
      public boolean isMatch(Class<?> cls) {
        return SomeTestInterface.class.isAssignableFrom(cls);
      }
    };

    Scanner scanner = new Scanner(classLoader);
    List<Class<?>> classes = scanner.scanForClasses(new Location("org/avaje/classpath/scanner/test/dummy"), predicate);

    assertEquals(3, classes.size());

    List<Class<?>> classes2 = scanner.scanForClasses("org/avaje/classpath/scanner/test/dummy", predicate);
    assertEquals(classes.size(), classes2.size());

    List<Class<?>> classes3 = scanner.scanForClasses("org.avaje.classpath.scanner.test.dummy", predicate);
    assertEquals(classes.size(), classes3.size());

  }
}