package io.avaje.classpath.scanner;

import io.avaje.classpath.scanner.core.Location;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ScannerTest {

  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

  @Test
  public void testScanForResources() {

    DScanner scanner = new DScanner(classLoader);
    List<Resource> resources = scanner.scanForResources(new Location("scantest"), FilterResource.bySuffix(".txt"));

    assertEquals(1, resources.size());
    Resource resource = resources.get(0);
    assertEquals("one.txt", resource.fileName());
    assertNotNull(resource.location());
    assertEquals("Hello", resource.loadAsString(StandardCharsets.UTF_8));

    resources = scanner.scanForResources("scantest", FilterResource.bySuffix(".txt"));
    assertEquals(1, resources.size());
    assertEquals("one.txt", resources.get(0).fileName());
  }

}
