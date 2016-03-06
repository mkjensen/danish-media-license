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

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter.ViewHolder;
import android.widget.TextView;

import com.github.mkjensen.dml.PowerMockTest;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * Unit tests for {@link VideoDetailsPresenter}.
 */
@PrepareForTest(Video.class)
public class VideoDetailsPresenterTest extends PowerMockTest {

  @Test
  public void onBindDescription_shouldSetBodyAndTitleOfViewHolder() {

    // Given
    ViewHolder viewHolderMock = createViewHolderMock();
    Video videoMock = mock(Video.class);
    String description = "description";
    when(videoMock.getDescription()).thenReturn(description);
    String title = "title";
    when(videoMock.getTitle()).thenReturn(title);

    // When
    new VideoDetailsPresenter().onBindDescription(viewHolderMock, videoMock);

    // Then
    verify(viewHolderMock.getBody()).setText(description);
    verify(viewHolderMock.getTitle()).setText(title);
  }

  private static ViewHolder createViewHolderMock() {
    ViewHolder mock = mock(ViewHolder.class);
    TextView bodyMock = mock(TextView.class);
    TextView titleMock = mock(TextView.class);
    when(mock.getBody()).thenReturn(bodyMock);
    when(mock.getTitle()).thenReturn(titleMock);
    return mock;
  }
}
