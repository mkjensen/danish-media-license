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

package com.github.mkjensen.dml.backend.loader;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * Instrumentation tests for {@link VideoLoader}.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class VideoLoaderAndroidTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private Context context;

  @Before
  public void before() {
    context = InstrumentationRegistry.getTargetContext();
  }

  @Test
  public void constructor_whenCalledWithNullVideoId_thenThrowsIllegalArgumentException() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    new VideoLoader(context, null);
  }

  @Test
  public void getVideoId_whenCalled_thenReturnsVideoIdSuppliedToConstructor() {

    // Given
    String id = "test";
    VideoLoader loader = new VideoLoader(context, id);

    // When
    String actualId = loader.getVideoId();

    // Then
    assertEquals(id, actualId);
  }
}
