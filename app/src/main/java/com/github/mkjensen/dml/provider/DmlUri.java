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

import static android.content.ContentResolver.CURSOR_DIR_BASE_TYPE;
import static android.content.ContentResolver.CURSOR_ITEM_BASE_TYPE;
import static com.github.mkjensen.dml.provider.DmlContract.AUTHORITY;

import android.net.Uri;

import com.github.mkjensen.dml.provider.DmlContract.Categories;
import com.github.mkjensen.dml.provider.DmlContract.CategoriesVideos;
import com.github.mkjensen.dml.provider.DmlContract.Videos;
import com.github.mkjensen.dml.provider.DmlDatabaseHelper.Tables;

/**
 * Contains the {@link Uri}s supported by {@link DmlProvider}. {@link DmlUri}s are matched against
 * {@link Uri}s by using {@link DmlUriMatcher}.
 */
enum DmlUri {

  // Implementation note: The ordering of the enum constants is important due to the matching rules
  // of UriMatcher which is used internally by DmlUriMatcher.

  /**
   * Categories.
   */
  CATEGORIES(10, Categories.PATH, Categories.CONTENT_TYPE_ID, false, Tables.CATEGORIES),

  /**
   * Categories by id.
   */
  CATEGORIES_ID(11, Categories.PATH + "/*", Categories.CONTENT_TYPE_ID, true, Tables.CATEGORIES),

  /**
   * Videos associated with a category.
   */
  CATEGORIES_ID_VIDEOS(12, CategoriesVideos.PATH, CategoriesVideos.CONTENT_TYPE_ID, false,
      Tables.CATEGORIES_VIDEOS),

  /**
   * Videos.
   */
  VIDEOS(20, Videos.PATH, Videos.CONTENT_TYPE_ID, false, Tables.VIDEOS),

  /**
   * Videos by id.
   */
  VIDEOS_ID(21, Videos.PATH + "/*", Videos.CONTENT_TYPE_ID, true, Tables.VIDEOS);

  private static final String CONTENT_TYPE = CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + ".";

  private static final String CONTENT_ITEM_TYPE = CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + ".";

  private final int code;

  private final String path;

  private final String contentType;

  private final String table;

  DmlUri(int code, String path, String contentTypeId, boolean item, String table) {
    this.code = code;
    this.path = path;
    this.contentType = (item ? CONTENT_ITEM_TYPE : CONTENT_TYPE) + contentTypeId;
    this.table = table;
  }

  int getCode() {
    return code;
  }

  String getPath() {
    return path;
  }

  String getContentType() {
    return contentType;
  }

  String getTable() {
    return table;
  }
}
