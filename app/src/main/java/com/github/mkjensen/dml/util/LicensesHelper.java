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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.github.mkjensen.dml.R;

/**
 * Helper class for showing a dialog with information about licenses.
 */
public final class LicensesHelper {

  private static final String LICENSES_FRAGMENT_TAG = "LicensesFragment";

  private LicensesHelper() {
  }

  public static void showLicenses(@NonNull FragmentManager fragmentManager) {
    new LicensesFragment().show(fragmentManager, LICENSES_FRAGMENT_TAG);
  }

  /**
   * A fragment containing licenses information and an "OK" button for dismissal.
   */
  public static final class LicensesFragment extends DialogFragment {

    private static final int PARENT_THEME_RES_ID = 0;

    private static final ViewGroup WEB_VIEW_ROOT = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      Context context = getContext();
      return new AlertDialog.Builder(context, PARENT_THEME_RES_ID)
          .setView(createWebView(context))
          .create();
    }

    private static WebView createWebView(Context context) {
      WebView view = (WebView) LayoutInflater.from(context)
          .inflate(R.layout.dialog_licenses, WEB_VIEW_ROOT);
      view.loadUrl("file:///android_asset/licenses.html");
      return view;
    }
  }
}
