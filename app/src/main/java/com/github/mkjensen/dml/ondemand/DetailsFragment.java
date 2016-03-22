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
import android.util.Log;

import com.github.mkjensen.dml.DmlException;
import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.backend.Video;

/**
 * Details screen for on-demand videos.
 */
public class DetailsFragment extends DetailsSupportFragment {

  private static final String TAG = "DetailsFragment";

  private static final int ACTION_PLAY = 0;

  private Video video;

  private ArrayObjectAdapter rows;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    initVideo();
    initAdapter();
    initDetails();
    initListeners();
  }

  private void initVideo() {
    video = getActivity().getIntent().getParcelableExtra(DetailsActivity.VIDEO);
    if (video == null) {
      throw new DmlException("Intent did not include argument: " + DetailsActivity.VIDEO);
    }
  }

  private void initAdapter() {
    FullWidthDetailsOverviewRowPresenter detailsPresenter =
        new FullWidthDetailsOverviewRowPresenter(new VideoDetailsPresenter());
    ClassPresenterSelector selector = new ClassPresenterSelector();
    selector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    selector.addClassPresenter(ListRow.class, new ListRowPresenter());
    rows = new ArrayObjectAdapter(selector);
    setAdapter(rows);
  }

  private void initDetails() {
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

  private void initListeners() {
    setOnItemViewClickedListener(new OnItemViewClickedListener() {

      @Override
      public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                RowPresenter.ViewHolder rowViewHolder, Row row) {
        Intent intent = new Intent(getActivity(), PlaybackActivity.class);
        intent.putExtra(PlaybackActivity.VIDEO, video);
        startActivity(intent);
      }
    });
  }
}
