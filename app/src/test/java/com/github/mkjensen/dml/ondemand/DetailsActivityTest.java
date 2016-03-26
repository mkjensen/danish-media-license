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
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.view.View;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.test.RobolectricTest;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

/**
 * Tests for {@link DetailsActivity}.
 */
public class DetailsActivityTest extends RobolectricTest {

  private DetailsActivity detailsActivity;

  /**
   * Creates a {@link DetailsActivity} instance.
   */
  @Before
  public void before() {
    detailsActivity = Robolectric.buildActivity(DetailsActivity.class)
        .withIntent(createIntent())
        .create()
        .get();
  }

  private static Intent createIntent() {
    Intent intent = new Intent(RuntimeEnvironment.application, DetailsActivity.class);
    intent.putExtra(DetailsActivity.VIDEO_ID, "test");
    return intent;
  }

  @Test
  public void findViewById_onDemandDetailsFragmentExists() {

    // When
    View detailsFragment = detailsActivity.findViewById(R.id.ondemand_details_fragment);

    // Then
    assertNotNull(detailsFragment);
  }

  @Test
  public void onSearchRequested_returnsTrue() {

    // When
    boolean searchRequested = detailsActivity.onSearchRequested();

    // Then
    assertTrue(searchRequested);
  }
}
