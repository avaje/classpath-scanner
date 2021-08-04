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
package io.avaje.classpath.scanner.core.scanner.classpath.android;

import android.content.Context;
import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.core.Location;
import io.avaje.classpath.scanner.core.ResourceAndClassScanner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Class & resource scanner for Android.
 */
public class AndroidScanner implements ResourceAndClassScanner {

  private final Context context;

  public AndroidScanner() {
    context = AndriodContextHolder.getContext();
    if (context == null) {
      throw new IllegalStateException("Unable to create scanner. " +
          "Within an activity you can fix this with org.avaje.classpath.scanner.android.ContextHolder.setContext(this);");
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

}
