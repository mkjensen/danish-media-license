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

import static com.github.mkjensen.dml.util.Preconditions.intentStringExtraNotNull;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsSupportFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.backend.loader.VideoLoader;
import com.github.mkjensen.dml.model.Video;
import com.github.mkjensen.dml.presenter.VideoDetailsPresenter;
import com.github.mkjensen.dml.util.BackgroundHelper;
import com.github.mkjensen.dml.util.LoadingHelper;

/**
 * Details screen for on-demand videos.
 */
public final class DetailsFragment extends DetailsSupportFragment
    implements LoaderManager.LoaderCallbacks<Video> {

  private static final String TAG = "DetailsFragment";

  private static final int ACTION_PLAY = 0;

  private String videoId;

  private ArrayObjectAdapter rows;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    LoadingHelper.showLoading(this);
    initVideoId();
    initUi();
    initListeners();
    initLoader();
  }

  private void initVideoId() {
    videoId = intentStringExtraNotNull(getActivity().getIntent(), DetailsActivity.VIDEO_ID);
  }

  private void initUi() {
    FullWidthDetailsOverviewRowPresenter detailsPresenter =
        new FullWidthDetailsOverviewRowPresenter(new VideoDetailsPresenter());
    ClassPresenterSelector selector = new ClassPresenterSelector();
    selector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    selector.addClassPresenter(ListRow.class, new ListRowPresenter());
    rows = new ArrayObjectAdapter(selector);
    setAdapter(rows);
  }

  private void initListeners() {
    setOnItemViewClickedListener(new OnItemViewClickedListener() {
      @Override
      public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (row instanceof DetailsOverviewRow) {
          onDetailsRowClicked((DetailsOverviewRow) row, item);
        } else {
          Log.w(TAG, "Unhandled row: " + row);
        }
      }
    });
  }

  private void onDetailsRowClicked(DetailsOverviewRow row, Object item) {
    if (item instanceof Action) {
      Action action = (Action) item;
      Video video = (Video) row.getItem();
      onActionClicked(action, video);
    } else {
      Log.w(TAG, "Unhandled item: " + item);
    }
  }

  private void onActionClicked(Action action, Video video) {
    if (action.getId() == ACTION_PLAY) {
      Intent intent = new Intent(getActivity(), PlaybackActivity.class);
      intent.putExtra(PlaybackActivity.VIDEO, video);
      startActivity(intent);
    } else {
      Log.w(TAG, "Unhandled action: " + action);
    }
  }

  private void initLoader() {
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public Loader<Video> onCreateLoader(int id, Bundle args) {
    Log.d(TAG, "onCreateLoader");
    return new VideoLoader(getActivity(), videoId);
  }

  @Override
  public void onLoadFinished(Loader<Video> loader, Video data) {
    Log.d(TAG, "onLoadFinished");
    LoadingHelper.hideLoading(this);
    rows.clear();
    if (data == null) {
      Log.w(TAG, "No data returned by loader");
      return;
    }
    BackgroundHelper.setBackground(getActivity(), data.getImageUrl());
    createDetailsRow(data);
  }

  private void createDetailsRow(Video video) {
    DetailsOverviewRow details = new DetailsOverviewRow(video);
    createActions(details);
    rows.add(details);
  }

  private void createActions(DetailsOverviewRow detailsRow) {
    SparseArrayObjectAdapter actionsAdapter = new SparseArrayObjectAdapter();
    actionsAdapter.set(ACTION_PLAY,
        new Action(ACTION_PLAY, getString(R.string.ondemand_details_action_play)));
    detailsRow.setActionsAdapter(actionsAdapter);
  }

  @Override
  public void onLoaderReset(Loader<Video> loader) {
    Log.d(TAG, "onLoaderReset");
    rows.clear();
  }
}
