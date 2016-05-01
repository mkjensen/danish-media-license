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
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.github.mkjensen.dml.test.CustomViewMatchers.withChildText;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.ondemand.settings.AboutActivity;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation tests for {@link BrowseActivity}.
 */
@Ignore
@RunWith(AndroidJUnit4.class)
@MediumTest
public class BrowseActivityAndroidTest {

  @Rule
  public final IntentsTestRule<BrowseActivity> intentsRule =
      new IntentsTestRule<>(BrowseActivity.class);

  @Test
  public void search_whenClicked_thenSearchActivityIsLaunched() {

    // When
    onView(withId(R.id.title_orb)).perform(click());

    // Then
    intended(hasComponent(SearchActivity.class.getCanonicalName()));
  }

  @Test
  public void settingsAbout_whenClicked_thenAboutActivityIsLaunched() {

    // Given
    onView(withChildText(R.id.browse_headers, R.string.ondemand_settings))
        .perform(click(), pressKey(KeyEvent.KEYCODE_ENTER));

    // When
    onView(withChildText(R.id.browse_container_dock, R.string.ondemand_settings_about))
        .perform(click());

    // Then
    intended(hasComponent(AboutActivity.class.getCanonicalName()));
  }

  @Test
  public void video_whenClicked_thenDetailsActivityIsLaunched() {

    // Given
    onView(withChildText(R.id.browse_headers, R.string.backend_category_new)).perform(click());

    // When
    onView(withChildText(R.id.browse_container_dock, "New Title")).perform(click());

    // Then
    intended(hasComponent(DetailsActivity.class.getCanonicalName()));
  }
}
