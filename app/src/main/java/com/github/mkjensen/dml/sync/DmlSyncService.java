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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Handles sync by instantiating {@link DmlSyncAdapter} and returning its {@link IBinder}.
 */
public final class DmlSyncService extends Service {

  /**
   * Locking object used to ensure that {@link #syncAdapter} is a singleton.
   */
  private static final Object LOCK = new Object();

  /**
   * Singleton {@link DmlSyncAdapter} instance created by {@link #onCreate()}.
   */
  private static DmlSyncAdapter syncAdapter;

  @Override
  public void onCreate() {
    synchronized (LOCK) {
      if (syncAdapter == null) {
        syncAdapter = new DmlSyncAdapter(getApplicationContext());
      }
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return syncAdapter.getSyncAdapterBinder();
  }
}
