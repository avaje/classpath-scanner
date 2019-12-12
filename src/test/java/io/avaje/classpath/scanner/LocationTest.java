package io.avaje.classpath.scanner;


import io.avaje.classpath.scanner.core.Location;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LocationTest {

  @Test
  public void defaultPrefix() {
    Location location = new Location("db/migration");
    assertEquals("classpath:", location.getPrefix());
    assertTrue(location.isClassPath());
    assertEquals("db/migration", location.getPath());
    assertEquals("classpath:db/migration", location.getDescriptor());
  }

  @Test
  public void classpathPrefix() {
    Location location = new Location("classpath:db/migration");
    assertEquals("classpath:", location.getPrefix());
    assertTrue(location.isClassPath());
    assertEquals("db/migration", location.getPath());
    assertEquals("classpath:db/migration", location.getDescriptor());
  }

  @Test
  public void filesystemPrefix() {
    Location location = new Location("filesystem:db/migration");
    assertEquals("filesystem:", location.getPrefix());
    assertFalse(location.isClassPath());
    assertEquals("db/migration", location.getPath());
    assertEquals("filesystem:db/migration", location.getDescriptor());
  }

  @Test
  public void filesystemPrefixAbsolutePath() {
    Location location = new Location("filesystem:/db/migration");
    assertEquals("filesystem:", location.getPrefix());
    assertFalse(location.isClassPath());
    assertEquals("/db/migration", location.getPath());
    assertEquals("filesystem:/db/migration", location.getDescriptor());
  }

  @Test
  public void filesystemPrefixWithDotsInPath() {
    Location location = new Location("filesystem:util-2.0.4/db/migration");
    assertEquals("filesystem:", location.getPrefix());
    assertFalse(location.isClassPath());
    assertEquals("util-2.0.4/db/migration", location.getPath());
    assertEquals("filesystem:util-2.0.4/db/migration", location.getDescriptor());
  }
}