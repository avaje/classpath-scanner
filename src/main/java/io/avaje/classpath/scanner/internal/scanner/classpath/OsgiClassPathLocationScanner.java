/*
  Copyright 2010-2016 Boxfuse GmbH
  <p/>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p/>
  http://www.apache.org/licenses/LICENSE-2.0
  <p/>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package io.avaje.classpath.scanner.internal.scanner.classpath;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OSGi specific scanner that performs the migration search in
 * the current bundle's classpath.
 *
 * <p>
 * The resources that this scanner returns can only be loaded if
 * Flyway's ClassLoader has access to the bundle that contains the migrations.
 * </p>
 */
public class OsgiClassPathLocationScanner implements ClassPathLocationScanner {

  //Felix and Equinox "host" resource url pattern starts with bundleId, which is
  // long according osgi core specification
  private static final Pattern bundleIdPattern = Pattern.compile("^\\d+");

  public Set<String> findResourceNames(String location, URL locationUrl) {
    Set<String> resourceNames = new TreeSet<>();

    Bundle bundle = targetBundleOrCurrent(FrameworkUtil.getBundle(getClass()), locationUrl);
    @SuppressWarnings({"unchecked"})
    Enumeration<URL> entries = bundle.findEntries(locationUrl.getPath(), "*", true);
    if (entries != null) {
      while (entries.hasMoreElements()) {
        resourceNames.add(pathWithoutLeadingSlash(entries.nextElement()));
      }
    }
    return resourceNames;
  }

  private Bundle targetBundleOrCurrent(Bundle currentBundle, URL locationUrl) {
    try {
      Bundle targetBundle = currentBundle.getBundleContext().getBundle(bundleId(locationUrl.getHost()));
      return targetBundle != null ? targetBundle : currentBundle;
    } catch (Exception e) {
      return currentBundle;
    }
  }

  private long bundleId(String host) {
    final Matcher matcher = bundleIdPattern.matcher(host);
    if (matcher.find()) {
      return Double.valueOf(matcher.group()).longValue();
    }
    throw new IllegalArgumentException("There's no bundleId in passed URL");
  }

  private String pathWithoutLeadingSlash(URL entry) {
    final String path = entry.getPath();
    return path.startsWith("/") ? path.substring(1) : path;
  }
}
