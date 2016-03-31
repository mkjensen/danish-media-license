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

package com.github.mkjensen.dml.backend;

import static com.github.mkjensen.dml.util.Preconditions.notNull;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.model.Category;
import com.github.mkjensen.dml.model.Video;
import com.github.mkjensen.dml.model.VideoManifest;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.Locale;

/**
 * A helper class that manages communication with backend web services. Methods on this class should
 * not be called from the UI thread as operations may take a while.
 */
public final class BackendHelper {

  private static final String TAG = "BackendHelper";

  private final Context context;

  private final DmlWebService webService;

  public BackendHelper(@NonNull Context context, @NonNull Retrofit retrofit) {
    this.context = notNull(context);
    this.webService = notNull(retrofit).create(DmlWebService.class);
  }

  /**
   * Loads the category containing the most viewed on-demand videos.
   */
  @NonNull
  public Category loadMostViewedCategory() throws IOException {
    Log.d(TAG, "loadMostViewedCategory");
    Call<Category> call = webService.getMostViewedCategory();
    Category category = executeCall(call);
    category.setTitle(context.getString(R.string.backend_category_most_viewed));
    return category;
  }

  /**
   * Loads the category containing the new on-demand videos.
   */
  @NonNull
  public Category loadNewCategory() throws IOException {
    Log.d(TAG, "loadNewCategory");
    Call<Category> call = webService.getNewCategory();
    Category category = executeCall(call);
    category.setTitle(context.getString(R.string.backend_category_new));
    return category;
  }

  /**
   * Loads the category containing the recommended on-demand videos.
   */
  @NonNull
  public Category loadRecommendedCategory() throws IOException {
    Log.d(TAG, "loadSelectedCategory");
    Call<Category> call = webService.getRecommendedCategory();
    Category category = executeCall(call);
    category.setTitle(context.getString(R.string.backend_category_recommended));
    return category;
  }

  /**
   * Executes the specified query and returns a category containing the relevant on-demand videos.
   */
  @NonNull
  public Category search(@NonNull String query) throws IOException {
    Log.d(TAG, String.format("search [%s]", query));
    Call<Category> call = webService.search(query);
    Category category = executeCall(call);
    category.setTitle(query);
    return category;
  }

  /**
   * Loads the specified on-demand video.
   */
  @NonNull
  public Video loadVideo(@NonNull String id) throws IOException {
    Log.d(TAG, String.format("loadVideoDetails [%s]", id));
    Call<Video> call = webService.getVideo(id);
    return executeCall(call);
  }

  /**
   * Loads the video manifest from the specified URL.
   */
  @NonNull
  public VideoManifest loadVideoManifest(@NonNull String manifestUrl) throws IOException {
    Log.d(TAG, String.format("loadVideoManifest [%s]", manifestUrl));
    Call<VideoManifest> call = webService.getVideoManifest(manifestUrl);
    return executeCall(call);
  }

  private static <T> T executeCall(Call<T> call) throws IOException {
    Response<T> response = call.execute();
    if (!response.isSuccessful()) {
      throw new IOException(String.format(Locale.US,
          "Got code: [%d], message: [%s] when requesting: [%s]",
          response.code(), response.message(), call.request().url()));
    }
    return response.body();
  }
}
