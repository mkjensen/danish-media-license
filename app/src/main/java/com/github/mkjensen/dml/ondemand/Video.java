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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Metadata about an on-demand video. Create instances using {@link Builder}.
 */
final class Video implements Parcelable {

  public static final Parcelable.Creator<Video> CREATOR = new ParcelableCreator();

  private String slug;
  private String title;
  private String description;
  private String imageUrl;

  private Video() {
  }

  @Override
  public String toString() {
    return String.format("Video{slug=%s}", slug);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(slug);
    dest.writeString(title);
    dest.writeString(description);
    dest.writeString(imageUrl);
  }

  String getSlug() {
    return slug;
  }

  String getTitle() {
    return title;
  }

  String getDescription() {
    return description;
  }

  String getImageUrl() {
    return imageUrl;
  }

  private Video copy() {
    Video copy = new Video();
    copy.slug = slug;
    copy.title = title;
    copy.description = description;
    copy.imageUrl = imageUrl;
    return copy;
  }

  static final class Builder {

    private final Video video;

    Builder() {
      video = new Video();
    }

    Video build() {
      return video.copy();
    }

    Builder slug(String slug) {
      video.slug = slug;
      return this;
    }

    Builder title(String title) {
      video.title = title;
      return this;
    }

    Builder description(String description) {
      video.description = description;
      return this;
    }

    Builder imageUrl(String imageUrl) {
      video.imageUrl = imageUrl;
      return this;
    }
  }

  private static final class ParcelableCreator implements Parcelable.Creator<Video> {

    @Override
    public Video createFromParcel(Parcel source) {
      Video video = new Video();
      video.slug = source.readString();
      video.title = source.readString();
      video.description = source.readString();
      video.imageUrl = source.readString();
      return video;
    }

    @Override
    public Video[] newArray(int size) {
      return new Video[size];
    }
  }
}
