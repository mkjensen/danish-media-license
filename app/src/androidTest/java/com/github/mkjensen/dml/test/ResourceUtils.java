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

import android.content.res.Resources;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;

import com.github.mkjensen.dml.DmlException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper methods for test resources.
 */
public final class ResourceUtils {

  private ResourceUtils() {
  }

  /**
   * Returns a string by using the instrumentation target context.
   */
  public static String getString(@StringRes int resId) {
    return InstrumentationRegistry.getTargetContext().getString(resId);
  }

  /**
   * Loads a resource and converts it to a string.
   */
  public static String loadAsString(@RawRes int resId) {
    Resources resources = InstrumentationRegistry.getContext().getResources();
    try (InputStream stream = resources.openRawResource(resId)) {
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
