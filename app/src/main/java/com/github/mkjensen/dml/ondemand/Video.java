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

package com.github.mkjensen.dml.ondemand;

/**
 * Metadata about an on-demand video. Create instances using {@link Builder}.
 */
final class Video {

  private String title;
  private String imageUrl;

  private Video() {
  }

  String getTitle() {
    return title;
  }

  String getImageUrl() {
    return imageUrl;
  }

  private Video copy() {
    Video copy = new Video();
    copy.title = title;
    copy.imageUrl = imageUrl;
    return copy;
  }

  static final class Builder {

    private final Video video;

    Builder() {
      video = new Video();
    }

    Video build() {
      return video.copy();
    }

    Builder title(String title) {
      video.title = title;
      return this;
    }

    Builder imageUrl(String imageUrl) {
      video.imageUrl = imageUrl;
      return this;
    }
  }
}
