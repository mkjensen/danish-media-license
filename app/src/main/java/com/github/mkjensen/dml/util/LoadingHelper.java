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

package com.github.mkjensen.dml.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.github.mkjensen.dml.R;

/**
 * Helper class for showing a progress indicator when a fragment is waiting for an operation to
 * complete.
 */
public final class LoadingHelper {

  private static final String TAG = "LoadingHelper";

  private static final String LOADING_FRAGMENT_TAG = "LoadingFragment";

  private LoadingHelper() {
  }

  /**
   * Shows the progress indicator for the specified fragment. If the progress indicator is already
   * present, {@link IllegalStateException} is thrown.
   */
  public static void showLoading(@NonNull Fragment fragment) {
    fragment.getActivity().getSupportFragmentManager()
        .beginTransaction()
        .add(fragment.getId(), new LoadingFragment(), LOADING_FRAGMENT_TAG)
        .commit();
  }

  /**
   * Hides the progress indicator, if present, for the specified fragment.
   */
  public static void hideLoading(@NonNull Fragment fragment) {
    FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
    Fragment loadingFragment = fragmentManager.findFragmentByTag(LOADING_FRAGMENT_TAG);
    if (loadingFragment == null) {
      Log.w(TAG, "Was asked to remove nonexistent progress indicator, ignoring");
      return;
    }
    fragmentManager
        .beginTransaction()
        .remove(loadingFragment)
        .commitAllowingStateLoss(); // TODO: Is commitAllowingStateLoss okay, can it be avoided?
  }

  /**
   * A fragment containing a progress indicator for use when another fragment is waiting for an
   * operation to complete.
   */
  public static final class LoadingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
      if (container == null) {
        return null;
      }
      Context context = container.getContext();
      ProgressBar progressBar = new ProgressBar(context);
      if (container instanceof FrameLayout) {
        Resources resources = context.getResources();
        int width = resources.getDimensionPixelSize(R.dimen.ondemand_loading_width);
        int height = resources.getDimensionPixelSize(R.dimen.ondemand_loading_height);
        FrameLayout.LayoutParams layoutParams =
            new FrameLayout.LayoutParams(width, height, Gravity.CENTER);
        progressBar.setLayoutParams(layoutParams);
      }
      return progressBar;
    }
  }
}
