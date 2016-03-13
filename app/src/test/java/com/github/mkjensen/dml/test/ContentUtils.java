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

import static org.junit.Assert.assertEquals;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.github.mkjensen.dml.provider.DmlContract;
import com.github.mkjensen.dml.provider.DmlProvider;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowContentResolver;

/**
 * Helper methods for working with {@link ContentProvider} and {@link ContentResolver}.
 */
public final class ContentUtils {

  private ContentUtils() {
  }

  /**
   * Registers {@link DmlProvider} for {@link DmlContract#AUTHORITY}. Also asserts that {@link
   * DmlProvider#onCreate()} returns {@code true}.
   */
  public static void registerContentProvider() {
    ContentProvider contentProvider = new DmlProvider();
    assertEquals(true, contentProvider.onCreate());
    ShadowContentResolver.registerProvider(DmlContract.AUTHORITY, contentProvider);
  }

  /**
   * Returns the {@link ContentResolver} from Robolectric.
   */
  public static ContentResolver getContentResolver() {
    return RuntimeEnvironment.application.getContentResolver();
  }

  /**
   * Delegates to {@link ContentResolver#query(Uri, String[], String, String[], String)}.
   */
  public static Cursor query(ContentResolver contentResolver, Uri uri) {
    return contentResolver.query(
        uri,
        null, // projection
        null, // selection
        null, // selectionArgs
        null // sortOrder
    );
  }

  /**
   * Delegates to {@link ContentResolver#delete(Uri, String, String[])}.
   */
  public static int delete(ContentResolver contentResolver, Uri uri) {
    return contentResolver.delete(
        uri,
        null, // where
        null); // selectionArgs
  }

  /**
   * Delegates to {@link ContentResolver#update(Uri, android.content.ContentValues, String,
   * String[])}.
   */
  public static int update(ContentResolver contentResolver, Uri uri) {
    return contentResolver.update(
        uri,
        null, // values
        null, // where
        null); // selectionArgs
  }
}
