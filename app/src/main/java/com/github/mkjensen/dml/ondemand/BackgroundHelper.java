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

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.BackgroundManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Provides convenience methods for updating the background image.
 */
public final class BackgroundHelper {

  private static final String TAG = "BackgroundHelper";
  private static final int DELAY_IN_MILLISECONDS = 300;

  private Activity activity;
  private BackgroundManager backgroundManager;
  private DisplayMetrics displayMetrics;
  private Handler handler;
  private Timer timer;

  /**
   * Uses the specified {@link Activity} to obtain {@link BackgroundManager} and {@link
   * DisplayMetrics} instances.
   */
  public BackgroundHelper(@NonNull Activity activity) {
    this.activity = activity;
    backgroundManager = BackgroundManager.getInstance(activity);
    backgroundManager.attach(activity.getWindow());
    displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    handler = new Handler();
  }

  /**
   * Cancels pending updates and immediately sets the background using {@link Glide} and {@link
   * BackgroundManager#setBitmap(Bitmap)}.
   */
  public void set(@NonNull String imageUrl) {
    cancelTimer();
    setBackground(imageUrl);
  }

  /**
   * Cancels pending updates and creates a new pending update that eventually sets the background
   * using {@link Glide} and {@link BackgroundManager#setBitmap(Bitmap)}.
   */
  public void setDelayed(@NonNull String imageUrl) {
    cancelTimer();
    Timer timer = new Timer();
    timer.schedule(new DelayedSetTimerTask(imageUrl), DELAY_IN_MILLISECONDS);
    this.timer = timer;
  }

  /**
   * Cancels pending updates and delegates to {@link BackgroundManager#release()}.
   */
  public void stop() {
    cancelTimer();
    backgroundManager.release();
  }

  /**
   * Releases all resources held by this instance which can then no longer be used.
   */
  public void destroy() {
    cancelTimer();
    handler = null;
    displayMetrics = null;
    backgroundManager = null;
    activity = null;
  }

  private boolean cancelTimer() {
    Timer timer = this.timer;
    if (timer == null) {
      return false;
    }
    timer.cancel();
    this.timer = null;
    return true;
  }

  private void setBackground(String imageUrl) {
    if (activity == null) {
      Log.e(TAG, "Request to set background when activity is null");
      return;
    }
    Glide.with(activity)
        .load(imageUrl)
        .asBitmap()
        .into(new SimpleTarget<Bitmap>(displayMetrics.widthPixels, displayMetrics.heightPixels) {

          @Override
          public void onResourceReady(Bitmap resource,
                                      GlideAnimation<? super Bitmap> glideAnimation) {
            backgroundManager.setBitmap(resource);
          }
        });
  }

  public interface Provider {

    BackgroundHelper getBackgroundHelper();
  }

  private final class DelayedSetTimerTask extends TimerTask {

    private final String imageUrl;

    private DelayedSetTimerTask(String imageUrl) {
      this.imageUrl = imageUrl;
    }

    @Override
    public void run() {
      Handler handler = BackgroundHelper.this.handler;
      if (handler == null) {
        Log.e(TAG, "Request to set background when handler is null");
        return;
      }
      handler.post(new Runnable() {

        @Override
        public void run() {
          if (cancelTimer()) {
            setBackground(imageUrl);
          }
        }
      });
    }
  }
}
