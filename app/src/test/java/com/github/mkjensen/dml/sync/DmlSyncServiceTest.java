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

import android.os.IBinder;
import android.os.RemoteException;

import com.github.mkjensen.dml.RobolectricTest;

import org.junit.Test;

/**
 * Tests for {@link DmlSyncService}.
 */
public class DmlSyncServiceTest extends RobolectricTest {

  @Test
  public void onBind_givenOnCreateHasBeenCalled_returnsISyncAdapter() throws RemoteException {

    // Given
    DmlSyncService syncService = new DmlSyncService();
    syncService.onCreate();

    // When
    IBinder binder = syncService.onBind(null);

    // When
    assertEquals("android.content.ISyncAdapter", binder.getInterfaceDescriptor());
  }
}
