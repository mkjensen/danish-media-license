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

import android.accounts.Account;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mkjensen.dml.provider.DmlContract;
import com.github.mkjensen.dml.sync.AccountHelper;
import com.github.mkjensen.dml.sync.SyncHelper;

/**
 * Browse screen for on-demand videos.
 */
public class BrowseFragment extends BrowseSupportFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final String TAG = "BrowseFragment";

  private static final int CATEGORIES_LOADER_ID = -1;

  private AccountHelper accountHelper;

  private SyncHelper syncHelper;

  private ArrayObjectAdapter categories;

  private ArrayMap<String, Integer> categoryIdToLoaderIdMap;

  private SparseArray<CursorObjectAdapter> loaderIdToAdapterMap;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    initAdapter();
    createSyncHelpers();
    initLoader();
    initUi();
    initListeners();
  }

  private void initAdapter() {
    categories = new ArrayObjectAdapter(new ListRowPresenter());
    setAdapter(categories);
  }

  private void createSyncHelpers() {
    accountHelper = new AccountHelper();
    syncHelper = new SyncHelper();
  }

  private void initLoader() {
    categoryIdToLoaderIdMap = new ArrayMap<>();
    loaderIdToAdapterMap = new SparseArray<>();
    getLoaderManager().initLoader(CATEGORIES_LOADER_ID, null, this);
  }

  private void initUi() {
    enableRowScaling(false);
  }

  private void initListeners() {
    setOnItemViewClickedListener(new OnItemViewClickedListener() {

      @Override
      public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Video) {
          Video video = (Video) item;
          Intent intent = new Intent(getActivity(), DetailsActivity.class);
          intent.putExtra(DetailsActivity.VIDEO, video);
          startActivity(intent);
        } else if (item instanceof DebugItem) {
          DebugItem debugItem = (DebugItem) item;
          debugItem.click();
        } else {
          Log.e(TAG, "Registered click on unsupported item: " + item);
        }
      }
    });
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Log.d(TAG, "onCreateLoader " + id);
    Uri uri;
    switch (id) {
      case CATEGORIES_LOADER_ID:
        uri = DmlContract.Categories.CONTENT_URI;
        break;
      default:
        String categoryId = args.getString(DmlContract.Categories.ID);
        uri = DmlContract.CategoriesVideos.buildUri(categoryId);
        break;
    }
    return createCursorLoader(uri);
  }

  private CursorLoader createCursorLoader(Uri uri) {
    return new CursorLoader(
        getActivity(),
        uri,
        null, // projection
        null, // selection
        null, // selectionArgs
        null //  sortOrder
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    int loaderId = loader.getId();
    Log.d(TAG, "onLoadFinished " + loaderId);
    if (data == null || !data.moveToFirst()) {
      Log.i(TAG, "No data returned by loader: " + loaderId);
      if (loaderId == CATEGORIES_LOADER_ID) {
        categories.clear();
        createDebugCategory();
      } else {
        resetVideoLoader(loaderId);
      }
      return;
    }
    if (loaderId == CATEGORIES_LOADER_ID) {
      finishCategoriesLoader(data);
    } else {
      finishVideoLoader(loaderId, data);
    }
  }

  private void finishCategoriesLoader(Cursor cursor) {
    categories.clear();
    int idIndex = cursor.getColumnIndex(DmlContract.Categories.ID);
    int titleIndex = cursor.getColumnIndex(DmlContract.Categories.TITLE);
    while (!cursor.isAfterLast()) {
      String id = cursor.getString(idIndex);
      String title = cursor.getString(titleIndex);
      ListRow videos = initVideos(id, title);
      categories.add(videos);
      cursor.moveToNext();
    }
    createDebugCategory();
  }

  private ListRow initVideos(String categoryId, String categoryTitle) {
    int loaderId = getOrAssignLoaderId(categoryId);
    HeaderItem header = new HeaderItem(categoryTitle);
    CursorObjectAdapter adapter = getOrCreateVideoAdapter(loaderId, categoryId);
    return new ListRow(header, adapter);
  }

  private int getOrAssignLoaderId(String categoryId) {
    Integer loaderId = categoryIdToLoaderIdMap.get(categoryId);
    if (loaderId == null) {
      loaderId = categoryIdToLoaderIdMap.size();
      categoryIdToLoaderIdMap.put(categoryId, loaderId);
    }
    return loaderId;
  }

  private CursorObjectAdapter getOrCreateVideoAdapter(int loaderId, String categoryId) {
    CursorObjectAdapter adapter = loaderIdToAdapterMap.get(loaderId);
    if (adapter == null) {
      adapter = createVideoAdapter(loaderId);
      initVideoLoader(loaderId, categoryId);
    }
    return adapter;
  }

  private CursorObjectAdapter createVideoAdapter(int loaderId) {
    CursorObjectAdapter adapter = new CursorObjectAdapter(new VideoPresenter());
    adapter.setMapper(new VideoCursorMapper());
    loaderIdToAdapterMap.put(loaderId, adapter);
    return adapter;
  }

  private void initVideoLoader(int loaderId, String categoryId) {
    Bundle args = new Bundle();
    args.putString(DmlContract.Categories.ID, categoryId);
    getLoaderManager().initLoader(loaderId, args, this);
  }

  private void finishVideoLoader(int loaderId, Cursor cursor) {
    CursorObjectAdapter adapter = loaderIdToAdapterMap.get(loaderId);
    if (adapter != null) {
      adapter.changeCursor(cursor);
    } else {
      Log.e(TAG, "Was asked to finish nonexistent video loader: " + loaderId);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    int loaderId = loader.getId();
    Log.d(TAG, "onLoaderReset " + loaderId);
    if (loaderId == CATEGORIES_LOADER_ID) {
      categories.clear();
    } else {
      resetVideoLoader(loaderId);
    }
  }

  private void resetVideoLoader(int loaderId) {
    CursorObjectAdapter adapter = loaderIdToAdapterMap.get(loaderId);
    if (adapter != null) {
      adapter.changeCursor(null);
    } else {
      Log.e(TAG, "Was asked to reset nonexistent video loader: " + loaderId);
    }
  }

  private void createDebugCategory() {
    DebugItemPresenter presenter = new DebugItemPresenter();
    ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenter);
    adapter.add(new DebugItem("Sync", new DebugItem.OnItemClickedListener() {
      @Override
      public void onItemClicked() {
        Account account = accountHelper.getOrCreateAccount(getActivity());
        syncHelper.requestSync(account);
      }
    }));
    adapter.add(new DebugItem("Remove all", new DebugItem.OnItemClickedListener() {
      @Override
      public void onItemClicked() {
        removeData();
      }
    }));
    HeaderItem header = new HeaderItem("Debug");
    ListRow debug = new ListRow(header, adapter);
    categories.add(debug);
  }

  private static final class DebugItem {

    final String title;

    final OnItemClickedListener onItemClickedListener;

    DebugItem(String title, OnItemClickedListener onItemClickedListener) {
      this.title = title;
      this.onItemClickedListener = onItemClickedListener;
    }

    void click() {
      onItemClickedListener.onItemClicked();
    }

    interface OnItemClickedListener {

      void onItemClicked();
    }
  }

  private static final class DebugItemPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
      TextView textView = new TextView(parent.getContext());
      textView.setFocusable(true);
      textView.setFocusableInTouchMode(true);
      return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
      DebugItem debugItem = (DebugItem) item;
      ((TextView) viewHolder.view).setText(debugItem.title);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
      // Do nothing.
    }
  }

  private void removeData() {
    Log.d(TAG, "removeData");
    getActivity().getContentResolver().delete(DmlContract.Categories.CONTENT_URI, null, null);
  }
}
