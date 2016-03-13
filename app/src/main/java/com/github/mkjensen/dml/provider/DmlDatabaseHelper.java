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

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.github.mkjensen.dml.provider.DmlContract.CategoriesColumns;
import com.github.mkjensen.dml.provider.DmlContract.VideosColumns;

/**
 * A helper class to manage database creation and version management for {@link DmlProvider}.
 */
final class DmlDatabaseHelper extends SQLiteOpenHelper {

  private static final String TAG = "DmlDatabaseHelper";

  /**
   * Name of the file in which the database is stored.
   */
  private static final String NAME = "dml.db";

  /**
   * Constant for specifying the default {@link SQLiteDatabase.CursorFactory}.
   */
  private static final SQLiteDatabase.CursorFactory FACTORY = null;

  /**
   * Monotonically increasing version number. Must be incremented when the database schema is
   * changed.
   */
  private static final int VERSION = 1;

  /**
   * Constant for specifying the default {@link DatabaseErrorHandler}.
   */
  private static final DatabaseErrorHandler ERROR_HANDLER = null;

  /**
   * Creates a helper object to create, open, and/or manage the database.
   *
   * @param context the {@link Context} to use to open or create the database
   */
  DmlDatabaseHelper(Context context) {
    super(context, NAME, FACTORY, VERSION, ERROR_HANDLER);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    Log.d(TAG, "onCreate");

    db.execSQL("CREATE TABLE " + Tables.CATEGORIES + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + CategoriesColumns.ID + " TEXT NOT NULL,"
        + CategoriesColumns.TITLE + " TEXT NOT NULL,"
        + CategoriesColumns.URL + " TEXT NOT NULL,"
        + "UNIQUE (" + CategoriesColumns.ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.VIDEOS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + VideosColumns.ID + " TEXT NOT NULL,"
        + VideosColumns.TITLE + " TEXT NOT NULL,"
        + VideosColumns.IMAGE_URL + " TEXT NOT NULL,"
        + VideosColumns.DETAILS_URL + " TEXT NOT NULL,"
        + VideosColumns.DESCRIPTION + " TEXT,"
        + VideosColumns.LIST_URL + " TEXT,"
        + VideosColumns.URL + " TEXT,"
        + "UNIQUE (" + VideosColumns.ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.CATEGORIES_VIDEOS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + CategoriesColumns.ID + " TEXT NOT NULL " + References.CATEGORY_ID + ","
        + VideosColumns.ID + " TEXT NOT NULL " + References.VIDEO_ID + ","
        + "UNIQUE (" + CategoriesColumns.ID + "," + VideosColumns.ID + ") ON CONFLICT REPLACE)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(TAG, String.format("onUpgrade %d, %d (data will be lost)", oldVersion, newVersion));
    db.execSQL("DROP TABLE IF EXISTS " + Tables.CATEGORIES_VIDEOS);
    db.execSQL("DROP TABLE IF EXISTS " + Tables.VIDEOS);
    db.execSQL("DROP TABLE IF EXISTS " + Tables.CATEGORIES);
    onCreate(db);
  }

  /**
   * Constants for tables.
   */
  interface Tables {

    String CATEGORIES = "categories";

    String VIDEOS = "videos";

    String CATEGORIES_VIDEOS = "categories_videos";

    String CATEGORIES_VIDEOS_JOIN_VIDEOS = String.format("%s INNER JOIN %s ON (%s.%s = %s.%s)",
        CATEGORIES_VIDEOS,
        VIDEOS,
        CATEGORIES_VIDEOS,
        VideosColumns.ID,
        VIDEOS,
        VideosColumns.ID);
  }

  /**
   * Constants for creating foreign keys.
   */
  private interface References {

    String CATEGORY_ID = "REFERENCES " + Tables.CATEGORIES + "(" + CategoriesColumns.ID + ")";

    String VIDEO_ID = "REFERENCES " + Tables.VIDEOS + "(" + VideosColumns.ID + ")";
  }
}
