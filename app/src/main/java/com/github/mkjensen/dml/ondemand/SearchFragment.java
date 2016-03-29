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

import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.SearchSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import com.github.mkjensen.dml.model.Category;
import com.github.mkjensen.dml.ondemand.loader.QueryLoader;
import com.github.mkjensen.dml.presenter.VideoPresenter;
import com.github.mkjensen.dml.util.BackgroundHelper;
import com.github.mkjensen.dml.util.LoadingHelper;

/**
 * Search screen for on-demand videos.
 */
public class SearchFragment extends SearchSupportFragment
    implements SearchSupportFragment.SearchResultProvider, LoaderManager.LoaderCallbacks<Category> {

  private static final String TAG = "SearchFragment";

  private static final String QUERY_ARGUMENT = "query";

  private static final int QUERY_DELAY_IN_MILLISECONDS = 300;

  private static final int QUERY_LOADER_ID = 0;

  private Handler handler;

  private ArrayObjectAdapter results;

  private ArrayObjectAdapter videos;

  private Runnable queryRunnable;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setSearchResultProvider(this);
    handler = new Handler();
    initUi();
    initListeners();
  }

  private void initUi() {
    results = new ArrayObjectAdapter(new ListRowPresenter());
    videos = new ArrayObjectAdapter(new VideoPresenter());
  }

  private void initListeners() {
    setOnItemViewClickedListener(Listeners.createOnItemViewClickedListener(getActivity()));
    setOnItemViewSelectedListener(Listeners.createOnItemViewSelectedListener(getActivity()));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    removePendingQuery();
  }

  private void removePendingQuery() {
    LoadingHelper.hideLoading(this);
    handler.removeCallbacks(queryRunnable);
  }

  @Override
  public ObjectAdapter getResultsAdapter() {
    return results;
  }

  @Override
  public boolean onQueryTextChange(String newQuery) {
    performQuery(newQuery);
    return true;
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    performQuery(query);
    return true;
  }

  private void performQuery(String query) {
    if (TextUtils.isEmpty(query)) {
      clearResults();
      return;
    }
    createPendingQuery(query);
  }

  private void clearResults() {
    removePendingQuery();
    BackgroundHelper.clearBackground(getActivity());
    results.clear();
    videos.clear();
  }

  private void createPendingQuery(final String query) {
    removePendingQuery();
    queryRunnable = new Runnable() {
      @Override
      public void run() {
        Bundle args = new Bundle();
        args.putString(QUERY_ARGUMENT, query);
        getLoaderManager().restartLoader(QUERY_LOADER_ID, args, SearchFragment.this);
      }
    };
    handler.postDelayed(queryRunnable, QUERY_DELAY_IN_MILLISECONDS);
  }

  @Override
  public Loader<Category> onCreateLoader(int id, Bundle args) {
    Log.d(TAG, "onCreateLoader");
    LoadingHelper.showLoading(this);
    String query = args.getString(QUERY_ARGUMENT);
    //noinspection ConstantConditions
    return new QueryLoader(getActivity(), query);
  }

  @Override
  public void onLoadFinished(Loader<Category> loader, Category data) {
    Log.d(TAG, "onLoadFinished");
    LoadingHelper.hideLoading(this);
    if (data == null || data.getVideos().isEmpty()) {
      clearResults();
      return;
    }
    addResults(data);
  }

  private void addResults(Category category) {
    results.clear();
    videos.clear();
    videos.addAll(0, category.getVideos());
    HeaderItem header = new HeaderItem(category.getTitle());
    results.add(new ListRow(header, videos));
  }

  @Override
  public void onLoaderReset(Loader<Category> loader) {
    Log.d(TAG, "onLoaderReset");
    videos.clear();
  }
}
