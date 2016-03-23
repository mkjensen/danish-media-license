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

import static android.media.session.MediaSession.FLAG_HANDLES_MEDIA_BUTTONS;
import static android.media.session.MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.PlaybackOverlaySupportFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
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

  private MediaSession mediaSession;
  private MediaController.Callback mediaControllerCallback;
  private Video video;
  private TextureView textureView;
  private ArrayObjectAdapter rows;
  private PlaybackControlsRow controls;
  private ArrayObjectAdapter primaryActions;
  private PlayPauseAction playPauseAction;
  private DemoPlayer player;

  @Override
  public void onAttach(Context context) {
    Log.d(TAG, "onAttach");
    super.onAttach(context);
    createMediaSession();
    registerMediaControllerCallback();
  }

  private void createMediaSession() {
    if (mediaSession != null) {
      return;
    }
    mediaSession = new MediaSession(getActivity(), TAG);
    mediaSession.setCallback(new MediaSessionCallback());
    mediaSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS | FLAG_HANDLES_TRANSPORT_CONTROLS);
    getActivity().setMediaController(
        new MediaController(getActivity(), mediaSession.getSessionToken()));
    setPlaybackState(PlaybackState.STATE_NONE);
  }

  private void registerMediaControllerCallback() {
    if (mediaControllerCallback != null) {
      return;
    }
    mediaControllerCallback = new MediaControllerCallback();
    getActivity().getMediaController().registerCallback(mediaControllerCallback);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    initVideo();
    initUi();
    registerTextureViewSurfaceTextureListener();
    preparePlayer(true);
  }

  private void initVideo() {
    video = getActivity().getIntent().getParcelableExtra(PlaybackActivity.VIDEO);
    if (video == null) {
      throw new DmlException("Intent did not include argument: " + PlaybackActivity.VIDEO);
    }
  }

  private void registerTextureViewSurfaceTextureListener() {
    if (textureView != null) {
      return;
    }
    textureView = (TextureView) getActivity().findViewById(R.id.ondemand_playback_textureview);
    textureView.setSurfaceTextureListener(new TextureViewSurfaceTextureListener());
  }

  private void initUi() {
    PlaybackControlsRowPresenter controlsPresenter =
        new PlaybackControlsRowPresenter(new VideoDetailsPresenter());
    ClassPresenterSelector presenterSelector = new ClassPresenterSelector();
    presenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    presenterSelector.addClassPresenter(PlaybackControlsRow.class, controlsPresenter);
    rows = new ArrayObjectAdapter(presenterSelector);
    createControls(controlsPresenter);
    setAdapter(rows);
  }

  private void createControls(PlaybackControlsRowPresenter controlsPresenter) {
    controls = new PlaybackControlsRow(video);
    createActions(controlsPresenter);
    rows.add(controls);
  }

  private void createActions(PlaybackControlsRowPresenter controlsPresenter) {
    ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
    primaryActions = new ArrayObjectAdapter(presenterSelector);
    playPauseAction = new PlayPauseAction(getActivity());
    primaryActions.add(playPauseAction);
    controls.setPrimaryActionsAdapter(primaryActions);
    controlsPresenter.setOnActionClickedListener(new OnActionClickedListener() {

      @Override
      public void onActionClicked(Action action) {
        if (action.getId() == playPauseAction.getId()) {
          if (playPauseAction.getIndex() == PlayPauseAction.PAUSE) {
            getActivity().getMediaController().getTransportControls().pause();
          } else {
            getActivity().getMediaController().getTransportControls().play();
          }
        }
      }
    });
  }

  private void preparePlayer(boolean playWhenReady) {
    if (player == null) {
      HlsRendererBuilder rendererBuilder =
          new HlsRendererBuilder(getActivity(), TAG, video.getUrl());
      player = new DemoPlayer(rendererBuilder);
      player.addListener(new DemoPlayerListener());
    } else {
      playPause(false);
    }
    player.seekTo(0L);
    player.prepare();
    playPause(playWhenReady);
  }

  private void playPause(boolean play) {
    if (player == null) {
      setPlaybackState(PlaybackState.STATE_NONE);
      return;
    }
    if (play && getPlaybackState() != PlaybackState.STATE_PLAYING) {
      player.setPlayWhenReady(true);
      setPlaybackState(PlaybackState.STATE_PLAYING);
    } else {
      player.setPlayWhenReady(false);
      setPlaybackState(PlaybackState.STATE_PAUSED);
    }
  }

  private int getPlaybackState() {
    PlaybackState state = getActivity().getMediaController().getPlaybackState();
    return state != null ? state.getState() : PlaybackState.STATE_NONE;
  }

  private void setPlaybackState(int state) {
    long currentPosition = 0L;
    if (player != null) {
      currentPosition = player.getCurrentPosition();
    }
    PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
        .setActions(getAvailableActions(state))
        .setState(state, currentPosition, 1f);
    mediaSession.setPlaybackState(stateBuilder.build());
  }

  private long getAvailableActions(int state) {
    long actions = 0;
    switch (state) {
      case PlaybackState.STATE_PAUSED:
        actions |= PlaybackState.ACTION_PLAY;
        break;
      case PlaybackState.STATE_PLAYING:
        actions |= PlaybackState.ACTION_PAUSE;
        break;
      default:
        Log.w(TAG, "Unhandled playback state: " + state);
        break;
    }
    return actions;
  }

  @Override
  public void onResume() {
    Log.d(TAG, "onResume");
    super.onResume();
    if (player == null) {
      preparePlayer(true);
    }
    mediaSession.setActive(true);
  }

  @Override
  public void onPause() {
    Log.d(TAG, "onPause");
    super.onPause();
    mediaSession.setActive(false);
    playPause(false);
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy");
    super.onDestroy();
    unregisterMediaControllerCallback();
    releaseMediaSession();
    releasePlayer();
  }

  private void unregisterMediaControllerCallback() {
    if (mediaControllerCallback != null) {
      getActivity().getMediaController().unregisterCallback(mediaControllerCallback);
      mediaControllerCallback = null;
    }
  }

  private void releaseMediaSession() {
    if (mediaSession != null) {
      mediaSession.release();
      mediaSession = null;
    }
  }

  private void releasePlayer() {
    if (player != null) {
      player.release();
      player = null;
    }
  }

  private final class MediaSessionCallback extends MediaSession.Callback {

    @Override
    public void onPlay() {
      playPause(true);
    }

    @Override
    public void onPause() {
      playPause(false);
    }
  }

  private final class MediaControllerCallback extends MediaController.Callback {

    @Override
    public void onPlaybackStateChanged(@NonNull PlaybackState state) {
      switch (state.getState()) {
        case PlaybackState.STATE_PAUSED:
          playPauseAction.setIndex(PlayPauseAction.PLAY);
          notifyActionChanged(playPauseAction);
          break;
        case PlaybackState.STATE_PLAYING:
          playPauseAction.setIndex(PlayPauseAction.PAUSE);
          notifyActionChanged(playPauseAction);
          break;
        default:
          Log.w(TAG, "Unhandled playback state: " + state.getState());
          break;
      }
    }

    private void notifyActionChanged(Action action) {
      int index = primaryActions.indexOf(action);
      if (index != -1) {
        primaryActions.notifyArrayItemRangeChanged(index, 1);
      }
    }
  }

  private final class TextureViewSurfaceTextureListener
      implements TextureView.SurfaceTextureListener {

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
      Log.d(TAG, "onSurfaceTextureAvailable");
      if (player != null) {
        player.setSurface(new Surface(surface));
      }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
      // Do nothing.
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
      Log.d(TAG, "onSurfaceTextureDestroyed");
      if (player != null) {
        player.blockingClearSurface();
      }
      return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
      // Do nothing.
    }
  }

  private final class DemoPlayerListener implements DemoPlayer.Listener {

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
      // Do nothing.
    }

    @Override
    public void onError(Exception ex) {
      Log.e(TAG, ex.toString());
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthHeightRatio) {
      // Do nothing.
    }
  }
}
