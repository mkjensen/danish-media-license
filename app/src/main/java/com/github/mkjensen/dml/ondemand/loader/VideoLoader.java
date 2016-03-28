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

package com.github.mkjensen.dml.ondemand.loader;

import static com.github.mkjensen.dml.Defense.notNull;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mkjensen.dml.backend.BackendLoader;
import com.github.mkjensen.dml.backend.Video;

import java.io.IOException;

/**
 * Loads a {@link Video} instance from the backend.
 */
public class VideoLoader extends BackendLoader<Video> {

  private static final String TAG = "VideoLoader";

  private final String videoId;

  public VideoLoader(@NonNull Context context, @NonNull String videoId) {
    super(context);
    this.videoId = notNull(videoId);
  }

  @Override
  public Video loadInBackground() {
    try {
      return backendHelper.loadVideo(videoId);
    } catch (IOException ex) {
      Log.e(TAG, String.format("Failed to load video [%s]", videoId), ex);
      return null;
    }
  }

  @NonNull
  String getVideoId() {
    return videoId;
  }
}
