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
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.model.IconHeaderItem;

/**
 * Presenter for {@link IconHeaderItem}.
 */
public final class IconHeaderItemPresenter extends RowHeaderPresenter {

  private static final boolean ATTACH_TO_ROOT = false;

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) parent.getContext()
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.ondemand_browse_header, parent, ATTACH_TO_ROOT);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
    IconHeaderItem header = (IconHeaderItem) ((ListRow) item).getHeaderItem();
    initIcon(viewHolder.view, header);
    initTitle(viewHolder.view, header);
  }

  private static void initIcon(View view, IconHeaderItem header) {
    Drawable icon = view.getResources().getDrawable(header.getIconResId(), null);
    ImageView imageView = (ImageView) view.findViewById(R.id.ondemand_browse_header_icon);
    imageView.setImageDrawable(icon);
  }


  private static void initTitle(View view, IconHeaderItem header) {
    TextView textView = (TextView) view.findViewById(R.id.ondemand_browse_header_title);
    textView.setText(header.getName());
  }

  @Override
  public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    // Do nothing.
  }

  @Override
  protected void onSelectLevelChanged(ViewHolder holder) {
    // Do nothing.
  }
}
