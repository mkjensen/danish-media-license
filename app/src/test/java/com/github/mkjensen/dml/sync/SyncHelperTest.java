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
import static org.junit.Assert.assertTrue;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import com.github.mkjensen.dml.provider.DmlContract;
import com.github.mkjensen.dml.test.RobolectricTest;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowContentResolver;

/**
 * Tests for {@link SyncHelper}.
 */
public class SyncHelperTest extends RobolectricTest {

  private SyncHelper syncHelper;

  private Account account;

  @Before
  public void before() {
    syncHelper = new SyncHelper();
    account = createAccount();
  }

  private Account createAccount() {
    AccountHelper accountHelper = new AccountHelper();
    return accountHelper.getOrCreateAccount(getContext());
  }

  @Test
  public void requestSync_contentResolverRequestSyncMustBeCalledWithCorrectExtras() {

    // When
    syncHelper.requestSync(account);

    // Then
    Bundle extras = getContentResolverStatus().syncExtras;
    assertTrue(extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL));
    assertTrue(extras.getBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED));
  }

  @Test
  public void requestSync_contentResolverRequestSyncMustBeCalled() {

    // Given
    int requestsBefore = getContentResolverStatus().syncRequests;

    // When
    syncHelper.requestSync(account);

    // Then
    int requestsAfter = getContentResolverStatus().syncRequests;
    assertEquals(requestsBefore + 1, requestsAfter);
  }

  private ShadowContentResolver.Status getContentResolverStatus() {
    return ShadowContentResolver.getStatus(account, DmlContract.AUTHORITY, true);
  }
}
