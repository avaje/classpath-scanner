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
package io.avaje.classpath.scanner.internal.scanner.classpath.android;

import android.content.Context;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.core.AndriodContextHolder;
import io.avaje.classpath.scanner.core.Location;
import io.avaje.classpath.scanner.internal.ScanLog;
import io.avaje.classpath.scanner.internal.ResourceAndClassScanner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;

/**
 * Class & resource scanner for Android.
 */
public class AndroidScanner implements ResourceAndClassScanner {

  private final Context context;

  private final PathClassLoader classLoader;

  public AndroidScanner(ClassLoader classLoader) {
    this.classLoader = (PathClassLoader) classLoader;
    context = AndriodContextHolder.getContext();
    if (context == null) {
      throw new IllegalStateException("Unable to create scanner. Within an activity fix this with io.avaje.classpath.scanner.android.ContextHolder.setContext(this);");
    }
  }

  public List<Resource> scanForResources(Location location, Predicate<String> predicate) {
    try {
      List<Resource> resources = new ArrayList<>();
      String path = location.path();
      for (String asset : context.getAssets().list(path)) {
        if (predicate.test(asset)) {
          resources.add(new AndroidResource(context.getAssets(), path, asset));
        }
      }
      return resources;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public List<Class<?>> scanForClasses(Location location, Predicate<Class<?>> predicate) {
    try {
      String pkg = location.path().replace("/", ".");
      List<Class<?>> classes = new ArrayList<>();
      DexFile dex = new DexFile(context.getApplicationInfo().sourceDir);
      Enumeration<String> entries = dex.entries();
      while (entries.hasMoreElements()) {
        String className = entries.nextElement();
        if (className.startsWith(pkg)) {
          Class<?> clazz = classLoader.loadClass(className);
          if (predicate.test(clazz)) {
            classes.add(clazz);
            ScanLog.log.trace("found {}", className);
          }
        }
      }
      return classes;

    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
