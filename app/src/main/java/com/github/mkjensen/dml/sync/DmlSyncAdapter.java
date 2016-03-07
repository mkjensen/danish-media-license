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
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Sync adapter for downloading content.
 */
final class DmlSyncAdapter extends AbstractThreadedSyncAdapter {

  private static final String TAG = "DmlSyncAdapter";

  /**
   * Whether or not sync requests that have {@link ContentResolver#SYNC_EXTRAS_INITIALIZE} set will
   * be internally handled by {@link AbstractThreadedSyncAdapter}.
   */
  private static final boolean AUTO_INITIALIZE = true;

  /**
   * Specifies whether or not syncs for different accounts are allowed to run at the same time, each
   * in their own thread. This must be consistent with the setting in the sync adapter's
   * configuration file.
   */
  private static final boolean ALLOW_PARALLEL_SYNCS = false;

  /**
   * Creates a {@link DmlSyncAdapter}.
   *
   * @param context the {@link Context} that the sync adapter is running within
   */
  DmlSyncAdapter(Context context) {
    super(context, AUTO_INITIALIZE, ALLOW_PARALLEL_SYNCS);
  }

  @Override
  public void onPerformSync(Account account, Bundle extras, String authority,
                            ContentProviderClient provider, SyncResult syncResult) {
    Log.d(TAG, "onPerformSync");
    SyncHelper.addTestData(provider, syncResult);
  }
}
