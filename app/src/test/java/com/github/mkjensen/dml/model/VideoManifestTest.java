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

package com.github.mkjensen.dml.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link VideoManifest}.
 */
public class VideoManifestTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private VideoManifest manifest;

  @Before
  public void before() {
    manifest = new VideoManifest();
  }

  @Test
  public void givenEmptyManifest_whenGettersCalled_thenTheyReturnNotSet() {

    // When/then
    assertEquals(VideoManifest.NOT_SET, manifest.getStreamUrl());
  }
}
