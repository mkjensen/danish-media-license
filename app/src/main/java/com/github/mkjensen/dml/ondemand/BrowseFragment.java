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

import android.content.ContentValues;
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

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.provider.DmlContract;

/**
 * Browse screen for on-demand videos.
 */
public class BrowseFragment extends BrowseSupportFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final String TAG = "BrowseFragment";

  private static final int CATEGORIES_LOADER_ID = -1;

  private ArrayObjectAdapter categories;

  private ArrayMap<String, Integer> categoryIdToLoaderIdMap;

  private SparseArray<CursorObjectAdapter> loaderIdToAdapterMap;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    initAdapter();
    initLoader();
    initUi();
    initListeners();
  }

  private void initAdapter() {
    categories = new ArrayObjectAdapter(new ListRowPresenter());
    setAdapter(categories);
  }

  private void initLoader() {
    categoryIdToLoaderIdMap = new ArrayMap<>();
    loaderIdToAdapterMap = new SparseArray<>();
    getLoaderManager().initLoader(CATEGORIES_LOADER_ID, null, this);
  }

  private void initUi() {
    setTitle(getString(R.string.app_name));
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
    adapter.add(new DebugItem("Add test data", new DebugItem.OnItemClickedListener() {
      @Override
      public void onItemClicked() {
        addTestData();
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

  private void addTestData() {
    Log.d(TAG, "addTestData");

    ContentValues values = new ContentValues();
    values.put(DmlContract.Categories.ID, "most-viewed");
    values.put(DmlContract.Categories.TITLE, "Most Viewed");
    values.put(DmlContract.Categories.URL, "TODO");
    getActivity().getContentResolver().insert(DmlContract.Categories.CONTENT_URI, values);

    values = new ContentValues();
    values.put(DmlContract.Categories.ID, "selected");
    values.put(DmlContract.Categories.TITLE, "Selected");
    values.put(DmlContract.Categories.URL, "TODO");
    getActivity().getContentResolver().insert(DmlContract.Categories.CONTENT_URI, values);

    values = new ContentValues();
    values.put(DmlContract.Videos.ID, "bedrag-10-10");
    values.put(DmlContract.Videos.TITLE, "Bedrag (10:10)");
    values.put(DmlContract.Videos.IMAGE_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56cec0826187a41eacfa6dd8");
    values.put(DmlContract.Videos.DETAILS_URL, "TODO");
    values.put(DmlContract.Videos.DESCRIPTION, "TODO");
    values.put(DmlContract.Videos.LIST_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56ce1df96187a41eacfa6925");
    values.put(DmlContract.Videos.URL, "http://drod08o-vh.akamaihd.net/i/all/clear/streaming/fb/"
        + "56cdb80ca11f9f11f88769fb/Bedrag--10-10-_021fde6ffdd049d7b45e3de5625e9b0c_,1125,562,2324,"
        + ".mp4.csmil/master.m3u8?cc1=name=Dansk~default=yes~forced=no~lang=da~uri=http://www.dr.dk"
        + "/muTest/api/1.2/subtitles/playlist/urn:dr:mu:manifest:56cdb80ca11f9f11f88769fb"
        + "?segmentsizeinms=60000");
    getActivity().getContentResolver().insert(DmlContract.Videos.CONTENT_URI, values);

    values = new ContentValues();
    values.put(DmlContract.Videos.ID, "x-factor-2016-03-04");
    values.put(DmlContract.Videos.TITLE, "X Factor");
    values.put(DmlContract.Videos.IMAGE_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56d7f8a26187a40e104a3f7e");
    values.put(DmlContract.Videos.DETAILS_URL, "TODO");
    values.put(DmlContract.Videos.DESCRIPTION, "TODO");
    values.put(DmlContract.Videos.LIST_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56da5734a11fa017189202c3");
    values.put(DmlContract.Videos.URL, "http://drod04b-vh.akamaihd.net/i/all/clear/streaming/24/"
        + "56da1dfb6187a416346a2a24/X-Factor_88457b85200f4b88ab7d271f639dd1ae_,1127,562,2317,.mp4"
        + ".csmil/master.m3u8?cc1=name=Dansk~default=yes~forced=no~lang=da~uri=http://www.dr.dk/"
        + "muTest/api/1.2/subtitles/playlist/urn:dr:mu:manifest:56da1dfb6187a416346a2a24"
        + "?segmentsizeinms=60000");
    getActivity().getContentResolver().insert(DmlContract.Videos.CONTENT_URI, values);

    values = new ContentValues();
    values.put(DmlContract.Videos.ID, "spoerg-mig-om-alt-5-8-3");
    values.put(DmlContract.Videos.TITLE, "Spørg mig om alt (5:8)");
    values.put(DmlContract.Videos.IMAGE_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56d6c469a11f9f0d085c37e9");
    values.put(DmlContract.Videos.DETAILS_URL, "TODO");
    values.put(DmlContract.Videos.DESCRIPTION, "TODO");
    values.put(DmlContract.Videos.LIST_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56d9e7326187a416346a294d");
    values.put(DmlContract.Videos.URL, "http://drod06h-vh.akamaihd.net/i/all/clear/download/e8/"
        + "56d9d3afa11fa0171891ffe8/Spoerg-mig-om-alt--5-8-_543f18cc538d4deb933937582334a226_,"
        + "1126,562,2325,.mp4.csmil/master.m3u8?cc1=name=Dansk~default=yes~forced=no~lang=da~uri="
        + "http://www.dr.dk/muTest/api/1.2/subtitles/playlist/"
        + "urn:dr:mu:manifest:56d9d3afa11fa0171891ffe8?segmentsizeinms=60000");
    getActivity().getContentResolver().insert(DmlContract.Videos.CONTENT_URI, values);

    values = new ContentValues();
    values.put(DmlContract.Videos.ID, "dokumania-naturens-uorden");
    values.put(DmlContract.Videos.TITLE, "Dokumania: Naturens uorden");
    values.put(DmlContract.Videos.IMAGE_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56b874266187a4086441a491");
    values.put(DmlContract.Videos.DETAILS_URL, "TODO");
    values.put(DmlContract.Videos.DESCRIPTION, "TODO");
    values.put(DmlContract.Videos.LIST_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56b5e10ea11f9f12b82a3264");
    values.put(DmlContract.Videos.URL, "http://drod03m-vh.akamaihd.net/i/dk/clear/streaming/53/"
        + "56b592836187a409c0fc0b53/Dokumania--Naturens-uorden_c3ff1414744c4456bec9568915b283fd_,"
        + "1127,562,2276,.mp4.csmil/master.m3u8");
    getActivity().getContentResolver().insert(DmlContract.Videos.CONTENT_URI, values);

    values = new ContentValues();
    values.put(DmlContract.CategoriesVideos.VIDEO_ID, "bedrag-10-10");
    getActivity().getContentResolver().insert(
        DmlContract.CategoriesVideos.buildUri("most-viewed"), values);

    values = new ContentValues();
    values.put(DmlContract.CategoriesVideos.VIDEO_ID, "x-factor-2016-03-04");
    getActivity().getContentResolver().insert(
        DmlContract.CategoriesVideos.buildUri("most-viewed"), values);

    values = new ContentValues();
    values.put(DmlContract.CategoriesVideos.VIDEO_ID, "spoerg-mig-om-alt-5-8-3");
    getActivity().getContentResolver().insert(
        DmlContract.CategoriesVideos.buildUri("selected"), values);

    values = new ContentValues();
    values.put(DmlContract.CategoriesVideos.VIDEO_ID, "dokumania-naturens-uorden");
    getActivity().getContentResolver().insert(
        DmlContract.CategoriesVideos.buildUri("selected"), values);
  }

  private void removeData() {
    Log.d(TAG, "removeData");
    getActivity().getContentResolver().delete(DmlContract.Videos.CONTENT_URI, null, null);
    getActivity().getContentResolver().delete(DmlContract.Categories.CONTENT_URI, null, null);
  }
}
