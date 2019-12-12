/**
 * Copyright 2010-2016 Boxfuse GmbH
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.avaje.classpath.scanner.internal.scanner.classpath;

import io.avaje.classpath.scanner.core.Location;
import io.avaje.classpath.scanner.ClassFilter;
import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.FilterResource;
import org.example.thing.SomeTestInterface;
import org.example.dummy.DummyAbstractJdbcMigration;
import org.example.dummy.V4__DummyExtendedAbstractJdbcMigration;
import org.example.dummy.Version3dot5;
import org.testng.annotations.Test;
import org.mockito.MockSettings;
import org.mockito.internal.creation.MockSettingsImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests for ClassPathScanner.
 */
public class ClassPathScannerSmallTest {

  private ClassPathScanner classPathScanner = new ClassPathScanner(Thread.currentThread().getContextClassLoader());

  @Test
  public void scanForResources() throws Exception {

    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:migration/sql"), FilterResource.byPrefixSuffix("V", ".sql"));

    assertEquals(4, resources.size());

    assertEquals("migration/sql/V1.1__View.sql", resources.get(0).getLocation());
    assertEquals("migration/sql/V1_2__Populate_table.sql", resources.get(1).getLocation());
    assertEquals("migration/sql/V1__First.sql", resources.get(2).getLocation());
    assertEquals("migration/sql/V2_0__Add_foreign_key_and_super_mega_humongous_padding_to_exceed_the_maximum_column_length_in_the_metadata_table.sql", resources.get(3).getLocation());
  }

  @Test
  public void scanForResourcesRoot() throws Exception {

    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:"), FilterResource.byPrefixSuffix("CheckValidate", ".sql"));

    //changed to 2 as new test cases are added for SybaseASE
    assertEquals(2, resources.size());

    Set<String> validPaths = new HashSet<String>();
    validPaths.add("migration/validate/CheckValidate1__First.sql");
    validPaths.add("migration/dbsupport/sybaseASE/validate/CheckValidate1__First.sql");

    assertEquals(true, validPaths.contains(resources.get(0).getLocation()));
    assertEquals(true, validPaths.contains(resources.get(1).getLocation()));
  }

  @Test
  public void scanForResourcesSomewhereInSubDir() throws Exception {

    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:migration"), FilterResource.byPrefixSuffix("CheckValidate", ".sql"));

    //changed to 2 as new test cases are added for SybaseASE
    assertEquals(2, resources.size());

    Set<String> validPaths = new HashSet<String>();
    validPaths.add("migration/validate/CheckValidate1__First.sql");
    validPaths.add("migration/dbsupport/sybaseASE/validate/CheckValidate1__First.sql");

    assertEquals(true, validPaths.contains(resources.get(0).getLocation()));
    assertEquals(true, validPaths.contains(resources.get(1).getLocation()));
  }

  @Test
  public void scanForResourcesDefaultPackage() throws Exception {
    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:"), FilterResource.byPrefixSuffix("logback", ""));

    assertEquals(1, resources.size());

    assertEquals("logback-test.xml", resources.get(0).getLocation());
  }

  @Test
  public void scanForResourcesSubDirectory() throws Exception {
    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:migration/subdir"), FilterResource.byPrefixSuffix("V", ".sql"));

    assertEquals(3, resources.size());

    assertEquals("migration/subdir/V1_1__Populate_table.sql", resources.get(0).getLocation());
    assertEquals("migration/subdir/dir1/V1__First.sql", resources.get(1).getLocation());
    assertEquals("migration/subdir/dir2/V2_0__Add_foreign_key.sql", resources.get(2).getLocation());
  }

  @Test
  public void scanForResourcesInvalidPath() throws Exception {
    classPathScanner.scanForResources(new Location("classpath:invalid"), FilterResource.byPrefixSuffix("V", ".sql"));
  }

  @Test
  public void scanForResourcesSplitDirectory() throws Exception {
    List<Resource> resources =
        classPathScanner.scanForResources(new Location("classpath:migration/dbsupport"), FilterResource.byPrefixSuffix("create", ".sql"));

    assertTrue(resources.size() > 3);
    assertEquals("migration/dbsupport/db2/createDatabase.sql", resources.get(0).getLocation());
  }

  @Test
  public void scanForResourcesJarFile() throws Exception {

    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:org/testng"), FilterResource.byPrefixSuffix("Af", ".class"));
    assertEquals(resources.size(), 6);

    assertEquals("org/testng/annotations/AfterClass.class", resources.get(0).getLocation());
    assertEquals("org/testng/annotations/AfterGroups.class", resources.get(1).getLocation());
    assertEquals("org/testng/annotations/AfterMethod.class", resources.get(2).getLocation());
  }

  @Test
  public void scanForClasses() throws Exception {

    ClassFilter predicate = getMatchClass(SomeTestInterface.class);

    List<Class<?>> classes = classPathScanner.scanForClasses(new Location("classpath:org/example/dummy"), predicate);

    assertEquals(3, classes.size());

    assertEquals(DummyAbstractJdbcMigration.class, classes.get(0));
    assertEquals(Version3dot5.class, classes.get(2));
    assertEquals(V4__DummyExtendedAbstractJdbcMigration.class, classes.get(1));
  }

  private ClassFilter getMatchClass(final Class<?> someAssignable) {
    return new ClassFilter() {
      @Override
      public boolean isMatch(Class<?> cls) {
        return someAssignable.isAssignableFrom(cls);
      }
    };
  }

  @Test
  public void scanForClassesSplitPackage() throws Exception {

    ClassFilter predicate = getMatchClass(SomeTestInterface.class);
    List<Class<?>> classes = classPathScanner.scanForClasses(new Location("classpath:org/example"), predicate);

    assertEquals(4, classes.size());
  }

  @Test
  public void scanForClassesJarFile() throws Exception {
    List<Class<?>> classes = classPathScanner.scanForClasses(new Location("classpath:org/mockito/internal/creation"), getMatchClass(MockSettings.class));

    assertTrue(classes.contains(MockSettingsImpl.class));
  }

  @Test
  public void scanForSpecificPathWhenMultiplePathsExist() throws Exception {
    List<Resource> resources = classPathScanner.scanForResources(new Location("classpath:net/sourceforge/jtds/jdbc"), FilterResource.byPrefixSuffix("create", ".class"));
    for (Resource resource : resources) {
      assertFalse(resource.getLocation().startsWith("net/sourceforge/jtds/jdbcx/"));
    }
  }
}
