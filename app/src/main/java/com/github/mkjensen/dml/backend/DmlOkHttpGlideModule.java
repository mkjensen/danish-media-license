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

package com.github.mkjensen.dml.backend;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.github.mkjensen.dml.DmlApplication;

import okhttp3.Call;

import java.io.InputStream;

import javax.inject.Inject;

/**
 * Extension of {@link OkHttpGlideModule} that supplies the {@link Call.Factory} via injection.
 *
 * <p>Note that this class must be configured for use via the Android manifest file. Also note that
 * {@link OkHttpGlideModule} must be excluded to avoid conflicts.
 *
 * <p>Configuration that must be added to the {@code application} section of {@code
 * AndroidManifest.xml}: <pre><code>
 * &lt;meta-data
 *     android:name="com.bumptech.glide.integration.okhttp3.OkHttpGlideModule"
 *     tools:node="remove" /&gt;
 *
 * &lt;meta-data
 *     android:name="com.github.mkjensen.dml.backend.DmlOkHttpGlideModule"
 *     android:value="GlideModule" /&gt;
 * </code></pre>
 */
public final class DmlOkHttpGlideModule extends OkHttpGlideModule {

  @Inject
  Call.Factory callFactory;

  public DmlOkHttpGlideModule() {
    DmlApplication.getInstance().getBackendComponent().inject(this);
  }

  @Override
  public void registerComponents(Context context, Glide glide) {
    glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(callFactory));
  }
}
