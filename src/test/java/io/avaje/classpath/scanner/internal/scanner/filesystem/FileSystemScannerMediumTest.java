/**
 * Copyright 2010-2016 Boxfuse GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.avaje.classpath.scanner.internal.scanner.filesystem;

import io.avaje.classpath.scanner.FilterResource;
import io.avaje.classpath.scanner.core.Location;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for FileSystemScanner.
 */
public class FileSystemScannerMediumTest {
    @Test
    public void nonExistentDirectory() throws Exception {
        new FileSystemScanner().scanForResources(new Location("filesystem:/invalid-path"), FilterResource.byPrefixSuffix("",""));
    }

    @Test
    public void emptyDirectory() throws IOException {
        File emptyDir = new File("./test/resources/migration/junk-empty");
        Set<String> resources = new FileSystemScanner().findResourceNamesFromFileSystem("junk-empty", emptyDir);
        assertTrue(resources.isEmpty());
    }
}
