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

import com.github.mkjensen.dml.RobolectricTest;
import com.github.mkjensen.dml.TestUtils;
import com.github.mkjensen.dml.provider.DmlContract.Categories;
import com.github.mkjensen.dml.provider.DmlContract.Videos;

import org.junit.Test;

/**
 * Unit tests for {@link DmlContract}.
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
  public void categories_constructorMustBePrivateAndParameterless() {
    assertTrue(TestUtils.hasPrivateParameterlessConstructor(Categories.class));
  }

  @Test
  public void videos_constructorMustBePrivateAndParameterless() {
    assertTrue(TestUtils.hasPrivateParameterlessConstructor(Videos.class));
  }
}
