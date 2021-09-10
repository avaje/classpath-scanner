package io.avaje.classpath.scanner.other;

import io.avaje.classpath.scanner.FilterResource;
import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.core.Location;
import io.avaje.classpath.scanner.core.Scanner;
import org.example.thing.SomeTestInterface;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScannerTest {

  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

  @Test
  public void testScanForResources() {

    Scanner scanner = new Scanner(classLoader);
    List<Resource> resources = scanner.scanForResources(new Location("scantest"), FilterResource.bySuffix(".txt"));

    assertEquals(1, resources.size());
    Resource resource = resources.get(0);
    assertEquals("one.txt", resource.name());
    assertNotNull(resource.location());
    assertEquals("Hello", resource.loadAsString(StandardCharsets.UTF_8));

    resources = scanner.scanForResources("scantest", FilterResource.bySuffix(".txt"));
    assertEquals(1, resources.size());
    assertEquals("one.txt", resources.get(0).name());
  }

  @Test
  public void testScanForClasses() {


    Predicate<Class<?>> predicate = SomeTestInterface.class::isAssignableFrom;

    Scanner scanner = new Scanner(classLoader);
    List<Class<?>> classes = scanner.scanForClasses(new Location("org/example/dummy"), predicate);

    assertEquals(3, classes.size());

    List<Class<?>> classes2 = scanner.scanForClasses("org/example/dummy", predicate);
    assertEquals(classes.size(), classes2.size());

    List<Class<?>> classes3 = scanner.scanForClasses("org.example.dummy", predicate);
    assertEquals(classes.size(), classes3.size());

  }
}
