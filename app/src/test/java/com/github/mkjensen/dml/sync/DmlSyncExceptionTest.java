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

package com.github.mkjensen.dml.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Tests for {@link DmlSyncException}.
 */
public class DmlSyncExceptionTest {

  @Test
  public void constructor_whenGetCauseAndGetMessage_thenEqualsConstructor() {

    // Given
    Exception syncException = new DmlSyncException();

    // When
    Throwable cause = syncException.getCause();
    String message = syncException.getMessage();

    // Then
    assertNull(cause);
    assertNull(message);
  }

  @Test
  public void constructorDetailMessage_whenGetCauseAndGetMessage_thenEqualsConstructor() {

    // Given
    String detailMessage = "detailMessage";
    Exception syncException = new DmlSyncException(detailMessage);

    // When
    Throwable cause = syncException.getCause();
    String message = syncException.getMessage();

    // Then
    assertNull(cause);
    assertEquals(detailMessage, message);
  }

  @Test
  public void constructorDetailMessageThrowable_whenGetCauseAndGetMessage_thenEqualsConstructor() {

    // Given
    String detailMessage = "detailMessage";
    Throwable throwable = new Throwable();
    Exception syncException = new DmlSyncException(detailMessage, throwable);

    // When
    Throwable cause = syncException.getCause();
    String message = syncException.getMessage();

    // Then
    assertEquals(throwable, cause);
    assertEquals(detailMessage, message);
  }

  @Test
  public void constructorThrowable_whenGetCauseAndGetMessage_thenEqualsConstructor() {

    // Given
    Throwable throwable = new Throwable();
    Exception syncException = new DmlSyncException(throwable);

    // When
    Throwable cause = syncException.getCause();
    String message = syncException.getMessage();

    // Then
    assertEquals(throwable, cause);
    assertEquals(throwable.toString(), message);
  }
}
