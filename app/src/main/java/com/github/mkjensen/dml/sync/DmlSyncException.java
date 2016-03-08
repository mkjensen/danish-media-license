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

import com.github.mkjensen.dml.DmlException;

/**
 * Thrown when a sync issue occurs.
 */
public class DmlSyncException extends DmlException {

  private static final long serialVersionUID = -6766437545060713221L;

  /**
   * Constructs a new {@link DmlSyncException} that includes the current stack trace.
   */
  public DmlSyncException() {
  }

  /**
   * Constructs a new {@link DmlSyncException} with the current stack trace and the specified detail
   * message.
   *
   * @param detailMessage the detail message for this exception
   */
  public DmlSyncException(String detailMessage) {
    super(detailMessage);
  }

  /**
   * Constructs a new {@link DmlSyncException} with the current stack trace, the specified detail
   * message and the specified cause.
   *
   * @param detailMessage the detail message for this exception
   * @param throwable     the cause of this exception
   */
  public DmlSyncException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  /**
   * Constructs a new {@link DmlSyncException} with the current stack trace and the specified
   * cause.
   *
   * @param throwable the cause of this exception
   */
  public DmlSyncException(Throwable throwable) {
    super(throwable);
  }
}
