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

import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

import java.util.Collections;
import java.util.List;

/**
 * A helper class that enables converting on-demand videos in JSON format to a collection of {@link
 * Video} instances.
 */
final class VideoLinksContainer {

  @Json(name = "Links")
  @SuppressWarnings("CanBeFinal")
  private List<Link> links = Collections.emptyList();

  private VideoLinksContainer() {
  }

  @Nullable
  String getVideoUrl() {
    for (Link link : links) {
      if ("HLS".equals(link.protocol)) {
        return link.url;
      }
    }
    return null;
  }

  private static final class Link {

    @Json(name = "Target")
    @SuppressWarnings("unused")
    String protocol;

    @Json(name = "Uri")
    @SuppressWarnings("unused")
    String url;
  }
}
