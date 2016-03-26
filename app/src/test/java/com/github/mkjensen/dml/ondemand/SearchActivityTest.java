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

import android.view.View;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.test.RobolectricTest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;

/**
 * Tests for {@link SearchActivity}.
 */
@Ignore("See https://github.com/mkjensen/danish-media-license/issues/26")
public class SearchActivityTest extends RobolectricTest {

  private SearchActivity searchActivity;

  /**
   * Creates a {@link SearchActivity} instance.
   */
  @Before
  public void before() {
    searchActivity = Robolectric.buildActivity(SearchActivity.class)
        .create()
        .get();
  }

  @Test
  public void findViewById_onDemandSearchFragmentExists() {

    // When
    View searchFragment = searchActivity.findViewById(R.id.ondemand_search_fragment);

    // Then
    assertNotNull(searchFragment);
  }
}