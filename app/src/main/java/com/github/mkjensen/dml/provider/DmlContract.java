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
   * Constants for the category table.
   */
  public static final class Category implements DmlDatabase.CategoryColumns, BaseColumns {

    static final String CONTENT_TYPE_ID = "category";

    static final String PATH = "categories";

    /**
     * The {@code content://} style {@link Uri} for this table.
     */
    public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath(PATH).build();

    private Category() {
    }

    /**
     * Builds a {@link Uri} for the requested {@link #CATEGORY_ID}.
     */
    public static Uri buildCategoryUri(String categoryId) {
      return CONTENT_URI.buildUpon().appendPath(categoryId).build();
    }

    /**
     * Returns the {@link #CATEGORY_ID} from the specified {@link Category} {@link Uri}.
     */
    public static String getCategoryId(Uri uri) {
      return uri.getPathSegments().get(1);
    }

    /**
     * Builds a {@link Uri} that references any {@link Video} associated with the requested {@link
     * Category#CATEGORY_ID}.
     */
    public static Uri buildVideosUri(String categoryId) {
      return Category.buildCategoryUri(categoryId).buildUpon().appendPath(Video.PATH).build();
    }
  }

  /**
   * Constants for the video table.
   */
  public static final class Video implements DmlDatabase.VideoColumns, BaseColumns {

    static final String CONTENT_TYPE_ID = "video";

    static final String PATH = "videos";

    /**
     * The {@code content://} style {@link Uri} for this table.
     */
    public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath(PATH).build();

    private Video() {
    }

    /**
     * Builds a {@link Uri} for the requested {@link #VIDEO_ID}.
     */
    public static Uri buildVideoUri(String videoId) {
      return CONTENT_URI.buildUpon().appendPath(videoId).build();
    }

    /**
     * Returns the {@link #VIDEO_ID} from the specified {@link Video} {@link Uri}.
     */
    public static String getVideoId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }
}
