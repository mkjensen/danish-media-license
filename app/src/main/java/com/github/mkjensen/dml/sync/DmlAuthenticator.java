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

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Stub authenticator that throws {@link UnsupportedOperationException} for all overridden methods.
 *
 * @see <a href="https://goo.gl/Isnl4M">Creating a Stub Authenticator</a>
 */
final class DmlAuthenticator extends AbstractAccountAuthenticator {

  public DmlAuthenticator(Context context) {
    super(context);
  }

  @Override
  public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                           String authTokenType, String[] requiredFeatures, Bundle options)
      throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                   Bundle options) throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                             String authTokenType, Bundle options) throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAuthTokenLabel(String authTokenType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                  String authTokenType, Bundle options)
      throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                            String[] features) throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }
}
