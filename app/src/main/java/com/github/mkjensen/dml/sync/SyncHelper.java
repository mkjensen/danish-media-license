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
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
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

  static void addTestData(ContentProviderClient provider, SyncResult syncResult) {
    Log.d(TAG, "addTestData");

    ContentValues values = new ContentValues();
    values.put(DmlContract.Categories.ID, "most-viewed");
    values.put(DmlContract.Categories.TITLE, "Most Viewed");
    values.put(DmlContract.Categories.URL, "TODO");
    insert(provider, DmlContract.Categories.CONTENT_URI, values, syncResult);

    values = new ContentValues();
    values.put(DmlContract.Categories.ID, "selected");
    values.put(DmlContract.Categories.TITLE, "Selected");
    values.put(DmlContract.Categories.URL, "TODO");
    insert(provider, DmlContract.Categories.CONTENT_URI, values, syncResult);

    values = new ContentValues();
    values.put(DmlContract.Videos.ID, "bedrag-10-10");
    values.put(DmlContract.Videos.TITLE, "Bedrag (10:10)");
    values.put(DmlContract.Videos.IMAGE_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56cec0826187a41eacfa6dd8");
    values.put(DmlContract.Videos.DETAILS_URL, "TODO");
    values.put(DmlContract.Videos.DESCRIPTION, "TODO");
    values.put(DmlContract.Videos.LIST_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56ce1df96187a41eacfa6925");
    values.put(DmlContract.Videos.URL, "http://drod08o-vh.akamaihd.net/i/all/clear/streaming/fb/"
        + "56cdb80ca11f9f11f88769fb/Bedrag--10-10-_021fde6ffdd049d7b45e3de5625e9b0c_,1125,562,2324,"
        + ".mp4.csmil/master.m3u8?cc1=name=Dansk~default=yes~forced=no~lang=da~uri=http://www.dr.dk"
        + "/muTest/api/1.2/subtitles/playlist/urn:dr:mu:manifest:56cdb80ca11f9f11f88769fb"
        + "?segmentsizeinms=60000");
    insert(provider, DmlContract.Videos.CONTENT_URI, values, syncResult);

    values = new ContentValues();
    values.put(DmlContract.Videos.ID, "x-factor-2016-03-04");
    values.put(DmlContract.Videos.TITLE, "X Factor");
    values.put(DmlContract.Videos.IMAGE_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56d7f8a26187a40e104a3f7e");
    values.put(DmlContract.Videos.DETAILS_URL, "TODO");
    values.put(DmlContract.Videos.DESCRIPTION, "TODO");
    values.put(DmlContract.Videos.LIST_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56da5734a11fa017189202c3");
    values.put(DmlContract.Videos.URL, "http://drod04b-vh.akamaihd.net/i/all/clear/streaming/24/"
        + "56da1dfb6187a416346a2a24/X-Factor_88457b85200f4b88ab7d271f639dd1ae_,1127,562,2317,.mp4"
        + ".csmil/master.m3u8?cc1=name=Dansk~default=yes~forced=no~lang=da~uri=http://www.dr.dk/"
        + "muTest/api/1.2/subtitles/playlist/urn:dr:mu:manifest:56da1dfb6187a416346a2a24"
        + "?segmentsizeinms=60000");
    insert(provider, DmlContract.Videos.CONTENT_URI, values, syncResult);

    values = new ContentValues();
    values.put(DmlContract.Videos.ID, "spoerg-mig-om-alt-5-8-3");
    values.put(DmlContract.Videos.TITLE, "Spørg mig om alt (5:8)");
    values.put(DmlContract.Videos.IMAGE_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56d6c469a11f9f0d085c37e9");
    values.put(DmlContract.Videos.DETAILS_URL, "TODO");
    values.put(DmlContract.Videos.DESCRIPTION, "TODO");
    values.put(DmlContract.Videos.LIST_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56d9e7326187a416346a294d");
    values.put(DmlContract.Videos.URL, "http://drod06h-vh.akamaihd.net/i/all/clear/download/e8/"
        + "56d9d3afa11fa0171891ffe8/Spoerg-mig-om-alt--5-8-_543f18cc538d4deb933937582334a226_,"
        + "1126,562,2325,.mp4.csmil/master.m3u8?cc1=name=Dansk~default=yes~forced=no~lang=da~uri="
        + "http://www.dr.dk/muTest/api/1.2/subtitles/playlist/"
        + "urn:dr:mu:manifest:56d9d3afa11fa0171891ffe8?segmentsizeinms=60000");
    insert(provider, DmlContract.Videos.CONTENT_URI, values, syncResult);

    values = new ContentValues();
    values.put(DmlContract.Videos.ID, "dokumania-naturens-uorden");
    values.put(DmlContract.Videos.TITLE, "Dokumania: Naturens uorden");
    values.put(DmlContract.Videos.IMAGE_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56b874266187a4086441a491");
    values.put(DmlContract.Videos.DETAILS_URL, "TODO");
    values.put(DmlContract.Videos.DESCRIPTION, "TODO");
    values.put(DmlContract.Videos.LIST_URL, "http://www.dr.dk/muTest/api/1.2/bar/"
        + "56b5e10ea11f9f12b82a3264");
    values.put(DmlContract.Videos.URL, "http://drod03m-vh.akamaihd.net/i/dk/clear/streaming/53/"
        + "56b592836187a409c0fc0b53/Dokumania--Naturens-uorden_c3ff1414744c4456bec9568915b283fd_,"
        + "1127,562,2276,.mp4.csmil/master.m3u8");
    insert(provider, DmlContract.Videos.CONTENT_URI, values, syncResult);

    values = new ContentValues();
    values.put(DmlContract.CategoriesVideos.VIDEO_ID, "bedrag-10-10");
    insert(provider, DmlContract.CategoriesVideos.buildUri("most-viewed"), values, syncResult);

    values = new ContentValues();
    values.put(DmlContract.CategoriesVideos.VIDEO_ID, "x-factor-2016-03-04");
    insert(provider, DmlContract.CategoriesVideos.buildUri("most-viewed"), values, syncResult);

    values = new ContentValues();
    values.put(DmlContract.CategoriesVideos.VIDEO_ID, "spoerg-mig-om-alt-5-8-3");
    insert(provider, DmlContract.CategoriesVideos.buildUri("selected"), values, syncResult);

    values = new ContentValues();
    values.put(DmlContract.CategoriesVideos.VIDEO_ID, "dokumania-naturens-uorden");
    insert(provider, DmlContract.CategoriesVideos.buildUri("selected"), values, syncResult);
  }

  private static void insert(ContentProviderClient provider, Uri uri, ContentValues values,
                             SyncResult syncResult) {
    try {
      provider.insert(uri, values);
      syncResult.stats.numInserts++;
    } catch (RemoteException ex) {
      syncResult.stats.numIoExceptions++;
    }
  }
}