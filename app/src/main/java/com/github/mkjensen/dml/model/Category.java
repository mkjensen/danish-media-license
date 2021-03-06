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

package com.github.mkjensen.dml.model;

import static com.github.mkjensen.dml.util.Preconditions.notNull;

import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

import java.util.Collections;
import java.util.List;

/**
 * An on-demand category which contains its associated on-demand videos.
 *
 * @see <a href="http://goo.gl/NhngFV">API</a>
 */
public final class Category {

  public static final String NOT_SET = "(not set)";

  private String title = NOT_SET;

  @Json(name = "Items")
  private List<Video> videos = Collections.emptyList();

  @NonNull
  public String getTitle() {
    return title;
  }

  public void setTitle(@NonNull String title) {
    this.title = notNull(title);
  }

  @NonNull
  public List<Video> getVideos() {
    return videos;
  }

  public void setVideos(@NonNull List<Video> videos) {
    this.videos = notNull(videos);
  }
}
