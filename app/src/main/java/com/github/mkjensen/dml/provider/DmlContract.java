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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link DmlProvider}.
 */
public final class DmlContract {

  /**
   * The authority for {@link DmlProvider}.
   */
  public static final String AUTHORITY = "com.github.mkjensen.dml.provider";

  /**
   * The {@code content://} style {@link Uri} to the authority for {@link DmlProvider}.
   */
  public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

  private DmlContract() {
  }

  /**
   * Constants for the categories table.
   */
  public static final class Categories implements CategoriesColumns, BaseColumns {

    static final String CONTENT_TYPE_ID = "category";

    static final String PATH = "categories";

    /**
     * The {@code content://} style {@link Uri} for this table.
     */
    public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon()
        .appendPath(PATH)
        .build();

    private Categories() {
    }

    /**
     * Builds a {@link Uri} for the requested {@link #ID}.
     */
    public static Uri buildCategoryUri(String categoryId) {
      return CONTENT_URI.buildUpon().appendPath(categoryId).build();
    }

    /**
     * Builds a {@link Uri} that references any {@link Videos} associated with the requested {@link
     * #ID}.
     */
    public static Uri buildVideosUri(String categoryId) {
      return CONTENT_URI.buildUpon().appendPath(categoryId).appendPath(Videos.PATH).build();
    }

    /**
     * Reads the {@link #ID} from the specified {@link Categories} {@link Uri}.
     */
    public static String getCategoryId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  /**
   * Constants for the videos table.
   */
  public static final class Videos implements VideosColumns, BaseColumns {

    static final String CONTENT_TYPE_ID = "video";

    static final String PATH = "videos";

    /**
     * The {@code content://} style {@link Uri} for this table.
     */
    public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon()
        .appendPath(PATH)
        .build();

    private Videos() {
    }

    /**
     * Builds a {@link Uri} for the requested {@link #ID}.
     */
    public static Uri buildVideoUri(String videoId) {
      return CONTENT_URI.buildUpon().appendPath(videoId).build();
    }

    /**
     * Reads the {@link #ID} from the specified {@link Videos} {@link Uri}.
     */
    public static String getVideoId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  /**
   * Columns of {@link Categories}.
   */
  interface CategoriesColumns {

    String ID = "id";

    String TITLE = "title";

    String URL = "url";
  }

  /**
   * Columns of {@link Videos}.
   */
  interface VideosColumns {

    String ID = "id";

    String TITLE = "title";

    String IMAGE_URL = "image_url";

    String DETAILS_URL = "details_url";

    String DESCRIPTION = "description";

    String LIST_URL = "list_url";

    String URL = "url";
  }

  /**
   * Columns for the many-to-many relation between {@link Categories} and {@link Videos}.
   */
  interface CategoriesVideosColumns {

    String CATEGORY_ID = "category_id";

    String VIDEO_ID = "video_id";
  }
}
