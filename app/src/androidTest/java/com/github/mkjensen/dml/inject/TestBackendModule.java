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

package com.github.mkjensen.dml.inject;

import com.github.mkjensen.dml.backend.LocalCallFactory;
import com.github.mkjensen.dml.test.ResourceUtils;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Interceptor;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * Test version of {@link BackendModule}.
 */
public class TestBackendModule extends BackendModule {

  public TestBackendModule(String apiBaseUrl) {
    super(apiBaseUrl);
  }

  @Override
  Call.Factory callFactory(Cache cache, List<Interceptor> networkInterceptors) {
    String categoryJson = ResourceUtils.loadAsString(com.github.mkjensen.dml.test.R.raw.category);
    // @formatter:off
    return LocalCallFactory.newBuilder()
        .forUrl(apiBaseUrl + "list/view/news")
          .code(HttpURLConnection.HTTP_OK)
          .responseBody(categoryJson)
          .up()
        .forUrl(apiBaseUrl + "list/view/selectedlist")
          .code(HttpURLConnection.HTTP_OK)
          .responseBody(categoryJson)
          .up()
        .forUrl(apiBaseUrl + "list/view/mostviewed")
          .code(HttpURLConnection.HTTP_OK)
          .responseBody(categoryJson)
          .up()
        .build();
    // @formatter:on
  }
}
