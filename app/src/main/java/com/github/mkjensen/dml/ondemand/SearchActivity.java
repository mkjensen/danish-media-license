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

package com.github.mkjensen.dml.ondemand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.SpeechRecognitionCallback;
import android.util.Log;

import com.github.mkjensen.dml.R;

/**
 * Host activity for {@link SearchFragment}.
 */
public final class SearchActivity extends OnDemandActivity {

  private static final String TAG = "SearchActivity";

  private static final int SPEECH_REQUEST_CODE = 0;

  private SearchFragment fragment;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ondemand_search);
    initFragment();
  }

  private void initFragment() {
    fragment = (SearchFragment) getSupportFragmentManager()
        .findFragmentById(R.id.ondemand_search_fragment);
    fragment.setSpeechRecognitionCallback(new SpeechRecognitionCallback() {
      @Override
      public void recognizeSpeech() {
        Log.d(TAG, "recognizeSpeech");
        startActivityForResult(fragment.getRecognizerIntent(), SPEECH_REQUEST_CODE);
      }
    });
  }

  @Override
  public boolean onSearchRequested() {
    fragment.startRecognition();
    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case SPEECH_REQUEST_CODE:
        handleSpeechResult(resultCode, data);
        break;
      default:
        Log.w(TAG, "Unhandled request code: " + requestCode);
        break;
    }
  }

  private void handleSpeechResult(int resultCode, Intent data) {
    switch (resultCode) {
      case RESULT_CANCELED:
        // Do nothing.
        break;
      case RESULT_OK:
        fragment.setSearchQuery(data, true);
        break;
      default:
        Log.w(TAG, "Unhandled result code: " + resultCode);
        break;
    }
  }
}
