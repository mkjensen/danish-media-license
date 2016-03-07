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
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

/**
 * Helper methods for interacting with {@link AccountManager}.
 */
public final class AccountHelper {

  private static final String TAG = "AccountHelper";

  /**
   * The name for the account returned by {@link #getOrCreateAccount(Context)}.
   */
  private static final String ACCOUNT_NAME = "sync";

  /**
   * The password for the account returned by {@link #getOrCreateAccount(Context)}.
   */
  private static final String ACCOUNT_PASSWORD = null;

  /**
   * The identification, in form of a domain name, for the account returned by {@link
   * #getOrCreateAccount(Context)}. This must be consistent with the setting in the configuration
   * files of the authenticator and the sync adapter.
   */
  private static final String ACCOUNT_TYPE = "com.github.mkjensen.dml";

  /**
   * The userdata for the account returned by {@link #getOrCreateAccount(Context)}.
   */
  private static final Bundle ACCOUNT_USERDATA = null;

  /**
   * Returns the {@link Account} used for syncing data. If the account does not exist, it will be
   * created.
   *
   * @param context the {@link Context} to use for retrieving {@link AccountManager}
   * @return the {@link Account} to use for syncing data
   */
  public Account getOrCreateAccount(Context context) {
    AccountManager accountManager = getAccountManager(context);
    Account[] accounts = getAccounts(accountManager);
    switch (accounts.length) {
      case 0:
        return createAccount(accountManager);
      case 1:
        return accounts[0];
      default:
        throw new DmlSyncException("More than one account was found: " + Arrays.toString(accounts));
    }
  }

  private static AccountManager getAccountManager(Context context) {
    return (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
  }

  private static Account[] getAccounts(AccountManager accountManager) {
    return accountManager.getAccountsByType(ACCOUNT_TYPE);
  }

  private static Account createAccount(AccountManager accountManager) {
    Log.d(TAG, "createAccount");
    Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
    if (accountManager.addAccountExplicitly(account, ACCOUNT_PASSWORD, ACCOUNT_USERDATA)) {
      return account;
    }
    throw new DmlSyncException("Unknown error occurred while adding account");
  }
}
