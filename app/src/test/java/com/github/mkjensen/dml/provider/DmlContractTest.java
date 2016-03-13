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

package com.github.mkjensen.dml.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.net.Uri;

import com.github.mkjensen.dml.provider.DmlContract.Category;
import com.github.mkjensen.dml.provider.DmlContract.Video;
import com.github.mkjensen.dml.test.RobolectricTest;
import com.github.mkjensen.dml.test.TestUtils;

import org.junit.Test;

/**
 * Tests for {@link DmlContract}.
 */
public class DmlContractTest extends RobolectricTest {

  private static final String AUTHORITY = "com.github.mkjensen.dml.provider";

  @Test
  public void constructor_mustBePrivateAndParameterless() {
    assertTrue(TestUtils.hasPrivateParameterlessConstructor(DmlContract.class));
  }

  @Test
  public void authority_mustBeCorrect() {
    assertEquals(AUTHORITY, DmlContract.AUTHORITY);
  }

  @Test
  public void authorityUri_mustBeCorrect() throws Exception {
    assertEquals(Uri.parse("content://" + AUTHORITY), DmlContract.AUTHORITY_URI);
  }

  @Test
  public void category_constructorMustBePrivateAndParameterless() {
    assertTrue(TestUtils.hasPrivateParameterlessConstructor(Category.class));
  }

  @Test
  public void video_constructorMustBePrivateAndParameterless() {
    assertTrue(TestUtils.hasPrivateParameterlessConstructor(Video.class));
  }
}
