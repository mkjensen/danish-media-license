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

package com.github.mkjensen.dml.backend.loader;

import static com.github.mkjensen.dml.util.Preconditions.notNull;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mkjensen.dml.model.VideoManifest;

import java.io.IOException;

/**
 * Loads a video manifest from the backend.
 */
public final class VideoManifestLoader extends BackendLoader<VideoManifest> {

  private static final String TAG = "VideoManifestLoader";

  private final String manifestUrl;

  public VideoManifestLoader(@NonNull Context context, @NonNull String manifestUrl) {
    super(context);
    this.manifestUrl = notNull(manifestUrl);
  }

  @Override
  public VideoManifest loadInBackground() {
    try {
      return backendHelper.loadVideoManifest(manifestUrl);
    } catch (IOException ex) {
      Log.e(TAG, String.format("Failed to load video manifest from [%s]", manifestUrl), ex);
      return null;
    }
  }

  @NonNull
  String getManifestUrl() {
    return manifestUrl;
  }
}
