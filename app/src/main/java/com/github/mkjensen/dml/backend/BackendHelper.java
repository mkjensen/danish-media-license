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

import static com.github.mkjensen.dml.Defense.notNull;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.mkjensen.dml.R;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.io.IOException;
import java.util.Locale;

/**
 * A helper class that manages communication with backend web services. Methods on this class should
 * not be called from the UI thread as operations may take a while.
 */
final class BackendHelper {

  private static final String TAG = "BackendHelper";

  private final Context context;

  private final DmlWebService webService;

  BackendHelper(@NonNull Context context) {
    this(context, null);
  }

  BackendHelper(@NonNull Context context, @Nullable okhttp3.Call.Factory callFactory) {
    this.context = notNull(context);
    this.webService = createWebService(callFactory);
  }

  private static DmlWebService createWebService(okhttp3.Call.Factory callFactory) {
    Retrofit.Builder builder = new Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(DmlWebService.BASE_URL);
    if (callFactory != null) {
      builder.callFactory(callFactory);
    }
    return builder.build().create(DmlWebService.class);
  }

  /**
   * Loads the most viewed on-demand category.
   */
  @NonNull
  Category loadMostViewedCategory() throws IOException {
    Log.d(TAG, "loadMostViewedCategory");
    Call<Category> call = webService.getMostViewed();
    Category category = executeCall(call);
    category.setTitle(context.getString(R.string.backend_category_most_viewed));
    return category;
  }

  /**
   * Loads the specified on-demand video.
   */
  @NonNull
  Video loadVideo(@NonNull String id) throws IOException {
    Log.d(TAG, String.format("loadVideoDetails [%s]", id));
    Call<Video> call = webService.getVideo(id);
    return executeCall(call);
  }

  /**
   * Loads the URL for the specified on-demand video.
   */
  @NonNull
  String loadVideoUrl(@NonNull String linksUrl) throws IOException {
    Log.d(TAG, String.format("loadVideoUrl [%s]", linksUrl));
    Call<VideoLinksContainer> call = webService.getVideoLinks(linksUrl);
    VideoLinksContainer linksContainer = executeCall(call);
    String videoUrl = linksContainer.getVideoUrl();
    if (videoUrl == null) {
      throw new IOException(String.format("Failed to extract video URL from [%s]", linksUrl));
    }
    return videoUrl;
  }

  /**
   * Executes the specified query and returns a category containing the relevant on-demand videos.
   */
  @NonNull
  Category search(@NonNull String query) throws IOException {
    Log.d(TAG, String.format("search [%s]", query));
    Call<Category> call = webService.search(query);
    Category category = executeCall(call);
    category.setTitle(query);
    return category;
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
