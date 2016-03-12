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

import com.github.mkjensen.dml.RobolectricTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link Video}.
 */
public class VideoTest extends RobolectricTest {

  private static final String ID = "My id";
  private static final String TITLE = "My title";
  private static final String IMAGE_URL = "My imageUrl";
  private static final String DETAILS_URL = "My detailsUrl";
  private static final String DESCRIPTION = "My description";
  private static final String LIST_URL = "My listUrl";
  private static final String URL = "My url";

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void builder_givenValues_whenBuilt_thenVideoHasValues() {

    // Given
    Video.Builder videoBuilder = createVideoBuilder();

    // When
    Video video = videoBuilder.build();

    // Then
    assertNotNull(video);
    assertEquals(0, video.describeContents());
    assertEquals(ID, video.getId());
    assertEquals(TITLE, video.getTitle());
    assertEquals(IMAGE_URL, video.getImageUrl());
    assertEquals(DETAILS_URL, video.getDetailsUrl());
    assertEquals(DESCRIPTION, video.getDescription());
    assertEquals(LIST_URL, video.getListUrl());
    assertEquals(URL, video.getUrl());
  }

  @Test
  public void toString_whenIdSet_thenContainsId() {

    // Given
    Video video = createVideoBuilder().build();

    // When
    String videoToString = video.toString();

    // Then
    assertEquals(String.format("Video {id=%s}", video.getId()), videoToString);
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

  private static Video.Builder createVideoBuilder() {
    return new Video.Builder()
        .id(ID)
        .title(TITLE)
        .imageUrl(IMAGE_URL)
        .detailsUrl(DETAILS_URL)
        .description(DESCRIPTION)
        .listUrl(LIST_URL)
        .url(URL);
  }

  @Test
  public void writeToParcel_givenInput_whenInputWrittenAndOutputCreated_thenTheyMustBeEqual() {

    // Given
    Video input = createVideoBuilder().build();

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
    assertEquals(input.getDetailsUrl(), output.getDetailsUrl());
    assertEquals(input.getDescription(), output.getDescription());
    assertEquals(input.getListUrl(), output.getListUrl());
    assertEquals(input.getUrl(), output.getUrl());
  }
}
