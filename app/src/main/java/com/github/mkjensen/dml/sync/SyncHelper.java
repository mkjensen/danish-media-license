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

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;

import com.github.mkjensen.dml.provider.DmlContract;

/**
 * Helper methods for interacting with {@link ContentResolver}.
 */
public final class SyncHelper {

  private static final String TAG = "SyncHelper";

  /**
   * Schedules sync request at the front of the sync request queue and without any delay.
   *
   * @param account the {@link Account} to use
   */
  public void requestSync(Account account) {
    Log.d(TAG, "requestSync");
    Bundle extras = new Bundle();
    extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
    extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    ContentResolver.requestSync(account, DmlContract.AUTHORITY, extras);
  }
}
