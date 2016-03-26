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

import static com.github.mkjensen.dml.Defense.notNull;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

/**
 * Metadata about an on-demand video.
 */
public class Video implements Parcelable {

  public static final Parcelable.Creator<Video> CREATOR = new ParcelableCreator();

  public static final String NOT_SET = "(not set)";

  @Json(name = "Slug")
  private String id = NOT_SET;

  @Json(name = "Title")
  private String title = NOT_SET;

  @Json(name = "Description")
  private String description = NOT_SET;

  @Json(name = "PrimaryImageUri")
  private String imageUrl = NOT_SET;

  @Json(name = "PrimaryAsset")
  @SuppressWarnings("CanBeFinal")
  private Asset asset = new Asset();

  private String url = NOT_SET;

  @NonNull
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
    dest.writeString(description);
    dest.writeString(imageUrl);
    dest.writeString(asset.linksUrl);
    dest.writeString(url);
  }

  @NonNull
  public String getId() {
    return id;
  }

  public void setId(@NonNull String id) {
    this.id = notNull(id);
  }

  @NonNull
  public String getTitle() {
    return title;
  }

  public void setTitle(@NonNull String title) {
    this.title = notNull(title);
  }

  @NonNull
  public String getDescription() {
    return description;
  }

  public void setDescription(@NonNull String description) {
    this.description = notNull(description);
  }

  @NonNull
  public String getImageUrl() {
    return notNull(imageUrl);
  }

  public void setImageUrl(@NonNull String imageUrl) {
    this.imageUrl = notNull(imageUrl);
  }

  @NonNull
  public String getLinksUrl() {
    return asset.linksUrl;
  }

  public void setLinksUrl(@NonNull String linksUrl) {
    asset.linksUrl = notNull(linksUrl);
  }

  @NonNull
  public String getUrl() {
    return url;
  }

  public void setUrl(@NonNull String url) {
    this.url = notNull(url);
  }

  private static final class ParcelableCreator implements Parcelable.Creator<Video> {

    @Override
    public Video createFromParcel(Parcel source) {
      Video video = new Video();
      video.id = source.readString();
      video.title = source.readString();
      video.description = source.readString();
      video.imageUrl = source.readString();
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
    String linksUrl = NOT_SET;
  }
}
