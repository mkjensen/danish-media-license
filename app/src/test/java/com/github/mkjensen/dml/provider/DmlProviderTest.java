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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.github.mkjensen.dml.provider.DmlContract.Category;
import com.github.mkjensen.dml.provider.DmlContract.Video;
import com.github.mkjensen.dml.test.ContentUtils;
import com.github.mkjensen.dml.test.CursorUtils;
import com.github.mkjensen.dml.test.RobolectricTest;
import com.github.mkjensen.dml.test.VideoUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
   * Registers the content provider and sets {@link #contentResolver}.
   */
  @Before
  public void before() {
    ContentUtils.registerContentProvider();
    contentResolver = ContentUtils.getContentResolver();
  }

  @Test
  public void query_whenInvalidUri_thenIllegalArgumentExceptionIsThrown() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown URI: " + INVALID_URL);
    ContentUtils.query(contentResolver, INVALID_URL);
  }

  @Test
  public void query_whenRequestingNonexistentCategory_thenEmptyCursorIsReturned() {

    // When
    try (Cursor cursor = ContentUtils.query(contentResolver,
        Category.buildCategoryUri(NONEXISTENT_ID))) {

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
    try (Cursor cursor = ContentUtils.query(contentResolver, Category.CONTENT_URI)) {

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
    try (Cursor cursor = ContentUtils.query(contentResolver,
        Category.buildCategoryUri(expectedId))) {

      // Then
      assertNotNull(cursor);
      assertTrue(cursor.moveToFirst());
      String actualId = CursorUtils.getString(cursor, Category.CATEGORY_ID);
      assertEquals(expectedId, actualId);
      assertEquals(getCategoryTitle(actualId),
          CursorUtils.getString(cursor, Category.CATEGORY_TITLE));
      assertEquals(getCategoryUrl(actualId), CursorUtils.getString(cursor, Category.CATEGORY_URL));
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
    try (Cursor cursor = ContentUtils.query(contentResolver, Category.buildVideosUri("c1"))) {

      // Then
      assertNotNull(cursor);
      assertTrue(cursor.moveToFirst());
      assertEquals(2, cursor.getCount());
    }
  }

  @Test
  public void query_whenRequestingNonexistentVideo_thenEmptyCursorIsReturned() {

    // When
    try (Cursor cursor = ContentUtils.query(contentResolver, Video.buildVideoUri(NONEXISTENT_ID))) {

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
    try (Cursor cursor = ContentUtils.query(contentResolver, Video.CONTENT_URI)) {

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
    try (Cursor cursor = ContentUtils.query(contentResolver, Video.buildVideoUri(expectedId))) {

      // Then
      assertNotNull(cursor);
      assertTrue(cursor.moveToFirst());
      String actualId = CursorUtils.getString(cursor, Video.VIDEO_ID);
      assertEquals(expectedId, actualId);
      assertEquals(VideoUtils.getVideoTitle(actualId),
          CursorUtils.getString(cursor, Video.VIDEO_TITLE));
      assertEquals(VideoUtils.getVideoImageUrl(actualId),
          CursorUtils.getString(cursor, Video.VIDEO_IMAGE_URL));
      assertEquals(VideoUtils.getVideoDescription(actualId),
          CursorUtils.getString(cursor, Video.VIDEO_DESCRIPTION));
      assertEquals(VideoUtils.getVideoListUrl(actualId),
          CursorUtils.getString(cursor, Video.VIDEO_LIST_URL));
      assertEquals(VideoUtils.getVideoUrl(actualId),
          CursorUtils.getString(cursor, Video.VIDEO_URL));
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
    assertEquals(Category.buildCategoryUri(id), uri);
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
    assertEquals(Category.buildVideosUri(categoryId), uri);
  }

  @Test
  public void insert_whenVideosUri_thenReturnedUriContainsId() {

    // When
    String id = "v0";
    Uri uri = insertVideo(id);

    // Then
    assertEquals(Video.buildVideoUri(id), uri);
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
    String type = contentResolver.getType(Category.CONTENT_URI);

    // Then
    assertEquals(DmlUri.CATEGORIES.getContentType(), type);
  }

  @Test
  public void getType_whenCategoriesIdUri_thenCategoriesIdContentTypeIsReturned() {

    // When
    String type = contentResolver.getType(Category.buildCategoryUri(NONEXISTENT_ID));

    // Then
    assertEquals(DmlUri.CATEGORIES_ID.getContentType(), type);
  }

  @Test
  public void getType_whenCategoriesIdVideosUri_thenCategoriesIdVideosContentTypeIsReturned() {

    // When
    String type = contentResolver.getType(Category.buildVideosUri(NONEXISTENT_ID));

    // Then
    assertEquals(DmlUri.CATEGORIES_ID_VIDEOS.getContentType(), type);
  }

  @Test
  public void getType_whenVideosUri_thenVideosContentTypeIsReturned() {

    // When
    String type = contentResolver.getType(Video.CONTENT_URI);

    // Then
    assertEquals(DmlUri.VIDEOS.getContentType(), type);
  }

  @Test
  public void getType_whenVideosIdUri_thenVideosIdContentTypeIsReturned() {

    // When
    String type = contentResolver.getType(Video.buildVideoUri(NONEXISTENT_ID));

    // Then
    assertEquals(DmlUri.VIDEOS_ID.getContentType(), type);
  }

  @Test
  public void delete_whenInvalidUri_thenIllegalArgumentExceptionIsThrown() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown URI: " + INVALID_URL);
    ContentUtils.delete(contentResolver, INVALID_URL);
  }

  @Test
  public void delete_whenCategoriesUri_thenAllCategoriesAreDeleted() {

    // Given
    insertCategory("c0");
    insertCategory("c1");
    insertCategory("c2");

    // When
    int deleted = ContentUtils.delete(contentResolver, Category.CONTENT_URI);

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
    int deleted = ContentUtils.delete(contentResolver, Category.buildCategoryUri("c1"));

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
    int deleted = ContentUtils.delete(contentResolver, Category.buildVideosUri("c1"));

    try (Cursor categoriesCursor = ContentUtils.query(contentResolver, Video.CONTENT_URI)) {

      try (Cursor videosCursor = ContentUtils.query(contentResolver, Category.CONTENT_URI)) {

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
    int deleted = ContentUtils.delete(contentResolver, Video.CONTENT_URI);

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
    int deleted = ContentUtils.delete(contentResolver, Video.buildVideoUri("v1"));

    // Then
    assertEquals(1, deleted);
  }

  @Test
  public void update_whenInvalidUri_thenIllegalArgumentExceptionIsThrown() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown URI: " + INVALID_URL);
    ContentUtils.update(contentResolver, INVALID_URL);
  }

  @Test
  public void update_whenCategoriesUri_thenZeroIsReturned() {

    // When
    int updated = ContentUtils.update(contentResolver, Category.CONTENT_URI);

    // Then
    assertEquals(0, updated);
  }

  @Test
  public void update_whenCategoriesIdUri_thenZeroIsReturned() {

    // When
    int updated = ContentUtils.update(contentResolver, Category.buildCategoryUri(NONEXISTENT_ID));

    // Then
    assertEquals(0, updated);
  }

  @Test
  public void update_whenCategoriesIdVideosUri_thenZeroIsReturned() {

    // When
    int updated = ContentUtils.update(contentResolver, Category.buildVideosUri(NONEXISTENT_ID));

    // Then
    assertEquals(0, updated);
  }

  @Test
  public void update_whenVideosUri_thenZeroIsReturned() {

    // When
    int updated = ContentUtils.update(contentResolver, Video.CONTENT_URI);

    // Then
    assertEquals(0, updated);
  }

  @Test
  public void update_whenVideosIdUri_thenZeroIsReturned() {

    // When
    int updated = ContentUtils.update(contentResolver, Video.buildVideoUri(NONEXISTENT_ID));

    // Then
    assertEquals(0, updated);
  }

  private Uri insertCategory(String id) {
    ContentValues values = new ContentValues();
    values.put(Category.CATEGORY_ID, id);
    values.put(Category.CATEGORY_TITLE, getCategoryTitle(id));
    values.put(Category.CATEGORY_URL, getCategoryUrl(id));
    return contentResolver.insert(Category.CONTENT_URI, values);
  }

  private static String getCategoryTitle(String id) {
    return id + Category.CATEGORY_TITLE;
  }

  private static String getCategoryUrl(String id) {
    return id + Category.CATEGORY_URL;
  }

  private Uri insertVideo(String id) {
    ContentValues values = VideoUtils.createContentValues(id);
    return contentResolver.insert(Video.CONTENT_URI, values);
  }

  private Uri addToCategory(String categoryId, String videoId) {
    ContentValues values = new ContentValues();
    values.put(Video.VIDEO_ID, videoId);
    return contentResolver.insert(Category.buildVideosUri(categoryId), values);
  }
}
