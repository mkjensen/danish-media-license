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

package com.github.mkjensen.dml.util;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.mkjensen.dml.DmlException;

/**
 * Helper methods for ensuring that supplied values meet expectations.
 */
public final class Preconditions {

  private Preconditions() {
  }

  /**
   * Returns {@code argument} if it is not {@code null}. Otherwise throws {@link
   * IllegalArgumentException}.
   */
  @NonNull
  public static <T> T notNull(@Nullable T argument) {
    return notNull(argument, null);
  }

  /**
   * Returns {@code argument} if it is not {@code null}. Otherwise throws {@link
   * IllegalArgumentException}.
   */
  @NonNull
  public static <T> T notNull(@Nullable T argument, @Nullable String message) {
    if (argument == null) {
      throw new IllegalArgumentException(message);
    }
    return argument;
  }

  /**
   * Returns extended data from the specified intent if it is not {@code null}. Otherwise throws
   * {@link DmlException}.
   */
  @NonNull
  public static String intentStringExtraNotNull(@NonNull Intent intent, @NonNull String name) {
    notNull(intent, "intent cannot be null");
    notNull(name, "name cannot be null");
    String extra = intent.getStringExtra(name);
    if (extra == null) {
      throw new DmlException(String.format("Intent [%s] must include string extra [%s]",
          intent, name));
    }
    return extra;
  }

  /**
   * Returns extended data from the specified intent if it is not {@code null}. Otherwise throws
   * {@link DmlException}.
   */
  @NonNull
  public static <T extends Parcelable> T intentParcelableExtraNotNull(@NonNull Intent intent,
                                                                      @NonNull String name) {
    notNull(intent, "intent cannot be null");
    notNull(name, "name cannot be null");
    T extra = intent.getParcelableExtra(name);
    if (extra == null) {
      throw new DmlException(String.format("Intent [%s] must include parcelable extra [%s]",
          intent, name));
    }
    return extra;
  }
}
