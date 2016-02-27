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

import android.annotation.SuppressLint;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

/**
 * Details presenter for {@link Video}.
 */
class VideoDetailsPresenter extends AbstractDetailsDescriptionPresenter {

  @SuppressLint("SetTextI18n")
  @Override
  protected void onBindDescription(ViewHolder vh, Object item) {
    Video video = (Video) item;
    vh.getTitle().setText(video.getTitle());
    vh.getBody().setText("Morbi quis pellentesque velit, non sodales lectus."
        + " Aliquam at enim ultricies, rhoncus sem venenatis, condimentum tortor."
        + " Pellentesque turpis turpis, iaculis eget magna vitae, efficitur sodales.");
  }
}
