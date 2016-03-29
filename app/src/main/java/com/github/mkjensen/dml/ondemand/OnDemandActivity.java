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
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.github.mkjensen.dml.util.BackgroundHelper;

/**
 * Extension of {@link FragmentActivity} that integrates with {@link BackgroundHelper} for
 * background handling and overrides {@link FragmentActivity#onSearchRequested()} to activate search
 * functionality.
 */
abstract class OnDemandActivity extends FragmentActivity {

  private static final String TAG = "OnDemandActivity";

  private static final int SEARCH_REQUEST_CODE = 0;

  private Drawable oldBackground;

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    BackgroundHelper.attach(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    BackgroundHelper.release(this);
  }

  @Override
  public boolean onSearchRequested() {
    clearBackground();
    startActivityForResult(new Intent(this, SearchActivity.class), SEARCH_REQUEST_CODE);
    return true;
  }

  private void clearBackground() {
    if (oldBackground == null) {
      oldBackground = BackgroundHelper.getBackground(this);
    }
    BackgroundHelper.clearBackground(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == SEARCH_REQUEST_CODE) {
      restoreBackground();
    } else {
      Log.w(TAG, "Unhandled request code: " + requestCode);
    }
  }

  private void restoreBackground() {
    if (oldBackground != null) {
      BackgroundHelper.setBackground(this, oldBackground);
      oldBackground = null;
    }
  }
}
