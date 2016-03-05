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

package com.github.mkjensen.dml.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * Matches {@link Uri}s to {@link DmlUri}s. Public methods of this class are thread-safe.
 */
final class DmlUriMatcher {

  /**
   * The {@link DmlUriMatcher} singleton returned by {@link #getInstance()}.
   */
  private static final DmlUriMatcher INSTANCE = new DmlUriMatcher();

  /**
   * A {@link UriMatcher} used to match {@link Uri}s to {@link DmlUri}s.
   */
  private final UriMatcher uriMatcher;

  /**
   * A map of {@link UriMatcher} codes to the {@link DmlUri}s registered for those codes.
   */
  private final SparseArray<DmlUri> codeToDmlUriMap;

  private DmlUriMatcher() {
    DmlUri[] dmlUris = DmlUri.values();
    uriMatcher = createUriMatcher(dmlUris);
    codeToDmlUriMap = createCodeToDmlUriMap(dmlUris);
  }

  private static UriMatcher createUriMatcher(DmlUri[] dmlUris) {
    UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    for (int i = 0; i < dmlUris.length; i++) {
      DmlUri dmlUri = dmlUris[i];
      uriMatcher.addURI(DmlContract.AUTHORITY, dmlUri.getPath(), dmlUri.getCode());
    }
    return uriMatcher;
  }

  private static SparseArray<DmlUri> createCodeToDmlUriMap(DmlUri[] dmlUris) {
    SparseArray<DmlUri> codeToDmlUriMap = new SparseArray<>(dmlUris.length);
    for (int i = 0; i < dmlUris.length; i++) {
      DmlUri dmlUri = dmlUris[i];
      codeToDmlUriMap.put(dmlUri.getCode(), dmlUri);
    }
    return codeToDmlUriMap;
  }

  /**
   * Returns the {@link DmlUriMatcher} singleton.
   *
   * @return the {@link DmlUriMatcher} singleton
   */
  static DmlUriMatcher getInstance() {
    return INSTANCE;
  }

  /**
   * Matches against the path in a {@link Uri} and returns the registered {@link DmlUri}.
   *
   * @param uri the {@link Uri} whose path to match against
   * @return the {@link DmlUri} registered for the {@link Uri}
   * @throws IllegalArgumentException if no {@link DmlUri} is registered for the {@link Uri}
   */
  DmlUri match(Uri uri) {
    int code = uriMatcher.match(uri);
    if (code == -1) {
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    return codeToDmlUriMap.get(code);
  }
}
