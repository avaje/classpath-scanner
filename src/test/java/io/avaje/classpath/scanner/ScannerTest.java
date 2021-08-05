package io.avaje.classpath.scanner;

import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ScannerTest {

  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

  @Test
  public void testScanForResources() throws IOException {

    DScanner scanner = new DScanner(classLoader);
    List<Resource> resources = scanner.scan("scantest", FilterResource.bySuffix(".txt"));

    assertEquals(1, resources.size());
    Resource resource = resources.get(0);
    assertEquals("one.txt", resource.fileName());
    assertNotNull(resource.location());
    assertEquals("Hello", resource.loadAsString(StandardCharsets.UTF_8));

    final List<String> lines = resource.loadAsLines(StandardCharsets.UTF_8);
    assertThat(lines).contains("Hello");

    final InputStream inputStream = resource.inputStream();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      assertThat(br.readLine()).isEqualTo("Hello");
    }

    resources = scanner.scan("scantest", FilterResource.bySuffix(".txt"));
    assertEquals(1, resources.size());
    assertEquals("one.txt", resources.get(0).fileName());
  }

}
