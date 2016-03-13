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

package com.github.mkjensen.dml.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.github.mkjensen.dml.provider.DmlContract.Categories;
import com.github.mkjensen.dml.provider.DmlContract.CategoriesVideos;
import com.github.mkjensen.dml.provider.DmlContract.Videos;
import com.github.mkjensen.dml.test.RobolectricTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowContentResolver;

/**
 * Tests for {@link DmlProvider}.
 */
public class DmlProviderTest extends RobolectricTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private static final Uri INVALID_URL = DmlContract.AUTHORITY_URI.buildUpon()
      .appendPath("invalid")
      .build();

  private static final String NONEXISTENT_ID = "nonexistent";

  private ContentResolver contentResolver;

  /**
   * Registers {@link DmlContract#AUTHORITY} to be handled by {@link DmlProvider}. Also asserts that
   * the {@link ContentProvider#onCreate()} method returns {@code true}.
   */
  @Before
  public void before() {
    ContentProvider contentProvider = new DmlProvider();
    assertEquals(true, contentProvider.onCreate());
    contentResolver = RuntimeEnvironment.application.getContentResolver();
    ShadowContentResolver.registerProvider(DmlContract.AUTHORITY, contentProvider);
  }

  @Test
  public void query_whenInvalidUri_thenIllegalArgumentExceptionIsThrown() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown URI: " + INVALID_URL);
    query(INVALID_URL);
  }

  @Test
  public void query_whenRequestingNonexistentCategory_thenEmptyCursorIsReturned() {

    // When
    try (Cursor cursor = query(Categories.buildCategoryUri(NONEXISTENT_ID))) {

      // Then
      assertNotNull(cursor);
      assertFalse(cursor.moveToFirst());
    }
  }

  @Test
  public void query_givenCategories_whenRequestingAllCategories_thenAllCategoriesAreReturned() {

    // Given
    insertCategory("c0");
    insertCategory("c1");
    insertCategory("c2");

    // When
    try (Cursor cursor = query(Categories.CONTENT_URI)) {

      // Then
      assertNotNull(cursor);
      assertTrue(cursor.moveToFirst());
      assertEquals(3, cursor.getCount());
    }
  }

  @Test
  public void query_givenCategories_whenRequestingCategory_thenCategoryIsReturned() {

    // Given
    insertCategory("c0");
    String expectedId = "c1";
    insertCategory(expectedId);
    insertCategory("c2");

    // When
    try (Cursor cursor = query(Categories.buildCategoryUri(expectedId))) {

      // Then
      assertNotNull(cursor);
      assertTrue(cursor.moveToFirst());
      String actualId = getString(cursor, Categories.ID);
      assertEquals(expectedId, actualId);
      assertEquals(getCategoryTitle(actualId), getString(cursor, Categories.TITLE));
      assertEquals(getCategoryUrl(actualId), getString(cursor, Categories.URL));
    }
  }

  @Test
  public void query_givenCategorizedVideos_whenRequestingVideosInCategory_thenVideosAreReturned() {

    // Given
    insertCategory("c0");
    insertCategory("c1");
    insertCategory("c2");
    insertVideo("v0");
    insertVideo("v1");
    insertVideo("v2");
    addToCategory("c0", "v0");
    addToCategory("c1", "v1");
    addToCategory("c1", "v2");

    // When
    try (Cursor cursor = query(CategoriesVideos.buildUri("c1"))) {

      // Then
      assertNotNull(cursor);
      assertTrue(cursor.moveToFirst());
      assertEquals(2, cursor.getCount());
    }
  }

  @Test
  public void query_whenRequestingNonexistentVideo_thenEmptyCursorIsReturned() {

    // When
    try (Cursor cursor = query(Videos.buildVideoUri(NONEXISTENT_ID))) {

      // Then
      assertNotNull(cursor);
      assertFalse(cursor.moveToFirst());
    }
  }

  @Test
  public void query_givenVideos_whenRequestingAllVideos_thenAllVideosAreReturned() {

    // Given
    insertVideo("v0");
    insertVideo("v1");
    insertVideo("v2");

    // When
    try (Cursor cursor = query(Videos.CONTENT_URI)) {

      // Then
      assertNotNull(cursor);
      assertTrue(cursor.moveToFirst());
      assertEquals(3, cursor.getCount());
    }
  }

  @Test
  public void query_givenVideos_whenRequestingVideo_thenVideoIsReturned() {

    // Given
    insertVideo("v0");
    String expectedId = "v1";
    insertVideo(expectedId);
    insertVideo("v2");

    // When
    try (Cursor cursor = query(Videos.buildVideoUri(expectedId))) {

      // Then
      assertNotNull(cursor);
      assertTrue(cursor.moveToFirst());
      String actualId = getString(cursor, Videos.ID);
      assertEquals(expectedId, actualId);
      assertEquals(getVideoTitle(actualId), getString(cursor, Videos.TITLE));
      assertEquals(getVideoImageUrl(actualId), getString(cursor, Videos.IMAGE_URL));
      assertEquals(getVideoDetailsUrl(actualId), getString(cursor, Videos.DETAILS_URL));
      assertEquals(getVideoDescriptionUrl(actualId), getString(cursor, Videos.DESCRIPTION));
      assertEquals(getVideoListUrl(actualId), getString(cursor, Videos.LIST_URL));
      assertEquals(getVideoUrl(actualId), getString(cursor, Videos.URL));
    }
  }

  @Test
  public void insert_whenInvalidUri_thenIllegalArgumentExceptionIsThrown() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown URI: " + INVALID_URL);
    contentResolver.insert(INVALID_URL, null);
  }

  @Test
  public void insert_whenCategoriesUri_thenReturnedUriContainsId() {

    // When
    String id = "c0";
    Uri uri = insertCategory(id);

    // Then
    assertEquals(Categories.buildCategoryUri(id), uri);
  }

  @Test
  public void insert_whenCategoriesVideosUri_thenReturnedUriContainsId() {

    // Given
    String categoryId = "c0";
    insertCategory(categoryId);
    String videoId = "v0";
    insertVideo(videoId);

    // When
    Uri uri = addToCategory(categoryId, videoId);

    // Then
    assertEquals(CategoriesVideos.buildUri(categoryId), uri);
  }

  @Test
  public void insert_whenVideosUri_thenReturnedUriContainsId() {

    // When
    String id = "v0";
    Uri uri = insertVideo(id);

    // Then
    assertEquals(Videos.buildVideoUri(id), uri);
  }

  @Test
  public void getType_whenInvalidUri_thenNullIsReturned() {

    // When
    String type = contentResolver.getType(INVALID_URL);

    // Then
    assertNull(type);
  }

  @Test
  public void getType_whenCategoriesUri_thenCategoriesContentTypeIsReturned() {

    // When
    String type = contentResolver.getType(Categories.CONTENT_URI);

    // Then
    assertEquals(DmlUri.CATEGORIES.getContentType(), type);
  }

  @Test
  public void getType_whenCategoriesIdUri_thenCategoriesIdContentTypeIsReturned() {

    // When
    String type = contentResolver.getType(Categories.buildCategoryUri(NONEXISTENT_ID));

    // Then
    assertEquals(DmlUri.CATEGORIES_ID.getContentType(), type);
  }

  @Test
  public void getType_whenCategoriesIdVideosUri_thenCategoriesIdVideosContentTypeIsReturned() {

    // When
    String type = contentResolver.getType(CategoriesVideos.buildUri(NONEXISTENT_ID));

    // Then
    assertEquals(DmlUri.CATEGORIES_ID_VIDEOS.getContentType(), type);
  }

  @Test
  public void getType_whenVideosUri_thenVideosContentTypeIsReturned() {

    // When
    String type = contentResolver.getType(Videos.CONTENT_URI);

    // Then
    assertEquals(DmlUri.VIDEOS.getContentType(), type);
  }

  @Test
  public void getType_whenVideosIdUri_thenVideosIdContentTypeIsReturned() {

    // When
    String type = contentResolver.getType(Videos.buildVideoUri(NONEXISTENT_ID));

    // Then
    assertEquals(DmlUri.VIDEOS_ID.getContentType(), type);
  }

  @Test
  public void delete_whenInvalidUri_thenIllegalArgumentExceptionIsThrown() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown URI: " + INVALID_URL);
    contentResolver.delete(
        INVALID_URL,
        null, // where
        null); // selectionArgs
  }

  @Test
  public void delete_whenCategoriesUri_thenAllCategoriesAreDeleted() {

    // Given
    insertCategory("c0");
    insertCategory("c1");
    insertCategory("c2");

    // When
    int deleted = contentResolver.delete(
        Categories.CONTENT_URI,
        null, // where
        null); // selectionArgs

    // Then
    assertEquals(3, deleted);
  }

  @Test
  public void delete_whenCategoriesIdUri_thenCategoryIsDeleted() {

    // Given
    insertCategory("c0");
    insertCategory("c1");
    insertCategory("c2");

    // When
    int deleted = contentResolver.delete(
        Categories.buildCategoryUri("c1"),
        null, // where
        null); // selectionArgs

    // Then
    assertEquals(1, deleted);
  }

  @Test
  public void delete_whenCategoriesIdVideosUri_thenVideosAreRemovedFromCategory() {

    // Given
    insertCategory("c0");
    insertCategory("c1");
    insertCategory("c2");
    insertVideo("v0");
    insertVideo("v1");
    insertVideo("v2");
    addToCategory("c0", "v0");
    addToCategory("c1", "v1");
    addToCategory("c1", "v2");

    // When
    int deleted = contentResolver.delete(
        CategoriesVideos.buildUri("c1"),
        null, // where
        null); // selectionArgs

    try (Cursor categoriesCursor = query(Videos.CONTENT_URI)) {

      try (Cursor videosCursor = query(Categories.CONTENT_URI)) {

        // Then
        assertEquals(2, deleted);

        assertNotNull(categoriesCursor);
        assertTrue(categoriesCursor.moveToFirst());
        assertEquals(3, categoriesCursor.getCount());

        assertNotNull(videosCursor);
        assertTrue(videosCursor.moveToFirst());
        assertEquals(3, videosCursor.getCount());
      }
    }
  }

  @Test
  public void delete_whenVideosUri_thenAllVideosAreDeleted() {

    // Given
    insertVideo("v0");
    insertVideo("v1");
    insertVideo("v2");

    // When
    int deleted = contentResolver.delete(
        Videos.CONTENT_URI,
        null, // where
        null); // selectionArgs

    // Then
    assertEquals(3, deleted);
  }

  @Test
  public void delete_whenVideosIdUri_thenVideoIsDeleted() {

    // Given
    insertVideo("v0");
    insertVideo("v1");
    insertVideo("v2");

    // When
    int deleted = contentResolver.delete(
        Videos.buildVideoUri("v1"),
        null, // where
        null); // selectionArgs

    // Then
    assertEquals(1, deleted);
  }

  @Test
  public void update_whenInvalidUri_thenIllegalArgumentExceptionIsThrown() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown URI: " + INVALID_URL);
    contentResolver.update(
        INVALID_URL,
        null, // values
        null, // where
        null); // selectionArgs
  }

  @Test
  public void update_whenCategoriesUri_thenZeroIsReturned() {

    // When
    int updated = contentResolver.update(
        Categories.CONTENT_URI,
        null, // values
        null, // where
        null); // selectionArgs

    // Then
    assertEquals(0, updated);
  }

  @Test
  public void update_whenCategoriesIdUri_thenZeroIsReturned() {

    // When
    int updated = contentResolver.update(
        Categories.buildCategoryUri(NONEXISTENT_ID),
        null, // values
        null, // where
        null); // selectionArgs

    // Then
    assertEquals(0, updated);
  }

  @Test
  public void update_whenCategoriesIdVideosUri_thenZeroIsReturned() {

    // When
    int updated = contentResolver.update(
        CategoriesVideos.buildUri(NONEXISTENT_ID),
        null, // values
        null, // where
        null); // selectionArgs

    // Then
    assertEquals(0, updated);
  }

  @Test
  public void update_whenVideosUri_thenZeroIsReturned() {

    // When
    int updated = contentResolver.update(
        Videos.CONTENT_URI,
        null, // values
        null, // where
        null); // selectionArgs

    // Then
    assertEquals(0, updated);
  }

  @Test
  public void update_whenVideosIdUri_thenZeroIsReturned() {

    // When
    int updated = contentResolver.update(
        Videos.buildVideoUri(NONEXISTENT_ID),
        null, // values
        null, // where
        null); // selectionArgs

    // Then
    assertEquals(0, updated);
  }

  private Uri insertCategory(String id) {
    ContentValues values = new ContentValues();
    values.put(Categories.ID, id);
    values.put(Categories.TITLE, getCategoryTitle(id));
    values.put(Categories.URL, getCategoryUrl(id));
    return contentResolver.insert(Categories.CONTENT_URI, values);
  }

  private static String getCategoryTitle(String id) {
    return id + Categories.TITLE;
  }

  private static String getCategoryUrl(String id) {
    return id + Categories.URL;
  }

  private Uri insertVideo(String id) {
    ContentValues values = new ContentValues();
    values.put(Videos.ID, id);
    values.put(Videos.TITLE, getVideoTitle(id));
    values.put(Videos.IMAGE_URL, getVideoImageUrl(id));
    values.put(Videos.DETAILS_URL, getVideoDetailsUrl(id));
    values.put(Videos.DESCRIPTION, getVideoDescriptionUrl(id));
    values.put(Videos.LIST_URL, getVideoListUrl(id));
    values.put(Videos.URL, getVideoUrl(id));
    return contentResolver.insert(Videos.CONTENT_URI, values);
  }

  private static String getVideoTitle(String id) {
    return id + Videos.TITLE;
  }

  private static String getVideoImageUrl(String id) {
    return id + Videos.IMAGE_URL;
  }

  private static String getVideoDescriptionUrl(String id) {
    return id + Videos.DESCRIPTION;
  }

  private static String getVideoListUrl(String id) {
    return id + Videos.LIST_URL;
  }

  private static String getVideoUrl(String id) {
    return id + Videos.URL;
  }

  private static String getVideoDetailsUrl(String id) {
    return id + Videos.DETAILS_URL;
  }

  private Uri addToCategory(String categoryId, String videoId) {
    ContentValues values = new ContentValues();
    values.put(CategoriesVideos.VIDEO_ID, videoId);
    return contentResolver.insert(CategoriesVideos.buildUri(categoryId), values);
  }

  private Cursor query(Uri uri) {
    return contentResolver.query(
        uri,
        null, // projection
        null, // selection
        null, // selectionArgs
        null // sortOrder
    );
  }

  private static String getString(Cursor cursor, String columnName) {
    int columnIndex = getColumnIndex(cursor, columnName);
    return cursor.getString(columnIndex);
  }

  private static int getColumnIndex(Cursor cursor, String columnName) {
    int columnIndex = cursor.getColumnIndex(columnName);
    assertFalse(columnIndex == -1);
    return columnIndex;
  }
}
