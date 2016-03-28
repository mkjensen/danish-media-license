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

import java.io.IOException;

/**
 * Loads a video URL from the backend.
 */
public class VideoUrlLoader extends BackendLoader<String> {

  private static final String TAG = "VideoUrlLoader";

  private final String linksUrl;

  public VideoUrlLoader(@NonNull Context context, @NonNull String linksUrl) {
    super(context);
    this.linksUrl = notNull(linksUrl);
  }

  @Override
  public String loadInBackground() {
    try {
      return backendHelper.loadVideoUrl(linksUrl);
    } catch (IOException ex) {
      Log.e(TAG, String.format("Failed to load video URL from [%s]", linksUrl), ex);
      return null;
    }
  }

  @NonNull
  String getLinksUrl() {
    return linksUrl;
  }
}
