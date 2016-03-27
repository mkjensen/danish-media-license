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

package com.github.mkjensen.dml;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.github.mkjensen.dml.inject.ApplicationModule;
import com.github.mkjensen.dml.inject.BackendComponent;
import com.github.mkjensen.dml.inject.BackendModule;
import com.github.mkjensen.dml.inject.DaggerBackendComponent;

/**
 * Subclass of {@link Application} that enables dependency injection using Dagger.
 *
 * @see <a href="https://github.com/google/dagger">Dagger</a>
 */
public class DmlApplication extends Application {

  private static DmlApplication application;

  private BackendComponent backendComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    application = this;
    backendComponent = DaggerBackendComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .backendModule(new BackendModule())
        .build();
  }

  @NonNull
  public BackendComponent getBackendComponent() {
    return backendComponent;
  }

  /**
   * Returns the {@link DmlApplication} singleton. If this method is called before {@link
   * #onCreate()} has been called, {@link DmlException} will be thrown.
   */
  @NonNull
  public static DmlApplication getInstance() {
    if (application == null) {
      throw new DmlException("getInstance was called before onCreate");
    }
    return application;
  }

  /**
   * Returns the {@link DmlApplication} singleton.
   */
  @NonNull
  public static DmlApplication from(@NonNull Context context) {
    return (DmlApplication) context.getApplicationContext();
  }
}
