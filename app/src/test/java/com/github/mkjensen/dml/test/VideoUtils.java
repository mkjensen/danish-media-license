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

import android.content.ContentValues;

import com.github.mkjensen.dml.backend.Video;
import com.github.mkjensen.dml.provider.DmlContract;

/**
 * Helper methods for working with {@link Video}.
 */
public final class VideoUtils {

  private static final String VIDEO_TITLE = "video_title";

  private static final String VIDEO_IMAGE_URL = "video_imageUrl";

  private static final String VIDEO_DESCRIPTION = "video_description";

  private static final String VIDEO_LINKS_URL = "video_linksUrl";

  private static final String VIDEO_URL = "video_url";

  private VideoUtils() {
  }

  /**
   * Creates a {@link Video.Builder} instance with values set using the specified {@code id}.
   */
  public static Video.Builder createVideoBuilder(String id) {
    return new Video.Builder()
        .id(id)
        .title(getVideoTitle(id))
        .imageUrl(getVideoImageUrl(id))
        .description(getVideoDescription(id))
        .linksUrl(getVideoLinksUrl(id))
        .url(getVideoUrl(id));
  }

  /**
   * Creates a {@link Video} instance with values set using the specified {@code id}.
   */
  public static Video createVideo(String id) {
    return createVideoBuilder(id).build();
  }

  /**
   * Creates a {@link ContentValues} instance representing a {@link Video} instance with values set
   * using the specified {@code id}.
   */
  public static ContentValues createContentValues(String id) {
    ContentValues values = new ContentValues();
    values.put(DmlContract.Video.VIDEO_ID, id);
    values.put(DmlContract.Video.VIDEO_TITLE, getVideoTitle(id));
    values.put(DmlContract.Video.VIDEO_IMAGE_URL, getVideoImageUrl(id));
    values.put(DmlContract.Video.VIDEO_DESCRIPTION, getVideoDescription(id));
    values.put(DmlContract.Video.VIDEO_LINKS_URL, getVideoLinksUrl(id));
    values.put(DmlContract.Video.VIDEO_URL, getVideoUrl(id));
    return values;
  }

  public static String getVideoTitle(String id) {
    return id + VIDEO_TITLE;
  }

  public static String getVideoImageUrl(String id) {
    return id + VIDEO_IMAGE_URL;
  }

  public static String getVideoDescription(String id) {
    return id + VIDEO_DESCRIPTION;
  }

  public static String getVideoLinksUrl(String id) {
    return id + VIDEO_LINKS_URL;
  }

  public static String getVideoUrl(String id) {
    return id + VIDEO_URL;
  }
}
