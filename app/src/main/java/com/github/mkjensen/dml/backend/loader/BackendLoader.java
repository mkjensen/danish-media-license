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

package com.github.mkjensen.dml.backend.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.github.mkjensen.dml.DmlApplication;
import com.github.mkjensen.dml.backend.BackendHelper;

import javax.inject.Inject;

/**
 * Superclass for convenient loading of backend data.
 */
public abstract class BackendLoader<T> extends AsyncTaskLoader<T> {

  private static final String TAG = "BackendLoader";

  @Inject
  protected BackendHelper backendHelper;

  private T data;

  protected BackendLoader(@NonNull Context context) {
    super(context);
    inject(context);
  }

  @SuppressWarnings("unchecked")
  private void inject(Context context) {
    DmlApplication.from(context).getBackendComponent().inject((BackendLoader<Object>) this);
  }

  @Override
  public void deliverResult(T data) {
    Log.d(TAG, "deliverResult");
    this.data = data;
    if (isStarted()) {
      super.deliverResult(data);
    }
  }

  @Override
  protected void onStartLoading() {
    Log.d(TAG, "onStartLoading");
    if (data != null) {
      deliverResult(data);
    } else {
      forceLoad();
    }
  }
}
