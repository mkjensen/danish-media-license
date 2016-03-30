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

import com.squareup.moshi.Json;

import java.util.Collections;
import java.util.List;

/**
 * Metadata about on-demand video streams.
 *
 * @see <a href="http://www.dr.dk/mu-online/Help/1.3/Api/GET-api-apiVersion-manifest-id">API</a>
 */
public final class VideoManifest {

  public static final String NOT_SET = "(not set)";

  @Json(name = "Links")
  @SuppressWarnings("CanBeFinal")
  private List<Stream> streams = Collections.emptyList();

  /**
   * Returns a URL to a stream using the HLS protocol if it exists. Otherwise returns {@link
   * #NOT_SET}.
   *
   * @see <a href="https://en.wikipedia.org/wiki/HTTP_Live_Streaming">HLS</a>
   */
  public String getStreamUrl() {
    for (Stream stream : streams) {
      if ("HLS".equals(stream.protocol)) {
        return stream.url;
      }
    }
    return NOT_SET;
  }

  private static final class Stream {

    @Json(name = "Target")
    @SuppressWarnings("unused")
    String protocol = NOT_SET;

    @Json(name = "Uri")
    @SuppressWarnings("unused")
    String url = NOT_SET;
  }
}
