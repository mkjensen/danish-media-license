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

package com.github.mkjensen.dml.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.os.Parcel;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation tests for {@link Video}.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class VideoAndroidTest {

  private Video video;

  @Before
  public void before() {
    video = new Video();
  }

  @Test
  public void writeToParcel_givenInput_whenInputWrittenAndOutputCreated_thenTheyMustBeEqual() {

    // Given
    video.setId("id");
    video.setTitle("title");
    video.setDescription("description");
    video.setImageUrl("imageUrl");
    video.setManifestUrl("manifestUrl");

    // When
    Parcel parcel = Parcel.obtain();
    video.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    Video output = Video.CREATOR.createFromParcel(parcel);
    parcel.recycle();

    // Then
    assertNotNull(output);
    assertEquals(0, video.describeContents());
    assertEquals(video.getId(), output.getId());
    assertEquals(video.getTitle(), output.getTitle());
    assertEquals(video.getDescription(), output.getDescription());
    assertEquals(video.getImageUrl(), output.getImageUrl());
    assertEquals(video.getManifestUrl(), output.getManifestUrl());
  }
}
