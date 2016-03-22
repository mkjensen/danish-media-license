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

package com.github.mkjensen.dml.test;

import android.content.Context;
import android.os.Build;

import com.github.mkjensen.dml.BuildConfig;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Base class for running Robolectric tests.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
    constants = BuildConfig.class,
    manifest = RobolectricTest.MANIFEST,
    sdk = RobolectricTest.ROBOLECTRIC_ANDROID_SDK_LEVEL)
public abstract class RobolectricTest {

  /**
   * The Android manifest file to load.
   */
  @SuppressWarnings("WeakerAccess")
  static final String MANIFEST = "src/main/AndroidManifest.xml";

  /**
   * The Android SDK level to emulate. The greatest supported SDK level as of Robolectric 3.0 is
   * {@link android.os.Build.VERSION_CODES#LOLLIPOP}.
   */
  @SuppressWarnings("WeakerAccess")
  static final int ROBOLECTRIC_ANDROID_SDK_LEVEL = Build.VERSION_CODES.LOLLIPOP;

  /**
   * Returns a {@link Context} instance from {@link RuntimeEnvironment}.
   *
   * @return a {@link Context} instance from {@link RuntimeEnvironment}
   */
  protected Context getContext() {
    return RuntimeEnvironment.application;
  }
}
