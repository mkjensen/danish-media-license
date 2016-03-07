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
 * Handles authentication by instantiating {@link DmlAuthenticator} and returning its {@link
 * IBinder}.
 */
public final class DmlAuthenticatorService extends Service {

  private DmlAuthenticator authenticator;

  @Override
  public void onCreate() {
    authenticator = new DmlAuthenticator(this);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return authenticator.getIBinder();
  }
}
