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

package com.github.mkjensen.dml.live;

import static com.github.mkjensen.dml.util.Preconditions.intentStringExtraNotNull;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.tv.TvContract;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.backend.loader.ChannelsLoader;
import com.github.mkjensen.dml.model.Channel;
import com.github.mkjensen.dml.model.Protocol;
import com.github.mkjensen.dml.util.LoadingHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Setup screen for live channels.
 */
public final class SetupFragment extends GuidedStepSupportFragment
    implements LoaderManager.LoaderCallbacks<List<Channel>> {

  private static final String TAG = "SetupFragment";

  private static final String BREADCRUMB = null;

  private static final long OK_ID = 0;

  private static final long CANCEL_ID = 1;

  private String inputId;

  private List<Channel> channels;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    LoadingHelper.showLoading(this);
    initInputId();
    initLoader();
  }

  private void initInputId() {
    inputId = intentStringExtraNotNull(getActivity().getIntent(), TvInputInfo.EXTRA_INPUT_ID);
  }

  private void initLoader() {
    getLoaderManager().initLoader(0, null, this);
  }

  @NonNull
  @Override
  public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
    Resources resources = getResources();
    String title = resources.getString(R.string.app_name);
    String description = resources.getString(R.string.live_setup_description);
    Drawable icon = resources.getDrawable(R.mipmap.ic_launcher, null);
    return new GuidanceStylist.Guidance(title, description, BREADCRUMB, icon);
  }

  @Override
  public void onGuidedActionClicked(GuidedAction action) {
    long actionId = action.getId();
    if (actionId == OK_ID) {
      processChannels();
      getActivity().finish();
    } else if (actionId == CANCEL_ID) {
      getActivity().finish();
    }
  }

  private void processChannels() {
    ContentResolver resolver = getContext().getContentResolver();
    deleteExistingChannels(resolver, inputId);
    ContentValues values = new ContentValues();
    values.put(Channels.COLUMN_INPUT_ID, inputId);
    values.put(Channels.COLUMN_SERVICE_TYPE, Channels.SERVICE_TYPE_AUDIO_VIDEO);
    values.put(Channels.COLUMN_TYPE, Channels.TYPE_OTHER);
    values.put(Channels.COLUMN_VIDEO_FORMAT, Channels.VIDEO_FORMAT_720P);
    int id = 0;
    for (Channel channel : channels) {
      String title = channel.getTitle();
      String url = getUrl(channel);
      if (url == null) {
        Log.d(TAG,
            String.format("Discarded channel [%s] because no suitable stream was found", title));
        continue;
      }
      id++;
      values.put(Channels.COLUMN_DISPLAY_NAME, title);
      values.put(Channels.COLUMN_DISPLAY_NUMBER, id);
      values.put(Channels.COLUMN_INTERNAL_PROVIDER_DATA, url);
      values.put(Channels.COLUMN_ORIGINAL_NETWORK_ID, id);
      values.put(Channels.COLUMN_SERVICE_ID, id);
      values.put(Channels.COLUMN_TRANSPORT_STREAM_ID, id);
      resolver.insert(Channels.CONTENT_URI, values);
    }
    Log.d(TAG, "Added channels: " + id);
  }

  private static void deleteExistingChannels(ContentResolver resolver, String inputId) {
    int deleted = resolver.delete(
        TvContract.buildChannelsUriForInput(inputId),
        null, // where
        null // selectionArgs
    );
    Log.d(TAG, "Deleted existing channels: " + deleted);
  }

  private static String getUrl(Channel channel) {
    for (Channel.Server server : channel.getServers()) {
      if (server.getProtocol() == Protocol.HLS) {
        int bestKilobitRate = 0;
        String bestPath = null;
        for (Channel.Server.Quality quality : server.getQualities()) {
          int kilobitRate = quality.getKilobitRate();
          if (kilobitRate > bestKilobitRate) {
            bestKilobitRate = kilobitRate;
            List<Channel.Server.Quality.Stream> streams = quality.getStreams();
            bestPath = streams.get(0).getPath();
          }
        }
        if (bestPath != null) {
          return server.getUrl() + "/" + bestPath;
        }
      }
    }
    return null;
  }

  @Override
  public Loader<List<Channel>> onCreateLoader(int id, Bundle args) {
    Log.d(TAG, "onCreateLoader");
    return new ChannelsLoader(getActivity());
  }

  @Override
  public void onLoadFinished(Loader<List<Channel>> loader, List<Channel> data) {
    Log.d(TAG, "onLoadFinished");
    LoadingHelper.hideLoading(this);
    channels = data;
    if (data == null) {
      Log.w(TAG, "No data returned by loader");
      return;
    }
    createActions();
  }

  private void createActions() {
    List<GuidedAction> actions = new ArrayList<>(channels.size() + 2);
    Context context = getContext();
    for (Channel channel : channels) {
      actions.add(new GuidedAction.Builder(context)
          .enabled(false)
          .title(channel.getTitle())
          .build());
    }
    actions.add(new GuidedAction.Builder(context)
        .id(OK_ID)
        .title(R.string.live_setup_ok)
        .build());
    actions.add(new GuidedAction.Builder(context)
        .id(CANCEL_ID)
        .title(R.string.live_setup_cancel)
        .build());
    setActions(actions);
  }

  @Override
  public void onLoaderReset(Loader<List<Channel>> loader) {
    Log.d(TAG, "onLoaderReset");
  }
}
