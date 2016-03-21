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

package com.github.mkjensen.dml.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.github.mkjensen.dml.ondemand.Video;
import com.github.mkjensen.dml.test.TestUtils;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for {@link Category}.
 */
public class CategoryTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private static final String ID = "id";

  private static final String TITLE = "title";

  private static final String URL = "url";

  private static final String JSON = String.format("{\n"
      + "  \"id\": \"%s\",\n"
      + "  \"title\": \"%s\",\n"
      + "  \"url\": \"%s\"\n"
      + "}", ID, TITLE, URL);

  private static final String JSON_EMPTY_OBJECT = "{}";

  @Test
  public void category_constructorMustBePrivateAndParameterless() {
    assertTrue(TestUtils.hasPrivateParameterlessConstructor(Category.class));
  }

  @Test
  public void givenEmptyCategoryFromJson_whenGettersCalled_thenDoNotReturnNull()
      throws IOException {

    // Given
    Category category = createFromJson(JSON_EMPTY_OBJECT);

    // When/Then
    assertNotNull(category);
    assertNotNull(category.getId());
    assertNotNull(category.getTitle());
    assertNotNull(category.getUrl());
    assertNotNull(category.getVideos());
  }

  @Test
  public void givenCategoryFromJson_whenGettersCalled_thenReturnValuesFromJson()
      throws IOException {

    // Given
    Category category = createFromJson(JSON);

    // When/Then
    assertNotNull(category);
    assertEquals(ID, category.getId());
    assertEquals(TITLE, category.getTitle());
    assertEquals(URL, category.getUrl());
    assertNotNull(category.getVideos());
  }

  @Test
  public void setVideos_whenNullArgument_thenIllegalArgumentExceptionIsThrown() throws IOException {

    // Given
    Category category = createFromJson(JSON_EMPTY_OBJECT);

    // When/then
    assertNotNull(category);
    thrown.expect(IllegalArgumentException.class);
    category.setVideos(null);
  }

  @Test
  public void setVideos_whenNonNullArgument_thenGetVideosReturnThatArgument() throws IOException {

    // Given
    Category category = createFromJson(JSON_EMPTY_OBJECT);
    List<Video> videos = new ArrayList<>();

    // When
    category.setVideos(videos);

    // Then
    assertSame(videos, category.getVideos());
  }

  private static Category createFromJson(String json) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Category> adapter = moshi.adapter(Category.class);
    return adapter.fromJson(json);
  }
}
