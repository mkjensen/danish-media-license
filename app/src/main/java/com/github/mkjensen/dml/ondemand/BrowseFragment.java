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

package com.github.mkjensen.dml.ondemand;

import static com.github.mkjensen.dml.ondemand.OnDemandListeners.createOnItemViewClickedListener;
import static com.github.mkjensen.dml.ondemand.OnDemandListeners.createOnItemViewSelectedListener;
import static com.github.mkjensen.dml.ondemand.OnDemandListeners.createOnSearchClickedListener;

import android.os.Bundle;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.backend.loader.CategoriesLoader;
import com.github.mkjensen.dml.model.Category;
import com.github.mkjensen.dml.model.SettingsItem;
import com.github.mkjensen.dml.presenter.SettingsItemPresenter;
import com.github.mkjensen.dml.presenter.VideoPresenter;
import com.github.mkjensen.dml.util.LoadingHelper;

import java.util.List;

/**
 * Browse screen for on-demand videos.
 */
public final class BrowseFragment extends BrowseSupportFragment
    implements LoaderManager.LoaderCallbacks<List<Category>> {

  private static final String TAG = "BrowseFragment";

  private ArrayObjectAdapter rows;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    LoadingHelper.showLoading(this);
    initUi();
    initListeners();
    initLoader();
  }

  private void initUi() {
    rows = new ArrayObjectAdapter(new ListRowPresenter());
    setAdapter(rows);
    setTitle(getString(R.string.app_name));
    enableRowScaling(false);
  }

  private void initListeners() {
    FragmentActivity activity = getActivity();
    setOnItemViewClickedListener(createOnItemViewClickedListener(activity));
    setOnItemViewSelectedListener(createOnItemViewSelectedListener(activity));
    setOnSearchClickedListener(createOnSearchClickedListener(activity));
  }

  private void initLoader() {
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
    Log.d(TAG, "onCreateLoader");
    return new CategoriesLoader(getActivity());
  }

  @Override
  public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {
    Log.d(TAG, "onLoadFinished");
    LoadingHelper.hideLoading(this);
    rows.clear();
    if (data == null) {
      Log.w(TAG, "No data returned by loader");
      createSettingsRow();
      return;
    }
    for (Category category : data) {
      ListRow categoryRow = createCategoryRow(category);
      rows.add(categoryRow);
    }
    createSettingsRow();
  }

  private ListRow createCategoryRow(Category category) {
    HeaderItem header = new HeaderItem(category.getTitle());
    ArrayObjectAdapter adapter = new ArrayObjectAdapter(new VideoPresenter());
    adapter.addAll(0, category.getVideos());
    return new ListRow(header, adapter);
  }

  private void createSettingsRow() {
    HeaderItem header = new HeaderItem(getString(R.string.ondemand_settings));
    ArrayObjectAdapter items = new ArrayObjectAdapter(new SettingsItemPresenter());
    items.add(new SettingsItem(R.string.ondemand_settings_about,
        R.drawable.ic_info_outline_black_24dp));
    rows.add(new ListRow(header, items));
  }

  @Override
  public void onLoaderReset(Loader<List<Category>> loader) {
    Log.d(TAG, "onLoaderReset");
    rows.clear();
  }
}
