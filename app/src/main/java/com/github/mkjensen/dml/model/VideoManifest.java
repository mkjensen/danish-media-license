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
 * Metadata about on-demand video streams.
 *
 * @see <a href="http://www.dr.dk/mu-online/Help/1.3/Api/GET-api-apiVersion-manifest-id">API</a>
 */
public final class VideoManifest {

  public static final String NOT_SET = "(not set)";

  @Json(name = "Links")
  private List<Stream> streams = Collections.emptyList();

  /**
   * Returns a URL to a stream using the specified protocol if it exists. Otherwise returns {@link
   * #NOT_SET}.
   */
  @NonNull
  public String getUrl(@NonNull Protocol protocol) {
    notNull(protocol);
    for (Stream stream : streams) {
      if (stream.getProtocol() == protocol) {
        return stream.getUrl();
      }
    }
    return NOT_SET;
  }

  @NonNull
  public List<Stream> getStreams() {
    return streams;
  }

  public void setStreams(@NonNull List<Stream> streams) {
    this.streams = notNull(streams);
  }

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
     * HTTP Live Streaming (HLS).
     *
     * @see <a href="https://en.wikipedia.org/wiki/HTTP_Live_Streaming">HTTP Live Streaming</a>
     */
    HLS,

    /**
     * Represents an unknown streaming protocol.
     */
    UNKNOWN
  }

  /**
   * An on-demand video stream.
   */
  public static final class Stream {

    @Json(name = "Target")
    private Protocol protocol = Protocol.UNKNOWN;

    @Json(name = "Uri")
    private String url = NOT_SET;

    @NonNull
    public Protocol getProtocol() {
      return protocol;
    }

    public void setProtocol(@NonNull Protocol protocol) {
      this.protocol = notNull(protocol);
    }

    @NonNull
    public String getUrl() {
      return url;
    }

    public void setUrl(@NonNull String url) {
      this.url = notNull(url);
    }
  }
}
