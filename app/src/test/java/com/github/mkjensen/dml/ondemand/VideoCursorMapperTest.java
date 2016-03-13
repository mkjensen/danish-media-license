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

package com.github.mkjensen.dml.ondemand;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.spy;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.github.mkjensen.dml.provider.DmlContract;
import com.github.mkjensen.dml.provider.DmlProvider;
import com.github.mkjensen.dml.test.PowerMockRobolectricTest;
import com.github.mkjensen.dml.test.VideoUtils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowContentResolver;

/**
 * Tests for {@link VideoCursorMapper}.
 */
public class VideoCursorMapperTest extends PowerMockRobolectricTest {

  private ContentResolver contentResolver;

  private VideoCursorMapper cursorMapper;

  /**
   * Registers {@link DmlProvider} as provider for {@link DmlContract#AUTHORITY} and sets {@link
   * #contentResolver} and {@link #cursorMapper}.
   */
  @Before
  public void before() {
    regusterContentProvider();
    contentResolver = RuntimeEnvironment.application.getContentResolver();
    cursorMapper = new VideoCursorMapper();
  }

  private static void regusterContentProvider() {
    ContentProvider contentProvider = new DmlProvider();
    contentProvider.onCreate();
    ShadowContentResolver.registerProvider(DmlContract.AUTHORITY, contentProvider);
  }

  @Test
  public void bindColumns_getsColumnIndicesFromCursorAndNothingElse() {

    // Given
    try (Cursor cursor = query()) {
      String[] columnNames = cursor.getColumnNames();
      Cursor cursorMock = spy(cursor);

      // When
      cursorMapper.bindColumns(cursorMock);

      // Then
      for (String columnName : columnNames) {
        if (DmlContract.Video._ID.equals(columnName)) {
          continue;
        }
        verify(cursorMock).getColumnIndex(columnName);
      }
      verifyNoMoreInteractions(cursorMock);
    }
  }

  @Test
  public void bind_getsDataFromCursor() throws IllegalAccessException {

    // Given
    insertVideo("id");
    try (Cursor cursor = query()) {
      cursor.moveToFirst();
      String[] columnNames = cursor.getColumnNames();
      cursorMapper.bindColumns(cursor);
      Cursor cursorMock = spy(cursor);

      // When
      cursorMapper.bind(cursorMock);

      // Then
      for (String columnName : columnNames) {
        if (DmlContract.Video._ID.equals(columnName)) {
          continue;
        }
        int columnIndex = cursor.getColumnIndex(columnName);
        verify(cursorMock).getString(columnIndex);
      }
      // Cannot use verifyNoMoreInteractions because the Cursor implementation apparently calls
      // itself multiple times.
    }
  }

  @Test
  public void convert_outputDataIsBuiltFromInputData() {

    // Given
    String id = "id";
    insertVideo(id);
    try (Cursor cursor = query()) {
      cursor.moveToFirst();

      // When
      Video video = (Video) cursorMapper.convert(cursor);

      // Then
      assertEquals(id, video.getId());
      assertEquals(VideoUtils.getVideoTitle(id), video.getTitle());
      assertEquals(VideoUtils.getVideoImageUrl(id), video.getImageUrl());
      assertEquals(VideoUtils.getVideoDetailsUrl(id), video.getDetailsUrl());
      assertEquals(VideoUtils.getVideoDescription(id), video.getDescription());
      assertEquals(VideoUtils.getVideoListUrl(id), video.getListUrl());
      assertEquals(VideoUtils.getVideoUrl(id), video.getUrl());
    }
  }

  private Cursor query() {
    return contentResolver.query(
        DmlContract.Video.CONTENT_URI,
        null, // projection
        null, // selection
        null, // selectionArgs
        null // sortOrder
    );
  }

  private Uri insertVideo(String id) {
    ContentValues values = VideoUtils.createContentValues(id);
    return contentResolver.insert(DmlContract.Video.CONTENT_URI, values);
  }
}
