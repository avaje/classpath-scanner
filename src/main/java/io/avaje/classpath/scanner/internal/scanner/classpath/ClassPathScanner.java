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

import io.avaje.classpath.scanner.FilterResource;
import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.core.Location;
import io.avaje.classpath.scanner.internal.ScanLog;
import io.avaje.classpath.scanner.internal.EnvironmentDetection;
import io.avaje.classpath.scanner.internal.ResourceAndClassScanner;
import io.avaje.classpath.scanner.internal.UrlUtils;
import io.avaje.classpath.scanner.internal.scanner.classpath.jboss.JBossVFSv2UrlResolver;
import io.avaje.classpath.scanner.internal.scanner.classpath.jboss.JBossVFSv3ClassPathLocationScanner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/**
 * ClassPath scanner.
 */
public class ClassPathScanner implements ResourceAndClassScanner {

  private static final System.Logger log = ScanLog.log;

  /**
   * The ClassLoader for loading migrations on the classpath.
   */
  private final ClassLoader classLoader;

  /**
   * Cache location lookups.
   */
  private final Map<Location, List<URL>> locationUrlCache = new HashMap<>();

  /**
   * Cache location scanners.
   */
  private final Map<String, ClassPathLocationScanner> locationScannerCache = new HashMap<>();

  /**
   * Cache resource names.
   */
  private final Map<ClassPathLocationScanner, Map<URL, Set<String>>> resourceNameCache = new HashMap<>();
  private final boolean websphere;

  /**
   * Creates a new Classpath scanner.
   *
   * @param classLoader The ClassLoader for loading migrations on the classpath.
   */
  public ClassPathScanner(ClassLoader classLoader) {
    this.classLoader = classLoader;
    this.websphere = classLoader.getClass().getName().startsWith("com.ibm");
  }

  @Override
  public List<Resource> scanForResources(Location path, Predicate<String> predicate) {
    try {
      List<Resource> resources = new ArrayList<>();
      for (String resourceName : findResourceNames(path, predicate)) {
        resources.add(new ClassPathResource(resourceName, classLoader));
      }
      return resources;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public List<Class<?>> scanForClasses(Location location, Predicate<Class<?>> predicate) {
    try {
      List<Class<?>> classes = new ArrayList<>();

      Set<String> resourceNames = findResourceNames(location, FilterResource.bySuffix(".class"));
      log.log(Level.TRACE, "scan for classes at {0} found {1}", location, resourceNames.size());
      for (String resourceName : resourceNames) {
        String className = toClassName(resourceName);
        try {
          if (!"module-info".equals(className)) {
            Class<?> clazz = classLoader.loadClass(className);
            if (predicate.test(clazz)) {
              classes.add(clazz);
            }
          }
        } catch (NoClassDefFoundError | ClassNotFoundException err) {
          // This happens on class that inherits from another class which are no longer in the classpath
          // e.g. "public class MyTestRunner extends BlockJUnit4ClassRunner" and junit was in scope "provided"
          log.log(Level.DEBUG, "class " + className + " not loaded and will be ignored", err);
        }
      }
      return classes;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Converts this resource name to a fully qualified class name.
   *
   * @param resourceName The resource name.
   * @return The class name.
   */
  private String toClassName(String resourceName) {
    String nameWithDots = resourceName.replace("/", ".");
    return nameWithDots.substring(0, (nameWithDots.length() - ".class".length()));
  }

  /**
   * Finds the resources names present at this location and below on the classpath starting with this prefix and
   * ending with this suffix.
   */
  private Set<String> findResourceNames(Location location, Predicate<String> predicate) throws IOException {

    Set<String> resourceNames = new TreeSet<>();

    List<URL> locationsUrls = locationUrlsForPath(location);
    for (URL locationUrl : locationsUrls) {
      log.log(Level.TRACE, "scan {0}", locationUrl.toExternalForm());

      UrlResolver urlResolver = createUrlResolver(locationUrl.getProtocol());
      URL resolvedUrl = urlResolver.toStandardJavaUrl(locationUrl);

      String protocol = resolvedUrl.getProtocol();
      ClassPathLocationScanner classPathLocationScanner = createLocationScanner(protocol);
      if (classPathLocationScanner == null) {
        String scanRoot = UrlUtils.toFilePath(resolvedUrl);
        log.log(Level.WARNING, "Unable to scan location: {0} (unsupported protocol: {1})", scanRoot, protocol);
      } else {
        Set<String> names = resourceNameCache.get(classPathLocationScanner).get(resolvedUrl);
        if (names == null) {
          names = classPathLocationScanner.findResourceNames(location.path(), resolvedUrl);
          resourceNameCache.get(classPathLocationScanner).put(resolvedUrl, names);
        }
        resourceNames.addAll(names);
      }
    }

    return filterResourceNames(resourceNames, predicate);
  }

  /**
   * Gets the physical location urls for this logical path on the classpath.
   *
   * @param location The location on the classpath.
   * @return The underlying physical URLs.
   * @throws IOException when the lookup fails.
   */
  private List<URL> locationUrlsForPath(Location location) throws IOException {
    final List<URL> urls = locationUrlCache.get(location);
    if (urls != null) {
      return urls;
    }
    List<URL> locationUrls = new ArrayList<>();
    if (websphere) {
      loadWebsphereUrls(location, locationUrls);
    } else {
      loadLocationUrls(location, locationUrls);
    }
    locationUrlCache.put(location, locationUrls);
    return locationUrls;
  }

  private void loadLocationUrls(Location location, List<URL> locationUrls) throws IOException {
    Enumeration<URL> urls = classLoader.getResources(location.path());
    while (urls.hasMoreElements()) {
      locationUrls.add(urls.nextElement());
    }
  }

  private void loadWebsphereUrls(Location location, List<URL> locationUrls) throws IOException {
    Enumeration<URL> urls = classLoader.getResources(location.toString());
    while (urls.hasMoreElements()) {
      URL url = urls.nextElement();
      locationUrls.add(new URL(URLDecoder.decode(url.toExternalForm(), StandardCharsets.UTF_8)));
    }
  }

  /**
   * Creates an appropriate URL resolver scanner for this url protocol.
   *
   * @param protocol The protocol of the location url to scan.
   * @return The url resolver for this protocol.
   */
  private UrlResolver createUrlResolver(String protocol) {
    if (new EnvironmentDetection(classLoader).isJBossVFSv2() && protocol.startsWith("vfs")) {
      return new JBossVFSv2UrlResolver();
    }
    return new DefaultUrlResolver();
  }

  /**
   * Creates an appropriate location scanner for this url protocol.
   *
   * @param protocol The protocol of the location url to scan.
   * @return The location scanner or {@code null} if it could not be created.
   */
  private ClassPathLocationScanner createLocationScanner(String protocol) {
    final ClassPathLocationScanner scanner = locationScannerCache.get(protocol);
    if (scanner != null) {
      return scanner;
    }

    if ("file".equals(protocol)) {
      FileSystemClassPathLocationScanner locationScanner = new FileSystemClassPathLocationScanner();
      locationScannerCache.put(protocol, locationScanner);
      resourceNameCache.put(locationScanner, new HashMap<>());
      return locationScanner;
    }

    //zip - WebLogic, wsjar - WebSphere
    if ("jar".equals(protocol) || "zip".equals(protocol) || "wsjar".equals(protocol)) {
      JarFileClassPathLocationScanner locationScanner = new JarFileClassPathLocationScanner();
      locationScannerCache.put(protocol, locationScanner);
      resourceNameCache.put(locationScanner, new HashMap<>());
      return locationScanner;
    }

    EnvironmentDetection featureDetector = new EnvironmentDetection(classLoader);
    if (featureDetector.isJBossVFSv3() && "vfs".equals(protocol)) {
      JBossVFSv3ClassPathLocationScanner locationScanner = new JBossVFSv3ClassPathLocationScanner();
      locationScannerCache.put(protocol, locationScanner);
      resourceNameCache.put(locationScanner, new HashMap<>());
      return locationScanner;
    }
    // bundle - Felix, bundleresource - Equinox
    if (featureDetector.isOsgi() && ("bundle".equals(protocol) || "bundleresource".equals(protocol)) ) {
      OsgiClassPathLocationScanner locationScanner = new OsgiClassPathLocationScanner();
      locationScannerCache.put(protocol, locationScanner);
      resourceNameCache.put(locationScanner, new HashMap<>());
      return locationScanner;
    }
    return null;
  }

  /**
   * Filters this list of resource names to only include the ones whose filename matches this prefix and this suffix.
   */
  private Set<String> filterResourceNames(Set<String> resourceNames, Predicate<String> predicate) {
    Set<String> filteredResourceNames = new TreeSet<>();
    for (String resourceName : resourceNames) {
      if (predicate.test(resourceName)) {
        filteredResourceNames.add(resourceName);
      }
    }
    return filteredResourceNames;
  }
}
