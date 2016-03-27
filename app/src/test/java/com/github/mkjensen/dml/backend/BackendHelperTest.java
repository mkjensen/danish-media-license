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

import com.github.mkjensen.dml.test.ResourceUtils;
import com.github.mkjensen.dml.test.RobolectricTest;

import okhttp3.Call;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Tests for {@link BackendHelper}.
 */
public class BackendHelperTest extends RobolectricTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private static final String BASE_URL = "http://test.com/";

  private static final String MOST_VIEWED_CATEGORY_URL = BASE_URL + "list/view/mostviewed";

  private static final String VIDEO_URL = BASE_URL + "programcard/test";

  private static final String SEARCH_URL = BASE_URL + "search/tv/programcards-with-asset/title/q";

  @Test
  public void constructor_whenCalledWithNullContext_thenThrowsIllegalArgumentException() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("ConstantConditions")
    BackendHelper backendHelper = new BackendHelper(null, createRetrofit(null));
    assertNotNull(backendHelper); // For your eyes only, PMD.
  }

  @Test
  public void constructor_whenCalledWithNullRetrofit_thenThrowsIllegalArgumentException() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("ConstantConditions")
    BackendHelper backendHelper = new BackendHelper(getContext(), null);
    assertNotNull(backendHelper); // For your eyes only, PMD.
  }

  @Test
  public void loadMostViewedCategory_whenHttpNotFound_thenThrowsIoException() throws IOException {

    // Given
    LocalCallFactory callFactory = LocalCallFactory.newBuilder()
        .forUrl(MOST_VIEWED_CATEGORY_URL)
        .code(HttpURLConnection.HTTP_NOT_FOUND)
        .up()
        .build();
    BackendHelper backendHelper = createBackendHelper(callFactory);

    // When/Then
    thrown.expect(IOException.class);
    Category category = backendHelper.loadMostViewedCategory();
    assertNotNull(category); // Hi PMD!
  }

  @Test
  public void loadMostViewedCategory_whenHttpOk_thenReturnsCategory() throws IOException {

    // Given
    LocalCallFactory callFactory = LocalCallFactory.newBuilder()
        .forUrl(MOST_VIEWED_CATEGORY_URL)
        .code(HttpURLConnection.HTTP_OK)
        .responseBody(ResourceUtils.loadAsString("backend/category.json"))
        .up()
        .build();
    BackendHelper backendHelper = createBackendHelper(callFactory);

    // When
    Category category = backendHelper.loadMostViewedCategory();

    // Then
    assertNotNull(category);
    assertEquals("Most Viewed", category.getTitle());
    List<Video> videos = category.getVideos();
    assertNotNull(videos);
    assertEquals(1, videos.size());
    Video video = videos.get(0);
    assertNotNull(video);
    assertEquals("id", video.getId());
    assertEquals("Title", video.getTitle());
    assertEquals(Video.NOT_SET, video.getDescription());
    assertEquals("http://image.com", video.getImageUrl());
    assertEquals("http://links.com", video.getLinksUrl());
    assertEquals(Video.NOT_SET, video.getUrl());
  }

  @Test
  public void loadVideo_whenHttpNotFound_thenThrowsIoException() throws IOException {

    // Given
    LocalCallFactory callFactory = LocalCallFactory.newBuilder()
        .forUrl(VIDEO_URL)
        .code(HttpURLConnection.HTTP_NOT_FOUND)
        .up()
        .build();
    BackendHelper backendHelper = createBackendHelper(callFactory);

    // When/Then
    thrown.expect(IOException.class);
    Video video = backendHelper.loadVideo("test");
    assertNotNull(video); // Hi PMD!
  }

  @Test
  public void loadVideo_whenHttpOk_thenVideosAreLoaded() throws IOException {

    // Given
    LocalCallFactory callFactory = LocalCallFactory.newBuilder()
        .forUrl(VIDEO_URL)
        .code(HttpURLConnection.HTTP_OK)
        .responseBody(ResourceUtils.loadAsString("backend/video.json"))
        .up()
        .build();
    BackendHelper backendHelper = createBackendHelper(callFactory);

    // When
    Video video = backendHelper.loadVideo("test");

    // Then
    assertNotNull(video);
    assertEquals("id", video.getId());
    assertEquals("Title", video.getTitle());
    assertEquals("Description", video.getDescription());
    assertEquals("http://image.com", video.getImageUrl());
    assertEquals("http://links.com", video.getLinksUrl());
    assertEquals(Video.NOT_SET, video.getUrl());
  }

  @Test
  public void loadVideoUrl_whenHttpNotFound_thenThrowsIoException() throws IOException {

    // Given
    String linksUrl = "http://links.com/";
    LocalCallFactory callFactory = LocalCallFactory.newBuilder()
        .forUrl(linksUrl)
        .code(HttpURLConnection.HTTP_NOT_FOUND)
        .up()
        .build();
    BackendHelper backendHelper = createBackendHelper(callFactory);

    // When/Then
    thrown.expect(IOException.class);
    String url = backendHelper.loadVideoUrl(linksUrl);
    assertNotNull(url); // Hi PMD!
  }

  @Test
  public void loadVideoUrl_whenHttpOk_thenSetsVideoUrl() throws IOException {

    // Given
    String linksUrl = "http://links.com/";
    LocalCallFactory callFactory = LocalCallFactory.newBuilder()
        .forUrl(linksUrl)
        .code(HttpURLConnection.HTTP_OK)
        .responseBody(ResourceUtils.loadAsString("backend/video-links.json"))
        .up()
        .build();
    BackendHelper backendHelper = createBackendHelper(callFactory);

    // When
    String url = backendHelper.loadVideoUrl(linksUrl);

    // Then
    assertEquals("http://hls.com/mp4", url);
  }

  @Test
  public void search_whenHttpNotFound_thenThrowsIoException() throws IOException {

    // Given
    LocalCallFactory callFactory = LocalCallFactory.newBuilder()
        .forUrl(SEARCH_URL)
        .code(HttpURLConnection.HTTP_NOT_FOUND)
        .up()
        .build();
    BackendHelper backendHelper = createBackendHelper(callFactory);

    // When/Then
    thrown.expect(IOException.class);
    Category category = backendHelper.search("test");
    assertNotNull(category); // Hi PMD!
  }

  @Test
  public void search_whenHttpOk_thenReturnsCategory() throws IOException {

    // Given
    LocalCallFactory callFactory = LocalCallFactory.newBuilder()
        .forUrl(SEARCH_URL)
        .code(HttpURLConnection.HTTP_OK)
        .responseBody(ResourceUtils.loadAsString("backend/search.json"))
        .up()
        .build();
    BackendHelper backendHelper = createBackendHelper(callFactory);

    // When
    Category category = backendHelper.search("q");

    // Then
    assertNotNull(category);
    assertEquals("q", category.getTitle());
    List<Video> videos = category.getVideos();
    assertNotNull(videos);
    assertEquals(1, videos.size());
    Video video = videos.get(0);
    assertNotNull(video);
    assertEquals("id", video.getId());
    assertEquals("Title", video.getTitle());
    assertEquals(Video.NOT_SET, video.getDescription());
    assertEquals("http://image.com", video.getImageUrl());
    assertEquals("http://links.com", video.getLinksUrl());
    assertEquals(Video.NOT_SET, video.getUrl());
  }

  private BackendHelper createBackendHelper(Call.Factory callFactory) {
    return new BackendHelper(getContext(), createRetrofit(callFactory));
  }

  private static Retrofit createRetrofit(Call.Factory callFactory) {
    Retrofit.Builder builder = new Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BASE_URL);
    if (callFactory != null) {
      builder.callFactory(callFactory);
    }
    return builder.build();
  }
}
