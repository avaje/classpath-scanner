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
package org.avaje.classpath.scanner.internal.scanner.classpath.android;

import android.content.res.AssetManager;
import org.avaje.classpath.scanner.internal.FileCopyUtils;
import org.avaje.classpath.scanner.Resource;
import org.avaje.classpath.scanner.core.ClassPathScanException;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Resource within an Android App.
 */
public class AndroidResource implements Resource {
  private final AssetManager assetManager;
  private final String path;
  private final String name;

  public AndroidResource(AssetManager assetManager, String path, String name) {
    this.assetManager = assetManager;
    this.path = path;
    this.name = name;
  }

  public String toString() {
    return getLocation();
  }

  @Override
  public String getLocation() {
    return path + "/" + name;
  }

  @Override
  public String getLocationOnDisk() {
    return null;
  }

  @Override
  public String loadAsString(String encoding) {
    try {
      return FileCopyUtils.copyToString(new InputStreamReader(assetManager.open(getLocation()), encoding));
    } catch (IOException e) {
      throw new ClassPathScanException("Unable to load asset: " + getLocation(), e);
    }
  }

  @Override
  public byte[] loadAsBytes() {
    try {
      return FileCopyUtils.copyToByteArray(assetManager.open(getLocation()));
    } catch (IOException e) {
      throw new ClassPathScanException("Unable to load asset: " + getLocation(), e);
    }
  }

  @Override
  public String getFilename() {
    return name;
  }
}
