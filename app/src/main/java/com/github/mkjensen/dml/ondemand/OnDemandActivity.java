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
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.github.mkjensen.dml.R;

/**
 * Host activity for on-demand related fragments.
 */
public class OnDemandActivity extends FragmentActivity
    implements BackgroundHelper.Provider, BrowseFragment.OnVideoSelectedListener {

  static final String VIDEO = "video";

  private static final String TAG = "OnDemandActivity";

  private BackgroundHelper backgroundHelper;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ondemand);
    backgroundHelper = new BackgroundHelper(this);
    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.ondemand_fragment_container, new BrowseFragment())
          .commit();
    }
  }

  @Override
  protected void onStop() {
    Log.d(TAG, "onStop");
    backgroundHelper.stop();
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    Log.d(TAG, "onDestroy");
    backgroundHelper.destroy();
    super.onDestroy();
  }

  @Override
  public BackgroundHelper getBackgroundHelper() {
    return backgroundHelper;
  }

  @Override
  public void onVideoSelected(Video video) {
    Bundle args = new Bundle();
    args.putParcelable(VIDEO, video);
    DetailsFragment detailsFragment = new DetailsFragment();
    detailsFragment.setArguments(args);
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.ondemand_fragment_container, detailsFragment)
        .addToBackStack(null)
        .commit();
  }
}
