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

package com.github.mkjensen.dml.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Helper class for interacting with {@link BackgroundManager}.
 */
public final class BackgroundHelper {

  private static final int DELAY_IN_MILLISECONDS = 300;

  private static Handler handler;

  private static Runnable setBackgroundRunnable;

  private BackgroundHelper() {
  }

  /**
   * Delegates to {@link BackgroundManager#attach(Window)}.
   */
  public static void attach(@NonNull Activity activity) {
    getManager(activity).attach(activity.getWindow());
  }

  /**
   * Delegates to {@link BackgroundManager#release()}.
   */
  public static void release(@NonNull Activity activity) {
    getManager(activity).release();
  }

  /**
   * Calls {@link #setBackground(Activity, Drawable)} with {@code null}.
   */
  public static void clearBackground(@NonNull Activity activity) {
    setBackground(activity, (Drawable) null);
  }

  /**
   * Delegates to {@link BackgroundManager#getDrawable()}.
   */
  public static Drawable getBackground(@NonNull Activity activity) {
    return getManager(activity).getDrawable();
  }

  /**
   * Delegates to {@link BackgroundManager#setDrawable(Drawable)}.
   */
  public static void setBackground(@NonNull final Activity activity, @Nullable Drawable drawable) {
    getManager(activity).setDrawable(drawable);
  }

  /**
   * Calls {@link BackgroundManager#setBitmap(Bitmap)} with a {@link Bitmap} loaded asynchronously
   * from the specified URL.
   */
  public static void setBackground(@NonNull final Activity activity, @NonNull String url) {
    Glide.with(activity)
        .load(url)
        .asBitmap()
        .into(new SimpleTarget<Bitmap>() {
          @Override
          public void onResourceReady(Bitmap resource,
                                      GlideAnimation<? super Bitmap> glideAnimation) {
            getManager(activity).setBitmap(resource);
          }
        });
  }

  /**
   * Calls {@link #setBackground(Activity, String)} after a slight delay, overriding pending calls,
   * enabling callers to call this method quickly in succession without {@link
   * #setBackground(Activity, String)} being called each time. Basically a performance
   * optimization.
   */
  public static void setBackgroundDelayed(@NonNull final Activity activity,
                                          @NonNull final String url) {
    ensureHandler();
    removePendingSetBackground();
    setBackgroundRunnable = new Runnable() {
      @Override
      public void run() {
        setBackground(activity, url);
      }
    };
    handler.postDelayed(setBackgroundRunnable, DELAY_IN_MILLISECONDS);
  }

  private static void removePendingSetBackground() {
    if (setBackgroundRunnable != null) {
      handler.removeCallbacks(setBackgroundRunnable);
    }
  }

  private static BackgroundManager getManager(Activity activity) {
    return BackgroundManager.getInstance(activity);
  }

  private static void ensureHandler() {
    if (handler == null) {
      handler = new Handler();
    }
  }
}
