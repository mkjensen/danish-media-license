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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

/**
 * Custom implementation of {@link okhttp3.Call.Factory} that avoids remote calls by enabling
 * clients to specify the result that would have been delivered from the remote system.
 */
final class LocalCallFactory implements okhttp3.Call.Factory {

  private int code;
  private ResponseBody responseBody;

  private LocalCallFactory() {
  }

  @Override
  public Call newCall(Request request) {
    return new LocalCall(this, request);
  }

  private LocalCallFactory copy() {
    LocalCallFactory copy = new LocalCallFactory();
    copy.code = code;
    copy.responseBody = responseBody;
    return this;
  }

  static final class Builder {

    private final LocalCallFactory factory;

    Builder() {
      factory = new LocalCallFactory();
    }

    LocalCallFactory build() {
      return factory.copy();
    }

    Builder withCode(int code) {
      factory.code = code;
      return this;
    }

    Builder withError(int code) {
      factory.code = code;
      factory.responseBody = ResponseBody.create(MediaType.parse("text/plain"), "Error");
      return this;
    }

    Builder withResponseBody(ResponseBody responseBody) {
      factory.responseBody = responseBody;
      return this;
    }

    Builder withJsonResponseBody(String json) {
      ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), json);
      return withResponseBody(responseBody);
    }
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
          .body(factory.responseBody)
          .code(factory.code)
          .protocol(Protocol.HTTP_1_0)
          .request(request)
          .build();
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
}
