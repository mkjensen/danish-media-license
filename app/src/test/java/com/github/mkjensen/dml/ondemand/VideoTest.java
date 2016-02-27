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

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link Video}.
 */
public class VideoTest {

  private static final String TITLE = "myTitle";
  private static final String IMAGE_URL = "myImageUrl";

  private Video.Builder builder;
  private Video video;

  /**
   * {@link #builder} and {@link #video} fields are shared.
   */
  @Before
  public void setUp() {

    // Given (shared)
    builder = new Video.Builder()
        .title(TITLE)
        .imageUrl(IMAGE_URL);
    video = builder.build();
  }

  @Test
  public void testBuilder() {

    // When
    Video video = builder.build();

    // Then
    assertEquals(TITLE, video.getTitle());
    assertEquals(IMAGE_URL, video.getImageUrl());
  }

  @Test
  public void testToString() {

    // When
    String videoToString = video.toString();

    // Then
    assertEquals(
        String.format("title: [%s], imageUrl: [%s]", video.getTitle(), video.getImageUrl()),
        videoToString);
  }
}
