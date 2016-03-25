/*
 * Copyright 2016 Martin Kamp Jensen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mkjensen.dml.test;

import com.github.mkjensen.dml.DmlException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper methods for loading test resources.
 */
public final class ResourceUtils {

  private ResourceUtils() {
  }

  /**
   * Loads a resource and converts it to a string.
   *
   * @param resName path to the resource to load, relative to {@code src/test/resources}
   */
  public static String loadAsString(String resName) {
    try (InputStream stream = ResourceUtils.class.getClassLoader().getResourceAsStream(resName)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      StringBuilder result = new StringBuilder(stream.available());
      String line;
      while ((line = reader.readLine()) != null) {
        result.append(line);
      }
      return result.toString();
    } catch (IOException ex) {
      throw new DmlException(ex);
    }
  }
}
