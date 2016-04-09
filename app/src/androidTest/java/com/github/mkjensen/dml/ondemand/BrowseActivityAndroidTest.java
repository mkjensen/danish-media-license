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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.github.mkjensen.dml.test.CustomViewMatchers.withChildText;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.ondemand.settings.AboutActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation tests for {@link BrowseActivity}.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class BrowseActivityAndroidTest {

  @Rule
  public final IntentsTestRule<BrowseActivity> intentsRule =
      new IntentsTestRule<>(BrowseActivity.class);

  private static final String[] CATEGORIES = new String[] {
      getString(R.string.backend_category_new),
      getString(R.string.backend_category_recommended),
      getString(R.string.backend_category_most_viewed),
      getString(R.string.ondemand_settings)
  };

  @Test
  public void category_whenSelected_thenIsDisplayed() {
    for (int i = 0; i < CATEGORIES.length; i++) {
      // First category is already selected.
      if (i > 0) {

        // When
        onView(withId(R.id.browse_headers)).perform(actionOnItemAtPosition(i, click()));
      }

      // Then
      onView(withChildText(R.id.browse_headers, CATEGORIES[i])).check(matches(isDisplayed()));
    }
  }

  @Test
  public void settings_whenClicked_thenSettingsAreDisplayed() {

    // When
    onView(withChildText(R.id.browse_headers, getString(R.string.ondemand_settings)))
        .perform(click());

    // Then
    onView(withChildText(R.id.browse_container_dock, getString(R.string.ondemand_settings_about)))
        .check(matches(isDisplayed()));
  }

  @Test
  public void settingsAbout_whenClicked_thenAboutActivityIsLaunched() {

    // Given
    onView(withChildText(R.id.browse_headers, getString(R.string.ondemand_settings)))
        .perform(click());

    // When
    onView(withChildText(R.id.browse_container_dock, getString(R.string.ondemand_settings_about)))
        .perform(click());

    // Then
    intended(hasComponent(AboutActivity.class.getCanonicalName()));
  }

  private static String getString(int resId) {
    return InstrumentationRegistry.getTargetContext().getString(resId);
  }
}
