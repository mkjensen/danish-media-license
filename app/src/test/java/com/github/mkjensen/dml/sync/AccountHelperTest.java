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
import static org.junit.Assert.assertSame;

import android.accounts.Account;

import com.github.mkjensen.dml.test.RobolectricTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link AccountHelper}.
 */
public class AccountHelperTest extends RobolectricTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private AccountHelper accountHelper;

  @Before
  public void before() {
    accountHelper = new AccountHelper();
  }

  @Test
  public void getAccount_returnsAccountWithCorrectName() {

    // When
    Account account = accountHelper.getOrCreateAccount(getContext());

    // Then
    assertEquals("sync", account.name);
  }

  @Test
  public void getAccount_returnsAccountWithCorrectType() {

    // When
    Account account = accountHelper.getOrCreateAccount(getContext());

    // Then
    assertEquals("com.github.mkjensen.dml", account.type);
  }

  @Test
  public void getAccount_returnsSameAccountIfCalledMultipleTimes() {

    // When
    Account account = accountHelper.getOrCreateAccount(getContext());
    Account accountSndCall = accountHelper.getOrCreateAccount(getContext());

    // Then
    assertSame(account, accountSndCall);
  }
}
