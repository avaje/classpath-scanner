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
package io.avaje.classpath.scanner.internal;

/**
 * Detects whether certain features are available or not.
 */
public class EnvironmentDetection {

  /**
   * The ClassLoader to use.
   */
  private final ClassLoader classLoader;

  /**
   * Creates a new FeatureDetector.
   *
   * @param classLoader The ClassLoader to use.
   */
  public EnvironmentDetection(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  /**
   * Flag indicating availability of JBoss VFS v2.
   */
  private Boolean jbossVFSv2;

  /**
   * Flag indicating availability of JBoss VFS v3.
   */
  private Boolean jbossVFSv3;

  /**
   * Flag indicating availability of the OSGi framework classes.
   */
  private Boolean osgi;

  /**
   * Checks whether JBoss VFS v2 is available.
   *
   * @return {@code true} if it is, {@code false if it is not}
   */
  public boolean isJBossVFSv2() {
    if (jbossVFSv2 == null) {
      jbossVFSv2 = isPresent("org.jboss.virtual.VFS", classLoader);
    }
    return jbossVFSv2;
  }

  /**
   * Checks whether JBoss VFS is available.
   *
   * @return {@code true} if it is, {@code false if it is not}
   */
  public boolean isJBossVFSv3() {
    if (jbossVFSv3 == null) {
      jbossVFSv3 = isPresent("org.jboss.vfs.VFS", classLoader);
    }
    return jbossVFSv3;
  }

  /**
   * Checks if OSGi framework is available.
   *
   * @return {@code true} if it is, {@code false if it is not}
   */
  public boolean isOsgi() {
    if (osgi == null) {
      osgi = isPresent("org.osgi.framework.Bundle", classLoader);
    }
    return osgi;
  }

  /**
   * Return true if the runtime is Andriod.
   */
  public static boolean isAndroid() {
    return "Android Runtime".equals(System.getProperty("java.runtime.name"));
  }

  /**
   * Determine whether the {@link Class} identified by the supplied name is present
   * and can be loaded. Will return {@code false} if either the class or
   * one of its dependencies is not present or cannot be loaded.
   *
   * @param className   the name of the class to check
   * @param classLoader The ClassLoader to use.
   * @return whether the specified class is present
   */
  private static boolean isPresent(String className, ClassLoader classLoader) {
    try {
      classLoader.loadClass(className);
      return true;
    } catch (Throwable ex) {
      // Class or one of its dependencies is not present...
      return false;
    }
  }
}
