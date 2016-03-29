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

package com.github.mkjensen.dml.ondemand.loader;

import static com.github.mkjensen.dml.util.Preconditions.notNull;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.mkjensen.dml.backend.BackendLoader;
import com.github.mkjensen.dml.model.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Loads {@link Category} instances from the backend.
 */
public final class CategoriesLoader extends BackendLoader<List<Category>> {

  private static final String TAG = "CategoriesLoader";

  private final String query;

  /**
   * Creates a loader that returns the default categories of interest.
   */
  public CategoriesLoader(@NonNull Context context) {
    super(context);
    query = null;
  }

  /**
   * Creates a loader that executes the specified query and returns the relevant categories.
   */
  public CategoriesLoader(@NonNull Context context, @NonNull String query) {
    super(context);
    this.query = notNull(query);
  }

  @Override
  public List<Category> loadInBackground() {
    Log.d(TAG, "loadInBackground");
    if (query == null) {
      return loadCategories();
    } else {
      return executeQuery();
    }
  }

  /**
   * Returns the query executed by this loader, or {@code null} if this loader does not execute a
   * query.
   */
  @Nullable
  public String getQuery() {
    return query;
  }

  private List<Category> loadCategories() {
    List<Category> categories = new ArrayList<>(2);
    loadMostViewedCategory(categories);
    loadSelectedCategory(categories);
    return categories;
  }

  private void loadMostViewedCategory(List<Category> categories) {
    try {
      categories.add(backendHelper.loadMostViewedCategory());
    } catch (IOException ex) {
      Log.e(TAG, "Failed to load most viewed category", ex);
    }
  }

  private void loadSelectedCategory(List<Category> categories) {
    try {
      categories.add(backendHelper.loadSelectedCategory());
    } catch (IOException ex) {
      Log.e(TAG, "Failed to load selected category", ex);
    }
  }

  private List<Category> executeQuery() {
    try {
      return Arrays.asList(backendHelper.search(query));
    } catch (IOException ex) {
      Log.e(TAG, String.format("Failed to execute query [%s]", query), ex);
      return Collections.emptyList();
    }
  }
}
