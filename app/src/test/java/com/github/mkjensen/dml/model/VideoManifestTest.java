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

package com.github.mkjensen.dml.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link VideoManifest}.
 */
public class VideoManifestTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private VideoManifest manifest;

  private VideoManifest.Stream stream;

  @Before
  public void before() {
    manifest = new VideoManifest();
    stream = new VideoManifest.Stream();
  }

  @Test
  public void givenEmptyManifest_whenGettersCalled_thenTheyReturnNotSet() {

    // When/then
    assertEquals(VideoManifest.NOT_SET, manifest.getUrl(Protocol.DOWNLOAD));
    assertEquals(VideoManifest.NOT_SET, manifest.getUrl(Protocol.HDS));
    assertEquals(VideoManifest.NOT_SET, manifest.getUrl(Protocol.HLS));
    assertEquals(VideoManifest.NOT_SET, manifest.getUrl(Protocol.UNKNOWN));
    assertEquals(Collections.emptyList(), manifest.getStreams());
    assertEquals(Protocol.UNKNOWN, stream.getProtocol());
    assertEquals(VideoManifest.NOT_SET, stream.getUrl());
  }

  @Test
  public void getUrl_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    Protocol protocol = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    manifest.getUrl(protocol);
  }

  @Test
  public void getUrl_whenNonexistentProtocolArgument_thenGetUrlReturnsNotSet() {

    // Given
    Protocol protocol = Protocol.HLS;

    // When
    String url = manifest.getUrl(protocol);

    // Then
    assertEquals(VideoManifest.NOT_SET, url);
  }

  @Test
  public void getUrl_givenDifferentStreams_whenNonNullArgument_thenGetUrlReturnsCorrectValue() {

    // Given
    Protocol protocol = Protocol.HDS;
    stream.setProtocol(protocol);
    String url = "test";
    stream.setUrl(url);
    manifest.setStreams(Arrays.asList(
        new VideoManifest.Stream(),
        stream,
        new VideoManifest.Stream())
    );

    // When
    String actualUrl = manifest.getUrl(protocol);

    // Then
    assertEquals(url, actualUrl);
  }

  @Test
  public void setStreams_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    List<VideoManifest.Stream> streams = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    manifest.setStreams(streams);
  }

  @Test
  public void setStreams_whenNonNullArgument_thenGetStreamsReturnThatArgument() {

    // Given
    List<VideoManifest.Stream> streams = new ArrayList<>();

    // When
    manifest.setStreams(streams);

    // Then
    assertEquals(streams, manifest.getStreams());
  }

  @Test
  public void setProtocol_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    Protocol protocol = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    stream.setProtocol(protocol);
  }

  @Test
  public void setProtocol_whenNonNullArgument_thenGetProtocolReturnThatArgument() {

    // Given
    Protocol protocol = Protocol.DOWNLOAD;

    // When
    stream.setProtocol(protocol);

    // Then
    assertEquals(protocol, stream.getProtocol());
  }

  @Test
  public void setUrl_whenNullArgument_thenIllegalArgumentExceptionIsThrown() {

    // Given
    String url = null;

    // When/then
    thrown.expect(IllegalArgumentException.class);
    //noinspection ConstantConditions
    stream.setUrl(url);
  }

  @Test
  public void setUrl_whenNonNullArgument_thenGetUrlReturnThatArgument() {

    // Given
    String url = "test";

    // When
    stream.setUrl(url);

    // Then
    assertEquals(url, stream.getUrl());
  }
}
