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

import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID;
import static android.support.v4.media.session.MediaSessionCompat.Callback;
import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS;
import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE;
import static android.support.v4.media.session.PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_ERROR;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_NONE;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_STOPPED;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v17.leanback.app.MediaControllerGlue;
import android.support.v17.leanback.app.PlaybackOverlaySupportFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.github.mkjensen.dml.DmlException;
import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.backend.Video;
import com.github.mkjensen.dml.exoplayer.DemoPlayer;
import com.github.mkjensen.dml.exoplayer.HlsRendererBuilder;

/**
 * Playback screen for on-demand videos.
 */
public class PlaybackFragment extends PlaybackOverlaySupportFragment {

  private static final String TAG = "PlaybackFragment";

  private Video video;

  private MediaSessionCompat mediaSession;

  private MediaControllerHelper mediaControllerHelper;

  private ArrayObjectAdapter rows;

  private DemoPlayer player;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    initVideo();
    initMediaSession();
    initMediaControllerHelper();
    initUi();
    initPlayer();
    initTextureView();
  }

  private void initVideo() {
    video = getActivity().getIntent().getParcelableExtra(PlaybackActivity.VIDEO);
    if (video == null) {
      throw new DmlException("Intent did not include argument: " + PlaybackActivity.VIDEO);
    }
  }

  private void initMediaSession() {
    FragmentActivity activity = getActivity();
    mediaSession = new MediaSessionCompat(activity, TAG);
    mediaSession.setCallback(new MediaSessionCallback());
    mediaSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS | FLAG_HANDLES_TRANSPORT_CONTROLS);
    mediaSession.setActive(true);
    updateMetadata();
    setPlaybackState(STATE_NONE);
    activity.setSupportMediaController(new MediaControllerCompat(activity, mediaSession));
  }

  private void initMediaControllerHelper() {
    mediaControllerHelper = new MediaControllerHelper(getActivity());
    mediaControllerHelper.attachToMediaController(mediaSession.getController());
  }

  private void initUi() {
    PlaybackControlsRowPresenter presenter = mediaControllerHelper.createControlsRowAndPresenter();
    rows = new ArrayObjectAdapter(presenter);
    rows.add(mediaControllerHelper.getControlsRow());
    setAdapter(rows);
    setOnItemViewClickedListener(new OnItemViewClickedListener() {

      @Override
      public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Action) {
          mediaControllerHelper.onActionClicked((Action) item);
        }
      }
    });
  }

  private void initPlayer() {
    HlsRendererBuilder rendererBuilder = new HlsRendererBuilder(getActivity(), TAG, video.getUrl());
    player = new DemoPlayer(rendererBuilder);
    player.addListener(new DemoPlayerListener());
    player.prepare();
  }

  private void initTextureView() {
    TextureView textureView = (TextureView) getActivity()
        .findViewById(R.id.ondemand_playback_textureview);
    textureView.setSurfaceTextureListener(new TextureViewSurfaceTextureListener());
  }

  private void updateMetadata() {
    MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder()
        .putString(METADATA_KEY_MEDIA_ID, video.getId())
        .putString(METADATA_KEY_DISPLAY_TITLE, video.getTitle())
        .putString(METADATA_KEY_DISPLAY_DESCRIPTION, video.getDescription());
    if (player != null) {
      builder.putLong(METADATA_KEY_DURATION, player.getDuration());
    } else {
      builder.putLong(METADATA_KEY_DURATION, DemoPlayer.UNKNOWN_TIME);
    }
    mediaSession.setMetadata(builder.build());
  }

  private void setPlaybackState(int state) {
    PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
    if (state == STATE_PAUSED || state == STATE_PLAYING) {
      builder.setActions(ACTION_PLAY_PAUSE);
    }
    if (player != null) {
      builder.setBufferedPosition(player.getBufferedPosition());
      builder.setState(state, player.getCurrentPosition(), state == STATE_PLAYING ? 1f : 0f);
    } else {
      builder.setState(state, PLAYBACK_POSITION_UNKNOWN, 0f);
    }
    mediaSession.setPlaybackState(builder.build());
  }

  private void playPause(boolean play) {
    player.setPlayWhenReady(play && !mediaControllerHelper.isMediaPlaying());
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy");
    super.onDestroy();
    player.release();
    mediaSession.release();
  }

  private final class MediaSessionCallback extends Callback {

    @Override
    public void onPlay() {
      playPause(true);
    }

    @Override
    public void onPause() {
      playPause(false);
    }
  }

  private final class MediaControllerHelper extends MediaControllerGlue {

    MediaControllerHelper(Context context) {
      super(context, null, new int[1]);
    }

    @Override
    protected void onRowChanged(PlaybackControlsRow row) {
      if (rows == null) {
        return;
      }
      int index = rows.indexOf(row);
      rows.notifyArrayItemRangeChanged(index, 1);
    }
  }

  private final class DemoPlayerListener implements DemoPlayer.Listener {

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
      switch (playbackState) {
        case DemoPlayer.STATE_IDLE:
          setPlaybackState(STATE_NONE);
          break;
        case DemoPlayer.STATE_BUFFERING:
        case DemoPlayer.STATE_PREPARING:
          updateMetadata();
          setPlaybackState(STATE_BUFFERING);
          break;
        case DemoPlayer.STATE_READY:
          setPlaybackState(playWhenReady ? STATE_PLAYING : STATE_PAUSED);
          break;
        case DemoPlayer.STATE_ENDED:
          setPlaybackState(STATE_STOPPED);
          break;
        default:
          Log.w(TAG, String.format("Unhandled state change: %s, %d", playWhenReady, playbackState));
          break;
      }
    }

    @Override
    public void onError(Exception ex) {
      Log.e(TAG, "An error occurred in ExoPlayer", ex);
      PlaybackStateCompat state = new PlaybackStateCompat.Builder()
          .setState(STATE_ERROR, PLAYBACK_POSITION_UNKNOWN, 0f)
          .setErrorMessage(ex.toString())
          .build();
      mediaSession.setPlaybackState(state);
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthHeightRatio) {
      // Do nothing.
    }
  }

  private final class TextureViewSurfaceTextureListener
      implements TextureView.SurfaceTextureListener {

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
      Log.d(TAG, "onSurfaceTextureAvailable");
      player.setSurface(new Surface(surface));
      playPause(true);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
      // Do nothing.
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
      Log.d(TAG, "onSurfaceTextureDestroyed");
      player.blockingClearSurface();
      return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
      // Do nothing.
    }
  }
}
