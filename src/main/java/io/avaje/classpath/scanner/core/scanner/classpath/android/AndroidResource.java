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

import android.content.res.AssetManager;
import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.core.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Resource within an Android App.
 */
class AndroidResource implements Resource {

  private final AssetManager assetManager;
  private final String path;
  private final String name;

  public AndroidResource(AssetManager assetManager, String path, String name) {
    this.assetManager = assetManager;
    this.path = path;
    this.name = name;
  }

  @Override
  public String toString() {
    return location();
  }

  @Override
  public String location() {
    return path + "/" + name;
  }

  @Override
  public String fileName() {
    return name;
  }

  @Override
  public InputStream inputStream() {
    try {
      return assetManager.open(location());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public List<String> loadAsLines(Charset charset) {
    return FileCopyUtils.readLines(inputStream(), charset);
  }

  @Override
  public String loadAsString(Charset charset) {
    try {
      return FileCopyUtils.copyToString(inputStream(), charset);
    } catch (IOException e) {
      throw new UncheckedIOException("Unable to load asset: " + location(), e);
    }
  }

}
