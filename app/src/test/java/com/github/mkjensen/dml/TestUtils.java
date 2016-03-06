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

package com.github.mkjensen.dml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Various utilities for testing.
 */
public final class TestUtils {

  private TestUtils() {
  }

  /**
   * Checks whether or not the specified class has a private parameterless constructor. The
   * constructor is invoked via reflection for code coverage purposes.
   *
   * @return {@code true} if {@code clazz} has a private parameterless contructor, {@code false}
   * otherwise
   */
  public static boolean hasPrivateParameterlessConstructor(Class<?> clazz) {
    Constructor<?> constructor;
    try {
      constructor = clazz.getDeclaredConstructor();
    } catch (NoSuchMethodException ex) {
      return false;
    }
    satisfyCodeCoverage(constructor);
    return Modifier.isPrivate(constructor.getModifiers());
  }

  private static void satisfyCodeCoverage(Constructor<?> constructor) {
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
    } catch (ReflectiveOperationException ex) {
      // Ignored.
    }
  }
}
