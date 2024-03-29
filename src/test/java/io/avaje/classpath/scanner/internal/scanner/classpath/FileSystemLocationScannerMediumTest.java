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

import io.avaje.classpath.scanner.internal.UrlUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for FileSystemClassPathLocationScanner.
 */
public class FileSystemLocationScannerMediumTest {

  @Test
  public void findResourceNamesFromFileSystem() throws Exception {

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    String path = UrlUtils.toFilePath(classLoader.getResources("migration").nextElement()) + File.separator;

    Set<String> resourceNames =
        new FileSystemClassPathLocationScanner().findResourceNamesFromFileSystem(path, "sql", new File(path, "sql"));

    assertEquals(4, resourceNames.size());
    String[] names = resourceNames.toArray(new String[4]);
    assertEquals("sql/V1.1__View.sql", names[0]);
    assertEquals("sql/V1_2__Populate_table.sql", names[1]);
    assertEquals("sql/V1__First.sql", names[2]);
    assertEquals("sql/V2_0__Add_foreign_key_and_super_mega_humongous_padding_to_exceed_the_maximum_column_length_in_the_metadata_table.sql", names[3]);
  }

  @Test
  public void findResourceNamesFromFileSystem_when_emptyDirectory() throws IOException {

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    String path = UrlUtils.toFilePath(classLoader.getResources("migration").nextElement()) + File.separator;

    Set<String> resources = new FileSystemClassPathLocationScanner().findResourceNamesFromFileSystem(path, "junk-empty", new File(path, "junk-empty"));
    assertTrue(resources.isEmpty());

  }

  @Test
  public void findResourceNamesNonExistantPath() throws Exception {

    URL url = new URL("file", null, 0, "X:\\dummy\\sql");

    Set<String> resourceNames = new FileSystemClassPathLocationScanner().findResourceNames("sql", url);

    assertEquals(0, resourceNames.size());
  }
}
