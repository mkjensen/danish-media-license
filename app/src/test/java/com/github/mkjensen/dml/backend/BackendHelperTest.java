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

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.github.mkjensen.dml.model.Category;
import com.github.mkjensen.dml.model.Video;
import com.github.mkjensen.dml.model.VideoManifest;
import com.github.mkjensen.dml.test.ResourceUtils;
import com.github.mkjensen.dml.test.RobolectricTest;

import okhttp3.Call;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.io.IOException;
import java.util.List;

/**
 * Tests for {@link BackendHelper}.
 */
public class BackendHelperTest extends RobolectricTest {

  private static final Converter.Factory CONVERTER_FACTORY = MoshiConverterFactory.create();

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private static final String BASE_URL = "http://test.com/";

  private static final String MOST_VIEWED_CATEGORY_URL = BASE_URL + "list/view/mostviewed";

  private static final String SELECTED_CATEGORY_URL = BASE_URL + "list/view/selectedlist";

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
    BackendHelper backendHelper = createBackendHelper(MOST_VIEWED_CATEGORY_URL, HTTP_NOT_FOUND);

    // When/Then
    thrown.expect(IOException.class);
    Category category = backendHelper.loadMostViewedCategory();
    assertNotNull(category); // Hi PMD!
  }

  @Test
  public void loadMostViewedCategory_whenHttpOk_thenReturnsCategory() throws IOException {

    // Given
    BackendHelper backendHelper =
        createBackendHelper(MOST_VIEWED_CATEGORY_URL, HTTP_OK, "category");

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
    assertEquals("http://manifest.com", video.getManifestUrl());
  }

  @Test
  public void loadSelectedCategory_whenHttpNotFound_thenThrowsIoException() throws IOException {

    // Given
    BackendHelper backendHelper = createBackendHelper(SELECTED_CATEGORY_URL, HTTP_NOT_FOUND);

    // When/Then
    thrown.expect(IOException.class);
    Category category = backendHelper.loadSelectedCategory();
    assertNotNull(category); // Hi PMD!
  }

  @Test
  public void loadSelectedCategory_whenHttpOk_thenReturnsCategory() throws IOException {

    // Given
    BackendHelper backendHelper = createBackendHelper(SELECTED_CATEGORY_URL, HTTP_OK, "category");

    // When
    Category category = backendHelper.loadSelectedCategory();

    // Then
    assertNotNull(category);
    assertEquals("Selected", category.getTitle());
    List<Video> videos = category.getVideos();
    assertNotNull(videos);
    assertEquals(1, videos.size());
    Video video = videos.get(0);
    assertNotNull(video);
    assertEquals("id", video.getId());
    assertEquals("Title", video.getTitle());
    assertEquals(Video.NOT_SET, video.getDescription());
    assertEquals("http://image.com", video.getImageUrl());
    assertEquals("http://manifest.com", video.getManifestUrl());
  }

  @Test
  public void loadVideo_whenHttpNotFound_thenThrowsIoException() throws IOException {

    // Given
    BackendHelper backendHelper = createBackendHelper(VIDEO_URL, HTTP_NOT_FOUND);

    // When/Then
    thrown.expect(IOException.class);
    Video video = backendHelper.loadVideo("test");
    assertNotNull(video); // Hi PMD!
  }

  @Test
  public void loadVideo_whenHttpOk_thenVideosAreLoaded() throws IOException {

    // Given
    BackendHelper backendHelper = createBackendHelper(VIDEO_URL, HTTP_OK, "video");

    // When
    Video video = backendHelper.loadVideo("test");

    // Then
    assertNotNull(video);
    assertEquals("id", video.getId());
    assertEquals("Title", video.getTitle());
    assertEquals("Description", video.getDescription());
    assertEquals("http://image.com", video.getImageUrl());
    assertEquals("http://manifest.com", video.getManifestUrl());
  }

  @Test
  public void loadVideoManifest_whenHttpNotFound_thenThrowsIoException() throws IOException {

    // Given
    String manifestUrl = "http://manifest.com/";
    BackendHelper backendHelper = createBackendHelper(manifestUrl, HTTP_NOT_FOUND);

    // When/Then
    thrown.expect(IOException.class);
    VideoManifest videoManifest = backendHelper.loadVideoManifest(manifestUrl);
    assertNotNull(videoManifest); // Hi PMD!
  }

  @Test
  public void loadVideoManifest_whenHttpOk_thenReturnsVideoManifest() throws IOException {

    // Given
    String manifestUrl = "http://manifest.com/";
    BackendHelper backendHelper = createBackendHelper(manifestUrl, HTTP_OK, "video-manifest");

    // When
    VideoManifest videoManifest = backendHelper.loadVideoManifest(manifestUrl);

    // Then
    assertNotNull(videoManifest);
    List<VideoManifest.Stream> streams = videoManifest.getStreams();
    assertNotNull(streams);
    assertEquals(4, streams.size());
    VideoManifest.Stream stream = streams.get(0);
    assertNotNull(stream);
    assertEquals(VideoManifest.Protocol.DOWNLOAD, stream.getProtocol());
    assertEquals("http://download.com/mp4/2048", stream.getUrl());
    stream = streams.get(1);
    assertNotNull(stream);
    assertEquals(VideoManifest.Protocol.DOWNLOAD, stream.getProtocol());
    assertEquals("http://download.com/mp4/1024", stream.getUrl());
    stream = streams.get(2);
    assertNotNull(stream);
    assertEquals(VideoManifest.Protocol.HDS, stream.getProtocol());
    assertEquals("http://hds.com/mp4", stream.getUrl());
    stream = streams.get(3);
    assertNotNull(stream);
    assertEquals(VideoManifest.Protocol.HLS, stream.getProtocol());
    assertEquals("http://hls.com/mp4", stream.getUrl());
  }

  @Test
  public void search_whenHttpNotFound_thenThrowsIoException() throws IOException {

    // Given
    BackendHelper backendHelper = createBackendHelper(SEARCH_URL, HTTP_NOT_FOUND);

    // When/Then
    thrown.expect(IOException.class);
    Category category = backendHelper.search("test");
    assertNotNull(category); // Hi PMD!
  }

  @Test
  public void search_whenHttpOk_thenReturnsCategory() throws IOException {

    // Given
    BackendHelper backendHelper = createBackendHelper(SEARCH_URL, HTTP_OK, "search");

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
    assertEquals("http://manifest.com", video.getManifestUrl());
  }

  private BackendHelper createBackendHelper(String url, int code) {
    return createBackendHelper(url, code, null);
  }

  private BackendHelper createBackendHelper(String url, int code, String jsonResource) {
    LocalCallFactory.Builder.ForUrlBuilder builder = LocalCallFactory.newBuilder()
        .forUrl(url)
        .code(code);
    if (jsonResource != null) {
      builder.responseBody(ResourceUtils.loadAsString("backend/" + jsonResource + ".json"));
    }
    return createBackendHelper(builder.up().build());
  }

  private BackendHelper createBackendHelper(Call.Factory callFactory) {
    return new BackendHelper(getContext(), createRetrofit(callFactory));
  }

  private static Retrofit createRetrofit(Call.Factory callFactory) {
    Retrofit.Builder builder = new Retrofit.Builder()
        .addConverterFactory(CONVERTER_FACTORY)
        .baseUrl(BASE_URL);
    if (callFactory != null) {
      builder.callFactory(callFactory);
    }
    return builder.build();
  }
}
