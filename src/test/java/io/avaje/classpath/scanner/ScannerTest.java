package io.avaje.classpath.scanner;

import io.avaje.classpath.scanner.core.Location;
import io.avaje.classpath.scanner.core.Scanner;
import org.example.thing.SomeTestInterface;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ScannerTest {

  private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

  @Test
  public void testScanForResources() throws Exception {

    Scanner scanner = new Scanner(classLoader);
    List<Resource> resources = scanner.scanForResources(new Location("scantest"), FilterResource.bySuffix(".txt"));

    assertEquals(1, resources.size());
    Resource resource = resources.get(0);
    assertEquals("one.txt", resource.getFilename());
    assertNotNull(resource.getLocation());
    assertEquals("Hello", resource.loadAsString(StandardCharsets.UTF_8));

    resources = scanner.scanForResources("scantest", FilterResource.bySuffix(".txt"));
    assertEquals(1, resources.size());
    assertEquals("one.txt", resources.get(0).getFilename());
  }

  @Test
  public void testScanForClasses() throws Exception {


    ClassFilter predicate = SomeTestInterface.class::isAssignableFrom;

    Scanner scanner = new Scanner(classLoader);
    List<Class<?>> classes = scanner.scanForClasses(new Location("org/example/dummy"), predicate);

    assertEquals(3, classes.size());

    List<Class<?>> classes2 = scanner.scanForClasses("org/example/dummy", predicate);
    assertEquals(classes.size(), classes2.size());

    List<Class<?>> classes3 = scanner.scanForClasses("org.example.dummy", predicate);
    assertEquals(classes.size(), classes3.size());

  }
}