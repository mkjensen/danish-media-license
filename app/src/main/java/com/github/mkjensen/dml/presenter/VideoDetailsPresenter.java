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

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.github.mkjensen.dml.model.Video;

/**
 * Details presenter for {@link Video}.
 */
public class VideoDetailsPresenter extends AbstractDetailsDescriptionPresenter {

  @Override
  protected void onBindDescription(ViewHolder vh, Object item) {
    Video video = (Video) item;
    vh.getTitle().setText(video.getTitle());
    vh.getBody().setText(video.getDescription());
  }
}
