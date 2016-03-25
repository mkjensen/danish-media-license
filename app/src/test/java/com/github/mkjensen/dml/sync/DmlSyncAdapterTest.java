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

package com.github.mkjensen.dml.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.ContentProviderClient;
import android.content.SyncResult;

import com.github.mkjensen.dml.backend.BackendHelper;
import com.github.mkjensen.dml.backend.LocalCallFactory;
import com.github.mkjensen.dml.provider.DmlContract;
import com.github.mkjensen.dml.test.ContentUtils;
import com.github.mkjensen.dml.test.ResourceUtils;
import com.github.mkjensen.dml.test.RobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.HttpURLConnection;

/**
 * Tests for {@link DmlSyncAdapter}.
 */
public class DmlSyncAdapterTest extends RobolectricTest {

  private ContentProviderClient provider;

  /**
   * Creates {@link #provider}.
   */
  @Before
  public void before() {
    provider = ContentUtils.getContentResolver().acquireContentProviderClient(
        DmlContract.AUTHORITY_URI);
    assertNotNull(provider);
  }

  /**
   * Releases {@link #provider}.
   */
  @After
  public void after() {
    if (provider != null) {
      provider.release();
    }
  }

  @Test
  public void onPerformSync_whenCategoriesFails_thenSyncResultHasIoException() {

    // Given
    BackendHelper backendHelper = createFailingCategoriesBackendHelper();

    // When
    SyncResult syncResult = onPerformSync(backendHelper);

    // Then
    assertEquals(1, syncResult.stats.numIoExceptions);
  }

  @Test
  public void onPerformSync_whenCategoryVideosFails_thenSyncResultHasIoException() {

    // Given
    BackendHelper backendHelper = createFailingCategoryVideosBackendHelper();

    // When
    SyncResult syncResult = onPerformSync(backendHelper);

    // Then
    assertEquals(1, syncResult.stats.numIoExceptions);
  }

  @Test
  public void onPerformSync_whenVideoDetailsFails_thenSyncResultHasIoException() {

    // Given
    BackendHelper backendHelper = createFailingVideoDetailsBackendHelper();

    // When
    SyncResult syncResult = onPerformSync(backendHelper);

    // Then
    assertEquals(1, syncResult.stats.numIoExceptions);
  }

  @Test
  public void onPerformSync_whenVideoUrlFails_thenSyncResultHasIoException() {

    // Given
    BackendHelper backendHelper = createFailingVideoUrlBackendHelper();

    // When
    SyncResult syncResult = onPerformSync(backendHelper);

    // Then
    assertEquals(1, syncResult.stats.numIoExceptions);
  }

  @Test
  public void onPerformSync_whenSuccessful_thenSyncResultIsWithoutError() {

    // Given
    BackendHelper backendHelper = createSuccessfulBackendHelper();

    // When
    SyncResult syncResult = onPerformSync(backendHelper);

    // Then
    assertFalse(syncResult.hasError());
  }

  private SyncResult onPerformSync(BackendHelper backendHelper) {
    DmlSyncAdapter syncAdapter = new DmlSyncAdapter(getContext(), backendHelper);
    SyncResult syncResult = new SyncResult();
    syncAdapter.onPerformSync(
        null, // account
        null, // extras
        DmlContract.AUTHORITY,
        provider,
        syncResult
    );
    return syncResult;
  }

  private static BackendHelper createFailingCategoriesBackendHelper() {
    LocalCallFactory.Builder builder = LocalCallFactory.newBuilder();
    addFailingCategories(builder);
    addCategoryVideos(builder);
    addVideoDetails(builder);
    addVideoUrl(builder);
    return new BackendHelper(builder.build());
  }

  private static BackendHelper createFailingCategoryVideosBackendHelper() {
    LocalCallFactory.Builder builder = LocalCallFactory.newBuilder();
    addCategories(builder);
    addFailingCategoryVideos(builder);
    addVideoDetails(builder);
    addVideoUrl(builder);
    return new BackendHelper(builder.build());
  }

  private static BackendHelper createFailingVideoDetailsBackendHelper() {
    LocalCallFactory.Builder builder = LocalCallFactory.newBuilder();
    addCategories(builder);
    addCategoryVideos(builder);
    addFailingVideoDetails(builder);
    addVideoUrl(builder);
    return new BackendHelper(builder.build());
  }

  private static BackendHelper createFailingVideoUrlBackendHelper() {
    LocalCallFactory.Builder builder = LocalCallFactory.newBuilder();
    addCategories(builder);
    addCategoryVideos(builder);
    addVideoDetails(builder);
    addFailingVideoUrl(builder);
    return new BackendHelper(builder.build());
  }

  private static BackendHelper createSuccessfulBackendHelper() {
    LocalCallFactory.Builder builder = LocalCallFactory.newBuilder();
    addCategories(builder);
    addCategoryVideos(builder);
    addVideoDetails(builder);
    addVideoUrl(builder);
    return new BackendHelper(builder.build());
  }

  private static void addCategories(LocalCallFactory.Builder builder) {
    builder
        .forUrl("https://mkjensen.github.io/danish-media-license/categories.json")
        .code(HttpURLConnection.HTTP_OK)
        .responseBody(ResourceUtils.loadAsString("sync/categories.json"));
  }

  private static void addFailingCategories(LocalCallFactory.Builder builder) {
    builder
        .forUrl("https://mkjensen.github.io/danish-media-license/categories.json")
        .code(HttpURLConnection.HTTP_NOT_FOUND);
  }

  private static void addCategoryVideos(LocalCallFactory.Builder builder) {
    builder
        .forUrl("http://category.com/")
        .code(HttpURLConnection.HTTP_OK)
        .responseBody(ResourceUtils.loadAsString("sync/category-videos.json"));
  }

  private static void addFailingCategoryVideos(LocalCallFactory.Builder builder) {
    builder
        .forUrl("http://category.com/")
        .code(HttpURLConnection.HTTP_NOT_FOUND);
  }

  private static void addVideoDetails(LocalCallFactory.Builder builder) {
    builder
        .forUrl("https://www.dr.dk/mu-online/api/1.3/programcard/id")
        .code(HttpURLConnection.HTTP_OK)
        .responseBody(ResourceUtils.loadAsString("sync/video-details.json"));
  }

  private static void addFailingVideoDetails(LocalCallFactory.Builder builder) {
    builder
        .forUrl("https://www.dr.dk/mu-online/api/1.3/programcard/id")
        .code(HttpURLConnection.HTTP_NOT_FOUND);
  }

  private static void addVideoUrl(LocalCallFactory.Builder builder) {
    builder
        .forUrl("http://links.com/")
        .code(HttpURLConnection.HTTP_OK)
        .responseBody(ResourceUtils.loadAsString("sync/video-links.json"));
  }

  private static void addFailingVideoUrl(LocalCallFactory.Builder builder) {
    builder
        .forUrl("http://links.com/")
        .code(HttpURLConnection.HTTP_NOT_FOUND);
  }
}
