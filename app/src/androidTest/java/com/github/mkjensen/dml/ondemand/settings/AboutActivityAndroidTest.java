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

package com.github.mkjensen.dml.ondemand.settings;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;
import static com.github.mkjensen.dml.test.CustomViewMatchers.withChildText;
import static com.github.mkjensen.dml.test.ResourceUtils.getString;
import static org.hamcrest.Matchers.is;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.github.mkjensen.dml.R;
import com.github.mkjensen.dml.ondemand.settings.AboutActivity;

import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation tests for {@link AboutActivity}.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class AboutActivityAndroidTest {

  @Rule
  public final ActivityTestRule<AboutActivity> activityRule =
      new ActivityTestRule<>(AboutActivity.class);

  @Test
  public void contentLicenses_whenClicked_thenWebViewWithContentLicensesIsLaunched() {

    // When
    onView(withChildText(R.id.guidedactions_list, R.string.ondemand_settings_about_content))
        .perform(pressKey(KeyEvent.KEYCODE_ENTER));

    // Then
    enableJavaScriptInWebViewToSupportEspresso();
    onWebView().withElement(findElement(Locator.XPATH, "/html/body/h1"))
        .check(webMatches(getText(), is(getString(R.string.ondemand_settings_about_content))));
  }

  @Test
  public void thirdPartyLicenses_whenClicked_thenWebViewWithThirdPartyLicensesIsLaunched() {

    // When
    onView(withChildText(R.id.guidedactions_list, R.string.ondemand_settings_about_thirdparty))
        .perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN), pressKey(KeyEvent.KEYCODE_ENTER));

    // Then
    enableJavaScriptInWebViewToSupportEspresso();
    onWebView().withElement(findElement(Locator.XPATH, "/html/body/h1"))
        .check(webMatches(getText(), is(getString(R.string.ondemand_settings_about_thirdparty))));
  }

  private static void enableJavaScriptInWebViewToSupportEspresso() {
    onWebView(new BoundedMatcher<View, WebView>(WebView.class) {

      @Override
      public void describeTo(Description description) {
        description.appendText("WebView with JavaScript disabled");
      }

      @Override
      public boolean matchesSafely(WebView webView) {
        return !webView.getSettings().getJavaScriptEnabled();
      }
    }).forceJavascriptEnabled();
  }
}
