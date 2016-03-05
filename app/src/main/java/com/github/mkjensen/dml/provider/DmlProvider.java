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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.mkjensen.dml.provider.DmlContract.Categories;
import com.github.mkjensen.dml.provider.DmlContract.CategoriesVideosColumns;
import com.github.mkjensen.dml.provider.DmlContract.Videos;
import com.github.mkjensen.dml.provider.DmlDatabaseHelper.Tables;

/**
 * Danish Media License content provider. The contract between this provider and applications is
 * defined in {@link DmlContract}.
 */
public final class DmlProvider extends ContentProvider {

  private static final String TAG = "DmlProvider";

  private DmlDatabaseHelper databaseHelper;

  private DmlUriMatcher uriMatcher;

  @Override
  public boolean onCreate() {
    Log.d(TAG, "onCreate");
    databaseHelper = new DmlDatabaseHelper(getContext());
    uriMatcher = DmlUriMatcher.getInstance();
    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    Log.v(TAG, "query " + uri);
    DmlUri dmlUri = uriMatcher.match(uri); // Throws IllegalArgumentException for invalid URIs.
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    queryBuilder.setTables(dmlUri.getTable());
    switch (dmlUri) {
      case CATEGORIES:
        break;
      case CATEGORIES_ID:
        queryBuilder.appendWhere(
            String.format("%s='%s'", Categories.ID, Categories.getCategoryId(uri)));
        break;
      case CATEGORIES_ID_VIDEOS:
        queryBuilder.setTables(Tables.CATEGORIES_VIDEOS_JOIN_VIDEOS);
        queryBuilder.appendWhere(String.format("%s.%s='%s'",
            Tables.CATEGORIES_VIDEOS,
            CategoriesVideosColumns.CATEGORY_ID,
            Categories.getCategoryId(uri)));
        break;
      case VIDEOS:
        break;
      case VIDEOS_ID:
        queryBuilder.appendWhere(String.format("%s='%s'", Videos.ID, Videos.getVideoId(uri)));
        break;
      default:
        throwUnsupportedOperationException(dmlUri);
        break;
    }
    SQLiteDatabase database = databaseHelper.getReadableDatabase();
    Cursor cursor = queryBuilder.query(
        database,
        projection,
        selection,
        selectionArgs,
        null, // groupBy
        null, // having
        sortOrder);
    setNotificationUri(cursor, uri);
    return cursor;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    Log.v(TAG, "getType " + uri);
    DmlUri dmlUri = uriMatcher.match(uri);  // Throws IllegalArgumentException for invalid URIs.
    return dmlUri.getContentType();
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, ContentValues values) {
    Log.v(TAG, "insert " + uri);
    DmlUri dmlUri = uriMatcher.match(uri); // Throws IllegalArgumentException for invalid URIs.
    switch (dmlUri) {
      case CATEGORIES:
      case VIDEOS:
        break;
      case CATEGORIES_ID_VIDEOS:
        values.put(CategoriesVideosColumns.CATEGORY_ID, Categories.getCategoryId(uri));
        break;
      default:
        throwUnsupportedOperationException(dmlUri);
    }
    try (SQLiteDatabase database = databaseHelper.getWritableDatabase()) {
      database.insertOrThrow(
          dmlUri.getTable(),
          null, // nullColumnHack
          values);
      notifyChange(uri);
    }
    switch (dmlUri) {
      case CATEGORIES:
      case CATEGORIES_ID_VIDEOS:
        return Categories.buildCategoryUri(values.getAsString(Categories.ID));
      case VIDEOS:
        return Videos.buildVideoUri(values.getAsString(Videos.ID));
      default:
        throwUnsupportedOperationException(dmlUri);
        return null;
    }
  }

  @Override
  public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
    Log.v(TAG, "delete " + uri);
    uriMatcher.match(uri); // Throws IllegalArgumentException for invalid URIs.
    return 0;
  }

  @Override
  public int update(@NonNull Uri uri, ContentValues values, String selection,
                    String[] selectionArgs) {
    Log.v(TAG, "update " + uri);
    uriMatcher.match(uri); // Throws IllegalArgumentException for invalid URIs.
    return 0;
  }

  private void setNotificationUri(Cursor cursor, Uri uri) {
    Context context = getContext();
    if (context == null) {
      return;
    }
    cursor.setNotificationUri(context.getContentResolver(), uri);
  }

  private void notifyChange(Uri uri) {
    Context context = getContext();
    if (context == null) {
      return;
    }
    context.getContentResolver().notifyChange(
        uri,
        null, // observer,
        false // syncToNetwork
    );
  }

  private void throwUnsupportedOperationException(DmlUri dmlUri) {
    throw new UnsupportedOperationException("Not implemented: " + dmlUri);
  }
}
