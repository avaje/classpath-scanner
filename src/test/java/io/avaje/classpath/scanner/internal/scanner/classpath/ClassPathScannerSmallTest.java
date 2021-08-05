package io.avaje.classpath.scanner.internal.scanner.classpath;

import io.avaje.classpath.scanner.FilterResource;
import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.core.Location;
import org.example.dummy.DummyAbstractJdbcMigration;
import org.example.dummy.V4__DummyExtendedAbstractJdbcMigration;
import org.example.dummy.Version3dot5;
import org.example.thing.SomeTestInterface;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.mockito.internal.creation.MockSettingsImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ClassPathScanner.
 */
public class ClassPathScannerSmallTest {

  private final ClassPathScanner classPathScanner = new ClassPathScanner(Thread.currentThread().getContextClassLoader());

  @Test
  public void scanForResources() {

    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:migration/sql"), FilterResource.byPrefixSuffix("V", ".sql"));

    assertEquals(4, resources.size());

    assertEquals("migration/sql/V1.1__View.sql", resources.get(0).location());
    assertEquals("migration/sql/V1_2__Populate_table.sql", resources.get(1).location());
    assertEquals("migration/sql/V1__First.sql", resources.get(2).location());
    assertEquals("migration/sql/V2_0__Add_foreign_key_and_super_mega_humongous_padding_to_exceed_the_maximum_column_length_in_the_metadata_table.sql", resources.get(3).location());
  }

  @Test
  public void scanForResourcesRoot() {

    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:"), FilterResource.byPrefixSuffix("CheckValidate", ".sql"));

    //changed to 2 as new test cases are added for SybaseASE
    assertEquals(2, resources.size());

    Set<String> validPaths = new HashSet<>();
    validPaths.add("migration/validate/CheckValidate1__First.sql");
    validPaths.add("migration/dbsupport/sybaseASE/validate/CheckValidate1__First.sql");

    assertTrue(validPaths.contains(resources.get(0).location()));
    assertTrue(validPaths.contains(resources.get(1).location()));
  }

  @Test
  public void scanForResourcesSomewhereInSubDir() {

    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:migration"), FilterResource.byPrefixSuffix("CheckValidate", ".sql"));

    //changed to 2 as new test cases are added for SybaseASE
    assertEquals(2, resources.size());

    Set<String> validPaths = new HashSet<>();
    validPaths.add("migration/validate/CheckValidate1__First.sql");
    validPaths.add("migration/dbsupport/sybaseASE/validate/CheckValidate1__First.sql");

    assertTrue(validPaths.contains(resources.get(0).location()));
    assertTrue(validPaths.contains(resources.get(1).location()));
  }

  @Test
  public void scanForResourcesDefaultPackage() {
    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:"), FilterResource.byPrefixSuffix("logback", ""));

    assertEquals(1, resources.size());

    assertEquals("logback-test.xml", resources.get(0).location());
  }

  @Test
  public void scanForResourcesSubDirectory() {
    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:migration/subdir"), FilterResource.byPrefixSuffix("V", ".sql"));

    assertEquals(3, resources.size());

    assertEquals("migration/subdir/V1_1__Populate_table.sql", resources.get(0).location());
    assertEquals("migration/subdir/dir1/V1__First.sql", resources.get(1).location());
    assertEquals("migration/subdir/dir2/V2_0__Add_foreign_key.sql", resources.get(2).location());
  }

  @Test
  public void scanForResourcesInvalidPath() {
    classPathScanner.scanForResources(new Location("classpath:invalid"), FilterResource.byPrefixSuffix("V", ".sql"));
  }

  @Test
  public void scanForResourcesSplitDirectory() {
    List<Resource> resources =
        classPathScanner.scanForResources(new Location("classpath:migration/dbsupport"), FilterResource.byPrefixSuffix("create", ".sql"));

    assertTrue(resources.size() > 3);
    assertEquals("migration/dbsupport/db2/createDatabase.sql", resources.get(0).location());
  }

  @Test
  public void scanForResourcesJarFile() {

    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:org/junit/jupiter/api"), FilterResource.byPrefixSuffix("Af", ".class"));
    assertEquals(resources.size(), 5);

    assertEquals("org/junit/jupiter/api/AfterAll.class", resources.get(0).location());
    assertEquals("org/junit/jupiter/api/AfterEach.class", resources.get(1).location());
    assertEquals("org/junit/jupiter/api/extension/AfterAllCallback.class", resources.get(2).location());
  }

  @Test
  public void scanForClasses() {

    Predicate<Class<?>> predicate = getMatchClass(SomeTestInterface.class);

    List<Class<?>> classes = classPathScanner.scanForClasses(new Location("classpath:org/example/dummy"), predicate);

    assertEquals(3, classes.size());

    assertEquals(DummyAbstractJdbcMigration.class, classes.get(0));
    assertEquals(Version3dot5.class, classes.get(2));
    assertEquals(V4__DummyExtendedAbstractJdbcMigration.class, classes.get(1));
  }

  private Predicate<Class<?>> getMatchClass(final Class<?> someAssignable) {
    return someAssignable::isAssignableFrom;
  }

  @Test
  public void scanForClassesSplitPackage() {

    Predicate<Class<?>> predicate = getMatchClass(SomeTestInterface.class);
    List<Class<?>> classes = classPathScanner.scanForClasses(new Location("classpath:org/example"), predicate);

    assertEquals(4, classes.size());
  }

  @Test
  public void scanForClassesJarFile() {
    List<Class<?>> classes = classPathScanner.scanForClasses(new Location("classpath:org/mockito/internal/creation"), getMatchClass(MockSettings.class));

    assertTrue(classes.contains(MockSettingsImpl.class));
  }

  @Test
  public void scanForSpecificPathWhenMultiplePathsExist() {
    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:net/sourceforge/jtds/jdbc"), FilterResource.byPrefixSuffix("create", ".class"));
    for (Resource resource : resources) {
      assertFalse(resource.location().startsWith("net/sourceforge/jtds/jdbcx/"));
    }
  }
}
