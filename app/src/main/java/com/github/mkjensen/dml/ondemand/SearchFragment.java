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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.SearchSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v17.leanback.widget.SpeechRecognitionCallback;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.backend.Category;
import com.github.mkjensen.dml.backend.QueryLoader;
import com.github.mkjensen.dml.backend.Video;

/**
 * Search screen for on-demand videos.
 */
public class SearchFragment extends SearchSupportFragment
    implements SearchSupportFragment.SearchResultProvider, LoaderManager.LoaderCallbacks<Category> {

  private static final String TAG = "SearchFragment";

  private static final int QUERY_DELAY_IN_MILLISECONDS = 300;

  private static final String QUERY_ARGUMENT = "query";

  private static final int QUERY_LOADER_ID = 0;

  private static final int SPEECH_REQUEST_CODE = 0;

  private Handler handler;

  private SparseArrayObjectAdapter results;

  private ArrayObjectAdapter videos;

  private Runnable queryRunnable;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    handler = new Handler();
    initUi();
    initListeners();
    setSearchResultProvider(this);
    ensureSpeechPermission();
  }

  private void initUi() {
    results = new SparseArrayObjectAdapter(new ListRowPresenter());
    videos = new ArrayObjectAdapter(new VideoPresenter());
  }

  private void initListeners() {
    setOnItemViewClickedListener(new OnItemViewClickedListener() {
      @Override
      public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Video) {
          Video video = (Video) item;
          Intent intent = new Intent(getActivity(), DetailsActivity.class);
          intent.putExtra(DetailsActivity.VIDEO_ID, video.getId());
          startActivity(intent);
        } else {
          Log.w(TAG, "Unhandled item: " + item);
        }
      }
    });
  }

  private void ensureSpeechPermission() {
    if (hasSpeechPermission()) {
      return;
    }
    setSpeechRecognitionCallback(new SpeechRecognitionCallback() {
      @Override
      public void recognizeSpeech() {
        startActivityForResult(getRecognizerIntent(), SPEECH_REQUEST_CODE);
      }
    });
  }

  private boolean hasSpeechPermission() {
    return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
        == PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case SPEECH_REQUEST_CODE:
        handleSpeechResult(resultCode, data);
        break;
      default:
        Log.w(TAG, "Unhandled request code: " + requestCode);
    }
  }

  private void handleSpeechResult(int resultCode, Intent data) {
    switch (resultCode) {
      case Activity.RESULT_CANCELED:
        // Do nothing.
        break;
      case Activity.RESULT_OK:
        setSearchQuery(data, true);
        break;
      default:
        Log.w(TAG, "Unhandled result code: " + resultCode);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    removePendingQuery();
  }

  private void removePendingQuery() {
    handler.removeCallbacks(queryRunnable);
  }

  @Override
  public void startRecognition() {
    setSearchQuery("", false);
    clearResults();
    super.startRecognition();
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
    removePendingQuery();
    createPendingQuery(query);
  }

  private void clearResults() {
    removePendingQuery();
    results.clear();
    videos.clear();
    HeaderItem header = new HeaderItem(getString(R.string.ondemand_search_no_results));
    results.set(0, new ListRow(header, videos));
  }

  private void createPendingQuery(final String query) {
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
    String query = args.getString(QUERY_ARGUMENT);
    //noinspection ConstantConditions
    return new QueryLoader(getActivity(), query);
  }

  @Override
  public void onLoadFinished(Loader<Category> loader, Category data) {
    Log.d(TAG, "onLoadFinished");
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
    results.set(0, new ListRow(header, videos));
  }

  @Override
  public void onLoaderReset(Loader<Category> loader) {
    Log.d(TAG, "onLoaderReset");
    videos.clear();
  }
}
