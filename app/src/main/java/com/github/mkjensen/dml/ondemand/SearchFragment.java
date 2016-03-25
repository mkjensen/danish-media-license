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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v17.leanback.app.SearchSupportFragment;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v17.leanback.widget.SpeechRecognitionCallback;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.provider.DmlContract;

/**
 * Search screen for on-demand videos.
 */
public class SearchFragment extends SearchSupportFragment
    implements SearchSupportFragment.SearchResultProvider, LoaderManager.LoaderCallbacks<Cursor> {

  private static final String TAG = "SearchFragment";

  private static final int SPEECH_REQUEST_CODE = 0;

  private static final String QUERY_LIKE_FORMAT = "%%%s%%";

  private SparseArrayObjectAdapter results;

  private CursorObjectAdapter videos;

  private SparseArrayObjectAdapter empty;

  private String currentQuery;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    initUi();
    setSearchResultProvider(this);
    ensureSpeechPermission();
  }

  private void initUi() {
    results = new SparseArrayObjectAdapter(new ListRowPresenter());
    videos = new CursorObjectAdapter(new VideoPresenter());
    videos.setMapper(new VideoCursorMapper());
    empty = new SparseArrayObjectAdapter();
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
      return;
    }
    currentQuery = query;
    getLoaderManager().restartLoader(0, null, this);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Log.d(TAG, "onCreateLoader");
    String selectionArgs = String.format(QUERY_LIKE_FORMAT, currentQuery);
    return new CursorLoader(
        getActivity(),
        DmlContract.Video.CONTENT_URI,
        null, // projection
        String.format("%s LIKE ? OR %s LIKE ?", DmlContract.Video.VIDEO_TITLE,
            DmlContract.Video.VIDEO_DESCRIPTION),
        new String[] {selectionArgs, selectionArgs},
        null // String sortOrder
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    Log.d(TAG, "onLoadFinished");
    HeaderItem header;
    ObjectAdapter adapter;
    if (data != null && data.moveToFirst()) {
      header = new HeaderItem(getString(R.string.ondemand_search_results, currentQuery));
      adapter = videos;
    } else {
      header = new HeaderItem(getString(R.string.ondemand_search_results_none, currentQuery));
      adapter = empty;
    }
    videos.changeCursor(data);
    results.set(0, new ListRow(header, adapter));
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    Log.d(TAG, "onLoaderReset");
    videos.changeCursor(null);
  }
}
