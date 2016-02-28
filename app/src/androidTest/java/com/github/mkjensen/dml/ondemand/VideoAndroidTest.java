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

import static junit.framework.Assert.assertEquals;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests for {@link Video}.
 */
@RunWith(AndroidJUnit4.class)
public class VideoAndroidTest {

  @Test
  public void testParcelable() {

    // Given
    Video.Builder builder = new Video.Builder()
        .slug("My slug")
        .title("My title")
        .description("My description")
        .imageUrl("My imageUrl");
    Video video = builder.build();

    // When
    Parcel parcel = Parcel.obtain();
    video.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    Video parceledVideo = Video.CREATOR.createFromParcel(parcel);

    // Then
    assertEquals(video.getSlug(), parceledVideo.getSlug());
    assertEquals(video.getTitle(), parceledVideo.getTitle());
    assertEquals(video.getDescription(), parceledVideo.getDescription());
    assertEquals(video.getImageUrl(), parceledVideo.getImageUrl());
  }
}
