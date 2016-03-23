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

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.github.mkjensen.dml.backend.BackendHelper;
import com.github.mkjensen.dml.backend.Category;
import com.github.mkjensen.dml.backend.Video;
import com.github.mkjensen.dml.provider.DmlContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Sync adapter for downloading content.
 */
final class DmlSyncAdapter extends AbstractThreadedSyncAdapter {

  private static final String TAG = "DmlSyncAdapter";

  private final BackendHelper backendHelper;

  // @formatter:off
  /**
   * Whether or not sync requests that have
   * {@link android.content.ContentResolver#SYNC_EXTRAS_INITIALIZE} set will be internally handled
   * by {@link AbstractThreadedSyncAdapter}.
   */
  // @formatter:on
  private static final boolean AUTO_INITIALIZE = true;

  /**
   * Specifies whether or not syncs for different accounts are allowed to run at the same time, each
   * in their own thread. This must be consistent with the setting in the sync adapter's
   * configuration file.
   */
  private static final boolean ALLOW_PARALLEL_SYNCS = false;

  /**
   * Creates a {@link DmlSyncAdapter}.
   *
   * @param context the {@link Context} that the sync adapter is running within
   */
  DmlSyncAdapter(Context context) {
    this(context, new BackendHelper());
  }

  /**
   * For testing.
   */
  DmlSyncAdapter(Context context, BackendHelper backendHelper) {
    super(context, AUTO_INITIALIZE, ALLOW_PARALLEL_SYNCS);
    this.backendHelper = backendHelper;
  }

  @Override
  public void onPerformSync(Account account, Bundle extras, String authority,
                            ContentProviderClient provider, SyncResult syncResult) {
    Log.d(TAG, "onPerformSync");
    ArrayList<ContentProviderOperation> operations = new ArrayList<>();
    for (Category category : loadCategories(syncResult)) {
      if (!loadVideos(category, syncResult)) {
        // TODO: Remove category if it exists
        continue;
      }
      if (!hasCategory(provider, category)) {
        addCategory(operations, category);
      }
      insertAndAddVideosToCategory(operations, category);
    }
    applyOperations(provider, operations);
    // TODO: Remove old videos that are no longer associated with a category
  }

  private List<Category> loadCategories(SyncResult syncResult) {
    try {
      return backendHelper.loadCategories();
    } catch (IOException ex) {
      Log.e(TAG, "Could not load categories", ex);
      syncResult.stats.numIoExceptions++;
      return Collections.emptyList();
    }
  }

  private static boolean hasCategory(ContentProviderClient provider, Category category) {
    try (Cursor cursor = provider.query(
        DmlContract.Category.buildCategoryUri(category.getId()),
        null, // projection,
        null, // selection
        null, //selectionArgs,
        null // sortOrder
    )) {
      return cursor != null && cursor.getCount() == 1;
    } catch (RemoteException ex) {
      Log.e(TAG, String.format("Error querying category [%s]", category.getId()), ex);
      return false;
    }
  }

  private static void addCategory(List<ContentProviderOperation> operations, Category category) {
    ContentProviderOperation operation = ContentProviderOperation
        .newInsert(DmlContract.Category.CONTENT_URI)
        .withValue(DmlContract.Category.CATEGORY_ID, category.getId())
        .withValue(DmlContract.Category.CATEGORY_TITLE, category.getTitle())
        .withValue(DmlContract.Category.CATEGORY_URL, category.getUrl())
        .build();
    operations.add(operation);
  }

  private boolean loadVideos(Category category, SyncResult syncResult) {
    if (!loadCategoryVideos(category, syncResult)) {
      return false;
    }
    List<Video> videos = category.getVideos();
    for (Iterator<Video> it = videos.iterator(); it.hasNext(); ) {
      Video video = it.next();
      if (!loadVideoDetails(video, syncResult) || !loadVideoUrl(video, syncResult)) {
        it.remove();
      }
    }
    return !videos.isEmpty();
  }

  private boolean loadCategoryVideos(Category category, SyncResult syncResult) {
    try {
      backendHelper.loadVideos(category);
      return true;
    } catch (IOException ex) {
      Log.e(TAG, String.format("Could not load videos for category [%s]", category.getId()), ex);
      syncResult.stats.numIoExceptions++;
      return false;
    }
  }

  private boolean loadVideoDetails(Video video, SyncResult syncResult) {
    try {
      backendHelper.loadVideoDetails(video);
      return true;
    } catch (IOException ex) {
      Log.e(TAG, String.format("Could not load video details for video [%s]", video.getId()), ex);
      syncResult.stats.numIoExceptions++;
      return false;
    }
  }

  private boolean loadVideoUrl(Video video, SyncResult syncResult) {
    try {
      backendHelper.loadVideoUrl(video);
      return true;
    } catch (IOException ex) {
      Log.e(TAG, String.format("Could not load video URL for video [%s]", video.getId()), ex);
      syncResult.stats.numIoExceptions++;
      return false;
    }
  }

  private static void insertAndAddVideosToCategory(List<ContentProviderOperation> operations,
                                                   Category category) {
    // TODO: Check if video already exists, possibly update instead of deleting and inserting
    deleteVideos(operations, category);
    for (Video video : category.getVideos()) {
      insertVideo(operations, video);
      addVideoToCategory(operations, category, video);
    }
  }

  private static void deleteVideos(List<ContentProviderOperation> operations, Category category) {
    ContentProviderOperation operation = ContentProviderOperation
        .newDelete(DmlContract.Category.buildVideosUri(category.getId()))
        .build();
    operations.add(operation);
  }

  private static void insertVideo(List<ContentProviderOperation> operations, Video video) {
    ContentProviderOperation operation = ContentProviderOperation
        .newInsert(DmlContract.Video.CONTENT_URI)
        .withValue(DmlContract.Video.VIDEO_ID, video.getId())
        .withValue(DmlContract.Video.VIDEO_TITLE, video.getTitle())
        .withValue(DmlContract.Video.VIDEO_IMAGE_URL, video.getImageUrl())
        .withValue(DmlContract.Video.VIDEO_DESCRIPTION, video.getDescription())
        .withValue(DmlContract.Video.VIDEO_LINKS_URL, video.getLinksUrl())
        .withValue(DmlContract.Video.VIDEO_URL, video.getUrl())

        .build();
    operations.add(operation);
  }

  private static void addVideoToCategory(List<ContentProviderOperation> operations,
                                         Category category, Video video) {
    ContentProviderOperation operation = ContentProviderOperation
        .newInsert(DmlContract.Category.buildVideosUri(category.getId()))
        .withValue(DmlContract.Video.VIDEO_ID, video.getId())
        .build();
    operations.add(operation);
  }

  private static void applyOperations(ContentProviderClient provider,
                                      ArrayList<ContentProviderOperation> operations) {
    if (operations.isEmpty()) {
      return;
    }
    try {
      Log.d(TAG, "Applying batch");
      provider.applyBatch(operations);
      // TODO: How to update syncResult.stats?
    } catch (RemoteException | OperationApplicationException ex) {
      Log.e(TAG, "Error applying batch", ex);
      // TODO: Update syncResult.stats?
    }
  }
}
