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
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import com.github.mkjensen.dml.ResourceUtils;
import com.github.mkjensen.dml.ondemand.Video;
import com.github.mkjensen.dml.test.PowerMockRobolectricTest;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Tests for {@link BackendHelper}.
 */
@PrepareForTest(Category.class)
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
    assertEquals(2, categories.size());
    for (int i = 0, n = categories.size(); i < n; i++) {
      Category category = categories.get(i);
      assertNotNull(category);
      if (i == 0) {
        assertEquals("1st", category.getId());
        assertEquals("First", category.getTitle());
        assertEquals("http://first.com", category.getUrl());
      } else {
        assertEquals("2nd", category.getId());
        assertEquals("Second", category.getTitle());
        assertEquals("http://second.com", category.getUrl());
      }
    }
  }

  @Test
  public void loadVideos_whenHttpNotFound_thenThrowsIoException() {

    // Given
    Category categoryMock = mock(Category.class);
    when(categoryMock.getUrl()).thenReturn("http://dummy.com");
    LocalCallFactory callFactory = new LocalCallFactory.Builder()
        .withError(HttpURLConnection.HTTP_NOT_FOUND)
        .build();
    BackendHelper backendHelper = new BackendHelper(callFactory);

    // When/Then
    try {
      backendHelper.loadVideos(categoryMock);
      fail();
      assertNotNull(categoryMock); // Hi PMD!
    } catch (IOException ex) {
      // Expected.
    }
  }

  @Test
  public void loadVideos_whenHttpOk_thenSetsVideos() throws IOException {

    // Given
    LocalCallFactory callFactory = new LocalCallFactory.Builder()
        .withCode(HttpURLConnection.HTTP_OK)
        .withJsonResponseBody(ResourceUtils.loadAsString("backend/videos.json"))
        .build();
    BackendHelper backendHelper = new BackendHelper(callFactory);
    Category categoryMock = mock(Category.class);
    when(categoryMock.getUrl()).thenReturn("http://dummy.com");

    // When
    backendHelper.loadVideos(categoryMock);

    // Then
    verify(categoryMock).setVideos(anyListOf(Video.class));
  }
}
