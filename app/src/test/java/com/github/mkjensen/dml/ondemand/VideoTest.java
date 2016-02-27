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

import org.junit.Test;

/**
 * Tests for {@link Video}.
 */
public class VideoTest {

  @Test
  public void testBuilder() {

    // Given
    String title = "My title";
    String imageUrl = "My imageUrl";
    Video.Builder builder = new Video.Builder()
        .title(title)
        .imageUrl(imageUrl);

    // When
    Video video = builder.build();

    // Then
    assertEquals(title, video.getTitle());
    assertEquals(imageUrl, video.getImageUrl());
  }
}
