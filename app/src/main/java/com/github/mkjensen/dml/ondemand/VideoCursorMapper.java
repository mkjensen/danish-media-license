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

import android.database.Cursor;
import android.support.v17.leanback.database.CursorMapper;

import com.github.mkjensen.dml.provider.DmlContract;

/**
 * Converts {@link Cursor} to {@link Video}.
 */
class VideoCursorMapper extends CursorMapper {

  private int idIndex;

  private int titleIndex;

  private int imageUrlIndex;

  private int detailsUrlIndex;

  private int descriptionIndex;

  private int listUrlIndex;

  private int urlIndex;

  @Override
  protected void bindColumns(Cursor cursor) {
    idIndex = cursor.getColumnIndex(DmlContract.Video.VIDEO_ID);
    titleIndex = cursor.getColumnIndex(DmlContract.Video.VIDEO_TITLE);
    imageUrlIndex = cursor.getColumnIndex(DmlContract.Video.VIDEO_IMAGE_URL);
    detailsUrlIndex = cursor.getColumnIndex(DmlContract.Video.VIDEO_DETAILS_URL);
    descriptionIndex = cursor.getColumnIndex(DmlContract.Video.VIDEO_DESCRIPTION);
    listUrlIndex = cursor.getColumnIndex(DmlContract.Video.VIDEO_LIST_URL);
    urlIndex = cursor.getColumnIndex(DmlContract.Video.VIDEO_URL);
  }

  @Override
  protected Object bind(Cursor cursor) {
    String id = cursor.getString(idIndex);
    String title = cursor.getString(titleIndex);
    String imageUrl = cursor.getString(imageUrlIndex);
    String detailsUrl = cursor.getString(detailsUrlIndex);
    String description = cursor.getString(descriptionIndex);
    String listUrl = cursor.getString(listUrlIndex);
    String url = cursor.getString(urlIndex);
    return new Video.Builder()
        .id(id)
        .title(title)
        .imageUrl(imageUrl)
        .detailsUrl(detailsUrl)
        .description(description)
        .listUrl(listUrl)
        .url(url)
        .build();
  }
}
