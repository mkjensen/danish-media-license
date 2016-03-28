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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mkjensen.dml.backend.BackendLoader;
import com.github.mkjensen.dml.model.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@link Category} instances from the backend.
 */
public class CategoriesLoader extends BackendLoader<List<Category>> {

  private static final String TAG = "CategoriesLoader";

  public CategoriesLoader(@NonNull Context context) {
    super(context);
  }

  @Override
  public List<Category> loadInBackground() {
    Log.d(TAG, "loadInBackground");
    List<Category> categories = new ArrayList<>(1);
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
}
