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

import static com.github.mkjensen.dml.Defense.notNull;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

/**
 * Executes a query and returns the results from the backend.
 */
public class QueryLoader extends BackendLoader<Category> {

  private static final String TAG = "QueryLoader";

  private final String query;

  public QueryLoader(@NonNull Context context, @NonNull String query) {
    super(context);
    this.query = notNull(query);
  }

  @Override
  public Category loadInBackground() {
    try {
      return backendHelper.search(query);
    } catch (IOException ex) {
      Log.e(TAG, String.format("Failed to perform query [%s]", query), ex);
      return null;
    }
  }

  @NonNull
  public String getQuery() {
    return query;
  }
}
