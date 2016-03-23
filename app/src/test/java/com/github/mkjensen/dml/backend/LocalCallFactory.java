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

import android.util.ArrayMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

/**
 * Custom implementation of OkHttp's {@link Call.Factory} that avoids remote calls by enabling
 * clients to specify the results that would have been delivered from the remote systems.
 */
public final class LocalCallFactory implements Call.Factory {

  private static final int MAPS_INITIAL_CAPACITY = 1;

  private static final String ANY_URL = null;

  private final ArrayMap<String, Integer> codeMap;

  private final ArrayMap<String, ResponseBody> responseBodyMap;

  private LocalCallFactory() {
    codeMap = new ArrayMap<>(MAPS_INITIAL_CAPACITY);
    responseBodyMap = new ArrayMap<>(MAPS_INITIAL_CAPACITY);
  }

  private LocalCallFactory(LocalCallFactory other) {
    codeMap = new ArrayMap<>(other.codeMap);
    responseBodyMap = new ArrayMap<>(other.responseBodyMap);
  }

  @Override
  public Call newCall(Request request) {
    return new LocalCall(this, request);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private static final class LocalCall implements Call {

    private final LocalCallFactory factory;

    private final Request request;

    private LocalCall(LocalCallFactory factory, Request request) {
      this.factory = factory;
      this.request = request;
    }

    @Override
    public Request request() {
      return request;
    }

    @Override
    public Response execute() throws IOException {
      return new Response.Builder()
          .body(getValue(factory.responseBodyMap))
          .code(getValue(factory.codeMap))
          .protocol(Protocol.HTTP_1_0)
          .request(request)
          .build();
    }

    private <T> T getValue(ArrayMap<String, T> map) throws IOException {
      String url = request.url().toString();
      T value = map.get(url);
      if (value == null) {
        value = map.get(ANY_URL);
        if (value == null) {
          throw new IOException(String.format("Unhandled URL: [%s]", url));
        }
      }
      return value;
    }

    @Override
    public void enqueue(Callback responseCallback) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void cancel() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isExecuted() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCanceled() {
      throw new UnsupportedOperationException();
    }
  }

  public static final class Builder {

    private final LocalCallFactory factory;

    private Builder() {
      factory = new LocalCallFactory();
    }

    public LocalCallFactory build() {
      return new LocalCallFactory(factory);
    }

    /**
     * Creates a response builder for the specified URL.
     */
    public ForUrlBuilder forUrl(String url) {
      if (url == null) {
        throw new IllegalArgumentException("url cannot be null");
      }
      return new ForUrlBuilder(this, url);
    }

    public ForUrlBuilder forAnyUrl() {
      return new ForUrlBuilder(this, ANY_URL);
    }

    public static final class ForUrlBuilder {

      private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

      private final Builder builder;

      private final String url;

      private ForUrlBuilder(Builder builder, String url) {
        this.builder = builder;
        this.url = url;
      }

      public Builder up() {
        return builder;
      }

      public ForUrlBuilder code(int code) {
        builder.factory.codeMap.put(url, code);
        return this;
      }

      public ForUrlBuilder responseBody(ResponseBody responseBody) {
        builder.factory.responseBodyMap.put(url, responseBody);
        return this;
      }

      public ForUrlBuilder responseBody(String json) {
        ResponseBody responseBody = ResponseBody.create(JSON_MEDIA_TYPE, json);
        return responseBody(responseBody);
      }
    }
  }
}
