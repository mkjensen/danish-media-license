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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link Video}.
 */
public class VideoTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private Video video;

  @Before
  public void before() {
    video = new Video();
  }

  @Test
  public void toString_whenIdSet_thenContainsId() {

    // Given
    String id = "id";
    video.setId(id);

    // When
    String videoToString = video.toString();

    // Then
    assertEquals(String.format("Video {id=%s}", id), videoToString);
  }

  @Test
  public void givenEmptyVideo_whenGettersCalled_thenTheyReturnNotSet() {

    // When/then
    assertEquals(Video.NOT_SET, video.getId());
    assertEquals(Video.NOT_SET, video.getTitle());
    assertEquals(Video.NOT_SET, video.getDescription());
    assertEquals(Video.NOT_SET, video.getImageUrl());
    assertEquals(Video.NOT_SET, video.getManifestUrl());
  }

  @Test
  public void setId_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    String id = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    video.setId(id);
  }

  @Test
  public void setId_whenNonNullArgument_thenGetIdReturnThatArgument() {

    // Given
    String id = "id";

    // When
    video.setId(id);

    // Then
    assertEquals(id, video.getId());
  }

  @Test
  public void setTitle_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    String title = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    video.setTitle(title);
  }

  @Test
  public void setTitle_whenNonNullArgument_thenGetTitleReturnThatArgument() {

    // Given
    String title = "title";

    // When
    video.setTitle(title);

    // Then
    assertEquals(title, video.getTitle());
  }

  @Test
  public void setDescription_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    String description = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    video.setDescription(description);
  }

  @Test
  public void setDescription_whenNonNullArgument_thenGetDescriptionReturnThatArgument() {

    // Given
    String description = "description";

    // When
    video.setDescription(description);

    // Then
    assertEquals(description, video.getDescription());
  }

  @Test
  public void setImageUrl_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    String imageUrl = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    video.setImageUrl(imageUrl);
  }

  @Test
  public void setImageUrl_whenNonNullArgument_thenGetImageUrlReturnThatArgument() {

    // Given
    String imageUrl = "imageUrl";

    // When
    video.setImageUrl(imageUrl);

    // Then
    assertEquals(imageUrl, video.getImageUrl());
  }

  @Test
  public void setManifestUrl_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    String manifestUrl = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    video.setManifestUrl(manifestUrl);
  }

  @Test
  public void setManifestUrl_whenNonNullArgument_thenGetManifestUrlReturnThatArgument() {

    // Given
    String manifestUrl = "url";

    // When
    video.setManifestUrl(manifestUrl);

    // Then
    assertEquals(manifestUrl, video.getManifestUrl());
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
    Video.CREATOR.newArray(-1);
  }
}
