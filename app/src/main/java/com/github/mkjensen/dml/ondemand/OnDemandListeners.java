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

import static com.github.mkjensen.dml.util.Preconditions.notNull;

import android.app.Activity;
import android.content.Intent;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.View;

import com.github.mkjensen.dml.model.SettingsItem;
import com.github.mkjensen.dml.model.Video;
import com.github.mkjensen.dml.ondemand.settings.AboutActivity;
import com.github.mkjensen.dml.util.BackgroundHelper;

/**
 * Helper class for creating common listeners.
 */
final class OnDemandListeners {

  private static final String TAG = "OnDemandListeners";

  private OnDemandListeners() {
  }

  static OnItemViewClickedListener createOnItemViewClickedListener(final Activity activity) {
    notNull(activity);
    return new OnItemViewClickedListener() {
      @Override
      public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Video) {
          Video video = (Video) item;
          Intent intent = new Intent(activity, DetailsActivity.class);
          intent.putExtra(DetailsActivity.VIDEO_ID, video.getId());
          activity.startActivity(intent);
        } else if (item instanceof SettingsItem) {
          Intent intent = new Intent(activity, AboutActivity.class);
          activity.startActivity(intent);
        } else {
          Log.w(TAG, "Unhandled item: " + item);
        }
      }
    };
  }

  static OnItemViewSelectedListener createOnItemViewSelectedListener(final Activity activity) {
    notNull(activity);
    return new OnItemViewSelectedListener() {
      @Override
      public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                 RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Video) {
          Video video = (Video) item;
          BackgroundHelper.setBackgroundDelayed(activity, video.getImageUrl());
        }
      }
    };
  }

  static View.OnClickListener createOnSearchClickedListener(final Activity activity) {
    notNull(activity);
    return new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        activity.onSearchRequested();
      }
    };
  }
}
