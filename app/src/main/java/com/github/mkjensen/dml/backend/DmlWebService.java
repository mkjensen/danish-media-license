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

import com.github.mkjensen.dml.model.Category;
import com.github.mkjensen.dml.model.Channel;
import com.github.mkjensen.dml.model.Video;
import com.github.mkjensen.dml.model.VideoManifest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

import java.util.List;

/**
 * Web service methods for use with Retrofit.
 *
 * @see <a href="https://github.com/square/retrofit">Retrofit</a>
 */
public interface DmlWebService {

  /**
   * Returns the category containing the most viewed on-demand videos.
   */
  @GET("list/view/mostviewed")
  Call<Category> getMostViewedCategory();

  /**
   * Returns the category containing the new on-demand videos.
   */
  @GET("list/view/news")
  Call<Category> getNewCategory();

  /**
   * Returns the category containing the recommended on-demand videos.
   */
  @GET("list/view/selectedlist")
  Call<Category> getRecommendedCategory();

  /**
   * Returns a category with on-demand videos relevant for the specified query.
   */
  @GET("search/tv/programcards-with-asset/title/{query}")
  Call<Category> search(@Path("query") String query);

  /**
   * Returns the on-demand video with the specified id.
   */
  @GET("programcard/{id}")
  Call<Video> getVideo(@Path("id") String id);

  /**
   * Returns the on-demand video manifest for the specified URL.
   */
  @GET
  Call<VideoManifest> getVideoManifest(@Url String url);

  /**
   * Returns live channels.
   */
  @GET("channel/all-active-dr-tv-channels")
  Call<List<Channel>> getChannels();
}
