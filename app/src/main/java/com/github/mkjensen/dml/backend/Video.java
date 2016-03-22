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

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

/**
 * Metadata about an on-demand video.
 */
public final class Video implements Parcelable {

  public static final Parcelable.Creator<Video> CREATOR = new ParcelableCreator();

  @Json(name = "Slug")
  private String id;

  @Json(name = "Title")
  private String title;

  @Json(name = "PrimaryImageUri")
  private String imageUrl;

  @Json(name = "Description")
  private String description;

  @Json(name = "PrimaryAsset")
  @SuppressWarnings("CanBeFinal")
  private Asset asset;

  private String url;

  private Video() {
    asset = new Asset();
  }

  @Override
  public String toString() {
    return String.format("Video {id=%s}", id);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(title);
    dest.writeString(imageUrl);
    dest.writeString(description);
    dest.writeString(asset.linksUrl);
    dest.writeString(url);
  }

  public String getId() {
    return id;
  }

  /* Testing */ void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getDescription() {
    return description;
  }

  void setDescription(String description) {
    this.description = description;
  }

  public String getLinksUrl() {
    return asset.linksUrl;
  }

  /* Testing */ void setLinksUrl(String linksUrl) {
    asset.linksUrl = linksUrl;
  }

  public String getUrl() {
    return url;
  }

  void setUrl(String url) {
    this.url = url;
  }

  private Video copy() {
    Video copy = new Video();
    copy.id = id;
    copy.title = title;
    copy.imageUrl = imageUrl;
    copy.description = description;
    copy.asset.linksUrl = asset.linksUrl;
    copy.url = url;
    return copy;
  }

  public static final class Builder {

    private final Video video;

    public Builder() {
      video = new Video();
    }

    public Video build() {
      return video.copy();
    }

    public Builder id(String id) {
      video.id = id;
      return this;
    }

    public Builder title(String title) {
      video.title = title;
      return this;
    }

    public Builder imageUrl(String imageUrl) {
      video.imageUrl = imageUrl;
      return this;
    }

    public Builder description(String description) {
      video.description = description;
      return this;
    }

    public Builder linksUrl(String linksUrl) {
      video.asset.linksUrl = linksUrl;
      return this;
    }

    public Builder url(String url) {
      video.url = url;
      return this;
    }
  }

  private static final class ParcelableCreator implements Parcelable.Creator<Video> {

    @Override
    public Video createFromParcel(Parcel source) {
      Video video = new Video();
      video.id = source.readString();
      video.title = source.readString();
      video.imageUrl = source.readString();
      video.description = source.readString();
      video.asset.linksUrl = source.readString();
      video.url = source.readString();
      return video;
    }

    @Override
    public Video[] newArray(int size) {
      return new Video[size];
    }
  }

  private static final class Asset {

    @Json(name = "Uri")
    String linksUrl;
  }
}