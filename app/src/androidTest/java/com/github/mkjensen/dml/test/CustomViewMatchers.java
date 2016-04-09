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

import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Locale;

/**
 * Custom view matchers not provided by {@link ViewMatchers}.
 */
public final class CustomViewMatchers {

  private CustomViewMatchers() {
  }

  /**
   * Returns a matcher that matches a child of the specified view that has a {@link
   * android.widget.TextView} with the specified text property value.
   */
  public static Matcher<View> withChildText(final int parentId, final String text) {
    return new TypeSafeMatcher<View>() {

      @Override
      public void describeTo(Description description) {
        description.appendText(
            String.format(Locale.US, "View with id [%d] having text [%s]", parentId, text));
      }

      @Override
      protected boolean matchesSafely(View item) {
        return allOf(isDescendantOfA(withId(parentId)), withText(text)).matches(item);
      }
    };
  }
}
