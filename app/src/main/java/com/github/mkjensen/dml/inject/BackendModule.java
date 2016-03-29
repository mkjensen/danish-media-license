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

import static com.github.mkjensen.dml.util.Preconditions.notNull;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.github.mkjensen.dml.BuildConfig;
import com.github.mkjensen.dml.backend.BackendHelper;

import dagger.Module;
import dagger.Provides;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

/**
 * Dagger module for backend services.
 */
@Module
public final class BackendModule {

  private final String apiBaseUrl;

  public BackendModule(String apiBaseUrl) {
    this.apiBaseUrl = notNull(apiBaseUrl);
  }

  @Provides
  @Singleton
  BackendHelper backendHelper(Context context, Retrofit retrofit) {
    return new BackendHelper(context, retrofit);
  }

  @Provides
  @Singleton
  Cache cache(Application application) {
    int maxSize = 50 * 1024 * 1024;
    return new Cache(application.getCacheDir(), maxSize);
  }

  @Provides
  @Singleton
  Converter.Factory converterFactory() {
    return MoshiConverterFactory.create();
  }

  @Provides
  @Singleton
  List<Interceptor> networkInterceptors(StethoInterceptor stethoInterceptor) {
    List<Interceptor> interceptors = new ArrayList<>(1);
    if (stethoInterceptor != null) {
      interceptors.add(stethoInterceptor);
    }
    return interceptors;
  }

  @Provides
  @Singleton
  OkHttpClient okHttpClient(Cache cache, List<Interceptor> networkInterceptors) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.cache(cache);
    for (Interceptor interceptor : networkInterceptors) {
      builder.addNetworkInterceptor(interceptor);
    }
    return builder.build();
  }

  @Provides
  @Singleton
  Retrofit retrofit(Converter.Factory converterFactory, OkHttpClient okHttpClient) {
    return new Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .baseUrl(apiBaseUrl)
        .client(okHttpClient)
        .build();
  }

  @Provides
  @Singleton
  StethoInterceptor stethoInterceptor() {
    return BuildConfig.DEBUG ? new StethoInterceptor() : null;
  }
}
