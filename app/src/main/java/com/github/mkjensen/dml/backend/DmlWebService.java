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
interface DmlWebService {

  /**
   * The API base URL.
   *
   * @see retrofit2.Retrofit.Builder#baseUrl(String)
   */
  String BASE_URL = "https://www.dr.dk/mu-online/api/1.3/";

  /**
   * Returns the on-demand categories without videos.
   */
  @GET("https://mkjensen.github.io/danish-media-license/categories.json")
  Call<List<Category>> getCategories();

  /**
   * Returns the on-demand videos for the specified URL.
   */
  @GET
  Call<VideoContainer> getVideos(@Url String url);

  /**
   * Returns the on-demand video with the specified id.
   */
  @GET("programcard/{id}")
  Call<Video> getVideo(@Path("id") String id);

  /**
   * Returns video links for the specified URL.
   */
  @GET
  Call<VideoLinksContainer> getVideoLinks(@Url String url);
}
