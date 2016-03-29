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

package com.github.mkjensen.dml.presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.support.membermodification.MemberMatcher.field;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mkjensen.dml.model.Video;
import com.github.mkjensen.dml.test.PowerMockRobolectricTest;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

/**
 * Tests for {@link VideoPresenter}.
 */
public class VideoPresenterTest extends PowerMockRobolectricTest {

  private VideoPresenter videoPresenter;

  @Before
  public void before() {
    videoPresenter = new VideoPresenter();
  }

  @Test
  public void onCreateViewHolder_shouldCreateView() {

    // When
    ViewHolder viewHolder = createViewHolder();

    // Then
    assertNotNull(viewHolder.view);
    assertEquals(ImageCardView.class, viewHolder.view.getClass());
  }

  @Test
  public void onBindViewHolder_shouldSetImageAndTitleOfView() throws IllegalAccessException {

    // Given
    ViewHolder viewHolder = createViewHolder();
    ImageCardView viewMock = (ImageCardView) spy(viewHolder.view);
    setViewHolderView(viewHolder, viewMock);
    Video video = new Video();
    String title = "title";
    video.setTitle(title);

    // When
    videoPresenter.onBindViewHolder(viewHolder, video);

    // Then
    verify(viewMock).setMainImage(isA(Drawable.class));
    assertEquals(title, viewMock.getTitleText());
  }

  @Test
  public void onUnbindViewHolder_shouldUnsetViewMainImage() throws IllegalAccessException {

    // Given
    ViewHolder viewHolderMock = mock(ViewHolder.class);
    ImageCardView viewMock = mock(ImageCardView.class);
    setViewHolderView(viewHolderMock, viewMock);

    // When
    videoPresenter.onUnbindViewHolder(viewHolderMock);

    // Then
    verify(viewMock).setMainImage(null);
  }

  private ViewHolder createViewHolder() {
    Activity activity = Robolectric.buildActivity(Activity.class).create().get();
    ViewGroup layout = new FrameLayout(activity);
    return videoPresenter.onCreateViewHolder(layout);
  }

  private static void setViewHolderView(ViewHolder viewHolder, View view)
      throws IllegalAccessException {
    field(ViewHolder.class, "view").set(viewHolder, view);
  }
}
