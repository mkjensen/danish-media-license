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

import static com.github.mkjensen.dml.provider.DmlDatabase.CategoryColumns.CATEGORY_ID;
import static com.github.mkjensen.dml.provider.DmlDatabase.CategoryColumns.CATEGORY_TITLE;
import static com.github.mkjensen.dml.provider.DmlDatabase.CategoryColumns.CATEGORY_URL;
import static com.github.mkjensen.dml.provider.DmlDatabase.VideoColumns.VIDEO_DESCRIPTION;
import static com.github.mkjensen.dml.provider.DmlDatabase.VideoColumns.VIDEO_ID;
import static com.github.mkjensen.dml.provider.DmlDatabase.VideoColumns.VIDEO_IMAGE_URL;
import static com.github.mkjensen.dml.provider.DmlDatabase.VideoColumns.VIDEO_LIST_URL;
import static com.github.mkjensen.dml.provider.DmlDatabase.VideoColumns.VIDEO_TITLE;
import static com.github.mkjensen.dml.provider.DmlDatabase.VideoColumns.VIDEO_URL;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * A helper class to manage database creation and version management for {@link DmlProvider}.
 */
final class DmlDatabase extends SQLiteOpenHelper {

  private static final String TAG = "DmlDatabase";

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
  DmlDatabase(Context context) {
    super(context, NAME, FACTORY, VERSION, ERROR_HANDLER);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    Log.d(TAG, "onCreate");

    db.execSQL("CREATE TABLE " + Table.CATEGORY + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + CATEGORY_ID + " TEXT NOT NULL,"
        + CATEGORY_TITLE + " TEXT NOT NULL,"
        + CATEGORY_URL + " TEXT NOT NULL,"
        + "UNIQUE (" + CATEGORY_ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Table.VIDEO + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + VIDEO_ID + " TEXT NOT NULL,"
        + VIDEO_TITLE + " TEXT NOT NULL,"
        + VIDEO_IMAGE_URL + " TEXT NOT NULL,"
        + VIDEO_DESCRIPTION + " TEXT,"
        + VIDEO_LIST_URL + " TEXT,"
        + VIDEO_URL + " TEXT,"
        + "UNIQUE (" + VIDEO_ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Table.CATEGORY_VIDEO + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + CATEGORY_ID + " TEXT NOT NULL " + getReference(Table.CATEGORY, CATEGORY_ID) + ","
        + VIDEO_ID + " TEXT NOT NULL " + getReference(Table.VIDEO, VIDEO_ID) + ","
        + "UNIQUE (" + CATEGORY_ID + "," + VIDEO_ID + ") ON CONFLICT REPLACE)");
  }

  private static String getReference(String table, String column) {
    return String.format("REFERENCES %s (%s)", table, column);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(TAG, String.format("onUpgrade %d, %d (data will be lost)", oldVersion, newVersion));
    db.execSQL("DROP TABLE IF EXISTS " + Table.CATEGORY_VIDEO);
    db.execSQL("DROP TABLE IF EXISTS " + Table.VIDEO);
    db.execSQL("DROP TABLE IF EXISTS " + Table.CATEGORY);
    onCreate(db);
  }

  /**
   * Constants for tables.
   */
  interface Table {

    String CATEGORY = "category";

    String VIDEO = "video";

    String CATEGORY_VIDEO = "category_video";

    String CATEGORY_VIDEO_JOIN_VIDEO = String.format("%s INNER JOIN %s ON (%s.%s = %s.%s)",
        CATEGORY_VIDEO,
        VIDEO,
        CATEGORY_VIDEO,
        VIDEO_ID,
        VIDEO,
        VIDEO_ID);
  }

  /**
   * Columns of {@link Table#CATEGORY}.
   */
  interface CategoryColumns {

    String CATEGORY_ID = "category_id";

    String CATEGORY_TITLE = "category_title";

    String CATEGORY_URL = "category_url";
  }

  /**
   * Columns of {@link Table#VIDEO}.
   */
  interface VideoColumns {

    String VIDEO_ID = "video_id";

    String VIDEO_TITLE = "video_title";

    String VIDEO_IMAGE_URL = "video_image_url";

    String VIDEO_DESCRIPTION = "video_description";

    String VIDEO_LIST_URL = "video_list_url";

    String VIDEO_URL = "video_url";
  }
}
