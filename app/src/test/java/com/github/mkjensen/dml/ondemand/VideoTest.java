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
import static org.junit.Assert.assertNull;

import android.os.Parcel;

import com.github.mkjensen.dml.backend.Video;
import com.github.mkjensen.dml.test.RobolectricTest;
import com.github.mkjensen.dml.test.VideoUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link Video}.
 */
public class VideoTest extends RobolectricTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void builder_givenValues_whenBuilt_thenVideoHasValues() {

    // Given
    String id = "id";
    Video.Builder videoBuilder = VideoUtils.createVideoBuilder(id);

    // When
    Video video = videoBuilder.build();

    // Then
    assertNotNull(video);
    assertEquals(0, video.describeContents());
    assertEquals(id, video.getId());
    assertEquals(VideoUtils.getVideoTitle(id), video.getTitle());
    assertEquals(VideoUtils.getVideoImageUrl(id), video.getImageUrl());
    assertEquals(VideoUtils.getVideoDescription(id), video.getDescription());
    assertEquals(VideoUtils.getVideoListUrl(id), video.getListUrl());
    assertEquals(VideoUtils.getVideoUrl(id), video.getUrl());
  }

  @Test
  public void toString_whenIdSet_thenContainsId() {

    // Given
    String id = "id";
    Video video = VideoUtils.createVideo(id);

    // When
    String videoToString = video.toString();

    // Then
    assertEquals(String.format("Video {id=%s}", id), videoToString);
  }

  @Test
  public void creatorNewArray_whenZeroSize_thenArrayHasZeroSize() {

    // When
    Video[] array = Video.CREATOR.newArray(0);

    // Then
    assertEquals(0, array.length);
  }

  @Test
  public void creatorNewArray_whenPositiveSize_thenArrayHasPositiveSize() {

    // When
    Video[] array = Video.CREATOR.newArray(1);

    // Then
    assertEquals(1, array.length);
  }

  @Test
  public void creatorNewArray_whenNegativeSize_thenThrowNegativeArraySizeException() {

    // When/then
    thrown.expect(NegativeArraySizeException.class);
    Video[] array = Video.CREATOR.newArray(-1);
    assertNull(array); // Make PMD happy.
  }

  @Test
  public void writeToParcel_givenInput_whenInputWrittenAndOutputCreated_thenTheyMustBeEqual() {

    // Given
    Video input = VideoUtils.createVideo("id");

    // When
    Parcel parcel = Parcel.obtain();
    input.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    Video output = Video.CREATOR.createFromParcel(parcel);
    parcel.recycle();

    // Then
    assertNotNull(output);
    assertEquals(0, input.describeContents());
    assertEquals(input.getId(), output.getId());
    assertEquals(input.getTitle(), output.getTitle());
    assertEquals(input.getImageUrl(), output.getImageUrl());
    assertEquals(input.getDescription(), output.getDescription());
    assertEquals(input.getListUrl(), output.getListUrl());
    assertEquals(input.getUrl(), output.getUrl());
  }
}
