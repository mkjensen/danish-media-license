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

import com.github.mkjensen.dml.ondemand.Video;

import java.util.Collections;
import java.util.List;

/**
 * An on-demand category which contains its associated on-demand videos.
 *
 * <p>Implementation note: Class fields are set using reflection by Moshi, a JSON library.
 *
 * @see <a href="https://github.com/square/moshi">Moshi</a>
 */
public final class Category {

  private static final String NOT_SET = "(not set)";

  @SuppressWarnings( {"CanBeFinal", "FieldCanBeLocal"})
  private String id = NOT_SET;

  @SuppressWarnings( {"CanBeFinal", "FieldCanBeLocal"})
  private String title = NOT_SET;

  @SuppressWarnings( {"CanBeFinal", "FieldCanBeLocal"})
  private String url = NOT_SET;

  private List<Video> videos = Collections.emptyList();

  private Category() {
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public List<Video> getVideos() {
    return videos;
  }

  void setVideos(List<Video> videos) {
    if (videos == null) {
      throw new IllegalArgumentException("videos cannot be null");
    }
    this.videos = videos;
  }
}
