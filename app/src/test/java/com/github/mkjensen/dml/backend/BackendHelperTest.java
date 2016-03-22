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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.github.mkjensen.dml.ResourceUtils;
import com.github.mkjensen.dml.test.PowerMockRobolectricTest;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Tests for {@link BackendHelper}.
 */
@PrepareForTest( {Category.class, Video.class})
public class BackendHelperTest extends PowerMockRobolectricTest {

  @Test
  public void loadCategories_whenHttpNotFound_thenThrowsIoException() {

    // Given
    LocalCallFactory callFactory = new LocalCallFactory.Builder()
        .withError(HttpURLConnection.HTTP_NOT_FOUND)
        .build();
    BackendHelper backendHelper = new BackendHelper(callFactory);

    // When/Then
    try {
      List<Category> categories = backendHelper.loadCategories();
      fail();
      assertNull(categories); // Hi PMD!
    } catch (IOException ex) {
      // Expected.
    }
  }

  @Test
  public void loadCategories_whenHttpOk_thenReturnsCategories() throws IOException {

    // Given
    LocalCallFactory callFactory = new LocalCallFactory.Builder()
        .withCode(HttpURLConnection.HTTP_OK)
        .withJsonResponseBody(ResourceUtils.loadAsString("backend/categories.json"))
        .build();
    BackendHelper backendHelper = new BackendHelper(callFactory);

    // When
    List<Category> categories = backendHelper.loadCategories();

    // Then
    assertNotNull(categories);
    assertEquals(1, categories.size());
    Category category = categories.get(0);
    assertEquals("id", category.getId());
    assertEquals("title", category.getTitle());
    assertEquals("http://category.com", category.getUrl());
  }

  @Test
  public void loadVideos_whenHttpNotFound_thenThrowsIoException() throws Exception {

    // Given
    LocalCallFactory callFactory = new LocalCallFactory.Builder()
        .withError(HttpURLConnection.HTTP_NOT_FOUND)
        .build();
    BackendHelper backendHelper = new BackendHelper(callFactory);
    Category category = createCategory();

    // When/Then
    try {
      backendHelper.loadVideos(category);
      fail();
      assertNotNull(category); // Hi PMD!
    } catch (IOException ex) {
      // Expected.
    }
  }

  @Test
  public void loadVideos_whenHttpOk_thenVideosAreLoaded() throws Exception {

    // Given
    LocalCallFactory callFactory = new LocalCallFactory.Builder()
        .withCode(HttpURLConnection.HTTP_OK)
        .withJsonResponseBody(ResourceUtils.loadAsString("backend/category-videos.json"))
        .build();
    BackendHelper backendHelper = new BackendHelper(callFactory);
    Category category = createCategory();

    // When
    backendHelper.loadVideos(category);

    // Then
    assertNotNull(category.getVideos());
    assertEquals(1, category.getVideos().size());
    Video video = category.getVideos().get(0);
    assertEquals("id", video.getId());
    assertEquals("title", video.getTitle());
    assertEquals("http://image.com", video.getImageUrl());
    assertEquals("http://links.com", video.getLinksUrl());
  }

  @Test
  public void loadVideoDetails_whenHttpNotFound_thenThrowsIoException() throws Exception {

    // Given
    LocalCallFactory callFactory = new LocalCallFactory.Builder()
        .withError(HttpURLConnection.HTTP_NOT_FOUND)
        .build();
    BackendHelper backendHelper = new BackendHelper(callFactory);
    Video video = createVideo("id");

    // When/Then
    try {
      backendHelper.loadVideoDetails(video);
      fail();
      assertNotNull(video); // Hi PMD!
    } catch (IOException ex) {
      // Expected.
    }
  }

  @Test
  public void loadVideoDetails_whenHttpOk_thenSetsVideoDetails() throws Exception {

    // Given
    LocalCallFactory callFactory = new LocalCallFactory.Builder()
        .withCode(HttpURLConnection.HTTP_OK)
        .withJsonResponseBody(ResourceUtils.loadAsString("backend/videos-details.json"))
        .build();
    BackendHelper backendHelper = new BackendHelper(callFactory);
    Video video = createVideo("id");

    // When
    backendHelper.loadVideoDetails(video);

    // Then
    assertEquals("description", video.getDescription());
  }

  private static Category createCategory() throws Exception {
    return PowerMockito.defaultConstructorIn(Category.class).newInstance();
  }

  private static Video createVideo(String id) throws Exception {
    Video video = PowerMockito.defaultConstructorIn(Video.class).newInstance();
    PowerMockito.field(Video.class, "id").set(video, id);
    return video;
  }
}
