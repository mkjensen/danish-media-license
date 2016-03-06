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

package com.github.mkjensen.dml;

import android.os.Build;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Base class for running Robolectric unit tests.
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
  static final String MANIFEST = "src/main/AndroidManifest.xml";

  /**
   * The Android SDK level to emulate. The greatest supported SDK level as of Robolectric 3.0 is
   * {@link android.os.Build.VERSION_CODES#LOLLIPOP}.
   */
  static final int ROBOLECTRIC_ANDROID_SDK_LEVEL = Build.VERSION_CODES.LOLLIPOP;
}
