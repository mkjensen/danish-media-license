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

import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;

import com.github.mkjensen.dml.R;

/**
 * Browse screen for on-demand videos.
 */
public class BrowseFragment extends BrowseSupportFragment {

  private static final String TAG = "BrowseFragment";

  private ArrayObjectAdapter categoryAdapter;

  @Override
  public void onAttach(Context context) {
    Log.d(TAG, "onAttach");
    super.onAttach(context);
    categoryAdapter = new ArrayObjectAdapter(new ListRowPresenter());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.d(TAG, "onActivityCreated");
    super.onActivityCreated(savedInstanceState);
    setTitle(getString(R.string.app_name));
    setAdapter(categoryAdapter);
    setupListeners();
  }

  private void setupListeners() {
    setOnItemViewSelectedListener(new OnItemViewSelectedListener() {

      @Override
      public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                 RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Video) {
          Video video = (Video) item;
          ((BackgroundHelper.Provider) getActivity()).getBackgroundHelper()
              .setDelayed(video.getImageUrl());
        }
      }
    });

    setOnItemViewClickedListener(new OnItemViewClickedListener() {

      @Override
      public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                RowPresenter.ViewHolder rowViewHolder, Row row) {
        Video video = (Video) item;
        ((OnVideoSelectedListener) getActivity()).onVideoSelected(video);
      }
    });
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    addTestData();
  }

  private void addTestData() {
    HeaderItem headerItem = new HeaderItem("Most viewed");
    VideoPresenter videoPresenter = new VideoPresenter();
    ArrayObjectAdapter videoAdapter = new ArrayObjectAdapter(videoPresenter);
    videoAdapter.add(new Video.Builder()
        .slug("bedrag-9-10")
        .title("Bedrag 9:10")
        .description("Alf begraver Mia og tynges af skyld, men Mads får ham ud af"
            + " selvmedlidenheden, da han gør en vild opdagelse. Energreen har ikke bare store"
            + " huller i regnskaberne - deres pengekasse er helt tom. Det kan de udnytte."
            + " Claudia er nået frem til samme konklusion om Energreen, men Sander insisterer på"
            + " at fortsætte det farlige spil - og sætter hende til at stikke en kæp i hjulet på"
            + " Bagmandspolitiets planer. Sanders håndlanger opdager, hvor Nicky og Bimse har deres"
            + " mange penge fra og insisterer på at få dem tilbage. Og Svenskeren er ikke en mand,"
            + " man siger nej til.\nSerien er uegnet for mindre børn.\nMedvirkende: Thomas Bo"
            + " Larsen, Natalie Madueño, Esben Smed, Nikolaj Lie Kaas m.fl.\nManuskript: Jeppe"
            + " Gjervig Gram, Anders August og Jannik Tai Mosholt\nInstruktion:"
            + " Søren Kragh-Jacobsen\nSe også: \nWeb: www.dr.dk/bedrag\nInstagram:"
            + " BEDRAGDR\nFacebook: https://www.facebook.com/bedragDR\nSendes med synstolkning"
            + " på DR1Syn")
        .imageUrl("http://www.dr.dk/muTest/api/1.2/bar/56c330b2a11f9f0fc418f087")
        .build());
    videoAdapter.add(new Video.Builder()
        .slug("x-factor-2016-02-26")
        .title("X Factor")
        .description("Konkurrencen er i top, når X Factor i aften blænder op for årets andet"
            + " liveshow. Otte deltagere er tilbage, men kun syv kan fortsætte til næste uge, så"
            + " der er lagt op til masser af spænding, når deltagerne skal kaste sig ud i sange"
            + " fra de nordiske lande. Dommerne har gjort, hvad de kan for at forberede dem, så nu"
            + " er det op til dem selv at synge sig videre. Hvem der fortjener en plads videre, og"
            + " hvem der skal i farezonen, er op til dig derhjemme. Se med i aften på DR1.")
        .imageUrl("http://www.dr.dk/muTest/api/1.2/bar/56cc4c076187a40a6cb56de5").build());
    categoryAdapter.add(new ListRow(headerItem, videoAdapter));
    headerItem = new HeaderItem("Spotlight");
    videoAdapter = new ArrayObjectAdapter(videoPresenter);
    videoAdapter.add(new Video.Builder()
        .slug("dokumania-naturens-uorden")
        .title("Dokumania: Naturens uorden")
        .description("Dansk dokumentar fra 2015 af filminstruktøren Christian Sønderby"
            + " Jepsen\nDokumentaren skildrer spastikeren Jacob Nossells selvironiske kamp for at"
            + " forstå og iscenesætte sit eget liv. Ifølge videnskaben burde spastikeren Jacob"
            + " allerede fra fødslen være 'fravalgt' af både forskere og forældre. Med et intakt"
            + " intellekt, fanget i et svækket kropshylster, er han selveste legemliggørelsen af"
            + " normalitetens dilemma: For handicappet til at være normal og for normal til at"
            + " acceptere sig selv som handicappet. I forsøget på at forstå sig selv og sin egen"
            + " situation opsøger han førende danske videnskabsfolk og stiller dem spørgsmålet:"
            + " \"Er jeg unormal?\".")
        .imageUrl("http://www.dr.dk/muTest/api/1.2/bar/56b874266187a4086441a491")
        .build());
    categoryAdapter.add(new ListRow(headerItem, videoAdapter));
  }

  interface OnVideoSelectedListener {

    void onVideoSelected(Video video);
  }
}
