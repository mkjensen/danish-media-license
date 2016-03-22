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

import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A helper class to manage communication with backend web services. Methods on this class should
 * not be called from the UI thread as operations may take a while.
 */
public final class BackendHelper {

  private static final String TAG = "BackendHelper";

  private final DmlWebService webService;


  public BackendHelper() {
    webService = createWebService(null);
  }

  public BackendHelper(okhttp3.Call.Factory callFactory) {
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
   * Returns on-demand categories without videos which must be loaded per category by calling {@link
   * #loadVideos(Category)}.
   */
  public List<Category> loadCategories() throws IOException {
    Log.v(TAG, "loadCategories");
    Call<List<Category>> call = webService.getCategories();
    return executeCall(call);
  }

  /**
   * Loads the associated videos for the specified on-demand category without some details which
   * must be loaded per video by calling {@link #loadVideoDetails(Video)} and/or {@link
   * #loadVideoUrl(Video)}.
   */
  public void loadVideos(Category category) throws IOException {
    Log.v(TAG, "loadVideos " + category.getId());
    Call<VideoContainer> call = webService.getVideos(category.getUrl());
    VideoContainer videoContainer = executeCall(call);
    category.setVideos(videoContainer.getVideos());
  }

  /**
   * Loads details for the specified on-demand video.
   */
  public void loadVideoDetails(Video video) throws IOException {
    String id = video.getId();
    Log.v(TAG, "loadVideoDetails " + id);
    Call<Video> call = webService.getVideo(id);
    Video detailedVideo = executeCall(call);
    video.setDescription(detailedVideo.getDescription());
  }

  /**
   * Loads the URL for the specified on-demand video.
   */
  public void loadVideoUrl(Video video) throws IOException {
    String linksUrl = video.getLinksUrl();
    Log.v(TAG, "loadVideoUrl " + linksUrl);
    Call<VideoLinksContainer> call = webService.getVideoLinks(linksUrl);
    VideoLinksContainer linksContainer = executeCall(call);
    video.setUrl(linksContainer.getVideoUrl());
  }

  private static <T> T executeCall(Call<T> call) throws IOException {
    Response<T> response = call.execute();
    if (response.isSuccessful()) {
      return response.body();
    }
    throw new IOException(String.format(Locale.US,
        "Got code: [%d], message: [%s] when requesting: [%s]",
        response.code(), response.message(), call.request().url()));
  }
}
