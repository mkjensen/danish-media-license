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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.mkjensen.dml.test.TestUtils;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Test;

import java.io.IOException;

/**
 * Tests for {@link VideoContainer}.
 */
public class VideoContainerTest {

  private static final String JSON = "{\n"
      + "  \"Items\": [\n"
      + "    {}\n"
      + "  ]\n"
      + "}\n";

  private static final String JSON_EMPTY_OBJECT = "{}";

  @Test
  public void category_constructorMustBePrivateAndParameterless() {
    assertTrue(TestUtils.hasPrivateParameterlessConstructor(VideoContainer.class));
  }

  @Test
  public void givenEmptyVideoContainerFromJson_whenGetVideosCalled_thenDoesNotReturnNull()
      throws IOException {

    // Given
    VideoContainer container = createFromJson(JSON_EMPTY_OBJECT);

    // When/Then
    assertNotNull(container);
    assertNotNull(container.getVideos());
  }

  @Test
  public void givenVideoContainerFromJson_whenGetVideosCalled_thenReturnsNonEmptyCollection()
      throws IOException {

    // Given
    VideoContainer container = createFromJson(JSON);

    // When/Then
    assertNotNull(container);
    assertNotNull(container.getVideos());
    assertEquals(1, container.getVideos().size());
  }

  private static VideoContainer createFromJson(String json) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<VideoContainer> adapter = moshi.adapter(VideoContainer.class);
    return adapter.fromJson(json);
  }
}
