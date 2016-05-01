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

import com.google.android.exoplayer.MediaFormat;

import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.media.tv.TvTrackInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;

import com.github.mkjensen.dml.exoplayer.DemoPlayer;
import com.github.mkjensen.dml.exoplayer.HlsRendererBuilder;

import java.util.ArrayList;
import java.util.List;

public final class DmlTvInputService extends TvInputService {

  private static final String TAG = "DmlTvInputService";

  private static SparseIntArray exoPlayerToTvTrackTypeMap;

  @Override
  public void onCreate() {
    super.onCreate();
    initExoPlayerToTvTrackTypeMap();
  }

  private void initExoPlayerToTvTrackTypeMap() {
    exoPlayerToTvTrackTypeMap = new SparseIntArray(3);
    exoPlayerToTvTrackTypeMap.put(DemoPlayer.TYPE_VIDEO, TvTrackInfo.TYPE_VIDEO);
    exoPlayerToTvTrackTypeMap.put(DemoPlayer.TYPE_AUDIO, TvTrackInfo.TYPE_AUDIO);
    exoPlayerToTvTrackTypeMap.put(DemoPlayer.TYPE_TEXT, TvTrackInfo.TYPE_SUBTITLE);
  }

  @Nullable
  @Override
  public Session onCreateSession(String inputId) {
    Log.d(TAG, "onCreateSession: " + inputId);
    return new TvInputServiceSession(this);
  }

  private static final class TvInputServiceSession extends TvInputService.Session
      implements DemoPlayer.Listener {

    private final Context context;

    private Surface surface;

    private DemoPlayer player;

    TvInputServiceSession(Context context) {
      super(context);
      this.context = context;
    }

    @Override
    public void onRelease() {
      Log.d(TAG, "onRelease");
      releasePlayer();
    }

    private void releasePlayer() {
      if (player == null) {
        return;
      }
      player.removeListener(this);
      player.setSurface(null);
      player.stop();
      player.release();
      player = null;
    }

    @Override
    public boolean onSetSurface(Surface surface) {
      this.surface = surface;
      if (player != null) {
        player.setSurface(surface);
      }
      return true;
    }

    @Override
    public void onSetStreamVolume(float volume) {
    }

    @Override
    public boolean onTune(Uri channelUri) {
      Log.d(TAG, "onTune " + channelUri);
      notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING);
      String[] projection = {TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA};
      try (Cursor cursor = context.getContentResolver().query(
          channelUri,
          projection,
          null, // selection
          null, // selectionArgs
          null // sortOrder
      )) {
        if (cursor == null || !cursor.moveToNext()) {
          Log.e(TAG, "Channel not found: " + channelUri);
          return false;
        }
        String url = cursor.getString(0);
        play(url);
        return true;
      }
    }

    private void play(String url) {
      releasePlayer();
      player = new DemoPlayer(new HlsRendererBuilder(context, TAG, url));
      player.addListener(this);
      player.prepare();
      player.setSurface(surface);
      player.setPlayWhenReady(true);
    }

    @Override
    public void onSetCaptionEnabled(boolean enabled) {
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
      if (playWhenReady && playbackState == DemoPlayer.STATE_READY) {
        notifyTracksChanged(getAllTracks());
        String videoId = getTrackId(TvTrackInfo.TYPE_VIDEO,
            player.getSelectedTrack(TvTrackInfo.TYPE_VIDEO));
        String audioId = getTrackId(TvTrackInfo.TYPE_AUDIO,
            player.getSelectedTrack(TvTrackInfo.TYPE_AUDIO));
        notifyTrackSelected(TvTrackInfo.TYPE_VIDEO, videoId);
        notifyTrackSelected(TvTrackInfo.TYPE_AUDIO, audioId);
        notifyVideoAvailable();
      }
    }

    private List<TvTrackInfo> getAllTracks() {
      List<TvTrackInfo> tracks = new ArrayList<>();
      for (int i = 0, n = exoPlayerToTvTrackTypeMap.size(); i < n; i++) {
        int exoPlayerTrackType = exoPlayerToTvTrackTypeMap.keyAt(i);
        int tvTrackType = exoPlayerToTvTrackTypeMap.valueAt(i);
        for (int j = 0, m = player.getTrackCount(exoPlayerTrackType); j < m; j++) {
          MediaFormat trackFormat = player.getTrackFormat(exoPlayerTrackType, j);
          String trackId = getTrackId(tvTrackType, j);
          TvTrackInfo.Builder builder = new TvTrackInfo.Builder(tvTrackType, trackId);
          if (tvTrackType == TvTrackInfo.TYPE_VIDEO) {
            builder.setVideoWidth(trackFormat.width);
            builder.setVideoHeight(trackFormat.height);
          } else if (tvTrackType == TvTrackInfo.TYPE_AUDIO) {
            builder.setAudioChannelCount(trackFormat.channelCount);
            builder.setAudioSampleRate(trackFormat.sampleRate);
            if (trackFormat.language != null) {
              builder.setLanguage(trackFormat.language);
            }
          }
          tracks.add(builder.build());
        }
      }
      return tracks;
    }

    private static String getTrackId(int trackType, int trackIndex) {
      return trackType + "-" + trackIndex;
    }

    @Override
    public void onError(Exception ex) {
      Log.e(TAG, "An error occurred in ExoPlayer", ex);
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthHeightRatio) {
      // Do nothing.
    }
  }
}
