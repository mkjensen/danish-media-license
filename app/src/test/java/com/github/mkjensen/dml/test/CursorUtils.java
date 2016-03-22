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

import static org.junit.Assert.assertFalse;

import android.database.Cursor;

/**
 * Helper methods for working with {@link Cursor}.
 */
public final class CursorUtils {

  private CursorUtils() {
  }

  /**
   * Delegates to {@link Cursor#getString(int)}, using {@link #getColumnIndex(Cursor, String)} to
   * get the column index for the specified column name.
   */
  public static String getString(Cursor cursor, String columnName) {
    int columnIndex = getColumnIndex(cursor, columnName);
    return cursor.getString(columnIndex);
  }

  private static int getColumnIndex(Cursor cursor, String columnName) {
    int columnIndex = cursor.getColumnIndex(columnName);
    assertFalse(columnIndex == -1);
    return columnIndex;
  }
}
