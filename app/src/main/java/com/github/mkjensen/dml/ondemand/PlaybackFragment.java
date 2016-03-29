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

import static android.support.v17.leanback.app.PlaybackControlGlue.PLAYBACK_SPEED_NORMAL;
import static android.support.v17.leanback.app.PlaybackControlGlue.PLAYBACK_SPEED_PAUSED;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID;
import static android.support.v4.media.session.MediaSessionCompat.Callback;
import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS;
import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_FAST_FORWARD;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_REWIND;
import static android.support.v4.media.session.PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_ERROR;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_FAST_FORWARDING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_NONE;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_REWINDING;
import static com.github.mkjensen.dml.util.Preconditions.intentParcelableExtraNotNull;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.backend.loader.VideoUrlLoader;
import com.github.mkjensen.dml.exoplayer.DemoPlayer;
import com.github.mkjensen.dml.exoplayer.HlsRendererBuilder;
import com.github.mkjensen.dml.model.Video;
import com.github.mkjensen.dml.util.LoadingHelper;

/**
 * Playback screen for on-demand videos.
 */
public final class PlaybackFragment extends PlaybackOverlaySupportFragment {

  private static final String TAG = "PlaybackFragment";

  private Video video;

  private MediaSessionCompat mediaSession;

  private MediaControllerHelper mediaControllerHelper;

  private ArrayObjectAdapter rows;

  private TextureView textureView;

  private DemoPlayer player;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    LoadingHelper.showLoading(this);
    initVideo();
    initMediaSession();
    initMediaControllerHelper();
    initUi();
    initTextureView();
    updateMetadata();
  }

  private void initVideo() {
    video = intentParcelableExtraNotNull(getActivity().getIntent(), PlaybackActivity.VIDEO);
  }

  private void initMediaSession() {
    FragmentActivity activity = getActivity();
    mediaSession = new MediaSessionCompat(activity, TAG);
    mediaSession.setCallback(new MediaSessionCallback());
    mediaSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS | FLAG_HANDLES_TRANSPORT_CONTROLS);
    mediaSession.setActive(true);
    setPlaybackState(STATE_NONE);
    activity.setSupportMediaController(mediaSession.getController());
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

  private void initTextureView() {
    textureView = (TextureView) getActivity().findViewById(R.id.ondemand_playback_textureview);
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
    builder.setActions(ACTION_FAST_FORWARD | ACTION_PLAY_PAUSE | ACTION_REWIND);
    if (player != null) {
      builder.setBufferedPosition(player.getBufferedPosition());
      builder.setState(state, player.getCurrentPosition(), getPlaybackSpeed(state));
    } else {
      builder.setState(state, PLAYBACK_POSITION_UNKNOWN, PLAYBACK_SPEED_PAUSED);
    }
    mediaSession.setPlaybackState(builder.build());
  }

  private static float getPlaybackSpeed(int state) {
    float playbackSpeed;
    switch (state) {
      case STATE_FAST_FORWARDING:
        playbackSpeed = MediaControllerHelper.SEEK_PLAYBACK_SPEED;
        break;
      case STATE_PLAYING:
        playbackSpeed = PLAYBACK_SPEED_NORMAL;
        break;
      case STATE_REWINDING:
        playbackSpeed = -MediaControllerHelper.SEEK_PLAYBACK_SPEED;
        break;
      default:
        playbackSpeed = PLAYBACK_SPEED_PAUSED;
        break;
    }
    return playbackSpeed;
  }

  private void playPause(boolean play) {
    if (player == null) {
      return;
    }
    if (play && !mediaControllerHelper.isMediaPlaying()) {
      setPlaybackState(STATE_PLAYING);
      player.setPlayWhenReady(true);
    } else {
      setPlaybackState(STATE_PAUSED);
      player.setPlayWhenReady(false);
    }
  }

  private void forwardRewind(boolean forward) {
    if (player == null) {
      return;
    }
    long position = player.getCurrentPosition();
    if (forward) {
      setPlaybackState(STATE_FAST_FORWARDING);
      position += MediaControllerHelper.SEEK_MILLISECONDS;
    } else {
      setPlaybackState(STATE_REWINDING);
      position -= MediaControllerHelper.SEEK_MILLISECONDS;
    }
    position = Math.max(0, Math.min(position, player.getDuration()));
    player.seekTo(position);
    mediaControllerHelper.getMediaController().getTransportControls().play();
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy");
    super.onDestroy();
    if (player != null) {
      player.release();
    }
    mediaControllerHelper.detach();
    mediaSession.release();
  }

  private final class MediaSessionCallback extends Callback {

    @Override
    public void onFastForward() {
      forwardRewind(true);
    }

    @Override
    public void onPause() {
      playPause(false);
    }

    @Override
    public void onPlay() {
      playPause(true);
    }

    @Override
    public void onRewind() {
      forwardRewind(false);
    }
  }

  private final class MediaControllerHelper extends MediaControllerGlue {

    static final long SEEK_MILLISECONDS = 10000L;

    static final int SEEK_PLAYBACK_SPEED = 2;

    final Handler handler;

    final Runnable updateProgressRunnable;

    MediaControllerHelper(Context context) {
      super(context, null, new int[] {SEEK_PLAYBACK_SPEED});
      handler = new Handler();
      updateProgressRunnable = createUpdateProgressRunnable();
    }

    private Runnable createUpdateProgressRunnable() {
      return new Runnable() {
        @Override
        public void run() {
          updateProgress();
          postDelayedUpdateProgressRunnable();
        }
      };
    }

    private void postDelayedUpdateProgressRunnable() {
      handler.postDelayed(updateProgressRunnable, getUpdatePeriod());
    }

    @Override
    public void detach() {
      enableProgressUpdating(false);
      super.detach();
    }

    @Override
    public void enableProgressUpdating(boolean enable) {
      if (enable) {
        postDelayedUpdateProgressRunnable();
      } else {
        handler.removeCallbacks(updateProgressRunnable);
      }
    }

    @Override
    public void updateProgress() {
      if (player == null) {
        return;
      }
      PlaybackControlsRow controlsRow = getControlsRow();
      controlsRow.setBufferedProgress((int) player.getBufferedPosition());
      controlsRow.setCurrentTime((int) player.getCurrentPosition());
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

    boolean updateMetadataWhenReady = true;

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
      if (updateMetadataWhenReady && playbackState == DemoPlayer.STATE_READY) {
        LoadingHelper.hideLoading(PlaybackFragment.this);
        updateMetadata();
        updateMetadataWhenReady = false;
      }
    }

    @Override
    public void onError(Exception ex) {
      Log.e(TAG, "An error occurred in ExoPlayer", ex);
      PlaybackStateCompat state = new PlaybackStateCompat.Builder()
          .setState(STATE_ERROR, PLAYBACK_POSITION_UNKNOWN, PLAYBACK_SPEED_PAUSED)
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
      getLoaderManager().initLoader(0, null, new LoaderCallbacks());
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

  private final class LoaderCallbacks implements LoaderManager.LoaderCallbacks<String> {

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
      Log.d(TAG, "onCreateLoader");
      return new VideoUrlLoader(getActivity(), video.getLinksUrl());
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
      Log.d(TAG, "onLoadFinished");
      if (data == null) {
        Log.w(TAG, "No data returned by loader");
        return;
      }
      createPlayer(data);
    }

    private void createPlayer(String videoUrl) {
      SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
      if (surfaceTexture == null) {
        // Temporary fix: The surface texture is no longer available because the user left the
        // playback fragment early. The flow of this class needs to be rethought to fix
        // https://github.com/mkjensen/danish-media-license/issues/29.
        return;
      }
      HlsRendererBuilder rendererBuilder = new HlsRendererBuilder(getActivity(), TAG, videoUrl);
      player = new DemoPlayer(rendererBuilder);
      player.addListener(new DemoPlayerListener());
      player.prepare();
      player.setSurface(new Surface(surfaceTexture));
      playPause(true);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
      Log.d(TAG, "onLoaderReset");
      // Do nothing.
    }
  }
}
