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

/**
 * A streaming protocol.
 *
 * @see <a href="https://goo.gl/5ccOrw">Streaming media protocols</a>
 */
public enum Protocol {

  /**
   * Represents a stream that downloads the content via HTTP.
   */
  @Json(name = "Download")
  DOWNLOAD,

  /**
   * Adobe HTTP Dynamic Streaming (HDS).
   *
   * @see <a href="https://goo.gl/hNk8QH">Adobe HTTP Dynamic Streaming</a>
   */
  HDS,

  /**
   * Adobe HTTP Dynamic Streaming (HDS) with embedded subtitles.
   *
   * @see <a href="https://goo.gl/hNk8QH">Adobe HTTP Dynamic Streaming</a>
   */
  @Json(name = "HDS_subtitles")
  HDS_SUBTITLES,

  /**
   * HTTP Live Streaming (HLS).
   *
   * @see <a href="https://en.wikipedia.org/wiki/HTTP_Live_Streaming">HTTP Live Streaming</a>
   */
  HLS,

  /**
   * HTTP Live Streaming (HLS) with embedded subtitles.
   *
   * @see <a href="https://en.wikipedia.org/wiki/HTTP_Live_Streaming">HTTP Live Streaming</a>
   */
  @Json(name = "HLS_subtitles")
  HLS_SUBTITLES,

  /**
   * Real Time Messaging Protocol (RTMP).
   *
   * @see <a href="https://goo.gl/M1FB1M">Real Time Messaging Protocol</a>
   */
  @Json(name = "_Streaming")
  RTMP,

  /**
   * Represents an unknown streaming protocol.
   */
  UNKNOWN
}
