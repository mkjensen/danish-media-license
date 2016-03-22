/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mkjensen.dml.exoplayer;

import com.google.android.exoplayer.DefaultLoadControl;
import com.google.android.exoplayer.LoadControl;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.hls.DefaultHlsTrackSelector;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.hls.PtsTimestampAdjusterProvider;
import com.google.android.exoplayer.metadata.MetadataTrackRenderer;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.metadata.id3.Id3Parser;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.upstream.TransferListener;
import com.google.android.exoplayer.upstream.UriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.ManifestFetcher.ManifestCallback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.os.Handler;

import com.github.mkjensen.dml.exoplayer.DemoPlayer.RendererBuilder;

import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.List;

/**
 * A {@link RendererBuilder} for HLS.
 *
 * @see <a href="https://goo.gl/QaNCjx">ExoPlayer HlsRendererBuilder</a>
 */
public class HlsRendererBuilder implements RendererBuilder {

  private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
  private static final int MAIN_BUFFER_SEGMENTS = 256;
  private static final int TEXT_BUFFER_SEGMENTS = 2;

  private static OkHttpClient httpClient;

  private final Context context;
  private final String userAgent;
  private final String url;

  private AsyncRendererBuilder currentAsyncBuilder;

  /**
   * Creates a new renderer builder for content served using HLS.
   */
  public HlsRendererBuilder(Context context, String userAgent, String url) {
    this.context = context;
    this.userAgent = userAgent;
    this.url = url;
  }

  @Override
  public void buildRenderers(DemoPlayer player) {
    currentAsyncBuilder = new AsyncRendererBuilder(context, userAgent, url, player);
    currentAsyncBuilder.init();
  }

  @Override
  public void cancel() {
    if (currentAsyncBuilder != null) {
      currentAsyncBuilder.cancel();
      currentAsyncBuilder = null;
    }
  }

  private static final class AsyncRendererBuilder implements ManifestCallback<HlsPlaylist> {

    private final Context context;
    private final String userAgent;
    private final String url;
    private final DemoPlayer player;
    private final ManifestFetcher<HlsPlaylist> playlistFetcher;

    private boolean canceled;

    public AsyncRendererBuilder(Context context, String userAgent, String url, DemoPlayer player) {
      this.context = context;
      this.userAgent = userAgent;
      this.url = url;
      this.player = player;
      HlsPlaylistParser parser = new HlsPlaylistParser();
      playlistFetcher = new ManifestFetcher<>(url, createDataSource(context, userAgent), parser);
    }

    public void init() {
      playlistFetcher.singleLoad(player.getMainHandler().getLooper(), this);
    }

    public void cancel() {
      canceled = true;
    }

    @Override
    public void onSingleManifestError(IOException ex) {
      if (canceled) {
        return;
      }

      player.onRenderersError(ex);
    }

    @Override
    public void onSingleManifest(HlsPlaylist manifest) {
      if (canceled) {
        return;
      }

      Handler mainHandler = player.getMainHandler();
      LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(BUFFER_SEGMENT_SIZE));
      DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
      PtsTimestampAdjusterProvider timestampAdjusterProvider = new PtsTimestampAdjusterProvider();

      // Build the video/audio/metadata renderers.
      DataSource dataSource = createDataSource(context, userAgent, bandwidthMeter);
      HlsChunkSource chunkSource = new HlsChunkSource(true /* isMaster */, dataSource, url,
          manifest, DefaultHlsTrackSelector.newDefaultInstance(context), bandwidthMeter,
          timestampAdjusterProvider, HlsChunkSource.ADAPTIVE_MODE_SPLICE);
      HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, loadControl,
          MAIN_BUFFER_SEGMENTS * BUFFER_SEGMENT_SIZE, mainHandler, player, DemoPlayer.TYPE_VIDEO);
      MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(context,
          sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT,
          5000, mainHandler, player, 50);
      final MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(
          sampleSource, MediaCodecSelector.DEFAULT, null, true, player.getMainHandler(), player,
          AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);
      final MetadataTrackRenderer<List<Id3Frame>> id3Renderer = new MetadataTrackRenderer<>(
          sampleSource, new Id3Parser(), player, mainHandler.getLooper());

      TrackRenderer[] renderers = new TrackRenderer[DemoPlayer.RENDERER_COUNT];
      renderers[DemoPlayer.TYPE_VIDEO] = videoRenderer;
      renderers[DemoPlayer.TYPE_AUDIO] = audioRenderer;
      renderers[DemoPlayer.TYPE_METADATA] = id3Renderer;
      player.onRenderers(renderers, bandwidthMeter);
    }
  }

  private static UriDataSource createDataSource(Context context, String userAgent) {
    return createDataSource(context, userAgent, null);
  }

  private static UriDataSource createDataSource(Context context, String userAgent,
                                                TransferListener transferListener) {
    return new DefaultUriDataSource(
        context,
        transferListener,
        new OkHttpDataSource(getHttpClient(), userAgent, null, transferListener)
    );
  }

  private static synchronized OkHttpClient getHttpClient() {
    if (httpClient == null) {
      httpClient = new OkHttpClient();
    }
    return httpClient;
  }
}
