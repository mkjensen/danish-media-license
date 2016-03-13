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

import com.github.mkjensen.dml.provider.DmlContract.Category;
import com.github.mkjensen.dml.provider.DmlContract.Video;
import com.github.mkjensen.dml.provider.DmlDatabase.Table;

/**
 * Danish Media License content provider. The contract between this provider and applications is
 * defined in {@link DmlContract}.
 */
public final class DmlProvider extends ContentProvider {

  private static final String TAG = "DmlProvider";

  private DmlDatabase database;

  private DmlUriMatcher uriMatcher;

  @Override
  public boolean onCreate() {
    Log.d(TAG, "onCreate");
    database = new DmlDatabase(getContext());
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
      case VIDEOS:
        break;
      case CATEGORIES_ID:
        queryBuilder.appendWhere(
            String.format("%s='%s'", Category.CATEGORY_ID, Category.getCategoryId(uri)));
        break;
      case CATEGORIES_ID_VIDEOS:
        queryBuilder.setTables(Table.CATEGORY_VIDEO_JOIN_VIDEO);
        queryBuilder.appendWhere(String.format("%s.%s='%s'",
            Table.CATEGORY_VIDEO,
            Category.CATEGORY_ID,
            Category.getCategoryId(uri)));
        break;
      case VIDEOS_ID:
        queryBuilder.appendWhere(String.format("%s='%s'", Video.VIDEO_ID, Video.getVideoId(uri)));
        break;
      default:
        throwUnsupportedOperationException(dmlUri);
        return null;
    }
    SQLiteDatabase db = database.getReadableDatabase();
    Cursor cursor = queryBuilder.query(
        db,
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
    try {
      DmlUri dmlUri = uriMatcher.match(uri);  // Throws IllegalArgumentException for invalid URIs.
      return dmlUri.getContentType();
    } catch (IllegalArgumentException ex) {
      return null;
    }
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
        values.put(Category.CATEGORY_ID, Category.getCategoryId(uri));
        break;
      default:
        throwUnsupportedOperationException(dmlUri);
        return null;
    }
    SQLiteDatabase db = database.getWritableDatabase();
    db.insertOrThrow(
        dmlUri.getTable(),
        null, // nullColumnHack
        values);
    notifyChange(uri);
    switch (dmlUri) {
      case CATEGORIES:
        return Category.buildCategoryUri(values.getAsString(Category.CATEGORY_ID));
      case CATEGORIES_ID_VIDEOS:
        return Category.buildVideosUri(values.getAsString(Category.CATEGORY_ID));
      case VIDEOS:
        return Video.buildVideoUri(values.getAsString(Video.VIDEO_ID));
      default:
        throwUnsupportedOperationException(dmlUri);
        return null;
    }
  }

  @Override
  public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
    Log.v(TAG, "delete " + uri);
    DmlUri dmlUri = uriMatcher.match(uri); // Throws IllegalArgumentException for invalid URIs.
    String whereClause = null;
    String[] whereArgs = null;
    switch (dmlUri) {
      case CATEGORIES:
      case VIDEOS:
        break;
      case CATEGORIES_ID:
        whereClause = Category.CATEGORY_ID + "=?";
        whereArgs = new String[] {Category.getCategoryId(uri)};
        break;
      case CATEGORIES_ID_VIDEOS:
        whereClause = Category.CATEGORY_ID + "=?";
        whereArgs = new String[] {Category.getCategoryId(uri)};
        break;
      case VIDEOS_ID:
        whereClause = Video.VIDEO_ID + "=?";
        whereArgs = new String[] {Video.getVideoId(uri)};
        break;
      default:
        throwUnsupportedOperationException(dmlUri);
        return 0;
    }
    SQLiteDatabase db = database.getWritableDatabase();
    int deleted = db.delete(
        dmlUri.getTable(),
        whereClause,
        whereArgs
    );
    notifyChange(uri);
    return deleted;
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
