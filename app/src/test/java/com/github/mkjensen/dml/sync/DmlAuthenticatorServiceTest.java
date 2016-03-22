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
import static org.junit.Assert.assertNotNull;

import android.os.IBinder;
import android.os.RemoteException;

import com.github.mkjensen.dml.test.RobolectricTest;

import org.junit.Test;

/**
 * Tests for {@link DmlAuthenticatorService}.
 */
public class DmlAuthenticatorServiceTest extends RobolectricTest {

  @Test
  public void onBind_givenOnCreateHasBeenCalled_returnsIAccountAuthenticator()
      throws RemoteException {

    // Given
    DmlAuthenticatorService authenticatorService = new DmlAuthenticatorService();
    authenticatorService.onCreate();

    // When
    IBinder binder = authenticatorService.onBind(null);

    // When
    assertNotNull(binder);
    assertEquals("android.accounts.IAccountAuthenticator", binder.getInterfaceDescriptor());
  }
}
