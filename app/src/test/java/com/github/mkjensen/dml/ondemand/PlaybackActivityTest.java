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

import static org.junit.Assert.assertNotNull;

import android.content.Intent;
import android.view.View;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.backend.Video;
import com.github.mkjensen.dml.test.RobolectricTest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

/**
 * Tests for {@link PlaybackActivity}.
 */
@Ignore("See https://github.com/mkjensen/danish-media-license/issues/18")
public class PlaybackActivityTest extends RobolectricTest {

  private PlaybackActivity playbackActivity;

  /**
   * Creates a {@link PlaybackActivity} instance.
   */
  @Before
  public void before() {
    playbackActivity = Robolectric.buildActivity(PlaybackActivity.class)
        .withIntent(createIntent())
        .create()
        .get();
  }

  private static Intent createIntent() {
    Intent intent = new Intent(RuntimeEnvironment.application, PlaybackActivity.class);
    intent.putExtra(PlaybackActivity.VIDEO, createVideo());
    return intent;
  }

  private static Video createVideo() {
    return new Video.Builder()
        .build();
  }

  @Test
  public void findViewById_onDemandPlaybackFragmentExists() {

    // When
    View playBackFragment = playbackActivity.findViewById(R.id.ondemand_playback_fragment);

    // Then
    assertNotNull(playBackFragment);
  }
}
