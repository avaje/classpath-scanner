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
package io.avaje.classpath.scanner.core;

import android.content.Context;

/**
 * Holds an Android context. The context must be set for Flyway to be able to scan assets and classes for migrations.
 *
 * <p>
 *     You can set this within an activity using ContextHolder.setContext(this);
 * </p>
 */
public class AndriodContextHolder {
  private AndriodContextHolder() {
  }

  /**
   * The Android context to use.
   */
  private static Context context;

  /**
   * @return The Android context to use to be able to scan assets and classes for migrations.
   */
  public static Context getContext() {
    return context;
  }

  /**
   * @param context The Android context to use to be able to scan assets and classes for migrations.
   */
  public static void setContext(Context context) {
    AndriodContextHolder.context = context;
  }
}
