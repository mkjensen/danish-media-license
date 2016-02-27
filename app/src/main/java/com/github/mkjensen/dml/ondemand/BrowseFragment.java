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

import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;

import com.github.mkjensen.dml.R;

/**
 * Browse screen for on-demand videos.
 */
public class BrowseFragment extends BrowseSupportFragment {

  private static final String TAG = "BrowseFragment";

  private ArrayObjectAdapter categoryAdapter;

  @Override
  public void onAttach(Context context) {
    Log.d(TAG, "onAttach");
    super.onAttach(context);
    categoryAdapter = new ArrayObjectAdapter(new ListRowPresenter());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.d(TAG, "onActivityCreated");
    super.onActivityCreated(savedInstanceState);
    setTitle(getString(R.string.app_name));
    setAdapter(categoryAdapter);
    setupListeners();
  }

  private void setupListeners() {
    setOnItemViewSelectedListener(new OnItemViewSelectedListener() {

      @Override
      public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                 RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Video) {
          Video video = (Video) item;
          ((BackgroundHelper.Provider) getActivity()).getBackgroundHelper()
              .setDelayed(video.getImageUrl());
        }
      }
    });

    setOnItemViewClickedListener(new OnItemViewClickedListener() {

      @Override
      public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                RowPresenter.ViewHolder rowViewHolder, Row row) {
        Video video = (Video) item;
        ((OnVideoSelectedListener) getActivity()).onVideoSelected(video);
      }
    });
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    addTestData();
  }

  private void addTestData() {
    HeaderItem headerItem = new HeaderItem("Most viewed");
    VideoPresenter videoPresenter = new VideoPresenter();
    ArrayObjectAdapter videoAdapter = new ArrayObjectAdapter(videoPresenter);
    videoAdapter.add(new Video.Builder()
        .title("Bedrag 9:10")
        .imageUrl("http://www.dr.dk/muTest/api/1.2/bar/56c330b2a11f9f0fc418f087")
        .build());
    videoAdapter.add(new Video.Builder()
        .title("X Factor")
        .imageUrl("http://www.dr.dk/muTest/api/1.2/bar/56c2e0b86187a415ac0ff6a4").build());
    categoryAdapter.add(new ListRow(headerItem, videoAdapter));
    headerItem = new HeaderItem("Spotlight");
    videoAdapter = new ArrayObjectAdapter(videoPresenter);
    videoAdapter.add(new Video.Builder()
        .title("Fifa, Sepp Blatter og mig!")
        .imageUrl("http://www.dr.dk/muTest/api/1.2/bar/56cadf266187a41af0811885")
        .build());
    categoryAdapter.add(new ListRow(headerItem, videoAdapter));
  }

  interface OnVideoSelectedListener {

    void onVideoSelected(Video video);
  }
}
