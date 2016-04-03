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

package com.github.mkjensen.dml.presenter;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.github.mkjensen.dml.model.SettingsItem;

/**
 * Presenter for {@link SettingsItem}.
 */
public final class SettingsItemPresenter extends Presenter {

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent) {
    ImageCardView view = createView(parent);
    return new ViewHolder(view);
  }

  private static ImageCardView createView(ViewGroup parent) {
    ImageCardView view = new ImageCardView(parent.getContext());
    view.setFocusable(true);
    view.setFocusableInTouchMode(true);
    view.setInfoVisibility(ImageCardView.CARD_REGION_VISIBLE_ALWAYS);
    return view;
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, Object item) {
    ImageCardView view = (ImageCardView) viewHolder.view;
    SettingsItem settingsItem = (SettingsItem) item;
    Context context = view.getContext();
    view.setTitleText(context.getString(settingsItem.getTitleResId()));
    view.setMainImage(context.getDrawable(settingsItem.getImageResId()));
  }

  @Override
  public void onUnbindViewHolder(ViewHolder viewHolder) {
    ImageCardView view = (ImageCardView) viewHolder.view;
    view.setMainImage(null);
  }
}
