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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mkjensen.dml.model.Channel;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Loads {@link Channel} instances from the backend.
 */
public final class ChannelsLoader extends BackendLoader<List<Channel>> {

  private static final String TAG = "ChannelsLoader";

  public ChannelsLoader(@NonNull Context context) {
    super(context);
  }

  @Override
  public List<Channel> loadInBackground() {
    Log.d(TAG, "loadInBackground");
    try {
      List<Channel> allChannels = backendHelper.loadChannels();
      List<Channel> channels = new ArrayList<>(allChannels.size());
      for (Channel channel : allChannels) {
        if (!channel.isWebChannel()) {
          channels.add(channel);
        }
      }
      Collections.sort(channels, new Comparator<Channel>() {
        @Override
        public int compare(Channel lhs, Channel rhs) {
          return Collator.getInstance().compare(lhs.getTitle(), rhs.getTitle());
        }
      });
      return channels;
    } catch (IOException ex) {
      Log.e(TAG, "Failed to load channels", ex);
      return null;
    }
  }
}
