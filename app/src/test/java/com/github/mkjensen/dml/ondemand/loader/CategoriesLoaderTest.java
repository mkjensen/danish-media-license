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

package com.github.mkjensen.dml.ondemand.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.github.mkjensen.dml.test.RobolectricTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link CategoriesLoader}.
 */
public class CategoriesLoaderTest extends RobolectricTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void constructorQuery_whenCalledWithNullQuery_thenThrowsIllegalArgumentException() {

    // When/then
    thrown.expect(IllegalArgumentException.class);
    @SuppressWarnings("ConstantConditions")
    CategoriesLoader loader = new CategoriesLoader(getContext(), null);
    assertNull(loader); // For your eyes only, PMD.
  }

  @Test
  public void getQuery_givenNonQueryLoader_whenCalled_thenReturnsNull() {

    // Given
    CategoriesLoader loader = new CategoriesLoader(getContext());

    // When
    String query = loader.getQuery();

    // Then
    assertNull(query);
  }

  @Test
  public void getQuery_givenQueryLoader_whenCalled_thenReturnsQuerySuppliedToConstructor() {

    // Given
    String query = "test";
    CategoriesLoader loader = new CategoriesLoader(getContext(), query);

    // When
    String actualQuery = loader.getQuery();

    // Then
    assertEquals(query, actualQuery);
  }
}
