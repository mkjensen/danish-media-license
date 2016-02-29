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

package com.github.mkjensen.dml.ondemand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link Video}.
 */
public class VideoTest {

  private static final String SLUG = "My slug";
  private static final String TITLE = "My title";
  private static final String DESCRIPTION = "My description";
  private static final String IMAGE_URL = "My imageUrl";
  private static final String VIDEO_URL = "My videoUrl";

  private Video.Builder builder;
  private Video video;

  /**
   * {@link #builder} and {@link #video} fields are shared.
   */
  @Before
  public void before() {

    // Given/when (shared)
    builder = new Video.Builder()
        .slug(SLUG)
        .title(TITLE)
        .description(DESCRIPTION)
        .imageUrl(IMAGE_URL)
        .videoUrl(VIDEO_URL);
    video = builder.build();
  }

  @Test
  public void builder_givenValues_whenBuilt_thenVideoHasValues() {

    // Then
    assertNotNull(video);
    assertEquals(0, video.describeContents());
    assertEquals(SLUG, video.getSlug());
    assertEquals(TITLE, video.getTitle());
    assertEquals(DESCRIPTION, video.getDescription());
    assertEquals(IMAGE_URL, video.getImageUrl());
    assertEquals(VIDEO_URL, video.getVideoUrl());
  }

  @Test
  public void toString_whenSlugSet_thenContainsSlug() {

    // When
    String videoToString = video.toString();

    // Then
    assertEquals(String.format("Video{slug=%s}", video.getSlug()), videoToString);
  }
}
