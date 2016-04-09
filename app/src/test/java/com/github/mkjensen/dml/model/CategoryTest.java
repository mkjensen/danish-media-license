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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link Category}.
 */
public class CategoryTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private Category category;

  @Before
  public void before() {
    category = new Category();
  }

  @Test
  public void givenEmptyCategory_whenGettersCalled_thenTheyReturnNotSet() {

    // When/then
    assertEquals(Category.NOT_SET, category.getTitle());
    assertEquals(Collections.emptyList(), category.getVideos());
  }

  @Test
  public void setTitle_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    String title = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    category.setTitle(title);
  }

  @Test
  public void setTitle_whenNonNullArgument_thenGetVideosReturnThatArgument() {

    // Given
    String title = "title";

    // When
    category.setTitle(title);

    // Then
    assertEquals(title, category.getTitle());
  }

  @Test
  public void setVideos_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    List<Video> videos = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    category.setVideos(videos);
  }

  @Test
  public void setVideos_whenNonNullArgument_thenGetVideosReturnThatArgument() {

    // Given
    List<Video> videos = new ArrayList<>();

    // When
    category.setVideos(videos);

    // Then
    assertEquals(videos, category.getVideos());
  }
}
