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

import static com.github.mkjensen.dml.util.Preconditions.notNull;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.github.mkjensen.dml.R;

/**
 * Helper class for displaying information about licenses.
 */
public final class LicensesHelper {

  private static final String LICENSES_FRAGMENT_TAG = "LicensesFragment";

  private LicensesHelper() {
  }

  public static void showContentLicenses(@NonNull FragmentManager fragmentManager) {
    show(fragmentManager, "file:///android_asset/content_licenses.html");
  }

  public static void showThirdPartyLicenses(@NonNull FragmentManager fragmentManager) {
    show(fragmentManager, "file:///android_asset/third_party_licenses.html");
  }

  private static void show(FragmentManager fragmentManager, String url) {
    LicensesFragment.newInstance(url).show(fragmentManager, LICENSES_FRAGMENT_TAG);
  }

  /**
   * A fragment displaying the content located at a specified URL.
   */
  public static final class LicensesFragment extends DialogFragment {

    private static final String URL = "url";

    private static final int PARENT_THEME_RES_ID = 0;

    private static final ViewGroup WEB_VIEW_ROOT = null;

    private String url;

    /**
     * Creates a new fragment displaying the content located at the specified URL.
     */
    public static LicensesFragment newInstance(@NonNull String url) {
      Bundle args = new Bundle();
      args.putString(URL, notNull(url));
      LicensesFragment fragment = new LicensesFragment();
      fragment.setArguments(args);
      return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      url = getArguments().getString(URL);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      Context context = getContext();
      return new AlertDialog.Builder(context, PARENT_THEME_RES_ID)
          .setView(createWebView(context))
          .create();
    }

    private WebView createWebView(Context context) {
      WebView view = (WebView) LayoutInflater.from(context)
          .inflate(R.layout.dialog_licenses, WEB_VIEW_ROOT);
      view.loadUrl(url);
      return view;
    }
  }
}
