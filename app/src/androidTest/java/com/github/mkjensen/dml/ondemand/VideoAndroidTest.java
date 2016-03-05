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
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * Integration tests for {@link Video}.
 */
@RunWith(AndroidJUnit4.class)
public class VideoAndroidTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void writeToParcel_givenInput_whenInputWrittenAndOutputCreated_thenTheyMustBeEqual() {

    // Given
    Video.Builder builder = new Video.Builder()
        .slug("My slug")
        .title("My title")
        .description("My description")
        .imageUrl("My imageUrl")
        .videoUrl("My videoUrl");
    Video input = builder.build();

    // When
    Parcel parcel = Parcel.obtain();
    input.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    Video output = Video.CREATOR.createFromParcel(parcel);
    parcel.recycle();

    // Then
    assertNotNull(output);
    assertEquals(0, input.describeContents());
    assertEquals(input.getSlug(), output.getSlug());
    assertEquals(input.getTitle(), output.getTitle());
    assertEquals(input.getDescription(), output.getDescription());
    assertEquals(input.getImageUrl(), output.getImageUrl());
    assertEquals(input.getVideoUrl(), output.getVideoUrl());
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

  @Test()
  public void creatorNewArray_whenNegativeSize_thenThrowNegativeArraySizeException() {

    // When/then
    thrown.expect(NegativeArraySizeException.class);
    Video[] array = Video.CREATOR.newArray(-1);
    assertNull(array); // Make PMD happy.
  }
}
