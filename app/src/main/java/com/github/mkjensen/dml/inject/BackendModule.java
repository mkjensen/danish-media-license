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

import android.app.Application;
import android.content.Context;

import com.github.mkjensen.dml.backend.BackendHelper;
import com.github.mkjensen.dml.backend.DmlWebService;

import dagger.Module;
import dagger.Provides;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import javax.inject.Singleton;

/**
 * Dagger module for backend services.
 */
@Module
public class BackendModule {

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
  OkHttpClient okHttpClient(Cache cache) {
    return new OkHttpClient.Builder()
        .cache(cache)
        .build();
  }

  @Provides
  @Singleton
  Retrofit retrofit(Converter.Factory converterFactory, OkHttpClient okHttpClient) {
    return new Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .baseUrl(DmlWebService.BASE_URL)
        .client(okHttpClient)
        .build();
  }
}
