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

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.model.Video;

/**
 * Presenter for {@link Video}.
 */
class VideoPresenter extends Presenter {

  private Drawable defaultImage;

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent) {
    defaultImage = parent.getContext().getDrawable(R.mipmap.ic_launcher);
    ImageCardView view = createView(parent);
    return new ViewHolder(view);
  }

  private static ImageCardView createView(ViewGroup parent) {
    Resources resources = parent.getResources();
    int width = resources.getDimensionPixelSize(R.dimen.ondemand_browse_video_width);
    int height = resources.getDimensionPixelSize(R.dimen.ondemand_browse_video_height);
    ImageCardView view = new ImageCardView(parent.getContext());
    view.setFocusable(true);
    view.setFocusableInTouchMode(true);
    view.setInfoVisibility(ImageCardView.CARD_REGION_VISIBLE_ALWAYS);
    view.setMainImageDimensions(width, height);
    return view;
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, Object item) {
    ImageCardView view = (ImageCardView) viewHolder.view;
    Video video = (Video) item;
    view.setTitleText(video.getTitle());
    setImage(view, video);
  }

  private void setImage(ImageCardView view, Video video) {
    String imageUrl = video.getImageUrl();
    if (!Video.NOT_SET.equals(imageUrl)) {
      Glide.with(view.getContext())
          .load(imageUrl)
          .error(defaultImage)
          .into(view.getMainImageView());
    } else {
      view.setMainImage(defaultImage);
    }
  }

  @Override
  public void onUnbindViewHolder(ViewHolder viewHolder) {
    ImageCardView view = (ImageCardView) viewHolder.view;
    view.setMainImage(null);
  }
}
