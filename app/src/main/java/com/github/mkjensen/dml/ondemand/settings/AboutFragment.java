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

package com.github.mkjensen.dml.ondemand.settings;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;

import com.github.mkjensen.dml.BuildConfig;
import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.util.LicensesHelper;

import java.util.List;

/**
 * About screen including information about licenses and more.
 */
public final class AboutFragment extends GuidedStepSupportFragment {

  private static final String TAG = "AboutFragment";

  private static final String BREADCRUMB = null;

  private static final String DESCRIPTION = null;

  private static final long CONTENT_ID = 0;

  private static final long THIRD_PARTY_ID = 1;

  @NonNull
  @Override
  public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
    Resources resources = getResources();
    String title = resources.getString(R.string.ondemand_settings_about);
    Drawable icon = resources.getDrawable(R.drawable.ic_info_outline_black_24dp, null);
    return new GuidanceStylist.Guidance(title, DESCRIPTION, BREADCRUMB, icon);
  }

  @Override
  public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
    actions.add(new GuidedAction.Builder(getContext())
        .id(CONTENT_ID)
        .title(R.string.ondemand_settings_about_content)
        .description(R.string.ondemand_settings_about_content_description)
        .build());
    actions.add(new GuidedAction.Builder(getContext())
        .id(THIRD_PARTY_ID)
        .title(R.string.ondemand_settings_about_thirdparty)
        .description(R.string.ondemand_settings_about_thirdparty_description)
        .build());
    actions.add(new GuidedAction.Builder(getContext())
        .infoOnly(true)
        .title(R.string.ondemand_settings_about_version)
        .description(BuildConfig.VERSION_NAME)
        .build());
  }

  @Override
  public void onGuidedActionClicked(GuidedAction action) {
    long actionId = action.getId();
    if (actionId == CONTENT_ID) {
      LicensesHelper.showContentLicenses(getActivity().getSupportFragmentManager());
    } else if (actionId == THIRD_PARTY_ID) {
      LicensesHelper.showThirdPartyLicenses(getActivity().getSupportFragmentManager());
    } else {
      Log.w(TAG, "Unhandled action: " + action);
    }
  }
}
