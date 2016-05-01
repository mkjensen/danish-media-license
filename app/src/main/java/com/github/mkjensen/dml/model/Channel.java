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
 * A live channel (video stream).
 *
 * @see <a href="http://goo.gl/PR6kfS">API</a>
 */
public final class Channel {

  public static final String NOT_SET = "(not set)";

  @Json(name = "Slug")
  private String id = NOT_SET;

  @Json(name = "Title")
  private String title = NOT_SET;

  @Json(name = "PrimaryImageUri")
  private String imageUrl = NOT_SET;

  @Json(name = "WebChannel")
  private boolean webChannel;

  @Json(name = "StreamingServers")
  private List<Server> servers = Collections.emptyList();

  @NonNull
  public String getId() {
    return id;
  }

  public void setId(@NonNull String id) {
    this.id = notNull(id);
  }

  @NonNull
  public String getTitle() {
    return title;
  }

  public void setTitle(@NonNull String title) {
    this.title = notNull(title);
  }

  @NonNull
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(@NonNull String imageUrl) {
    this.imageUrl = notNull(imageUrl);
  }

  public boolean isWebChannel() {
    return webChannel;
  }

  public void setWebChannel(boolean webChannel) {
    this.webChannel = webChannel;
  }

  @NonNull
  public List<Server> getServers() {
    return servers;
  }

  public void setServers(@NonNull List<Server> servers) {
    this.servers = notNull(servers);
  }

  /**
   * A server with live video streams.
   */
  public static final class Server {

    @Json(name = "LinkType")
    private Protocol protocol = Protocol.UNKNOWN;

    @Json(name = "Server")
    private String url = NOT_SET;

    @Json(name = "Qualities")
    private List<Quality> qualities = Collections.emptyList();

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

    @NonNull
    public List<Quality> getQualities() {
      return qualities;
    }

    public void setQualities(@NonNull List<Quality> qualities) {
      this.qualities = notNull(qualities);
    }

    /**
     * A live video stream of a certain quality.
     */
    public static final class Quality {

      @Json(name = "Kbps")
      private int kilobitRate;

      @Json(name = "Streams")
      private List<Stream> streams = Collections.emptyList();

      public int getKilobitRate() {
        return kilobitRate;
      }

      public void setKilobitRate(int kilobitRate) {
        this.kilobitRate = kilobitRate;
      }

      @NonNull
      public List<Stream> getStreams() {
        return streams;
      }

      public void setStreams(@NonNull List<Stream> streams) {
        this.streams = notNull(streams);
      }

      /**
       * A live video stream.
       */
      public static final class Stream {

        @Json(name = "Stream")
        private String path = NOT_SET;

        @NonNull
        public String getPath() {
          return path;
        }

        public void setPath(@NonNull String path) {
          this.path = notNull(path);
        }
      }
    }
  }
}
